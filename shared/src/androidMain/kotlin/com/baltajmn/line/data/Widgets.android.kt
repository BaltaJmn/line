package com.baltajmn.line.data

import java.io.File

/** filesDir, next to the diary but readable by the widget process, which the diary never is. */
actual fun writeWidgetState(json: String) {
    val dir = Storage.rootOverride ?: AndroidContext.value.filesDir
    val temp = File(dir, "widget.tmp.json")
    temp.writeText(json)
    if (!temp.renameTo(File(dir, "widget.json"))) error("could not move widget.json into place")
}

// ponytail: the Glance widgets land in #20, and updateAll needs their classes. Until then the file
// is the whole contract and there is nothing on a home screen to redraw.
actual fun refreshWidgets() = Unit
