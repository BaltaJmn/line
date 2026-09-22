package com.baltajmn.line

import com.baltajmn.line.data.REMINDER_WINDOW
import com.baltajmn.line.data.nextFire
import com.baltajmn.line.data.reminderPlan
import com.baltajmn.line.data.search
import com.baltajmn.line.data.withText
import com.baltajmn.line.model.LineEntry
import com.baltajmn.line.model.Settings
import com.baltajmn.line.model.logicalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

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

    // 14. The next Android firing. docs/tecnico.md 6.10
    @Test
    fun theAlarmSkipsADayThatIsAlreadyWritten() {
        val written = setOf("2027-01-17")
        fun fire(at: String) = nextFire(LocalDateTime.parse(at), 21, 0) { it.toString() in written }
        assertEquals(LocalDateTime.parse("2027-01-16T21:00"), fire("2027-01-16T20:59"))
        assertEquals(LocalDateTime.parse("2027-01-18T21:00"), fire("2027-01-17T20:59"))
        assertEquals(LocalDateTime.parse("2027-01-18T21:00"), fire("2027-01-16T21:00"))
    }

    // 7. The iOS window. docs/tecnico.md 6.10
    @Test
    fun theWindowStartsTodayOnlyWhileTodayIsBlank() {
        val on = Settings(reminderOn = true)
        val blank = reminderPlan(emptyMap(), on, LocalDateTime.parse("2027-01-17T10:00"))
        assertEquals(REMINDER_WINDOW, blank.size)
        assertEquals(LocalDateTime.parse("2027-01-17T21:00"), blank.first().at)

        val done = mapOf("2027-01-17" to LineEntry("hoy"))
        assertEquals(
            LocalDateTime.parse("2027-01-18T21:00"),
            reminderPlan(done, on, LocalDateTime.parse("2027-01-17T10:00")).first().at,
        )
        assertEquals(
            LocalDateTime.parse("2027-01-18T21:00"),
            reminderPlan(emptyMap(), on, LocalDateTime.parse("2027-01-17T22:00")).first().at,
        )
        assertTrue(reminderPlan(emptyMap(), Settings(), LocalDateTime.parse("2027-01-17T10:00")).isEmpty())
    }

    @Test
    fun aReminderQuotesLastYearOnlyWithTheLockOff() {
        val j = mapOf("2026-01-18" to LineEntry("Mismo dia, sol."), "2027-02-28" to LineEntry("Bisiesto"))
        val now = LocalDateTime.parse("2027-01-17T10:00")
        val open = reminderPlan(j, Settings(reminderOn = true), now)
        assertEquals("Mismo dia, sol.", open.first { it.id == "reminder-2027-01-18" }.body)
        assertNull(open.first { it.id == "reminder-2027-01-17" }.body)

        val locked = reminderPlan(j, Settings(reminderOn = true, lockOn = true), now)
        assertTrue(locked.all { it.body == null })

        // A 29 February has no year before it, so it never carries a memory of its own.
        val leap = reminderPlan(j, Settings(reminderOn = true), LocalDateTime.parse("2028-02-01T10:00"))
        assertNull(leap.first { it.id == "reminder-2028-02-29" }.body)
    }

    @Test
    fun anEarlyReminderBelongsToTheDayThatIsEnding() {
        val plan = reminderPlan(
            emptyMap(),
            Settings(reminderOn = true, reminderHour = 1, reminderMinute = 30),
            LocalDateTime.parse("2027-01-17T10:00"),
        )
        assertEquals(LocalDateTime.parse("2027-01-18T01:30"), plan.first().at)
        assertEquals(LocalDate.parse("2027-01-17"), logicalDate(plan.first().at))
        assertEquals("reminder-2027-01-17", plan.first().id)
    }
}
