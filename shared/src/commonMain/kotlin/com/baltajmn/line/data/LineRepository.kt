package com.baltajmn.line.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.JournalFile
import com.baltajmn.line.model.JournalJson
import com.baltajmn.line.model.LineEntry
import com.baltajmn.line.model.Settings
import com.baltajmn.line.model.isoKey
import com.baltajmn.line.model.logicalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

const val SAVE_DEBOUNCE_MS = 800L

/** A month of writing with no backup is when the question earns the interruption, once. */
const val BACKUP_NOTICE_AFTER_DAYS = 30

/** "Today" everywhere in the app: the logical day, which ends at 03:00 local time. */
@OptIn(ExperimentalTime::class)
fun today(): LocalDate = logicalDate(Clock.System.now(), TimeZone.currentSystemDefault())

/** Wall clock, which is what the reminder is booked against. */
@OptIn(ExperimentalTime::class)
fun nowLocal(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * Single source of truth. The whole diary is one JSON file; state changes at once on the main
 * thread and a single writer persists the latest snapshot off it, so two saves never cross and
 * never rotate the backup twice.
 */
object LineRepository {

    var file by mutableStateOf(JournalFile())
        private set

    val journal: Journal get() = file.entries
    val settings: Settings get() = file.settings

    /** The last write failed. Today shows it; the next change retries. */
    var saveFailed by mutableStateOf(false)
        private set

    /** Neither file could be read. Both were moved aside and the diary starts empty. */
    var corrupt by mutableStateOf(false)
        private set

    private val scope by lazy { MainScope() }
    private val writeLock = Mutex()
    private var saveJob: Job? = null
    private var written: JournalFile? = null

    /** Reads the diary, falling back to the backup, and never writes over a file it could not read. */
    fun load() {
        val main = Storage.read()
        var loaded = decode(main)
        var previous: String? = null
        if (loaded == null) {
            previous = Storage.readPrevious()
            loaded = decode(previous)
            if (loaded != null) runCatching { Storage.restoreMain(previous!!) }
        }
        corrupt = false
        when {
            loaded != null -> {
                file = loaded
                sweep(loaded)
            }
            main == null && previous == null -> file = JournalFile()
            else -> {
                Storage.quarantine()
                file = JournalFile()
                corrupt = true
            }
        }
        written = file
        syncWidgets(file.entries, file.settings, today())
    }

    fun dismissCorrupt() {
        corrupt = false
    }

    fun edit(change: (JournalFile) -> JournalFile) {
        file = change(file)
        saveJob?.cancel()
        // Cancelling only ever stops the wait: a write that has started always finishes.
        saveJob = scope.launch {
            delay(SAVE_DEBOUNCE_MS)
            withContext(NonCancellable) { persist() }
        }
    }

    /** Writes the latest state now. Going to the background and leaving an editor call it. */
    suspend fun flush() {
        saveJob?.cancel()
        withContext(NonCancellable) { persist() }
    }

    fun saveNow() {
        scope.launch { flush() }
    }

    fun updateSettings(change: (Settings) -> Settings) = edit { it.copy(settings = change(it.settings)) }

    // --- entries ----------------------------------------------------------------------------

    fun entryOn(date: LocalDate): LineEntry? = journal[date.isoKey()]

    fun setText(date: LocalDate, text: String, today: LocalDate) {
        val next = journal.withText(date, text, today) ?: return
        edit { it.copy(entries = next) }
    }

    /** Asked once, a month in, and never again after a backup or after being waved away. */
    fun needsBackupNotice(today: LocalDate): Boolean {
        if (settings.backupNoticeDone || settings.lastBackup != null) return false
        val first = journal.keys.minOrNull()?.let(LocalDate::parse) ?: return false
        return first.daysUntil(today) >= BACKUP_NOTICE_AFTER_DAYS
    }

    /**
     * Takes in a merge: the photos the backup brought move out of import/ first, under a free name
     * if theirs was taken, and only then does the diary change. Nothing of this phone is dropped.
     */
    fun applyImport(result: MergeResult, delivered: Set<String>, onDone: () -> Unit = {}) {
        val before = journal
        scope.launch {
            // Only what the backup actually carried is adopted. A backup that names a photo it does
            // not bring must not pick up whatever a previous import happened to leave in import/.
            val adoptable = result.photosFromIncoming.filter { it in delivered && isSafePhotoName(it) }
            val renamed = withContext(Dispatchers.IO) {
                val taken = (Storage.listPhotos() + before.values.mapNotNull { it.photo }).toMutableSet()
                val renamed = mutableMapOf<String, String>()
                adoptable.forEach { name ->
                    val target = if (name in taken) freePhotoName(taken) else name
                    taken += target
                    renamed[name] = target
                    Storage.adoptImport(name, target)
                }
                renamed
            }
            val merged = result.journal.mapValues { (key, entry) ->
                val photo = entry.photo
                val fromBackup = photo != null && before[key]?.photo != photo
                when {
                    // Only a photo that came with the backup is renamed: the one already here keeps its name.
                    fromBackup && photo in renamed -> entry.copy(photo = renamed.getValue(photo!!))
                    // Named but not delivered: the line is kept, the photo that does not exist is not.
                    fromBackup -> entry.copy(photo = null)
                    else -> entry
                }
            }
            edit { it.copy(entries = merged) }
            flush()
            withContext(Dispatchers.IO) { Storage.importDir() }
            onDone()
        }
    }

    fun delete(date: LocalDate) {
        val key = date.isoKey()
        if (key in journal) edit { it.copy(entries = it.entries - key) }
    }

    // --- internals ----------------------------------------------------------------------------

    private suspend fun persist() = writeLock.withLock {
        val snapshot = file
        val previous = written
        if (snapshot === previous) return@withLock
        val ok = withContext(Dispatchers.IO) {
            runCatching { Storage.write(encode(snapshot)) }.isSuccess
        }
        if (ok) {
            written = snapshot
            saveFailed = false
            // Cover, Pro and the lock all travel through here, so one call covers every reason the
            // widgets would be out of date.
            withContext(Dispatchers.IO) { syncWidgets(snapshot.entries, snapshot.settings, today()) }
            // The window is rebuilt here and not on every keystroke: a saved diary is the only one
            // the reminder has to agree with.
            Reminder.sync(askPermission = false)
            val gone = photosOf(previous) - photosOf(snapshot)
            if (gone.isNotEmpty()) withContext(Dispatchers.IO) { gone.forEach(Storage::deletePhoto) }
        } else {
            saveFailed = true
        }
    }

    /** Photos nobody references any more, and whatever an import left half done. */
    private fun sweep(f: JournalFile) {
        val referenced = photosOf(f)
        Storage.listPhotos().filter { it !in referenced }.forEach(Storage::deletePhoto)
        Storage.importDir()
    }

    private fun photosOf(f: JournalFile?): Set<String> =
        f?.entries?.values?.mapNotNull { it.photo }?.toSet().orEmpty()

    private fun decode(text: String?): JournalFile? =
        text?.let { runCatching { JournalJson.decodeFromString(JournalFile.serializer(), it) }.getOrNull() }

    /** Sorted by date, so the file reads in order and backups diff sensibly. */
    internal fun encode(f: JournalFile): String =
        JournalJson.encodeToString(JournalFile.serializer(), f.copy(entries = f.entries.toSortedByKey()))

    private fun Map<String, LineEntry>.toSortedByKey() = entries.sortedBy { it.key }.associate { it.toPair() }
}

/**
 * The diary after typing [text] on [date], or null when nothing changes. Creating marks the entry
 * late when its day is over, editing keeps whatever it was, an entry left with no text and no
 * photo leaves the diary, and nothing is written after today.
 */
internal fun Journal.withText(date: LocalDate, text: String, today: LocalDate): Journal? {
    if (date > today) return null
    val key = date.isoKey()
    val old = this[key]
    if (old == null && text.isBlank()) return null
    val entry = old?.copy(text = text) ?: LineEntry(text = text, late = date < today)
    return if (entry.text.isBlank() && entry.photo == null) this - key else this + (key to entry)
}
