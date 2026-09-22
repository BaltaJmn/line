package com.baltajmn.line.data

import androidx.compose.ui.graphics.ImageBitmap

/** Largest side of a stored photo. Big enough for the share card, small enough to stay instant. */
const val PHOTO_MAX_PX = 1024

/** Decode a JPEG that came from the picker or from photos/. */
expect fun decodeImage(bytes: ByteArray): ImageBitmap?

/**
 * The system photo picker. It hands back an already downscaled JPEG: a phone photo is 4000 px wide
 * and this app draws it at the width of a column.
 */
expect object PhotoPicker {
    /** False until the platform has wired one up, so the button hides instead of lying. */
    val available: Boolean

    fun pick(onResult: (ByteArray?) -> Unit)
}

/** Decoded photos, kept in memory so a day that is drawn twice is not read from disk twice. */
object Photos {
    private val cache = mutableMapOf<String, ImageBitmap?>()

    fun get(name: String?): ImageBitmap? {
        if (name == null) return null
        // containsKey, not getOrPut: a photo whose file went missing has to be remembered as
        // missing, or every frame goes back to the disk looking for it.
        if (cache.containsKey(name)) return cache[name]
        val decoded = Storage.readPhoto(name)?.let(::decodeImage)
        cache[name] = decoded
        return decoded
    }

    fun forget(name: String) {
        cache.remove(name)
    }
}
