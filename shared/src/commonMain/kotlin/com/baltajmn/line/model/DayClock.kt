package com.baltajmn.line.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/** A line written at 01:30 still belongs to the day that is ending, not to the one starting. */
const val DAY_CUTOFF_HOUR = 3

@OptIn(ExperimentalTime::class)
fun logicalDate(now: Instant, tz: TimeZone): LocalDate = logicalDate(now.toLocalDateTime(tz))

// The local wall-clock hour is compared, never an instant minus three hours: a DST change does not
// move the cutoff.
fun logicalDate(local: LocalDateTime): LocalDate =
    if (local.hour < DAY_CUTOFF_HOUR) local.date.minus(1, DateTimeUnit.DAY) else local.date

fun LocalDate.isoKey(): String = toString()

/** Same month and day in earlier years, most recent first. 29 February only matches itself. */
fun pastYears(j: Journal, d: LocalDate): List<Pair<LocalDate, LineEntry>> =
    j.mapNotNull { (k, e) ->
        LocalDate.parse(k).takeIf {
            it.month.number == d.month.number && it.day == d.day && it.year < d.year
        }?.let { it to e }
    }.sortedByDescending { it.first }

data class Echoes(val week: Pair<LocalDate, LineEntry>?, val month: Pair<LocalDate, LineEntry>?)

/** The first-year bridge: a week and a month ago, only while the page has no past years. */
fun echoes(j: Journal, d: LocalDate): Echoes? {
    if (pastYears(j, d).isNotEmpty()) return null
    val w = d.minus(7, DateTimeUnit.DAY)
    val m = d.minus(1, DateTimeUnit.MONTH) // 31 March gives the last day of February
    return Echoes(j[w.isoKey()]?.let { w to it }, j[m.isoKey()]?.let { m to it })
}

/** The date this page next comes back: a year later, or the next 29 February. */
fun nextReturn(d: LocalDate): LocalDate =
    if (d.month.number == 2 && d.day == 29) {
        LocalDate(generateSequence(d.year + 1) { it + 1 }.first(::isLeap), 2, 29)
    } else {
        d.plus(1, DateTimeUnit.YEAR)
    }

private fun isLeap(year: Int) = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
