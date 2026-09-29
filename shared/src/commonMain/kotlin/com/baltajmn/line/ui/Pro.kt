package com.baltajmn.line.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.baltajmn.line.billing.Billing
import com.baltajmn.line.billing.PurchaseOutcome
import com.baltajmn.line.data.onIos
import com.baltajmn.line.i18n.S
import com.baltajmn.line.ui.theme.Cover
import com.baltajmn.line.ui.theme.Styles
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.launch

/**
 * The only paywall, opened from wherever a free user hits a wall and never at startup: the fourth
 * photo, a Pro cover, a Pro widget (docs/pantallas.md 9.4).
 */
object Paywall {
    var open by mutableStateOf(false)
}

@Composable
fun ProDialog(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var pack by remember { mutableStateOf<Package?>(null) }
    var busy by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        pack = Billing.proPackage()
        if (pack == null) note = S.storeUnavailable
    }

    val target = pack
    val price = target?.storeProduct?.price?.formatted
    val buy = { product: Package ->
        busy = true
        note = null
        scope.launch {
            when (Billing.purchase(product)) {
                PurchaseOutcome.Success -> onDismiss()
                // Changing your mind says nothing and shows nothing.
                PurchaseOutcome.Cancelled -> busy = false
                PurchaseOutcome.Failed -> {
                    busy = false
                    note = S.buyFailed
                }
            }
        }
    }
    val restore = {
        busy = true
        scope.launch {
            val found = Billing.restore()
            busy = false
            if (found) onDismiss() else note = S.restoreNothing
        }
    }

    Dialog(onDismissRequest = { if (!busy) onDismiss() }) {
        Column(
            Modifier.widthIn(max = 400.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surface)
                // Eight lines and a big text size do not fit every phone: the offer scrolls, never cuts.
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // What Pro looks like before any words: the eight covers, as two rows of stitches.
            Swatch(Cover.entries.map { it.color }.chunked(4), stitchWidth = 28.dp)
            Spacer(Modifier.height(20.dp))
            Text(S.proTitle, style = Styles.heading)
            Spacer(Modifier.height(4.dp))
            Text(S.proOnce, style = Styles.secondary)
            Spacer(Modifier.height(20.dp))
            val lines = listOfNotNull(
                S.proPhotos,
                S.proCovers,
                S.proYearWidget,
                S.proLockWidget.takeIf { onIos },
                S.proBook,
                S.proMemoryWidget,
                S.proSiri.takeIf { onIos },
                S.proTile.takeUnless { onIos },
            )
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                lines.forEachIndexed { i, line ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StitchMark(MaterialTheme.colorScheme.primary, tilt = stitchTilt(i))
                        Spacer(Modifier.width(12.dp))
                        Text(line, style = Styles.body)
                    }
                }
            }
            note?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, style = Styles.secondary, modifier = Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(24.dp))
            // With no store there is no price, and a button that cannot say what it costs is not an
            // offer: the note explains it instead (docs/pantallas.md 9.4).
            if (target != null && price != null) {
                PillAction(if (busy) S.working else S.buy(price), onClick = { buy(target) }, Modifier.fillMaxWidth(), enabled = !busy)
                Spacer(Modifier.height(8.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextAction(S.restore, enabled = !busy, onClick = { restore() })
                TextAction(S.notNow, enabled = !busy, onClick = { onDismiss() })
            }
        }
    }
}
