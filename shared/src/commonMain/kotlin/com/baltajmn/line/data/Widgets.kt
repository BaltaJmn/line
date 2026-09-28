package com.baltajmn.line.data

import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.Settings
import kotlin.concurrent.Volatile
import kotlinx.datetime.LocalDate

/**
 * The only channel to the widgets. They read widget.json and nothing else, so wherever the diary
 * lives the widgets cannot reach it, on either platform.
 */
expect fun writeWidgetState(json: String)

/** Tells the system the state changed. Both platforms redraw on their own schedule otherwise. */
expect fun refreshWidgets()

/**
 * Whether the memory widget is on a home screen. Each platform finds out on its own (Glance ids,
 * WidgetCenter) and sets it; until then no line leaves the diary.
 */
@Volatile
var memoryWidgetPlaced = false

fun syncWidgets(j: Journal, s: Settings, today: LocalDate) {
    runCatching {
        writeWidgetState(WidgetJson.encodeToString(widgetState(j, s, today, memoryWidgetPlaced)))
        refreshWidgets()
    }
}
