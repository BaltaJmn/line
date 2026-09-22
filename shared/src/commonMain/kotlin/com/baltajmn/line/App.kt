package com.baltajmn.line

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.baltajmn.line.data.today
import com.baltajmn.line.ui.DaySheet
import com.baltajmn.line.ui.SettingsScreen
import com.baltajmn.line.ui.TodayScreen
import com.baltajmn.line.ui.YearScreen
import com.baltajmn.line.ui.theme.LineTheme
import kotlinx.datetime.LocalDate

/** Three screens do not justify a navigation library. */
enum class Screen { Today, Year, Settings }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    remember { LineRepository.load() }
    var day by remember { mutableStateOf(today()) }
    var screen by remember { mutableStateOf(Screen.Today) }
    // Coming back after 03:00 is a new day.
    LifecycleEventEffect(Lifecycle.Event.ON_START) { day = today() }
    // The debounce may still be waiting when the app leaves the screen: write now.
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { LineRepository.saveNow() }

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

            BackHandler(screen != Screen.Today || openDay != null) {
                if (openDay != null) closeDay() else screen = Screen.Today
            }
        }
    }
}
