package com.baltajmn.line.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus

const val STREAK_SHOWN_FROM = 2

private fun Journal.onTime(d: LocalDate) = this[d.isoKey()]?.let { !it.late } == true

/**
 * Days in a row written on their own day, ending today or, while today is still blank, yesterday.
 * Filling in a missed day later neither raises nor breaks it.
 */
fun streak(j: Journal, today: LocalDate): Int {
    var d = if (j.onTime(today)) today else today.minus(1, DateTimeUnit.DAY)
    var n = 0
    while (j.onTime(d)) {
        n++
        d = d.minus(1, DateTimeUnit.DAY)
    }
    return n
}

/** The longest on-time run inside one year: the year card and, in v1.2, the recap. */
fun longestStreak(j: Journal, year: Int): Int {
    var best = 0
    var run = 0
    var d = LocalDate(year, 1, 1)
    while (d.year == year) {
        run = if (j.onTime(d)) run + 1 else 0
        best = maxOf(best, run)
        d = d.plus(1, DateTimeUnit.DAY)
    }
    return best
}

/** Day N of the diary, counted from its first entry. */
fun dayNumber(j: Journal, today: LocalDate): Int {
    val first = j.keys.minOrNull()?.let(LocalDate::parse) ?: return 1
    return if (first > today) 1 else first.daysUntil(today) + 1
}

enum class Milestone { FirstLine, Thirty, Hundred, Anniversary, ThreeYears }

/** At most one, in this priority. Late entries count as lines here. */
fun milestone(j: Journal, today: LocalDate): Milestone? {
    val written = today.isoKey() in j
    val first = j.keys.minOrNull()?.let(LocalDate::parse) ?: return null
    if (today == first.plus(1, DateTimeUnit.YEAR)) return Milestone.Anniversary
    if (written && threeYearsDate(j) == today) return Milestone.ThreeYears
    if (written && j.size == 100) return Milestone.Hundred
    if (written && j.size == 30) return Milestone.Thirty
    if (written && j.size == 1) return Milestone.FirstLine
    return null
}

/** The first date, in order, whose month and day gathers entries from three different years. */
fun threeYearsDate(j: Journal): LocalDate? {
    val years = mutableMapOf<String, Int>()
    for (k in j.keys.sorted()) {
        val md = k.substring(5)
        val n = (years[md] ?: 0) + 1
        years[md] = n
        if (n == 3) return LocalDate.parse(k)
    }
    return null
}
