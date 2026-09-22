package com.baltajmn.line

import com.baltajmn.line.data.ImportFailed
import com.baltajmn.line.data.ImportProblem
import com.baltajmn.line.data.REMINDER_WINDOW
import com.baltajmn.line.data.journalMarkdown
import com.baltajmn.line.data.readBackup
import com.baltajmn.line.data.ZipDamaged
import com.baltajmn.line.data.ZipReader
import com.baltajmn.line.data.ZipWriter
import com.baltajmn.line.data.crc32
import com.baltajmn.line.data.nextFire
import com.baltajmn.line.data.reminderPlan
import com.baltajmn.line.data.search
import com.baltajmn.line.data.widgetState
import com.baltajmn.line.data.widgetView
import com.baltajmn.line.data.withText
import com.baltajmn.line.model.LineEntry
import com.baltajmn.line.model.Settings
import com.baltajmn.line.model.logicalDate
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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

    // 6. The backup zip. docs/tecnico.md 4.3, 6.9
    @Test
    fun crc32MatchesTheCheckValueOfTheStandard() {
        assertEquals(0xCBF43926.toInt(), crc32("123456789".encodeToByteArray()))
    }

    @Test
    fun aZipWeWroteComesBackWithEveryByte() {
        val photo = ByteArray(300) { (it * 7).toByte() }
        val other = byteArrayOf(0, -1, 127, -128, 10, 13)
        val zip = zipOf(
            "entries.json" to """{"version":1}""".encodeToByteArray(),
            "journal.md" to "# Purl\n\n2027-01-17\nCafe\n".encodeToByteArray(),
            "photos/p-1.jpg" to photo,
            "photos/p-2.jpg" to other,
        )
        val read = mutableListOf<Pair<String, ByteArray>>()
        reader(zip).forEach { name, bytes -> read += name to bytes }
        assertEquals(listOf("entries.json", "journal.md", "photos/p-1.jpg", "photos/p-2.jpg"), read.map { it.first })
        assertContentEquals(photo, read[2].second)
        assertContentEquals(other, read[3].second)
    }

    @Test
    fun aZipCutInHalfIsRefused() {
        val zip = zipOf("entries.json" to ByteArray(500) { 42 })
        assertFailsWith<ZipDamaged> { reader(zip.copyOfRange(0, zip.size / 2)).forEach { _, _ -> } }
    }

    @Test
    fun aBrokenCrcIsRefused() {
        val zip = zipOf("entries.json" to "hola".encodeToByteArray())
        // The payload starts right after the 30 byte local header and the name.
        val payload = 30 + "entries.json".length
        zip[payload] = (zip[payload] + 1).toByte()
        assertFailsWith<ZipDamaged> { reader(zip).forEach { _, _ -> } }
    }

    private fun zipOf(vararg files: Pair<String, ByteArray>): ByteArray {
        val out = mutableListOf<Byte>()
        val writer = ZipWriter({ bytes -> bytes.forEach { out += it } })
        files.forEach { (name, bytes) -> writer.add(name, bytes) }
        writer.finish()
        return out.toByteArray()
    }

    private fun reader(data: ByteArray): ZipReader {
        var pos = 0
        return ZipReader { n ->
            if (pos >= data.size) {
                null
            } else {
                data.copyOfRange(pos, minOf(pos + n, data.size)).also { pos += it.size }
            }
        }
    }

    // 15. What an import refuses. docs/tecnico.md 4.6
    @Test
    fun anyJsonIsNotABackup() {
        // ignoreUnknownKeys would decode this into an empty diary, which would read as "nothing to merge".
        assertEquals(ImportProblem.NotBackup, refusal("""{"hello":"world"}"""))
        assertEquals(ImportProblem.NotBackup, refusal("""{"version":1}"""))
        assertEquals(ImportProblem.NotBackup, refusal("""{"entries":{"2027-01-17":{"text":"x"}}}"""))
        assertEquals(ImportProblem.NotBackup, refusal("not a file at all"))
    }

    @Test
    fun aBackupFromALaterVersionIsRefusedWholeAndAnEmptyOneToo() {
        assertEquals(ImportProblem.TooNew, refusal("""{"version":99,"entries":{"2027-01-17":{"text":"x"}}}"""))
        assertEquals(ImportProblem.Empty, refusal("""{"version":1,"entries":{}}"""))
        assertEquals(ImportProblem.Damaged, refusal("""{"version":1,"entries":{"manana":{"text":"x"}}}"""))
    }

    @Test
    fun aPhotoNameThatWalksOutOfTheFolderIsRefused() {
        // Without this the name reaches the file system and "../entries.json" is the diary itself.
        val evil = """{"version":1,"entries":{"2027-03-04":{"text":"x","photo":"../entries.json"}}}"""
        assertEquals(ImportProblem.Damaged, refusal(evil))
        assertEquals(ImportProblem.Damaged, refusal("""{"version":1,"entries":{"2027-03-04":{"text":"x","photo":"a/b.jpg"}}}"""))
    }

    @Test
    fun aVersionThatIsNotANumberIsNotABackupInsteadOfACrash() {
        assertEquals(ImportProblem.NotBackup, refusal("""{"version":[1],"entries":{"2027-01-17":{"text":"x"}}}"""))
        assertEquals(ImportProblem.NotBackup, refusal("""{"version":{"a":1},"entries":{}}"""))
    }

    @Test
    fun aMoodTrakerBackupIsRecognised() {
        val mood = """{"app":"mood","store":{"entries":[{"date":"2027-01-17","note":"hola"}]}}"""
        assertEquals(ImportProblem.IsMoodTraker, refusal(mood))
    }

    @Test
    fun aBareEntriesJsonImportsWithoutPhotos() {
        val backup = readBackup(sourceOf("""{"version":1,"entries":{"2027-01-17":{"text":"Cafe","late":true}}}"""))
        assertEquals(LineEntry("Cafe", late = true), backup.journal["2027-01-17"])
        assertTrue(backup.photos.isEmpty())
    }

    // 4.4: the file someone opens in fifty years, in a plain text editor.
    @Test
    fun theMarkdownIsTheDiaryInOrderWithItsPhotos() {
        val j = mapOf(
            "2027-01-17" to LineEntry("Mismo dia, sol.", photo = "p-3f9a1c2e.jpg"),
            "2026-01-17" to LineEntry("Primer dia de vacaciones."),
        )
        assertEquals(
            "# Purl\n" +
                "\n## 2026-01-17\nPrimer dia de vacaciones.\n" +
                "\n## 2027-01-17\nMismo dia, sol.\n\n![](photos/p-3f9a1c2e.jpg)\n",
            journalMarkdown(j),
        )
    }

    // Test 9: what the widgets see, and what they can still get right after 03:00 on their own.
    @Test
    fun theWidgetStateIsDerivedAndNeverCarriesText() {
        val j = mapOf(
            "2026-01-17" to LineEntry("Primer dia."),
            "2026-01-18" to LineEntry("Segundo dia."),
            "2027-01-17" to LineEntry("Hoy."),
        )
        val st = widgetState(j, Settings(cover = "clay", pro = true), today)
        assertEquals("2027-01-17", st.date)
        assertTrue(st.written)
        assertTrue(st.memory)
        assertTrue(st.memoryNext)
        assertEquals("clay", st.cover)
        assertTrue(st.pro)
        assertEquals(366, st.days.length)
        assertEquals('1', st.days[16])
        assertEquals('0', st.days[17])
        // 4.2: no line of the diary reaches the widgets in v1.0.
        assertNull(st.line)
        assertNull(st.lineNext)

        // The widget woke up after 03:00 still holding yesterday.
        val next = widgetView(st, LocalDate.parse("2027-01-18"))
        assertEquals("2027-01-18", next.date)
        assertEquals(false, next.written)
        assertTrue(next.memory)

        // Two days stale: nothing can be assumed about the memory any more.
        assertEquals(false, widgetView(st, LocalDate.parse("2027-01-19")).memory)

        // A new year empties the grid rather than painting last year's days on it.
        val newYear = widgetView(st, LocalDate.parse("2028-01-01"))
        assertEquals(2028, newYear.year)
        assertEquals("0".repeat(366), newYear.days)
    }

    private fun refusal(text: String): ImportProblem? =
        runCatching { readBackup(sourceOf(text)) }.exceptionOrNull().let { (it as? ImportFailed)?.problem }

    private fun sourceOf(text: String): (Int) -> ByteArray? {
        val data = text.encodeToByteArray()
        var pos = 0
        return { n ->
            if (pos >= data.size) {
                null
            } else {
                data.copyOfRange(pos, minOf(pos + n, data.size)).also { pos += it.size }
            }
        }
    }
}
