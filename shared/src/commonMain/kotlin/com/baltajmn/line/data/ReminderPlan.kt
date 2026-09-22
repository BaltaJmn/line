package com.baltajmn.line.data

import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.Settings
import com.baltajmn.line.model.clampCodePoints
import com.baltajmn.line.model.codePointCount
import com.baltajmn.line.model.logicalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** iOS allows 64 pending requests, and one a day makes the window worth 60 days of margin. */
const val REMINDER_WINDOW = 60

/** Code points of last year's line that fit in a notification without becoming the diary. */
const val MEMORY_SNIPPET_MAX = 120

/** The same local time on another day: after a daylight saving change the reminder is still at 21:00. */
private fun LocalDateTime.plusDays(n: Int) = LocalDateTime(date.plus(n, DateTimeUnit.DAY), time)

/** The first firing strictly after [now] whose logical day is not written yet. */
fun nextFire(now: LocalDateTime, hour: Int, minute: Int, written: (LocalDate) -> Boolean): LocalDateTime {
    var c = LocalDateTime(now.date, LocalTime(hour, minute))
    if (c <= now) c = c.plusDays(1)
    while (written(logicalDate(c))) c = c.plusDays(1)
    return c
}

data class Planned(val id: String, val at: LocalDateTime, val title: String, val body: String?)

/**
 * The whole window at once, one request per day instead of a repeating one: a repeating reminder
 * cannot be told that today is already written, and a diary that nags after you wrote is a diary
 * you turn off.
 */
fun reminderPlan(j: Journal, s: Settings, now: LocalDateTime): List<Planned> {
    if (!s.reminderOn) return emptyList()
    val first = nextFire(now, s.reminderHour, s.reminderMinute) { it.toString() in j }
    return (0 until REMINDER_WINDOW).map { i ->
        val at = first.plusDays(i)
        val day = logicalDate(at)
        // With the lock on, the diary never leaves the lock screen, not even one line of it.
        val memory = if (s.lockOn) null else memorySnippet(j, day)
        Planned(
            id = "reminder-$day",
            at = at,
            title = if (memory == null) S.reminderTitle else S.reminderMemoryTitle,
            body = memory,
        )
    }
}

/** The line of exactly one year back, or null. 29 February has none: the subtraction clips to the 28th. */
fun memorySnippet(j: Journal, day: LocalDate): String? {
    val y = day.minus(1, DateTimeUnit.YEAR)
    if (y.day != day.day) return null
    val text = j[y.toString()]?.text?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    if (text.codePointCount() <= MEMORY_SNIPPET_MAX) return text.replace('\n', ' ')
    val cut = text.clampCodePoints(MEMORY_SNIPPET_MAX)
    val space = cut.lastIndexOf(' ')
    return (if (space > 0) cut.substring(0, space) else cut).replace('\n', ' ') + "..."
}
