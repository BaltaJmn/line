package com.baltajmn.line.book

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.toArgb
import com.baltajmn.line.data.AndroidContext
import com.baltajmn.line.data.Storage
import java.io.File

// Twice the size the photo is printed at: sharp on a screen and in print, and a book full of
// photos stays megabytes, not the originals embedded a hundred times over.
private const val THUMB_W = (PHOTO_W * 2).toInt()
private const val THUMB_H = (PHOTO_H * 2).toInt()

actual object BookWriter {

    private val file: File get() = File(AndroidContext.value.cacheDir, "book.pdf")

    actual fun write(
        literata: ByteArray,
        pages: (BookMeasure) -> Sequence<BookPage>,
        cancelled: () -> Boolean,
        onPage: (BookPage) -> Unit,
    ): Boolean {
        var ok = false
        val document = PdfDocument()
        try {
            val paints = paints(literata)
            val measure = BookMeasure { text, style -> paints.getValue(style).measureText(text) }
            var number = 0
            for (page in pages(measure)) {
                if (cancelled()) return false
                number++
                // PdfDocument pages are measured in points, the unit the layout is written in.
                val pdfPage = document.startPage(PdfDocument.PageInfo.Builder(PAGE_W.toInt(), PAGE_H.toInt(), number).create())
                draw(pdfPage.canvas, page.ops, paints)
                document.finishPage(pdfPage)
                onPage(page)
            }
            file.outputStream().use { document.writeTo(it) }
            ok = true
        } catch (e: Exception) {
            // A book that could not be made says so; nothing half written is kept.
        } finally {
            document.close()
            if (!ok) file.delete()
        }
        return ok
    }

    actual fun deliver(sink: (ByteArray) -> Unit) {
        file.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val n = input.read(buffer)
                if (n <= 0) break
                sink(buffer.copyOf(n))
            }
        }
    }

    actual fun discard() {
        file.delete()
    }

    private fun paints(literata: ByteArray): Map<BookStyle, Paint> {
        // Typeface only loads a font from a file or the assets, never from bytes.
        val fontFile = File(AndroidContext.value.cacheDir, "literata.ttf").apply { writeBytes(literata) }
        val serif = Typeface.createFromFile(fontFile)
        val medium = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        return BookStyle.entries.associateWith { style ->
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = style.size
                typeface = when {
                    style.serif -> serif
                    style.medium -> medium
                    else -> Typeface.DEFAULT
                }
            }
        }
    }

    private fun draw(canvas: Canvas, ops: List<BookOp>, paints: Map<BookStyle, Paint>) {
        for (op in ops) {
            when (op) {
                is BookOp.Fill -> canvas.drawColor(op.color.toArgb())
                is BookOp.Text -> {
                    val paint = paints.getValue(op.style).apply { color = op.color.toArgb() }
                    canvas.drawText(op.text, op.x, op.baseline, paint)
                }
                is BookOp.Rule -> canvas.drawRect(
                    op.x, op.y, op.x + op.width, op.y + 0.5f,
                    Paint().apply { color = op.color.toArgb() },
                )
                is BookOp.Photo -> drawPhoto(canvas, op)
            }
        }
    }

    private fun drawPhoto(canvas: Canvas, op: BookOp.Photo) {
        // A photo that cannot be read leaves its text to the page: the words are the book.
        val thumb = Storage.readPhoto(op.name)?.let(::thumbnail) ?: return
        val box = RectF(op.x, op.y, op.x + PHOTO_W, op.y + PHOTO_H)
        canvas.save()
        canvas.clipPath(Path().apply { addRoundRect(box, PHOTO_RADIUS, PHOTO_RADIUS, Path.Direction.CW) })
        canvas.drawBitmap(thumb, null, box, Paint(Paint.FILTER_BITMAP_FLAG))
        canvas.restore()
        thumb.recycle()
    }

    /** The middle 4:3 of the photo at [THUMB_W] x [THUMB_H], decoded no larger than needed. */
    private fun thumbnail(bytes: ByteArray): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= THUMB_W && bounds.outHeight / (sample * 2) >= THUMB_H) sample *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options) ?: return null
        val thumb = Bitmap.createBitmap(THUMB_W, THUMB_H, Bitmap.Config.ARGB_8888)
        Canvas(thumb).drawBitmap(
            decoded,
            centreCrop(decoded.width, decoded.height),
            Rect(0, 0, THUMB_W, THUMB_H),
            Paint(Paint.FILTER_BITMAP_FLAG),
        )
        decoded.recycle()
        return thumb
    }

    private fun centreCrop(width: Int, height: Int): Rect {
        val ratio = PHOTO_W / PHOTO_H
        return if (width.toFloat() / height > ratio) {
            val w = (height * ratio).toInt()
            val left = (width - w) / 2
            Rect(left, 0, left + w, height)
        } else {
            val h = (width / ratio).toInt()
            val top = (height - h) / 2
            Rect(0, top, width, top + h)
        }
    }
}
