package com.baltajmn.line.data

/**
 * The little that Swift needs to ask Kotlin. Swift owns the window, and the window is what the
 * system photographs for the task switcher, so the answer has to cross the bridge before Compose
 * would have had a chance to repaint (docs/tecnico.md 7).
 */
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

    /** com.baltajmn.line://today from a widget. Anything else is left alone rather than guessed. */
    fun open(url: String) {
        Route.pending = url.substringAfterLast('/').takeIf { it.isNotEmpty() }
    }
}
