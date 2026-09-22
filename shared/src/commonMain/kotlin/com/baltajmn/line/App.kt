package com.baltajmn.line

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.Lock
import com.baltajmn.line.data.Reminder
import com.baltajmn.line.data.syncWidgets
import com.baltajmn.line.data.today
import com.baltajmn.line.ui.DaySheet
import com.baltajmn.line.ui.LockScreen
import com.baltajmn.line.ui.SettingsScreen
import com.baltajmn.line.ui.TodayScreen
import com.baltajmn.line.ui.YearScreen
import com.baltajmn.line.ui.theme.LineTheme
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import kotlinx.datetime.LocalDate

/** Three screens do not justify a navigation library. */
enum class Screen { Today, Year, Settings }

/** A minute in the background. Short enough to protect, long enough to answer the door. */
val RELOCK_AFTER = 60.seconds

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    remember {
        LineRepository.load()
        Reminder.sync(askPermission = false)
    }
    var day by remember { mutableStateOf(today()) }
    var screen by remember { mutableStateOf(Screen.Today) }
    var locked by remember { mutableStateOf(LineRepository.settings.lockOn) }
    var leftAt by remember { mutableStateOf<TimeSource.Monotonic.ValueTimeMark?>(null) }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Coming back after 03:00 is a new day, and the widgets are told before they are looked at.
        day = today()
        syncWidgets(LineRepository.journal, LineRepository.settings, day)
        // A minute away locks it again; stepping out to a photo or a share sheet does not.
        val away = leftAt?.elapsedNow()
        if (LineRepository.settings.lockOn && away != null && away >= RELOCK_AFTER) locked = true
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        // The debounce may still be waiting when the app leaves the screen: write now.
        LineRepository.saveNow()
        leftAt = TimeSource.Monotonic.markNow()
    }
    // The task switcher takes its picture without asking, so the window is told in advance.
    LaunchedEffect(LineRepository.settings.lockOn) { Lock.setHidesPreview(LineRepository.settings.lockOn) }

    // The open day is an overlay over whichever screen called it, so back closes it first.
    var openDay by remember { mutableStateOf<LocalDate?>(null) }
    val closeDay = {
        LineRepository.saveNow()
        openDay = null
    }

    LineTheme {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            when (screen) {
                Screen.Today -> TodayScreen(
                    today = day,
                    onYear = { screen = Screen.Year },
                    onSettings = { screen = Screen.Settings },
                    onOpenDay = { openDay = it },
                )
                Screen.Year -> YearScreen(
                    today = day,
                    onBack = { screen = Screen.Today },
                    onOpenDay = { openDay = it },
                )
                Screen.Settings -> SettingsScreen(onBack = { screen = Screen.Today })
            }
            openDay?.let { DaySheet(it, day, closeDay) }

            // Over everything, including the open day and any dialog under it.
            if (locked) LockScreen { locked = false }

            BackHandler(!locked && (screen != Screen.Today || openDay != null)) {
                if (openDay != null) closeDay() else screen = Screen.Today
            }
        }
    }
}
