package com.baltajmn.line.data

import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.Settings
import com.baltajmn.line.model.pastYears
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Everything the widgets are allowed to know, and nothing else. No line of the diary crosses this
 * file in v1.0: a widget that cannot read the text cannot leak it onto a locked screen.
 *
 * `line` and `lineNext` exist only so Swift can decode the same shape in v1.1; nothing writes them.
 */
@Serializable
data class WidgetState(
    /** Logical date of the day this state was built for. */
    val date: String,
    val written: Boolean,
    val memory: Boolean,
    /** Whether tomorrow has past years, so the widget can be right after 03:00 without the app. */
    val memoryNext: Boolean,
    val year: Int,
    /** 366 characters, '1' where that day of `year` has an entry. */
    val days: String,
    val cover: String,
    val pro: Boolean,
    val line: String? = null,
    val lineNext: String? = null,
)

/** Swift decodes every field, so none of them may be missing. */
@OptIn(ExperimentalSerializationApi::class)
val WidgetJson = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
}

fun widgetState(j: Journal, s: Settings, today: LocalDate): WidgetState {
    val tomorrow = today.plus(1, DateTimeUnit.DAY)
    val days = CharArray(366) { '0' }
    j.keys.forEach { k -> LocalDate.parse(k).takeIf { it.year == today.year }?.let { days[it.dayOfYear - 1] = '1' } }
    return WidgetState(
        date = today.toString(),
        written = today.toString() in j,
        memory = pastYears(j, today).isNotEmpty(),
        memoryNext = pastYears(j, tomorrow).isNotEmpty(),
        year = today.year,
        days = days.concatToString(),
        cover = s.cover,
        pro = s.pro,
    )
}

/**
 * What the widget should paint right now, without the app having run. A widget wakes up after 03:00
 * holding yesterday's state, and yesterday's answer to "is today written" is always no.
 */
fun widgetView(st: WidgetState, today: LocalDate): WidgetState = when (st.date) {
    today.toString() -> st
    today.minus(1, DateTimeUnit.DAY).toString() ->
        st.copy(date = today.toString(), written = false, memory = st.memoryNext, line = st.lineNext)
    else -> st.copy(date = today.toString(), written = false, memory = false, line = null)
}.let { if (it.year != today.year) it.copy(year = today.year, days = "0".repeat(366)) else it }
