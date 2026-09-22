package com.baltajmn.line

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.ui.theme.LineTheme
import com.baltajmn.line.ui.theme.Styles

@Composable
fun App() {
    remember { LineRepository.load() }
    // The debounce may still be waiting when the app leaves the screen: write now.
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { LineRepository.saveNow() }

    LineTheme {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            Text("Purl", style = Styles.userLarge)
        }
    }
}
