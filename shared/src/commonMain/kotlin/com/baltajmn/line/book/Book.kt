package com.baltajmn.line.book

import androidx.compose.ui.graphics.Color
import com.baltajmn.line.data.EXPORT_PREFIX
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.LineEntry
import com.baltajmn.line.share.Empty
import com.baltajmn.line.share.Ink
import com.baltajmn.line.share.Muted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import purl.shared.generated.resources.Res

/**
 * The book, laid out once for both platforms (docs/pantallas.md 15.1). This file decides where every
 * word goes; [BookWriter] only measures text and draws what it is told, so the two PDFs come out the
 * same page for page.
 *
 * All measures are PDF points on an A5 page, with the origin at the top left.
 */
const val PAGE_W = 420f
const val PAGE_H = 595f
const val PHOTO_W = 96f
const val PHOTO_H = 72f
const val PHOTO_RADIUS = 6f
const val PDF_MIME = "application/pdf"

private const val MARGIN = 42f
private const val CONTENT_W = PAGE_W - 2 * MARGIN
private const val DATE_BASELINE = 51f
private const val RULE_Y = 60f
private const val TOP = 74f
private const val BOTTOM = PAGE_H - MARGIN
private const val FOLIO_BASELINE = PAGE_H - 20f
private const val COVER_TITLE_BASELINE = 220f
private const val COVER_YEARS_BASELINE = 250f

// A block: the year, 4 of air, then the lines, 16 apart. Measured from the top of the block.
private const val YEAR_BASELINE = 8f
private const val TEXT_TOP = 15f
private const val FIRST_BASELINE = 12f
private const val LINE_HEIGHT = 16f
private const val BLOCK_GAP = 14f
private const val PHOTO_GAP = 12f

// Any leap year: a page is a day of the calendar, and the 29th of February is one of them.
private const val CALENDAR_YEAR = 2024

enum class BookStyle(val size: Float, val serif: Boolean, val medium: Boolean) {
    CoverTitle(44f, serif = true, medium = false),
    CoverYears(16f, serif = true, medium = false),
    Date(11f, serif = false, medium = true),
    Year(9f, serif = false, medium = true),
    Line(12f, serif = true, medium = false),
    Folio(8f, serif = false, medium = false),
}

/** How wide a text is set in a style, in points. The one question the layout asks the platform. */
fun interface BookMeasure {
    fun width(text: String, style: BookStyle): Float
}

/** What to draw. Text is placed by its baseline, which is where the eye lines type up. */
sealed interface BookOp {
    data class Fill(val color: Color) : BookOp

    data class Text(val text: String, val x: Float, val baseline: Float, val style: BookStyle, val color: Color) : BookOp

    data class Rule(val x: Float, val y: Float, val width: Float, val color: Color) : BookOp

    /** A photo of photos/, cropped to 4:3 into [PHOTO_W] x [PHOTO_H] with [PHOTO_RADIUS] corners. */
    data class Photo(val name: String, val x: Float, val y: Float) : BookOp
}

/** One page, and how many days of the book are done once it is drawn, for the progress dialog. */
class BookPage(val ops: List<BookOp>, val daysDone: Int)

/** A page of the calendar: the same day in every year that wrote it, oldest first. */
class BookDay(val month: Int, val day: Int, val years: List<Pair<Int, LineEntry>>)

fun bookName(today: LocalDate) = "$EXPORT_PREFIX-book-$today.pdf"

fun bookDays(journal: Journal): List<BookDay> =
    journal.entries
        .groupBy({ it.key.substring(5) }, { it.key.take(4).toInt() to it.value })
        .entries
        .sortedBy { it.key }
        .map { (monthDay, years) ->
            BookDay(monthDay.take(2).toInt(), monthDay.substring(3).toInt(), years.sortedBy { it.first })
        }

/**
 * The whole book, one page at a time: nothing past the page being drawn is laid out, so a diary
 * of any length costs the memory of one page.
 */
fun bookPages(journal: Journal, cover: Color, measure: BookMeasure): Sequence<BookPage> = sequence {
    yield(BookPage(coverPage(journal, cover, measure), daysDone = 0))
    // The cover is page 1 without a number, so the number printed is the one the PDF viewer shows.
    var number = 1
    bookDays(journal).forEachIndexed { index, day ->
        for (ops in dayPages(day, measure)) {
            number++
            val folio = centred(number.toString(), BookStyle.Folio, FOLIO_BASELINE, Muted, measure)
            yield(BookPage(ops + folio, daysDone = index + 1))
        }
    }
}

/**
 * Lays out and draws the book on a background thread, into a file that [BookWriter.deliver] then
 * hands on. [onProgress] gets the days done and the days in total. Cancelling the calling coroutine
 * stops it at the next page and leaves nothing behind.
 */
suspend fun makeBook(journal: Journal, cover: Color, onProgress: (Int, Int) -> Unit): Boolean {
    val literata = Res.readBytes("font/literata_regular.ttf")
    val total = bookDays(journal).size
    val job = currentCoroutineContext().job
    // One blocking call from the first page to the last: the iOS PDF context belongs to the thread
    // that opened it, and a suspension in between could resume on another one.
    return withContext(Dispatchers.Default) {
        BookWriter.write(
            literata = literata,
            pages = { measure -> bookPages(journal, cover, measure) },
            cancelled = { !job.isActive },
            onPage = { onProgress(it.daysDone, total) },
        )
    }
}

private fun coverPage(journal: Journal, cover: Color, measure: BookMeasure): List<BookOp> {
    val years = journal.keys.map { it.take(4).toInt() }
    val from = years.min()
    val to = years.max()
    return listOf(
        BookOp.Fill(cover),
        centred("Purl", BookStyle.CoverTitle, COVER_TITLE_BASELINE, Ink, measure),
        // "2026 to 2026" says less than "2026".
        centred(if (from == to) "$from" else S.bookYears(from, to), BookStyle.CoverYears, COVER_YEARS_BASELINE, Ink.copy(alpha = 0.7f), measure),
    )
}

/**
 * The pages of one day. A year that does not fit in what is left of a page moves whole to the next
 * one, under the date again; only a year longer than a full page is split, and it keeps its year on
 * top of every part so the reader never has to look back.
 */
internal fun dayPages(day: BookDay, measure: BookMeasure): List<List<BookOp>> {
    val date = S.shortDate(LocalDate(CALENDAR_YEAR, day.month, day.day)).uppercase()
    val header = listOf(
        BookOp.Text(date, MARGIN, DATE_BASELINE, BookStyle.Date, Muted),
        BookOp.Rule(MARGIN, RULE_Y, CONTENT_W, Empty),
    )
    val pages = mutableListOf(header.toMutableList())
    var y = TOP

    for ((year, entry) in day.years) {
        val photo = entry.photo
        val width = if (photo != null) CONTENT_W - PHOTO_W - PHOTO_GAP else CONTENT_W
        var lines = wrap(entry.text, width, BookStyle.Line, measure)
        if (lines.isEmpty() && photo == null) continue
        var first = true
        while (true) {
            val photoHeight = if (first && photo != null) PHOTO_H else 0f
            val room = BOTTOM - y
            val whole = maxOf(TEXT_TOP + lines.size * LINE_HEIGHT, photoHeight)
            val take = when {
                whole <= room -> lines.size
                y > TOP && whole <= BOTTOM - TOP -> -1
                else -> ((room - TEXT_TOP) / LINE_HEIGHT).toInt().coerceAtMost(lines.size)
            }
            if (take < 0 || (take == 0 && lines.isNotEmpty()) || photoHeight > room) {
                pages += header.toMutableList()
                y = TOP
                continue
            }
            val page = pages.last()
            page += BookOp.Text(year.toString(), MARGIN, y + YEAR_BASELINE, BookStyle.Year, Muted)
            lines.take(take).forEachIndexed { i, line ->
                page += BookOp.Text(line, MARGIN, y + TEXT_TOP + FIRST_BASELINE + i * LINE_HEIGHT, BookStyle.Line, Ink)
            }
            if (photoHeight > 0f) page += BookOp.Photo(photo!!, PAGE_W - MARGIN - PHOTO_W, y)
            y += maxOf(TEXT_TOP + take * LINE_HEIGHT, photoHeight) + BLOCK_GAP
            lines = lines.drop(take)
            first = false
            if (lines.isEmpty()) break
            pages += header.toMutableList()
            y = TOP
        }
    }
    return pages
}

/**
 * Greedy word wrap, the one every reader expects: as many words as fit, then the next line. A line
 * break the user typed is kept, and a word wider than the line is cut between code points.
 */
internal fun wrap(text: String, width: Float, style: BookStyle, measure: BookMeasure): List<String> {
    if (text.isEmpty()) return emptyList()
    val lines = mutableListOf<String>()
    for (paragraph in text.split('\n')) {
        var line = ""
        for (word in paragraph.split(' ')) {
            val joined = if (line.isEmpty()) word else "$line $word"
            if (measure.width(joined, style) <= width) {
                line = joined
                continue
            }
            if (line.isNotEmpty()) lines += line
            var rest = word
            while (measure.width(rest, style) > width) {
                val cut = fittingPrefix(rest, width, style, measure)
                lines += rest.substring(0, cut)
                rest = rest.substring(cut)
            }
            line = rest
        }
        lines += line
    }
    return lines
}

/** The longest start of [word] that fits in [width], never cutting a surrogate pair, never empty. */
private fun fittingPrefix(word: String, width: Float, style: BookStyle, measure: BookMeasure): Int {
    var end = 0
    while (end < word.length) {
        val next = end + if (word[end].isHighSurrogate() && end + 1 < word.length) 2 else 1
        if (end > 0 && measure.width(word.substring(0, next), style) > width) break
        end = next
    }
    return end
}

private fun centred(text: String, style: BookStyle, baseline: Float, color: Color, measure: BookMeasure) =
    BookOp.Text(text, (PAGE_W - measure.width(text, style)) / 2, baseline, style, color)
