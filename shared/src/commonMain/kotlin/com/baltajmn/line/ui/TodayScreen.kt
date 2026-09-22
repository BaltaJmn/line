package com.baltajmn.line.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.COUNTER_FROM
import com.baltajmn.line.model.LINE_LIMIT
import com.baltajmn.line.model.STREAK_SHOWN_FROM
import com.baltajmn.line.model.codePointCount
import com.baltajmn.line.model.isoKey
import com.baltajmn.line.model.streak
import com.baltajmn.line.ui.theme.Cover
import com.baltajmn.line.ui.theme.MAX_CONTENT_WIDTH
import com.baltajmn.line.ui.theme.Styles
import kotlinx.datetime.LocalDate

@Composable
fun TodayScreen(today: LocalDate, onYear: () -> Unit, onSettings: () -> Unit) {
    val journal = LineRepository.journal
    val settings = LineRepository.settings
    val text = journal[today.isoKey()]?.text.orEmpty()
    var editing by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = MAX_CONTENT_WIDTH).fillMaxWidth().padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(Cover.of(settings.cover).color))
                Spacer(Modifier.width(8.dp))
                Text(S.longDate(today), style = Styles.dateLine, modifier = Modifier.weight(1f))
                GlyphButton(Glyph.YEAR, S.a11yYear, onYear)
                GlyphButton(Glyph.SETTINGS, S.a11ySettings, onSettings)
            }

            // A new day is a new field: its cursor and its focus do not carry over from yesterday.
            key(today) {
                val focus = remember { FocusRequester() }
                val keyboard = LocalSoftwareKeyboardController.current
                // Unwritten, zero taps before writing. Written, it opens reading.
                LaunchedEffect(Unit) {
                    if (text.isEmpty()) {
                        focus.requestFocus()
                        keyboard?.show()
                    }
                }
                Spacer(Modifier.height(16.dp))
                LineField(
                    text = text,
                    onChange = { LineRepository.setText(today, it, today) },
                    placeholder = S.todayPlaceholder,
                    modifier = Modifier.fillMaxWidth().focusRequester(focus).onFocusChanged { editing = it.isFocused },
                )
            }

            if (journal.isEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(S.firstHelp, style = Styles.secondary)
            }

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
                val days = streak(journal, today)
                if (days >= STREAK_SHOWN_FROM) Text(S.streakDays(days), style = Styles.light)
                Spacer(Modifier.weight(1f))
                val count = text.codePointCount()
                if (count >= COUNTER_FROM) Text(S.counter(count, LINE_LIMIT), style = Styles.light)
            }

            TodayNotice(editing)
            Spacer(Modifier.height(32.dp))
        }
    }
}

/** One notice at a time, by priority: corrupt, save failed, reminder offer (pantallas 4.3). */
@Composable
private fun TodayNotice(editing: Boolean) {
    val settings = LineRepository.settings
    when {
        LineRepository.corrupt -> Notice(S.noticeCorrupt, S.ok to LineRepository::dismissCorrupt)
        LineRepository.saveFailed -> Notice(S.noticeSaveFailed)
        // After the first line, not while it is being typed.
        !settings.reminderOffered && LineRepository.journal.isNotEmpty() && !editing -> Notice(
            S.offerReminder(settings.reminderHour, settings.reminderMinute),
            S.notNow to { LineRepository.updateSettings { it.copy(reminderOffered = true) } },
            S.yes to { LineRepository.updateSettings { it.copy(reminderOffered = true, reminderOn = true) } },
        )
    }
}

@Composable
private fun Notice(message: String, vararg actions: Pair<String, () -> Unit>) {
    Spacer(Modifier.height(24.dp))
    Column(
        Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
    ) {
        Text(message, style = Styles.body)
        if (actions.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                actions.forEach { (label, onClick) -> TextAction(label, onClick) }
            }
        }
    }
}

/** A text button: 40 high, 16 of side padding, no background. */
@Composable
fun TextAction(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.heightIn(min = 40.dp).clip(MaterialTheme.shapes.small).clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, style = Styles.action) }
}
