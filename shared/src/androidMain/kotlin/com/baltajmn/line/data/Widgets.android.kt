package com.baltajmn.line.data

import androidx.glance.appwidget.updateAll
import com.baltajmn.line.widget.TodayWidget
import com.baltajmn.line.widget.YearWidget
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** filesDir, next to the diary but readable by the widget process, which the diary never is. */
actual fun writeWidgetState(json: String) {
    val dir = Storage.rootOverride ?: AndroidContext.value.filesDir
    val temp = File(dir, "widget.tmp.json")
    temp.writeText(json)
    if (!temp.renameTo(File(dir, "widget.json"))) error("could not move widget.json into place")
}

/** The only thing a widget is allowed to read, for the widget side to read it. */
fun readWidgetState(): WidgetState? {
    val dir = Storage.rootOverride ?: AndroidContext.value.filesDir
    val text = runCatching { File(dir, "widget.json").readText() }.getOrNull() ?: return null
    return runCatching { WidgetJson.decodeFromString<WidgetState>(text) }.getOrNull()
}

actual fun refreshWidgets() {
    val context = AndroidContext.value
    CoroutineScope(Dispatchers.Default).launch {
        TodayWidget().updateAll(context)
        YearWidget().updateAll(context)
    }
}
