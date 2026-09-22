package com.baltajmn.line

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
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
import com.baltajmn.line.i18n.S
import com.baltajmn.line.ui.Glyph
import com.baltajmn.line.ui.GlyphButton
import com.baltajmn.line.ui.TodayScreen
import com.baltajmn.line.ui.theme.LineTheme

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

    LineTheme {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            when (screen) {
                Screen.Today -> TodayScreen(
                    today = day,
                    onYear = { screen = Screen.Year },
                    onSettings = { screen = Screen.Settings },
                )
                // ponytail: Year arrives with #11 and Settings with #12; until then, only the way back.
                Screen.Year, Screen.Settings -> {
                    BackHandler { screen = Screen.Today }
                    GlyphButton(Glyph.BACK, S.a11yBack, { screen = Screen.Today }, Modifier.safeDrawingPadding())
                }
            }
        }
    }
}
