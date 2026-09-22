package com.baltajmn.line

import com.baltajmn.line.model.Echoes
import com.baltajmn.line.model.Edit
import com.baltajmn.line.model.JournalFile
import com.baltajmn.line.model.JournalJson
import com.baltajmn.line.model.LineEntry
import com.baltajmn.line.model.Milestone
import com.baltajmn.line.model.Settings
import com.baltajmn.line.model.clampCodePoints
import com.baltajmn.line.model.codePointCount
import com.baltajmn.line.model.dayNumber
import com.baltajmn.line.model.echoes
import com.baltajmn.line.model.fold
import com.baltajmn.line.model.limitEdit
import com.baltajmn.line.model.logicalDate
import com.baltajmn.line.model.longestStreak
import com.baltajmn.line.model.milestone
import com.baltajmn.line.model.nextReturn
import com.baltajmn.line.model.pastYears
import com.baltajmn.line.model.streak
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

// A smiling face, U+1F642, built from its surrogates so no emoji lives in the source.
private val SMILE = "${0xD83D.toChar()}${0xDE42.toChar()}"

private fun date(s: String) = LocalDate.parse(s)

private fun journal(vararg keys: String, late: Set<String> = emptySet()) =
    keys.associateWith { LineEntry(text = "line $it", late = it in late) }

/** Consecutive days ending on [last], [count] of them. */
private fun run(last: String, count: Int): Array<String> =
    (0 until count).map { date(last).minus(it, DateTimeUnit.DAY).toString() }.toTypedArray()

private fun hasLoneSurrogate(s: String): Boolean {
    var i = 0
    while (i < s.length) {
        val c = s[i]
        if (c.isHighSurrogate()) {
            if (i + 1 >= s.length || !s[i + 1].isLowSurrogate()) return true
            i += 2
        } else {
            if (c.isLowSurrogate()) return true
            i++
        }
    }
    return false
}

class ModelTest {

    // 1. Serialization
    @Test
    fun journalRoundTripKeepsTextAndDropsDefaults() {
        val text = "Dijo \"vale\"\nsegunda línea $SMILE, café y niño"
        val file = JournalFile(
            entries = mapOf(
                "2027-01-17" to LineEntry(text = text, late = true),
                "2027-01-18" to LineEntry(text = "plain"),
            ),
        )
        val json = JournalJson.encodeToString(JournalFile.serializer(), file)

        assertEquals(file, JournalJson.decodeFromString(JournalFile.serializer(), json))
        assertTrue("\"version\":1" in json)
        assertTrue("\"entries\"" in json)
        assertTrue("\"late\":true" in json)
        assertFalse("\"late\":false" in json)
        assertFalse("photo" in json)
        assertFalse("settings" in json)
    }

    @Test
    fun emptyJournalStillWritesVersionAndEntries() {
        val json = JournalJson.encodeToString(JournalFile.serializer(), JournalFile())
        assertEquals("{\"version\":1,\"entries\":{}}", json)
    }

    // 5. Migration
    @Test
    fun filesWithoutNewFieldsOrWithUnknownOnesStillRead() {
        val old = """{"version":1,"entries":{"2026-05-01":{"text":"old"}}}"""
        val future = """{"version":1,"entries":{"2026-05-01":{"text":"old","mood":"m3","tags":["x"]}},"sync":{"on":true}}"""
        for (json in listOf(old, future)) {
            val f = JournalJson.decodeFromString(JournalFile.serializer(), json)
            assertEquals(LineEntry(text = "old"), f.entries["2026-05-01"])
            assertEquals(Settings(), f.settings)
        }
    }

    // 3. Past years
    @Test
    fun pastYearsMostRecentFirst() {
        val j = journal("2026-01-17", "2028-01-17", "2028-01-18", "2029-01-17")
        assertEquals(
            listOf(date("2028-01-17"), date("2026-01-17")),
            pastYears(j, date("2029-01-17")).map { it.first },
        )
    }

    @Test
    fun leapDayOnlyMatchesItself() {
        val j = journal("2028-02-29")
        assertTrue(pastYears(j, date("2029-02-28")).isEmpty())
        assertTrue(pastYears(j, date("2030-03-01")).isEmpty())
        assertEquals(listOf(date("2028-02-29")), pastYears(j, date("2032-02-29")).map { it.first })
    }

    // 4. Logical date and streak
    @Test
    fun dayEndsAtThree() {
        assertEquals(date("2027-01-16"), logicalDate(LocalDateTime(2027, 1, 17, 2, 59)))
        assertEquals(date("2027-01-17"), logicalDate(LocalDateTime(2027, 1, 17, 3, 0)))
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun daylightSavingDoesNotMoveTheCutoff() {
        // Europe/Madrid jumps from 02:00 to 03:00 on 2027-03-28.
        val madrid = TimeZone.of("Europe/Madrid")
        assertEquals(date("2027-03-27"), logicalDate(Instant.parse("2027-03-28T00:59:59Z"), madrid))
        assertEquals(date("2027-03-28"), logicalDate(Instant.parse("2027-03-28T01:00:00Z"), madrid))
        // And back from 03:00 to 02:00 on 2027-10-31: 02:30 happens twice, both still the 30th.
        assertEquals(date("2027-10-30"), logicalDate(Instant.parse("2027-10-31T00:30:00Z"), madrid))
        assertEquals(date("2027-10-30"), logicalDate(Instant.parse("2027-10-31T01:30:00Z"), madrid))
        assertEquals(date("2027-10-31"), logicalDate(Instant.parse("2027-10-31T02:00:00Z"), madrid))
    }

    @Test
    fun streakCountsOnlyDaysWrittenOnTime() {
        val onTime = journal("2027-01-15", "2027-01-16", "2027-01-17")
        assertEquals(3, streak(onTime, date("2027-01-17")))
        assertEquals(1, streak(journal("2027-01-15", "2027-01-16", "2027-01-17", late = setOf("2027-01-16")), date("2027-01-17")))
        // The morning after, still blank: yesterday's run is not broken yet.
        assertEquals(3, streak(onTime, date("2027-01-18")))
        assertEquals(0, streak(onTime, date("2027-01-19")))
    }

    @Test
    fun longestStreakStaysInsideTheYear() {
        val j = journal(*run("2027-01-02", 5), "2027-03-01", "2027-03-02", late = setOf("2026-12-31"))
        assertEquals(2, longestStreak(j, 2027))
        assertEquals(2, longestStreak(j, 2026))
    }

    @Test
    fun dayNumberCountsFromTheFirstEntry() {
        assertEquals(12, dayNumber(journal("2027-01-06", "2027-01-10"), date("2027-01-17")))
        assertEquals(1, dayNumber(emptyMap(), date("2027-01-17")))
    }

    // 8. The 280 limit
    @Test
    fun anEmojiCountsAsOne() {
        assertEquals(1, SMILE.codePointCount())
        assertEquals(3, "a${SMILE}b".codePointCount())
        assertEquals("a", "a$SMILE".clampCodePoints(1))
    }

    @Test
    fun pastingIntoAnEmptyFieldKeepsTheFirst280() {
        val pasted = "x".repeat(300)
        assertEquals(Edit("x".repeat(280), 280), limitEdit("", pasted, pasted.length))
    }

    @Test
    fun pastingInTheMiddleKeepsTheEnd() {
        val old = "a".repeat(100) + "z".repeat(179)
        val new = "a".repeat(100) + "INSERTED" + "z".repeat(179)
        assertEquals(Edit("a".repeat(100) + "I" + "z".repeat(179), 101), limitEdit(old, new, 108))
    }

    @Test
    fun neverLeavesHalfAPair() {
        val full = "a".repeat(280)
        assertEquals(full, limitEdit(full, full.substring(0, 140) + SMILE + full.substring(140), 142).text)

        val almost = "a".repeat(279)
        val withTwo = almost.substring(0, 10) + SMILE + SMILE + almost.substring(10)
        val result = limitEdit(almost, withTwo, 14).text
        assertFalse(hasLoneSurrogate(result))
        assertEquals(280, result.codePointCount())

        // The typed emoji equals the one already at the end, so prefix and suffix both claim it.
        val before = "a".repeat(279) + SMILE
        val after = before + SMILE
        assertEquals(before, limitEdit(before, after, after.length).text)
    }

    @Test
    fun aPasteThatStartsLikeTheNextWordKeepsThatWord() {
        val head = "a".repeat(266) + "went "
        val old = head + "home"
        val new = head + "happy " + "home"
        assertEquals(Edit(head + "happy" + "home", head.length + 5), limitEdit(old, new, new.length - 4))

        val start = "a".repeat(260)
        val tail = "home sweet home"
        val pasted = start + "home is where" + tail
        assertEquals(start + "home " + tail, limitEdit(start + tail, pasted, pasted.length - tail.length).text)
    }

    @Test
    fun theCutNeverSplitsACharacterInTwo() {
        val heart = "${0x2764.toChar()}${0xFE0F.toChar()}"
        val almost = "a".repeat(279)
        assertEquals(almost, limitEdit(almost, almost + heart, almost.length + 2).text)
        val accent = "e${0x301.toChar()}"
        assertEquals(almost, limitEdit(almost, almost + accent, almost.length + 2).text)
    }

    @Test
    fun anOverlongTextCanShrinkButNotGrow() {
        val long = "y".repeat(300)
        assertEquals(long.dropLast(1), limitEdit(long, long.dropLast(1), 299).text)
        assertEquals(long, limitEdit(long, long + "more", 304).text)
    }

    // 11. Milestones
    @Test
    fun countMilestonesNeedTodayWrittenAndTheExactCount() {
        assertEquals(Milestone.FirstLine, milestone(journal("2027-01-17"), date("2027-01-17")))
        assertNull(milestone(journal("2027-01-16"), date("2027-01-17")))

        val thirty = journal(*run("2027-02-15", 30))
        assertEquals(Milestone.Thirty, milestone(thirty, date("2027-02-15")))
        assertNull(milestone(thirty, date("2027-02-16")))
        assertNull(milestone(journal(*run("2027-02-16", 31)), date("2027-02-16")))

        val hundred = journal(*run("2027-06-01", 100))
        assertEquals(Milestone.Hundred, milestone(hundred, date("2027-06-01")))
    }

    @Test
    fun anniversaryShowsEvenWithTodayBlank() {
        assertEquals(Milestone.Anniversary, milestone(journal("2026-01-17", "2026-05-01"), date("2027-01-17")))
    }

    @Test
    fun aDiaryStartedOnALeapDayTurnsOneOnThe28th() {
        assertEquals(Milestone.Anniversary, milestone(journal("2028-02-29"), date("2029-02-28")))
        assertNull(milestone(journal("2028-02-29"), date("2029-03-01")))
    }

    @Test
    fun threeYearsOnlyOnTheFirstDateThatGathersThem() {
        val j = journal("2025-03-05", "2026-03-05", "2027-03-05", "2025-03-06", "2026-03-06", "2027-03-06")
        assertEquals(Milestone.ThreeYears, milestone(j, date("2027-03-05")))
        assertNull(milestone(j, date("2027-03-06")))
    }

    @Test
    fun milestonePriority() {
        // Anniversary and the thirtieth line on the same day.
        val j = journal("2026-01-17", *run("2027-01-17", 29))
        assertEquals(30, j.size)
        assertEquals(Milestone.Anniversary, milestone(j, date("2027-01-17")))

        // Three years and the hundredth line on the same day.
        val k = journal("2025-06-01", "2026-06-01", *run("2027-06-01", 98))
        assertEquals(100, k.size)
        assertEquals(Milestone.ThreeYears, milestone(k, date("2027-06-01")))
    }

    // 12. Echoes and the page coming back
    @Test
    fun echoesInTheFirstYear() {
        val j = journal("2027-03-24", "2027-02-28")
        val e = echoes(j, date("2027-03-31"))
        assertEquals(date("2027-03-24"), e?.week?.first)
        assertEquals(date("2027-02-28"), e?.month?.first)
        assertEquals(Echoes(null, null), echoes(emptyMap(), date("2027-03-31")))
    }

    @Test
    fun noEchoesOncePastYearsExist() {
        assertNull(echoes(journal("2026-03-31", "2027-03-24"), date("2027-03-31")))
    }

    @Test
    fun pageComesBackNextYearOrNextLeapDay() {
        assertEquals(date("2028-01-17"), nextReturn(date("2027-01-17")))
        assertEquals(date("2032-02-29"), nextReturn(date("2028-02-29")))
        assertEquals(date("2104-02-29"), nextReturn(date("2096-02-29")))
    }

    // 13, the folding half. Search itself arrives with the year screen.
    @Test
    fun foldDropsCaseAndDiacritics() {
        assertEquals("cafe", fold("Café"))
        assertEquals("ano", fold("AÑO"))
        assertEquals("strasse", fold("Straße"))
        assertEquals("coeur", fold("cœur"))
        assertEquals("cafe", fold("Cafe${0x301.toChar()}"))
    }
}
