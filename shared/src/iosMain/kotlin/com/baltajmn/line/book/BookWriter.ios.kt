package com.baltajmn.line.book

import androidx.compose.ui.graphics.Color
import com.baltajmn.line.data.Storage
import com.baltajmn.line.data.readChunk
import com.baltajmn.line.data.toNSData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.useContents
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFURLRef
import platform.CoreGraphics.CGContextRestoreGState
import platform.CoreGraphics.CGContextSaveGState
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.CoreText.CTFontManagerRegisterFontsForURL
import platform.CoreText.kCTFontManagerScopeProcess
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSError
import platform.Foundation.NSFileHandle
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.closeFile
import platform.Foundation.fileHandleForReadingAtPath
import platform.Foundation.writeToFile
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIFontWeightMedium
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIGraphicsImageRendererFormat
import platform.UIKit.UIGraphicsPDFRenderer
import platform.UIKit.UIGraphicsPDFRendererFormat
import platform.UIKit.UIImage
import platform.UIKit.UIRectFill
import platform.UIKit.drawAtPoint
import platform.UIKit.sizeWithAttributes

private const val LITERATA = "Literata-Regular"

@OptIn(ExperimentalForeignApi::class)
actual object BookWriter {

    private val path: String get() = NSTemporaryDirectory() + "book.pdf"

    private var registered = false

    actual fun write(
        literata: ByteArray,
        pages: (BookMeasure) -> Sequence<BookPage>,
        cancelled: () -> Boolean,
        onPage: (BookPage) -> Unit,
    ): Boolean {
        if (!register(literata)) return false
        val fonts = BookStyle.entries.associateWith { style ->
            val size = style.size.toDouble()
            when {
                style.serif -> UIFont.fontWithName(LITERATA, size) ?: return false
                style.medium -> UIFont.systemFontOfSize(size, UIFontWeightMedium)
                else -> UIFont.systemFontOfSize(size)
            }
        }
        val measure = BookMeasure { text, style ->
            (text as NSString).sizeWithAttributes(mapOf<Any?, Any?>(NSFontAttributeName to fonts.getValue(style)))
                .useContents { width.toFloat() }
        }
        val renderer = UIGraphicsPDFRenderer(
            bounds = CGRectMake(0.0, 0.0, PAGE_W.toDouble(), PAGE_H.toDouble()),
            format = UIGraphicsPDFRendererFormat.defaultFormat(),
        )
        var complete = false
        val written = memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            renderer.writePDFToURL(
                NSURL.fileURLWithPath(path),
                withActions = { context ->
                    // A Kotlin exception must not cross back into UIKit's block: it would kill the
                    // app instead of saying the book could not be made.
                    complete = runCatching {
                        for (page in pages(measure)) {
                            if (cancelled()) return@runCatching false
                            // One pool per page: the photos of a page go before the next is drawn.
                            autoreleasepool {
                                context?.beginPage()
                                draw(page.ops, fonts)
                            }
                            onPage(page)
                        }
                        true
                    }.getOrDefault(false)
                },
                error = error.ptr,
            )
        }
        if (!written || !complete) {
            discard()
            return false
        }
        return true
    }

    actual fun deliver(sink: (ByteArray) -> Unit) {
        val handle = NSFileHandle.fileHandleForReadingAtPath(path) ?: error("no book")
        try {
            while (true) sink(handle.readChunk(64 * 1024)?.takeIf { it.isNotEmpty() } ?: break)
        } finally {
            handle.closeFile()
        }
    }

    actual fun discard() {
        NSFileManager.defaultManager.removeItemAtPath(path, null)
    }

    /** CoreText only knows a font by name once it is registered, and only from a file. Once per process. */
    private fun register(literata: ByteArray): Boolean {
        if (registered) return true
        val file = NSTemporaryDirectory() + "literata.ttf"
        literata.toNSData().writeToFile(file, true)
        val url = CFBridgingRetain(NSURL.fileURLWithPath(file)) as CFURLRef?
        CTFontManagerRegisterFontsForURL(url, kCTFontManagerScopeProcess, null)
        url?.let { CFRelease(it) }
        // Registering twice in one process fails but leaves the font there: ask for it by name.
        registered = UIFont.fontWithName(LITERATA, 12.0) != null
        return registered
    }

    private fun draw(ops: List<BookOp>, fonts: Map<BookStyle, UIFont>) {
        for (op in ops) {
            when (op) {
                is BookOp.Fill -> {
                    op.color.toUIColor().setFill()
                    UIRectFill(CGRectMake(0.0, 0.0, PAGE_W.toDouble(), PAGE_H.toDouble()))
                }
                is BookOp.Text -> {
                    val font = fonts.getValue(op.style)
                    // UIKit places text by the top of its line; the layout gives the baseline.
                    (op.text as NSString).drawAtPoint(
                        CGPointMake(op.x.toDouble(), op.baseline - font.ascender),
                        withAttributes = mapOf<Any?, Any?>(
                            NSFontAttributeName to font,
                            NSForegroundColorAttributeName to op.color.toUIColor(),
                        ),
                    )
                }
                is BookOp.Rule -> {
                    op.color.toUIColor().setFill()
                    UIRectFill(CGRectMake(op.x.toDouble(), op.y.toDouble(), op.width.toDouble(), 0.5))
                }
                is BookOp.Photo -> drawPhoto(op)
            }
        }
    }

    private fun drawPhoto(op: BookOp.Photo) {
        // A photo that cannot be read leaves its text to the page: the words are the book.
        val image = Storage.readPhoto(op.name)?.let { UIImage.imageWithData(it.toNSData()) } ?: return
        val thumb = thumbnail(image) ?: return
        val box = CGRectMake(op.x.toDouble(), op.y.toDouble(), PHOTO_W.toDouble(), PHOTO_H.toDouble())
        val context = UIGraphicsGetCurrentContext()
        CGContextSaveGState(context)
        UIBezierPath.bezierPathWithRoundedRect(box, PHOTO_RADIUS.toDouble()).addClip()
        thumb.drawInRect(box)
        CGContextRestoreGState(context)
    }

    /**
     * The middle 4:3 of the photo, redrawn at twice the printed size: sharp on a screen and in print,
     * and the PDF embeds this and not the original a hundred times over.
     */
    private fun thumbnail(image: UIImage): UIImage? {
        val width = image.size.useContents { width }
        val height = image.size.useContents { height }
        if (width <= 0.0 || height <= 0.0) return null
        val boxW = PHOTO_W.toDouble()
        val boxH = PHOTO_H.toDouble()
        val scale = maxOf(boxW / width, boxH / height)
        val drawnW = width * scale
        val drawnH = height * scale
        val format = UIGraphicsImageRendererFormat.defaultFormat().apply { setScale(2.0) }
        return UIGraphicsImageRenderer(size = CGSizeMake(boxW, boxH), format = format).imageWithActions {
            image.drawInRect(CGRectMake((boxW - drawnW) / 2, (boxH - drawnH) / 2, drawnW, drawnH))
        }
    }
}

private fun Color.toUIColor(): UIColor =
    UIColor.colorWithRed(red.toDouble(), green.toDouble(), blue.toDouble(), alpha.toDouble())
