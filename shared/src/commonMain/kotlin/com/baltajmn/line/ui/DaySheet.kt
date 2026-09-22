package com.baltajmn.line.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.PhotoPicker
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.COUNTER_FROM
import com.baltajmn.line.model.LINE_LIMIT
import com.baltajmn.line.model.codePointCount
import com.baltajmn.line.model.isoKey
import com.baltajmn.line.ui.theme.Cover
import com.baltajmn.line.ui.theme.MAX_CONTENT_WIDTH
import com.baltajmn.line.ui.theme.Styles
import kotlinx.datetime.LocalDate

/**
 * One day, opened from anywhere. Any past day can be written, however old: the diary is not a
 * streak machine, and a day filled in later is marked late rather than refused (pantallas 6).
 */
@Composable
fun DaySheet(date: LocalDate, today: LocalDate, onClose: () -> Unit, onShare: (LocalDate) -> Unit) {
    val journal = LineRepository.journal
    val settings = LineRepository.settings
    val entry = journal[date.isoKey()]
    val text = entry?.text.orEmpty()
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = MAX_CONTENT_WIDTH).fillMaxWidth().padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                GlyphButton(Glyph.CLOSE, S.a11yClose, onClose)
                Spacer(Modifier.weight(1f))
                // The card of a line is only offered for a day that has one.
                if (text.isNotBlank()) GlyphButton(Glyph.SHARE, S.a11yShare, { onShare(date) })
                if (date.isoKey() in journal) {
                    GlyphButton(Glyph.TRASH, S.a11yDelete, { confirmDelete = true })
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(Cover.of(settings.cover).color))
                Spacer(Modifier.width(8.dp))
                Text(S.longDateWithYear(date), style = Styles.dateLine)
            }

            val focus = remember { FocusRequester() }
            val keyboard = LocalSoftwareKeyboardController.current
            LaunchedEffect(Unit) {
                if (text.isEmpty()) {
                    focus.requestFocus()
                    keyboard?.show()
                }
            }
            Spacer(Modifier.height(16.dp))
            LineField(
                text = text,
                onChange = { LineRepository.setText(date, it, today) },
                placeholder = S.todayPlaceholder,
                modifier = Modifier.fillMaxWidth().focusRequester(focus),
            )

            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth().height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                if (entry?.photo == null) PhotoButton(date, today)
                val count = text.codePointCount()
                if (count >= COUNTER_FROM) {
                    Spacer(Modifier.width(8.dp))
                    Text(S.counter(count, LINE_LIMIT), style = Styles.light)
                }
            }

            entry?.photo?.let {
                DayPhoto(it)
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    TextAction(
                        S.changePhoto,
                        onClick = {
                            PhotoPicker.pick { bytes -> bytes?.let { b -> LineRepository.setPhoto(date, b, today) } }
                        },
                    )
                    TextAction(S.removePhoto, onClick = { LineRepository.setPhoto(date, null, today) })
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(S.deleteTitle, style = Styles.title) },
            text = { Text(S.deleteText, style = Styles.body) },
            confirmButton = {
                TextAction(
                    S.delete,
                    onClick = {
                        confirmDelete = false
                        LineRepository.delete(date)
                        onClose()
                    },
                )
            },
            dismissButton = { TextAction(S.cancel, onClick = { confirmDelete = false }) },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}
