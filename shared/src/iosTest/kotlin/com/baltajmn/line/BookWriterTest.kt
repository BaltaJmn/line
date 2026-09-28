package com.baltajmn.line

import androidx.compose.ui.graphics.Color
import com.baltajmn.line.book.BookWriter
import com.baltajmn.line.book.bookPages
import com.baltajmn.line.data.toNSData
import com.baltajmn.line.model.LineEntry
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFURLRef
import platform.CoreGraphics.CGPDFDocumentCreateWithURL
import platform.CoreGraphics.CGPDFDocumentGetNumberOfPages
import platform.CoreGraphics.CGPDFDocumentRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.writeToFile
import purl.shared.generated.resources.Res
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The one part the layout tests cannot see: that UIKit really writes a PDF with those pages. */
@OptIn(ExperimentalForeignApi::class)
class BookWriterTest {

    private val journal = mapOf(
        "2025-01-17" to LineEntry("First line of the book"),
        "2026-01-17" to LineEntry("And the same day, a year later"),
        "2026-03-01" to LineEntry("Another day"),
    )

    private val literata = runBlocking { Res.readBytes("font/literata_regular.ttf") }

    @Test
    fun writesOnePdfPagePerBookPage() {
        var drawn = 0
        val ok = BookWriter.write(literata, { bookPages(journal, Color.Green, it) }, { false }, { drawn++ })
        assertTrue(ok)
        val copy = NSTemporaryDirectory() + "book-test.pdf"
        val bytes = mutableListOf<ByteArray>()
        BookWriter.deliver { bytes += it }
        val all = bytes.fold(ByteArray(0)) { acc, b -> acc + b }
        assertEquals("%PDF-", all.decodeToString(0, 5))
        BookWriter.discard()

        // Deliver streams exactly what was written: read it back as a PDF and count its pages.
        all.toNSData().writeToFile(copy, true)
        val url = CFBridgingRetain(NSURL.fileURLWithPath(copy)) as CFURLRef?
        val pdf = CGPDFDocumentCreateWithURL(url)
        url?.let { CFRelease(it) }
        assertEquals(drawn.toULong(), CGPDFDocumentGetNumberOfPages(pdf))
        assertEquals(3, drawn)
        CGPDFDocumentRelease(pdf)
    }

    @Test
    fun aCancelledBookLeavesNothing() {
        var drawn = 0
        val ok = BookWriter.write(literata, { bookPages(journal, Color.Green, it) }, { drawn >= 1 }, { drawn++ })
        assertFalse(ok)
        assertEquals(1, drawn)
    }
}
