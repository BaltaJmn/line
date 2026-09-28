package com.baltajmn.line.data

import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.JournalJson
import com.baltajmn.line.model.LineEntry
import com.baltajmn.line.model.addTag
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

/**
 * The notes of a MoodTraker backup as Purl lines (docs/tecnico.md 4.5, 12.5). Read by hand as a
 * JsonObject: MoodTraker's classes are not this app's to copy, and only three fields matter. The
 * photos are skipped, they belong to a mood and not to a day.
 */
fun readMoodBackup(source: (Int) -> ByteArray?): Journal {
    val text = drain(source).decodeToString()
    val root = runCatching { JournalJson.parseToJsonElement(text).jsonObject }.getOrNull()
        ?: throw ImportFailed(ImportProblem.MoodNotBackup)
    if ((root["app"] as? JsonPrimitive)?.contentOrNull != "mood") throw ImportFailed(ImportProblem.MoodNotBackup)
    val entries = ((root["store"] as? JsonObject)?.get("entries") as? JsonArray)
        ?: throw ImportFailed(ImportProblem.MoodNotBackup)

    val out = mutableMapOf<String, LineEntry>()
    for (item in entries) {
        val o = item as? JsonObject ?: continue
        val date = (o["date"] as? JsonPrimitive)?.contentOrNull?.takeIf { runCatching { LocalDate.parse(it) }.isSuccess }
            ?: continue
        val note = (o["note"] as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() } ?: continue
        val tags = (o["tags"] as? JsonArray).orEmpty()
            .mapNotNull { (it as? JsonPrimitive)?.contentOrNull }
            .fold(emptyList<String>()) { acc, raw -> addTag(acc, raw) }
        out[date] = LineEntry(text = note, tags = tags)
    }
    if (out.isEmpty()) throw ImportFailed(ImportProblem.Empty)
    return out
}

/**
 * Only the dates this phone has no entry for come in. Unlike a Purl backup nothing is joined: a
 * note about a mood is not the line someone chose to keep for that day. [MergeResult.same] carries
 * the dates skipped.
 */
fun mergeMood(device: Journal, notes: Journal): MergeResult {
    val fresh = notes.filterKeys { it !in device }
    return MergeResult(
        journal = device + fresh,
        added = fresh.size,
        joined = 0,
        same = notes.size - fresh.size,
        photosFromIncoming = emptySet(),
    )
}
