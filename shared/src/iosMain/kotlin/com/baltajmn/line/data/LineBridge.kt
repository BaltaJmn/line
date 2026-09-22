package com.baltajmn.line.data

/**
 * The little that Swift needs to ask Kotlin. Swift owns the window, and the window is what the
 * system photographs for the task switcher, so the answer has to cross the bridge before Compose
 * would have had a chance to repaint (docs/tecnico.md 7).
 */
object LineBridge {
    fun isLockOn(): Boolean = LineRepository.settings.lockOn
}
