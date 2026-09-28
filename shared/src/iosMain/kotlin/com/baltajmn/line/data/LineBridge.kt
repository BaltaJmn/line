package com.baltajmn.line.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The little that Swift needs to ask Kotlin. Swift owns the window, and the window is what the
 * system photographs for the task switcher, so the answer has to cross the bridge before Compose
 * would have had a chance to repaint (docs/tecnico.md 7).
 */
enum class DictateResult { Saved, NeedsPro }

object LineBridge {
    fun isLockOn(): Boolean = LineRepository.settings.lockOn

    /** Assigned by iOSApp.swift: WidgetCenter belongs to Swift, and Kotlin only asks. */
    var reloadWidgets: (() -> Unit)? = null

    /**
     * Swift asks WidgetCenter whether the memory widget is on a home screen when the app starts and
     * each time it comes back, and hands the answer over. Only a change rewrites widget.json.
     */
    fun setMemoryWidgetPlaced(placed: Boolean) {
        if (placed == memoryWidgetPlaced) return
        memoryWidgetPlaced = placed
        syncWidgets(LineRepository.journal, LineRepository.settings, today())
    }

    /**
     * Siri's line (docs/tecnico.md 12.2). It may run with no window at all, so the diary is read
     * here if the app has not. A day already written gets the new line under it, never instead,
     * and nothing is cut: the 280 lives in the field, not in the diary.
     */
    suspend fun dictate(text: String): DictateResult = withContext(Dispatchers.Main) {
        LineRepository.loadOnce()
        if (!LineRepository.settings.pro) return@withContext DictateResult.NeedsPro
        val line = text.trim()
        if (line.isNotEmpty()) {
            val day = today()
            val old = LineRepository.entryOn(day)?.text.orEmpty()
            LineRepository.setText(day, if (old.isBlank()) line else old + "\n" + line, day)
            LineRepository.flush()
        }
        DictateResult.Saved
    }

    /** com.baltajmn.line://today from a widget. Anything else is left alone rather than guessed. */
    fun open(url: String) {
        Route.pending = url.substringAfterLast('/').takeIf { it.isNotEmpty() }
    }
}
