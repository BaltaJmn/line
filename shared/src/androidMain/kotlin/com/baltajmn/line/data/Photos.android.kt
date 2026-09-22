package com.baltajmn.line.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val PHOTO_JPEG_QUALITY = 80

actual fun decodeImage(bytes: ByteArray): ImageBitmap? =
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()

actual object PhotoPicker {

    /** Set by MainActivity: the system picker needs an Activity to launch from. */
    var pickImage: (() -> Unit)? = null

    /**
     * The answer comes back to the Activity, which may be a new instance by then, so what is
     * waiting for it lives here, like in FilePicker.
     */
    private var waiting: ((ByteArray?) -> Unit)? = null

    private val scope = MainScope()

    actual val available: Boolean get() = pickImage != null

    /** Called by MainActivity when the system answers. */
    fun onPicked(uri: Uri?) {
        val answer = waiting ?: return
        waiting = null
        if (uri == null) return answer(null)
        scope.launch {
            // A 12 MP photo is decoded and scaled off the thread that draws.
            val bytes = withContext(Dispatchers.IO) {
                runCatching {
                    uri.toThumbnailJpeg { AndroidContext.value.contentResolver.openInputStream(it) }
                }.getOrNull()
            }
            answer(bytes)
        }
    }

    actual fun pick(onResult: (ByteArray?) -> Unit) {
        val launch = pickImage ?: return onResult(null)
        // One pick at a time: a second one would leave the first picker with nobody listening.
        if (waiting != null) return onResult(null)
        waiting = onResult
        launch()
    }
}

/**
 * Reads the picked image at roughly [PHOTO_MAX_PX] on the long side and re-encodes it as JPEG.
 * Decoding the whole 12 MP original to draw it a column wide would stall and waste the storage the
 * diary shares with it.
 */
fun Uri.toThumbnailJpeg(open: (Uri) -> InputStream?): ByteArray? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    open(this)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    val longSide = max(bounds.outWidth, bounds.outHeight)
    if (longSide <= 0) return null

    var sample = 1
    while (longSide / sample > PHOTO_MAX_PX * 2) sample *= 2

    val decoded = open(this)?.use {
        BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
    } ?: return null

    val scale = PHOTO_MAX_PX.toFloat() / max(decoded.width, decoded.height)
    val bitmap = if (scale < 1f) {
        Bitmap.createScaledBitmap(
            decoded, (decoded.width * scale).toInt(), (decoded.height * scale).toInt(), true,
        )
    } else {
        decoded
    }

    return ByteArrayOutputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, PHOTO_JPEG_QUALITY, out)
        out.toByteArray()
    }
}
