package com.baltajmn.line.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.Route
import com.baltajmn.line.data.PickResult
import com.baltajmn.line.data.Reminder
import com.baltajmn.line.data.startExport
import com.baltajmn.line.i18n.QUESTION_COUNT
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.COUNTER_FROM
import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.LINE_LIMIT
import com.baltajmn.line.model.Milestone
import com.baltajmn.line.model.STREAK_SHOWN_FROM
import com.baltajmn.line.model.codePointCount
import com.baltajmn.line.model.dayNumber
import com.baltajmn.line.model.echoes
import com.baltajmn.line.model.isoKey
import com.baltajmn.line.model.milestone
import com.baltajmn.line.model.nextReturn
import com.baltajmn.line.model.pastYears
import com.baltajmn.line.model.streak
import com.baltajmn.line.ui.theme.Cover
import com.baltajmn.line.ui.theme.MAX_CONTENT_WIDTH
import com.baltajmn.line.ui.theme.Styles
import com.baltajmn.line.ui.theme.coverWash
import kotlinx.datetime.LocalDate

@Composable
fun TodayScreen(today: LocalDate, onYear: () -> Unit, onSettings: () -> Unit, onOpenDay: (LocalDate) -> Unit) {
    val journal = LineRepository.journal
    val settings = LineRepository.settings
    val cover = Cover.of(settings.cover).color
    val entry = journal[today.isoKey()]
    val text = entry?.text.orEmpty()
    var editing by remember { mutableStateOf(false) }
    var exportFailed by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = MAX_CONTENT_WIDTH).fillMaxWidth().padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                DayHeader(today, S.monthNames()[today.month.ordinal], S.longDate(today), Modifier.weight(1f))
                YearPill(today.year, onYear)
                GlyphButton(Glyph.SETTINGS, S.a11ySettings, onSettings)
            }

            milestone(journal, today)?.let {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StitchMark(cover)
                    Spacer(Modifier.width(10.dp))
                    Text(milestoneText(it), style = Styles.body)
                }
            }

            Spacer(Modifier.height(20.dp))
            // A new day is a new field: its cursor and its focus do not carry over from yesterday.
            key(today) {
                val focus = remember { FocusRequester() }
                val keyboard = LocalSoftwareKeyboardController.current
                // Unwritten, zero taps before writing. Written, it opens reading, unless the tile asked.
                LaunchedEffect(Route.focusToday) {
                    if (text.isEmpty() || Route.focusToday) {
                        focus.requestFocus()
                        keyboard?.show()
                    }
                    Route.focusToday = false
                }
                Page(cover, editing) {
                    LineField(
                        text = text,
                        onChange = { LineRepository.setText(today, it, today) },
                        placeholder = if (settings.questionsOn) {
                            S.question(today.toEpochDays().mod(QUESTION_COUNT))
                        } else {
                            S.todayPlaceholder
                        },
                        modifier = Modifier.fillMaxWidth().focusRequester(focus).onFocusChanged { editing = it.isFocused },
                    )
                    entry?.photo?.let {
                        Spacer(Modifier.height(12.dp))
                        DayPhoto(it) { onOpenDay(today) }
                    }
                    TagRow(today, entry)
                    Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                        // While typing the count matters and the streak does not; there is no room for both.
                        val count = text.codePointCount()
                        val days = streak(journal, today)
                        when {
                            editing && count >= COUNTER_FROM -> Text(S.counter(count, LINE_LIMIT), style = Styles.secondary)
                            !editing && days >= STREAK_SHOWN_FROM -> Text(S.streakDays(days), style = Styles.secondary)
                        }
                        Spacer(Modifier.weight(1f))
                        if (entry?.photo == null) PhotoButton(today, today)
                    }
                }
            }

            if (journal.isEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(S.firstHelp, style = Styles.secondary)
            }

            TodayNotice(editing, today) { exportFailed = true }
            Memories(journal, today, cover, onOpenDay)
            Spacer(Modifier.height(32.dp))
        }
    }

    // pantallas 9.3: a backup that could not be saved says so, wherever it was asked for.
    if (exportFailed) Ask(null, S.exportFailed, S.ok, onConfirm = { exportFailed = false })
}

/**
 * The head of a page: the day of the month in Literata, which is the one thing every memory below
 * shares, and the weekday and the month beside it. Read aloud as the whole date.
 */
@Composable
fun DayHeader(date: LocalDate, month: String, spoken: String, modifier: Modifier = Modifier) {
    Row(
        modifier.semantics(mergeDescendants = true) {
            contentDescription = spoken
            heading()
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(date.day.toString(), style = Styles.display)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(S.weekdayNames()[date.dayOfWeek.ordinal].uppercase(), style = Styles.eyebrow)
            Text(month.replaceFirstChar { it.uppercase() }, style = Styles.title)
        }
    }
}

/** The year, named: a swatch and the number say where the grid is better than a glyph alone. */
@Composable
private fun YearPill(year: Int, onClick: () -> Unit) {
    Row(
        Modifier.minimumInteractiveComponentSize()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) { contentDescription = S.a11yYear }
            .padding(start = 12.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlyphIcon(Glyph.YEAR, size = 18.dp)
        Spacer(Modifier.width(6.dp))
        Text(year.toString(), style = Styles.action.copy(color = MaterialTheme.colorScheme.onBackground))
    }
}

/**
 * The page of one day: the field on the cover's colour, so the place to write is never in doubt.
 * Its edge takes the cover while the keyboard is up.
 */
@Composable
fun Page(cover: Color, editing: Boolean, content: @Composable ColumnScope.() -> Unit) {
    val shape = MaterialTheme.shapes.large
    Column(
        Modifier.fillMaxWidth()
            .clip(shape)
            .background(coverWash(cover))
            .border(1.5.dp, if (editing) cover else Color.Transparent, shape)
            .padding(start = 20.dp, end = 12.dp, top = 18.dp),
        content = content,
    )
}

/**
 * The echo of the past. Past years if the page has any; while it has none, the first-year bridge:
 * a week and a month back, and the date this page will first be remembered. Nothing invented.
 * Each memory hangs from a stitch, and the stitches turn one way and the other like the rows of a
 * knit: one row per year.
 */
@Composable
private fun Memories(journal: Journal, today: LocalDate, cover: Color, onOpenDay: (LocalDate) -> Unit) {
    val past = pastYears(journal, today)
    if (past.isEmpty() && journal.isEmpty()) return
    Spacer(Modifier.height(36.dp))
    past.forEachIndexed { i, (date, entry) ->
        if (i > 0) Spacer(Modifier.height(28.dp))
        Memory(date.year.toString(), S.yearsAgo(today.year - date.year), entry.text, entry.photo, cover, i) { onOpenDay(date) }
    }
    if (past.isNotEmpty()) return

    val echo = echoes(journal, today)
    echo?.week?.let { (date, entry) ->
        Memory(S.echoWeek, S.shortDate(date), entry.text, entry.photo, cover, 0) { onOpenDay(date) }
        Spacer(Modifier.height(28.dp))
    }
    echo?.month?.let { (date, entry) ->
        Memory(S.echoMonth, S.shortDate(date), entry.text, entry.photo, cover, 1) { onOpenDay(date) }
        Spacer(Modifier.height(28.dp))
    }
    // The stitch this page is still missing: empty until the day comes back.
    Row(Modifier.semantics(mergeDescendants = true) {}) {
        StitchMark(MaterialTheme.colorScheme.onSurfaceVariant, Modifier.padding(top = 4.dp), filled = false)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(S.returnsOn(nextReturn(today)), style = Styles.secondary)
            Text(S.dayNumber(dayNumber(journal, today)), style = Styles.secondary)
        }
    }
}

/**
 * When it was, how long ago, and the line, whole: a memory is never cut short. The line hangs under
 * its stitch so the column of stitches reads as the rows of one knit.
 */
@Composable
private fun Memory(title: String, detail: String, text: String, photo: String?, cover: Color, row: Int, onOpen: () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(role = Role.Button, onClick = onOpen)
            .semantics(mergeDescendants = true) { contentDescription = "$title, $detail. $text. ${S.a11yOpenDay}" },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StitchMark(cover, tilt = stitchTilt(row))
            Spacer(Modifier.width(10.dp))
            Text(title, style = Styles.action.copy(color = MaterialTheme.colorScheme.onBackground))
            Spacer(Modifier.width(8.dp))
            Text(detail, style = Styles.secondary)
        }
        Spacer(Modifier.height(6.dp))
        Column(Modifier.padding(start = 30.dp)) {
            Text(text, style = Styles.userMedium)
            photo?.let {
                Spacer(Modifier.height(12.dp))
                DayPhoto(it)
            }
        }
    }
}

private fun milestoneText(m: Milestone): String = when (m) {
    Milestone.FirstLine -> S.milestoneFirst
    Milestone.Thirty -> S.milestoneThirty
    Milestone.Hundred -> S.milestoneHundred
    Milestone.Anniversary -> S.milestoneAnniversary
    Milestone.ThreeYears -> S.milestoneThreeYears
}

/** One notice at a time, by priority: corrupt, save failed, reminder offer (pantallas 4.3). */
@Composable
private fun TodayNotice(editing: Boolean, today: LocalDate, onExportFailed: () -> Unit) {
    val settings = LineRepository.settings
    when {
        LineRepository.corrupt -> Notice(S.noticeCorrupt, S.ok to LineRepository::dismissCorrupt)
        LineRepository.saveFailed -> Notice(S.noticeSaveFailed)
        // After the first line, not while it is being typed.
        !settings.reminderOffered && LineRepository.journal.isNotEmpty() && !editing -> Notice(
            S.offerReminder(settings.reminderHour, settings.reminderMinute),
            S.notNow to { LineRepository.updateSettings { it.copy(reminderOffered = true) } },
            S.yes to {
                LineRepository.updateSettings { it.copy(reminderOffered = true, reminderOn = true) }
                Reminder.sync(askPermission = true)
            },
        )
        // Asked once. Waving it away counts as answered: a second nag is not a better backup.
        LineRepository.needsBackupNotice(today) && !editing -> Notice(
            S.noticeBackup,
            S.notNow to { LineRepository.updateSettings { it.copy(backupNoticeDone = true) } },
            S.makeBackup to {
                startExport(today) { result -> if (result == PickResult.Failed) onExportFailed() }
            },
        )
    }
}

/** The last action is the one the notice is for, and the only one in ink. */
@Composable
private fun Notice(message: String, vararg actions: Pair<String, () -> Unit>) {
    Spacer(Modifier.height(16.dp))
    Column(
        Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(20.dp),
    ) {
        Text(message, style = Styles.body)
        if (actions.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actions.forEachIndexed { i, (label, onClick) ->
                    if (i == actions.lastIndex) PillAction(label, onClick) else TextAction(label, onClick)
                }
            }
        }
    }
}

/** One dialog shape for the app: an optional title, a line of text and at most two actions. */
@Composable
fun Ask(
    title: String?,
    text: String,
    confirm: String,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss ?: onConfirm,
        title = title?.let { { Text(it, style = Styles.title) } },
        text = { Text(text, style = Styles.body) },
        confirmButton = { TextAction(confirm, onClick = onConfirm) },
        dismissButton = onDismiss?.let { { TextAction(S.cancel, onClick = it) } },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

/**
 * The action a place is for: a pill in ink, 48 high (docs/pantallas.md 1.3). [tonal] is its
 * quieter second, on `surfaceVariant`.
 */
@Composable
fun PillAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tonal: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier.heightIn(min = 48.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(CircleShape)
            .background(if (tonal) colors.surfaceVariant else colors.onBackground)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = Styles.action.copy(color = if (tonal) colors.onBackground else colors.background), maxLines = 1)
    }
}

/** A text button: 40 high, 16 of side padding, no background. */
@Composable
fun TextAction(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Box(
        modifier.heightIn(min = 40.dp).clip(MaterialTheme.shapes.small)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        val style = Styles.action
        Text(label, style = if (enabled) style else style.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}
