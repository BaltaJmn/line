package com.baltajmn.line.data

import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.Settings
import kotlinx.datetime.LocalDate

/**
 * The only channel to the widgets. They read widget.json and nothing else, so wherever the diary
 * lives the widgets cannot reach it, on either platform.
 */
expect fun writeWidgetState(json: String)

/** Tells the system the state changed. Both platforms redraw on their own schedule otherwise. */
expect fun refreshWidgets()

fun syncWidgets(j: Journal, s: Settings, today: LocalDate) {
    runCatching {
        writeWidgetState(WidgetJson.encodeToString(widgetState(j, s, today)))
        refreshWidgets()
    }
}
