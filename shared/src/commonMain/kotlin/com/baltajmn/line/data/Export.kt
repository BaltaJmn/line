package com.baltajmn.line.data

import com.baltajmn.line.model.BACKUP_VERSION
import com.baltajmn.line.model.ExportFile
import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.JournalFile
import com.baltajmn.line.model.JournalJson
import com.baltajmn.line.model.LineEntry
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject

const val EXPORT_PREFIX = "purl"

const val ENTRIES_NAME = "entries.json"
const val JOURNAL_NAME = "journal.md"
const val PHOTOS_DIR = "photos/"

fun exportName(today: LocalDate) = "$EXPORT_PREFIX-$today.zip"

/**
 * Asks the system where to put the backup and writes it there. The date of the last backup only
 * moves when the system says the file is written: a cancelled picker has saved nothing.
 */
fun startExport(today: LocalDate, onResult: (PickResult) -> Unit) {
    LineRepository.saveNow()
    val snapshot = LineRepository.file
    FilePicker.exportZip(exportName(today), { sink -> exportZip(snapshot, sink) }) { result ->
        if (result == PickResult.Done) {
            LineRepository.updateSettings { it.copy(lastBackup = today.toString(), backupNoticeDone = true) }
        }
        onResult(result)
    }
}

/**
 * The diary as one readable file, undated by any app: whoever opens the backup in fifty years gets
 * the lines in a text editor, not a format to reverse engineer. Never translated, for the same reason.
 */
fun journalMarkdown(journal: Journal): String = buildString {
    append("# Purl\n")
    journal.keys.sorted().forEach { key ->
        val entry = journal.getValue(key)
        append("\n## ").append(key).append("\n")
        if (entry.text.isNotEmpty()) append(entry.text).append("\n")
        entry.photo?.let { append("\n![](").append(PHOTOS_DIR).append(it).append(")\n") }
    }
}

/**
 * Writes the backup into [sink] entry by entry. Each photo is read whole to get its CRC before its
 * header, so at most one photo is in memory at a time however long the diary is (tecnico 6.8).
 */
fun exportZip(file: JournalFile, sink: (ByteArray) -> Unit) {
    val zip = ZipWriter(sink)
    // Only the diary travels: the settings of this phone are not part of anyone's years.
    // Sorted by date so the file reads in order and two backups diff against each other.
    val sorted = file.entries.keys.sorted().associateWith { file.entries.getValue(it) }
    zip.add(ENTRIES_NAME, JournalJson.encodeToString(ExportFile(entries = sorted)).encodeToByteArray())
    zip.add(JOURNAL_NAME, journalMarkdown(file.entries).encodeToByteArray())
    file.entries.keys.sorted()
        .mapNotNull { file.entries.getValue(it).photo }
        .distinct()
        .forEach { name -> Storage.readPhoto(name)?.let { zip.add(PHOTOS_DIR + name, it) } }
    zip.finish()
}

/** Why a file was refused. The key is the text the user reads (tecnico 4.6). */
enum class ImportProblem(val key: String) {
    NotBackup("importNotBackup"),
    Damaged("importDamaged"),
    TooNew("importTooNew"),
    Empty("importEmpty"),
    IsMoodTraker("importIsMoodTraker"),
}

class ImportFailed(val problem: ImportProblem) : Exception(problem.key)

/** What a backup brought: the diary, and the photos already parked in import/. */
data class Backup(val journal: Journal, val photos: Set<String>)

/**
 * Reads a backup, zip or bare entries.json, leaving the diary untouched if anything is off.
 *
 * ignoreUnknownKeys means any JSON at all would decode into an empty diary, and an empty diary
 * imported over a real one reads as "nothing to merge" (the lesson from Quilt). So the shape is
 * checked by hand before anything is decoded.
 */
fun readBackup(source: (Int) -> ByteArray?): Backup {
    val head = readAtLeast(source, 4) ?: throw ImportFailed(ImportProblem.NotBackup)
    val isZip = head.size >= 4 && head[0] == 0x50.toByte() && head[1] == 0x4B.toByte() &&
        head[2] == 0x03.toByte() && head[3] == 0x04.toByte()

    var json: String? = null
    val photos = mutableSetOf<String>()
    if (isZip) {
        // Emptied before parking anything, and again on any refusal or cancel (tecnico 6.7).
        Storage.importDir()
        val rest = replay(head, source)
        try {
            ZipReader(rest).forEach { name, bytes ->
                when {
                    name == ENTRIES_NAME -> json = bytes.decodeToString()
                    isPhotoName(name) -> {
                        // "photos/.." would land on the folder above import/, so the inner name is
                        // checked too and not only the shape of the entry.
                        val photo = name.substringAfter(PHOTOS_DIR)
                        if (!isSafePhotoName(photo)) throw ImportFailed(ImportProblem.Damaged)
                        Storage.writeImport(photo, bytes)
                        photos += photo
                    }
                }
            }
        } catch (e: ZipDamaged) {
            throw ImportFailed(ImportProblem.Damaged)
        }
        if (json == null) throw ImportFailed(ImportProblem.NotBackup)
    } else {
        if (head[0] != '{'.code.toByte()) throw ImportFailed(ImportProblem.NotBackup)
        json = drain(replay(head, source)).decodeToString()
    }

    return Backup(journalOf(json!!), photos)
}

/** Clears whatever a refused or cancelled import parked in import/ (tecnico 6.7, steps 3 and 5). */
fun abandonImport() {
    Storage.importDir()
}

/**
 * A photo name is a plain file name inside photos/ and nothing else. A hand written backup saying
 * "../entries.json" would otherwise walk out of the folder and take the diary with it.
 */
fun isSafePhotoName(name: String): Boolean =
    name.isNotEmpty() && '/' !in name && '\\' !in name && name != "." && name != ".."

/** The shape check of tecnico 4.6, before a single field is trusted. */
private fun journalOf(text: String): Journal {
    val root = runCatching { JournalJson.parseToJsonElement(text).jsonObject }.getOrNull()
        ?: throw ImportFailed(ImportProblem.NotBackup)
    // as? and not jsonPrimitive: an "app" or a "version" that is an object throws, and a file that
    // is merely not ours has to read as not ours, never as a crash.
    if ((root["app"] as? JsonPrimitive)?.contentOrNull == "mood") throw ImportFailed(ImportProblem.IsMoodTraker)

    val version = (root["version"] as? JsonPrimitive)?.intOrNull ?: throw ImportFailed(ImportProblem.NotBackup)
    if (version > BACKUP_VERSION) throw ImportFailed(ImportProblem.TooNew)
    val entries = (root["entries"] as? JsonObject) ?: throw ImportFailed(ImportProblem.NotBackup)
    if (entries.isEmpty()) throw ImportFailed(ImportProblem.Empty)
    entries.keys.forEach { key ->
        if (runCatching { LocalDate.parse(key) }.isFailure) throw ImportFailed(ImportProblem.Damaged)
    }

    val file = runCatching { JournalJson.decodeFromString<ExportFile>(text) }.getOrNull()
        ?: throw ImportFailed(ImportProblem.Damaged)
    if (file.entries.values.any { e -> e.photo?.let { !isSafePhotoName(it) } == true }) {
        throw ImportFailed(ImportProblem.Damaged)
    }
    // Pro is never restored from a backup: what was paid for is asked of the store, not of a file.
    return file.entries.mapValues { (_, e) -> LineEntry(e.text, e.photo, e.late) }
}

// --- reading the picked file ---------------------------------------------------------------------

private fun readAtLeast(source: (Int) -> ByteArray?, n: Int): ByteArray? {
    var out = ByteArray(0)
    while (out.size < n) {
        val chunk = source(n - out.size) ?: return out.takeIf { it.isNotEmpty() }
        if (chunk.isEmpty()) return out.takeIf { it.isNotEmpty() }
        out += chunk
    }
    return out
}

/** Hands back what was already read before the rest of the file. */
private fun replay(head: ByteArray, source: (Int) -> ByteArray?): (Int) -> ByteArray? {
    var at = 0
    return { n ->
        if (at < head.size) {
            val take = minOf(n, head.size - at)
            head.copyOfRange(at, at + take).also { at += take }
        } else {
            source(n)
        }
    }
}

private fun drain(source: (Int) -> ByteArray?): ByteArray {
    var out = ByteArray(0)
    while (true) {
        val chunk = source(64 * 1024) ?: return out
        if (chunk.isEmpty()) return out
        out += chunk
    }
}
