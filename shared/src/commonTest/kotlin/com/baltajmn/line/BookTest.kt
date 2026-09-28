package com.baltajmn.line

import androidx.compose.ui.graphics.Color
import com.baltajmn.line.book.BookDay
import com.baltajmn.line.book.BookMeasure
import com.baltajmn.line.book.BookOp
import com.baltajmn.line.book.BookStyle
import com.baltajmn.line.book.bookDays
import com.baltajmn.line.book.bookPages
import com.baltajmn.line.book.dayPages
import com.baltajmn.line.book.wrap
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.LineEntry
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/** Every character 6 points wide: the lines of a test can be counted by hand. */
private val measure = BookMeasure { text, _ -> text.length * 6f }

class BookTest {

    private var lang = ""

    @BeforeTest
    fun english() {
        lang = S.lang
        S.lang = "en"
    }

    @AfterTest
    fun restore() {
        S.lang = lang
    }

    private fun texts(ops: List<BookOp>, style: BookStyle) =
        ops.filterIsInstance<BookOp.Text>().filter { it.style == style }.map { it.text }

    @Test
    fun daysFollowTheCalendarAndYearsGoOldestFirst() {
        val days = bookDays(
            mapOf(
                "2027-03-01" to LineEntry("c"),
                "2025-03-01" to LineEntry("a"),
                "2026-01-17" to LineEntry("b"),
                "2024-02-29" to LineEntry("leap"),
                "2026-03-01" to LineEntry("b"),
            ),
        )
        assertEquals(listOf(1 to 17, 2 to 29, 3 to 1), days.map { it.month to it.day })
        assertEquals(listOf(2025, 2026, 2027), days.last().years.map { it.first })
    }

    @Test
    fun wrapFillsLinesKeepsBreaksAndCutsLongWords() {
        // 60 points: ten characters a line.
        assertEquals(listOf("one two", "three four"), wrap("one two three four", 60f, BookStyle.Line, measure))
        assertEquals(listOf("one", "", "two"), wrap("one\n\ntwo", 60f, BookStyle.Line, measure))
        assertEquals(listOf("abcdefghij", "klmnop"), wrap("abcdefghijklmnop", 60f, BookStyle.Line, measure))
        assertEquals(emptyList(), wrap("", 60f, BookStyle.Line, measure))
    }

    @Test
    fun aLongWordIsNeverCutInsideASurrogatePair() {
        val smile = "\uD83D\uDE42"
        val lines = wrap("aaaaaaaaa$smile", 60f, BookStyle.Line, measure)
        assertEquals(listOf("aaaaaaaaa", smile), lines)
    }

    @Test
    fun aYearThatDoesNotFitMovesWholeUnderTheDateAgain() {
        // Twelve lines each: two years fit on a page, the third starts the next one.
        val twelve = List(12) { "line $it" }.joinToString("\n")
        val day = BookDay(1, 17, listOf(2025 to LineEntry(twelve), 2026 to LineEntry(twelve), 2027 to LineEntry(twelve)))
        val pages = dayPages(day, measure)
        assertEquals(2, pages.size)
        pages.forEach { assertEquals(listOf("JANUARY 17"), texts(it, BookStyle.Date)) }
        assertEquals(listOf("2025", "2026"), texts(pages[0], BookStyle.Year))
        assertEquals(listOf("2027"), texts(pages[1], BookStyle.Year))
        assertEquals(12, texts(pages[1], BookStyle.Line).size)
    }

    @Test
    fun aYearLongerThanAPageIsSplitAndKeepsItsYearOnEachPart() {
        val forty = List(40) { "line $it" }.joinToString("\n")
        val pages = dayPages(BookDay(1, 17, listOf(2026 to LineEntry(forty))), measure)
        assertEquals(2, pages.size)
        assertEquals(listOf("2026"), texts(pages[0], BookStyle.Year))
        assertEquals(listOf("2026"), texts(pages[1], BookStyle.Year))
        assertEquals(40, pages.sumOf { texts(it, BookStyle.Line).size })
        val baselines = pages[0].filterIsInstance<BookOp.Text>().filter { it.style == BookStyle.Line }.map { it.baseline }
        assertTrue(baselines.last() <= 595f - 42f, "the last line stays above the bottom margin")
    }

    @Test
    fun aPhotoNarrowsTheTextAndAPhotoAloneStillHasItsBlock() {
        val long = "word ".repeat(20).trim()
        val withPhoto = dayPages(BookDay(1, 17, listOf(2026 to LineEntry(long, photo = "p.jpg"))), measure).single()
        val withoutPhoto = dayPages(BookDay(1, 17, listOf(2026 to LineEntry(long))), measure).single()
        assertTrue(texts(withPhoto, BookStyle.Line).size > texts(withoutPhoto, BookStyle.Line).size)
        assertEquals(1, withPhoto.filterIsInstance<BookOp.Photo>().size)

        val alone = dayPages(BookDay(1, 17, listOf(2026 to LineEntry("", photo = "p.jpg"))), measure).single()
        assertEquals(listOf("2026"), texts(alone, BookStyle.Year))
        assertEquals(emptyList(), texts(alone, BookStyle.Line))
        assertEquals(1, alone.filterIsInstance<BookOp.Photo>().size)
    }

    @Test
    fun theCoverComesFirstAndPagesAreNumberedAsTheViewerCountsThem() {
        val journal = mapOf("2025-01-17" to LineEntry("a"), "2026-03-01" to LineEntry("b"))
        val pages = bookPages(journal, Color.Green, measure).toList()
        assertIs<BookOp.Fill>(pages[0].ops.first())
        assertEquals(listOf("Purl", "2025 to 2026"), texts(pages[0].ops, BookStyle.CoverTitle) + texts(pages[0].ops, BookStyle.CoverYears))
        assertEquals(listOf("2", "3"), pages.drop(1).map { texts(it.ops, BookStyle.Folio).single() })
        assertEquals(listOf(0, 1, 2), pages.map { it.daysDone })
    }

    @Test
    fun aBookOfOneYearSaysOneYear() {
        val pages = bookPages(mapOf("2026-01-17" to LineEntry("a")), Color.Green, measure).toList()
        assertEquals(listOf("2026"), texts(pages[0].ops, BookStyle.CoverYears))
    }
}
