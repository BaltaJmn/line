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

const val SAVE_DEBOUNCE_MS = 800L

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

    // --- entries ----------------------------------------------------------------------------

    fun entryOn(date: LocalDate): LineEntry? = journal[date.isoKey()]

    fun setText(date: LocalDate, text: String, today: LocalDate) {
        val next = journal.withText(date, text, today) ?: return
        edit { it.copy(entries = next) }
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
