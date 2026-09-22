package com.baltajmn.line.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.isoKey
import com.baltajmn.line.share.Sharing
import com.baltajmn.line.share.encodeToPng
import com.baltajmn.line.share.renderLineCard
import com.baltajmn.line.share.renderYearCard
import com.baltajmn.line.ui.theme.Cover
import com.baltajmn.line.ui.theme.MAX_CONTENT_WIDTH
import com.baltajmn.line.ui.theme.Styles
import kotlinx.datetime.LocalDate

/** What the card is made of: a whole year, or the line of one day. */
sealed interface ShareTarget {
    data class Year(val year: Int) : ShareTarget

    data class Line(val date: LocalDate) : ShareTarget
}

/**
 * The card, at the size it will be posted, and two ways out of the app with it. Free forever: what
 * is shared is a drawing of the diary, never the diary (SPEC 5).
 */
@Composable
fun ShareScreen(target: ShareTarget, today: LocalDate, onClose: () -> Unit) {
    val journal = LineRepository.journal
    val cover = Cover.of(LineRepository.settings.cover).color
    val literata = Styles.literata
    val measurer = rememberTextMeasurer()
    var saved by remember { mutableStateOf<String?>(null) }

    val card = remember(target, journal, cover) {
        when (target) {
            is ShareTarget.Year -> renderYearCard(journal, target.year, today, cover, literata, measurer)
            is ShareTarget.Line ->
                renderLineCard(target.date, journal[target.date.isoKey()]?.text.orEmpty(), cover, literata, measurer)
        }
    }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = MAX_CONTENT_WIDTH).fillMaxWidth().padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                GlyphButton(Glyph.CLOSE, S.a11yClose, onClose)
            }
            Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                Image(
                    bitmap = card,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.widthIn(max = 320.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium),
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                OutlinedAction(S.share, onClick = { Sharing.sharePng(card.encodeToPng()) })
                if (Sharing.canSaveToPhotos) {
                    OutlinedAction(
                        S.saveToPhotos,
                        onClick = {
                            Sharing.savePngToPhotos(card.encodeToPng()) { ok ->
                                saved = if (ok) S.saved else S.saveFailed
                            }
                        },
                    )
                }
            }
        }
    }

    saved?.let { Ask(null, it, S.ok, onConfirm = { saved = null }) }
}
