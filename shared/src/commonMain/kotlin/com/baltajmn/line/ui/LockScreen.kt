package com.baltajmn.line.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baltajmn.line.data.Lock
import com.baltajmn.line.i18n.S
import com.baltajmn.line.ui.theme.Styles

/**
 * Over everything else, with nothing on it: the name and a way back in. The system dialog comes up
 * on its own, and the button is only there for whoever dismissed it.
 */
@Composable
fun LockScreen(onUnlocked: () -> Unit) {
    // The field underneath keeps its focus while the app is away, and the keyboard comes back up
    // over the lock with it. Clearing the focus is what sends it down and keeps it down.
    val focus = LocalFocusManager.current
    LaunchedEffect(Unit) {
        focus.clearFocus(force = true)
        Lock.authenticate { if (it) onUnlocked() }
    }

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Purl", style = Styles.userLarge.copy(fontSize = 32.sp, lineHeight = 40.sp))
            Spacer(Modifier.height(32.dp))
            Box(
                Modifier.heightIn(min = 44.dp)
                    .clip(MaterialTheme.shapes.large)
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), MaterialTheme.shapes.large)
                    .clickable(role = Role.Button) { Lock.authenticate { if (it) onUnlocked() } }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) { Text(S.unlock, style = Styles.action) }
        }
    }
}
