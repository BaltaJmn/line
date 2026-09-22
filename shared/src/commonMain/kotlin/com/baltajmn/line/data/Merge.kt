package com.baltajmn.line.data

import com.baltajmn.line.model.Journal
import kotlin.random.Random

data class MergeResult(
    val journal: Journal,
    /** Dates only the backup had. */
    val added: Int,
    /** Dates on both sides that change. */
    val joined: Int,
    /** Dates on both sides that do not change. */
    val same: Int,
    /** Photo names of the backup that have to be brought over. */
    val photosFromIncoming: Set<String>,
)

/**
 * Importing joins, it never replaces: no text that is on this phone is ever lost, so a backup can
 * be imported by mistake and the worst that happens is a day with two lines in it.
 *
 * Pure. The repository is the one that copies the photos and saves.
 */
fun merge(device: Journal, incoming: Journal): MergeResult {
    val out = device.toMutableMap()
    val photos = mutableSetOf<String>()
    var added = 0
    var joined = 0
    var same = 0

    for ((key, from) in incoming) {
        val mine = device[key]
        if (mine == null) {
            out[key] = from
            from.photo?.let(photos::add)
            added++
            continue
        }

        val a = mine.text.trim()
        val b = from.text.trim()
        val text = when {
            a == b || b.isEmpty() -> mine.text
            a.isEmpty() -> from.text
            a.contains(b) -> mine.text
            b.contains(a) -> from.text
            else -> mine.text + "\n" + from.text
        }
        val photo = mine.photo ?: from.photo
        if (mine.photo == null) from.photo?.let(photos::add)

        // late stays as this phone recorded it: the backup does not get to say when you wrote.
        val merged = mine.copy(text = text, photo = photo)
        out[key] = merged
        if (merged == mine) same++ else joined++
    }

    return MergeResult(out, added, joined, same, photos)
}

/** A photo name that no entry and no file uses yet: an imported name may already be taken. */
fun freePhotoName(taken: Set<String>): String {
    while (true) {
        val name = "p-" + Random.nextInt().toUInt().toString(16).padStart(8, '0') + ".jpg"
        if (name !in taken) return name
    }
}

/** True for a name a LineEntry could carry. Used to keep a foreign file from writing anywhere. */
fun isPhotoName(name: String): Boolean =
    name.startsWith("photos/") && !name.substringAfter("photos/").contains('/')
