package com.baltajmn.line

import com.baltajmn.line.data.search
import com.baltajmn.line.data.withText
import com.baltajmn.line.model.LineEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate

class DataTest {
    private val today = LocalDate.parse("2027-01-17")

    // docs/tecnico.md 6.13
    @Test
    fun creatingMarksLateOnlyAfterTheDay() {
        assertEquals(LineEntry("hoy"), emptyMap<String, LineEntry>().withText(today, "hoy", today)?.get("2027-01-17"))
        assertEquals(
            LineEntry("ayer", late = true),
            emptyMap<String, LineEntry>().withText(LocalDate.parse("2027-01-16"), "ayer", today)?.get("2027-01-16"),
        )
    }

    @Test
    fun editingKeepsLate() {
        val j = mapOf("2027-01-10" to LineEntry("old", late = true))
        assertEquals(LineEntry("new", late = true), j.withText(LocalDate.parse("2027-01-10"), "new", today)?.get("2027-01-10"))
    }

    @Test
    fun aBlankEntryLeavesTheDiaryUnlessItHasAPhoto() {
        val j = mapOf("2027-01-17" to LineEntry("x"), "2027-01-16" to LineEntry("y", photo = "p-00000001.jpg"))
        assertTrue("2027-01-17" !in j.withText(today, "  ", today)!!)
        assertEquals(
            LineEntry("", photo = "p-00000001.jpg"),
            j.withText(LocalDate.parse("2027-01-16"), "", today)!!["2027-01-16"],
        )
    }

    @Test
    fun nothingChangesForBlankNewDaysOrTheFuture() {
        assertNull(emptyMap<String, LineEntry>().withText(today, " ", today))
        assertNull(emptyMap<String, LineEntry>().withText(LocalDate.parse("2027-01-18"), "later", today))
    }

    // 13, the search half. docs/tecnico.md 6.5
    @Test
    fun searchIgnoresCaseAndAccentsAndAnswersFromEveryYear() {
        val j = mapOf(
            "2026-03-04" to LineEntry("Cafe\u0301 con Ana"),
            "2027-01-17" to LineEntry("Un ano entero"),
            "2028-05-02" to LineEntry("CAFE solo"),
        )
        assertEquals(
            listOf(LocalDate.parse("2028-05-02"), LocalDate.parse("2026-03-04")),
            search(j, " café ").map { it.first },
        )
        assertEquals(listOf(LocalDate.parse("2027-01-17")), search(j, "año").map { it.first })
        assertTrue(search(j, "   ").isEmpty())
    }
}
