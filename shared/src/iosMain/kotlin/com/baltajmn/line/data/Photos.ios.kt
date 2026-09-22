package com.baltajmn.line.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun decodeImage(bytes: ByteArray): ImageBitmap? =
    runCatching { Image.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()

/** PHPicker needs no photo-library permission: the user hands us one image and nothing else. */
actual object PhotoPicker {

    actual val available: Boolean get() = rootController != null

    actual fun pick(onResult: (ByteArray?) -> Unit) {
        val root = rootController ?: return onResult(null)
        val config = PHPickerConfiguration().apply {
            setFilter(PHPickerFilter.imagesFilter())
            setSelectionLimit(1)
        }
        val controller = PHPickerViewController(configuration = config)
        // PHPickerViewController holds its delegate weakly, so it has to be kept alive here.
        val delegate = PickerDelegate(onResult)
        held = delegate
        controller.delegate = delegate
        root.presentViewController(controller, true, null)
    }

    private var held: PickerDelegate? = null

    private val rootController: UIViewController?
        get() = UIApplication.sharedApplication.keyWindow?.rootViewController

    private class PickerDelegate(
        private val onResult: (ByteArray?) -> Unit,
    ) : NSObject(), PHPickerViewControllerDelegateProtocol {

        override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
            picker.dismissViewControllerAnimated(true, null)
            val provider = (didFinishPicking.firstOrNull() as? PHPickerResult)?.itemProvider
            if (provider == null) return finish(null)
            // loadObjectOfClass takes a class object that Kotlin/Native does not bind cleanly,
            // so ask for the raw representation and build the UIImage here instead.
            provider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
                finish(data?.let { UIImage.imageWithData(it) }?.thumbnailJpeg())
            }
        }

        /** The item provider calls back off the main thread; Compose state must not be touched there. */
        private fun finish(bytes: ByteArray?) {
            dispatch_async(dispatch_get_main_queue()) {
                held = null
                onResult(bytes)
            }
        }
    }
}

const val PHOTO_JPEG_QUALITY = 0.8

/**
 * Redraws the picked image at roughly [PHOTO_MAX_PX] on the long side. Keeping the 12 MP original
 * would waste the storage it shares with the diary, to draw it a column wide.
 */
@OptIn(ExperimentalForeignApi::class)
private fun UIImage.thumbnailJpeg(): ByteArray? {
    val width = size.useContents { this.width }
    val height = size.useContents { this.height }
    if (width <= 0.0 || height <= 0.0) return null

    val scale = (PHOTO_MAX_PX.toDouble() / maxOf(width, height)).coerceAtMost(1.0)
    val targetWidth = width * scale
    val targetHeight = height * scale

    val renderer = UIGraphicsImageRenderer(size = CGSizeMake(targetWidth, targetHeight))
    val scaled = renderer.imageWithActions {
        this@thumbnailJpeg.drawInRect(CGRectMake(0.0, 0.0, targetWidth, targetHeight))
    }
    return UIImageJPEGRepresentation(scaled, PHOTO_JPEG_QUALITY)?.toByteArray()
}
