package com.baltajmn.line

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun App() {
    Box(Modifier.fillMaxSize().background(Color(0xFFFBF8F3)), contentAlignment = Alignment.Center) {
        Text("Purl")
    }
}
