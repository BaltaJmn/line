package com.baltajmn.line.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.baltajmn.line.book.BookWriter
import com.baltajmn.line.book.PDF_MIME
import com.baltajmn.line.book.bookDays
import com.baltajmn.line.book.bookName
import com.baltajmn.line.book.makeBook
import com.baltajmn.line.data.AppInfo
import com.baltajmn.line.data.Backup
import com.baltajmn.line.data.abandonImport
import com.baltajmn.line.data.mergeMood
import com.baltajmn.line.data.readMoodBackup
import com.baltajmn.line.data.FilePicker
import com.baltajmn.line.data.ImportFailed
import com.baltajmn.line.data.ImportProblem
import com.baltajmn.line.data.MergeResult
import com.baltajmn.line.data.PickResult
import com.baltajmn.line.billing.Billing
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.Lock
import com.baltajmn.line.data.PRIVACY_URL
import com.baltajmn.line.data.Reminder
import com.baltajmn.line.data.merge
import com.baltajmn.line.data.readBackup
import com.baltajmn.line.data.startExport
import com.baltajmn.line.data.today
import com.baltajmn.line.data.SIBLINGS
import com.baltajmn.line.data.storeUrl
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.Journal
import com.baltajmn.line.ui.theme.Cover
import com.baltajmn.line.ui.theme.MAX_CONTENT_WIDTH
import com.baltajmn.line.ui.theme.OnCover
import com.baltajmn.line.ui.theme.Styles
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * The third screen. It only gathers the switches: each one is wired in the issue that owns it, so
 * the rows that still lead nowhere are the ones whose feature has not landed yet.
 */
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settings = LineRepository.settings
    var pickTime by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var applying by remember { mutableStateOf(false) }
    var pending by remember { mutableStateOf<Pair<MergeResult, Set<String>>?>(null) }
    // The pending import came from MoodTraker: same dialog, its own sentence.
    var fromMood by remember { mutableStateOf(false) }
    // Title and text together: an export that fails is not an import that fails (pantallas 9.3).
    var failure by remember { mutableStateOf<Pair<String?, String>?>(null) }
    var imported by remember { mutableStateOf<Int?>(null) }
    var restoring by remember { mutableStateOf(false) }
    var restored by remember { mutableStateOf<String?>(null) }
    // Days laid out and days in all while the book is being made; null the rest of the time.
    var making by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var book by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = MAX_CONTENT_WIDTH).fillMaxWidth().padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                GlyphButton(Glyph.BACK, S.a11yBack, onBack, Modifier.offset(x = (-12).dp))
            }
            Text(S.settingsTitle, style = Styles.heading, modifier = Modifier.semantics { heading() })

            Section(S.sectionReminder) {
                SettingRow(
                    title = S.reminderRow,
                    subtitle = if (settings.reminderOn) {
                        S.reminderAt(settings.reminderHour, settings.reminderMinute)
                    } else {
                        S.reminderOff
                    },
                    onClick = if (settings.reminderOn) ({ pickTime = true }) else null,
                ) {
                    SoftSwitch(settings.reminderOn) { on ->
                        LineRepository.updateSettings { it.copy(reminderOn = on) }
                        // Only here is the permission asked: turning it on is the one moment the
                        // question makes sense.
                        Reminder.sync(askPermission = on)
                    }
                }
            }

            Section(S.sectionWriting) {
                SettingRow(title = S.questionsRow) {
                    SoftSwitch(settings.questionsOn) { on -> LineRepository.updateSettings { it.copy(questionsOn = on) } }
                }
            }

            Section(S.sectionPrivacy) {
                var canLock by remember { mutableStateOf(Lock.isAvailable()) }
                // The subtitle sends them off to set a screen lock: coming back has to find it.
                LifecycleEventEffect(Lifecycle.Event.ON_START) { canLock = Lock.isAvailable() }
                SettingRow(
                    title = S.lockRow,
                    subtitle = if (canLock) S.lockSubtitle else S.lockUnavailable,
                    enabled = canLock,
                ) {
                    SoftSwitch(settings.lockOn, enabled = canLock) { on ->
                        // Turning it on proves who is asking: otherwise whoever has the phone in
                        // their hand could lock the owner out of their own diary.
                        val store = {
                            LineRepository.updateSettings { it.copy(lockOn = on) }
                            // The lock decides whether a reminder may quote the diary, so the window
                            // is rebuilt without it.
                            Reminder.sync(askPermission = false)
                        }
                        if (on) Lock.authenticate { if (it) store() } else store()
                    }
                }
            }

            Section(S.sectionCover) {
                Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(16.dp)) {
                    Covers(settings.cover, settings.pro)
                    if (!settings.pro) {
                        Text(S.coverProHint, style = Styles.secondary, modifier = Modifier.padding(top = 12.dp))
                    }
                }
            }

            Section(S.sectionBackup) {
                val empty = LineRepository.journal.isEmpty()
                SettingRow(
                    title = S.exportRow,
                    subtitle = if (empty) {
                        S.exportNothing
                    } else {
                        settings.lastBackup?.let { S.lastBackup(LocalDate.parse(it)) } ?: S.lastBackupNever
                    },
                    enabled = !empty && FilePicker.available && !busy,
                    onClick = {
                        startExport(today()) { result ->
                            // pantallas 9.3: the export failure has no dialog of its own, only the line and ok.
                            if (result == PickResult.Failed) failure = null to S.exportFailed
                        }
                    },
                )
                SettingRow(
                    title = S.importRow,
                    subtitle = S.importSubtitle,
                    enabled = FilePicker.available && !busy,
                    onClick = {
                        busy = true
                        var read: Result<Backup>? = null
                        FilePicker.importFile({ source -> read = runCatching { readBackup(source) } }) { result ->
                            busy = false
                            val answer = read
                            when {
                                result == PickResult.Cancelled -> abandonImport()
                                answer == null -> {
                                    abandonImport()
                                    failure = S.importFailedTitle to S.importDamaged
                                }
                                else -> answer.fold(
                                    onSuccess = {
                                        fromMood = false
                                        pending = merge(LineRepository.journal, it.journal) to it.photos
                                    },
                                    onFailure = {
                                        // The photos it had already parked go with the refusal.
                                        abandonImport()
                                        failure = S.importFailedTitle to importText(it)
                                    },
                                )
                            }
                        }
                    },
                )
                SettingRow(
                    title = S.importMoodRow,
                    enabled = FilePicker.available && !busy,
                    onClick = {
                        busy = true
                        var read: Result<Journal>? = null
                        FilePicker.importFile({ source -> read = runCatching { readMoodBackup(source) } }) { result ->
                            busy = false
                            val answer = read
                            when {
                                result == PickResult.Cancelled -> Unit
                                answer == null -> failure = S.importFailedTitle to S.importDamaged
                                else -> answer.fold(
                                    onSuccess = { notes ->
                                        val merged = mergeMood(LineRepository.journal, notes)
                                        // Nothing new is not a question: it is the "already up to date" answer.
                                        if (merged.added == 0) {
                                            imported = 0
                                        } else {
                                            fromMood = true
                                            pending = merged to emptySet()
                                        }
                                    },
                                    onFailure = { failure = S.importFailedTitle to importText(it) },
                                )
                            }
                        }
                    },
                )
                SettingRow(
                    title = S.bookRow,
                    subtitle = S.bookSubtitle,
                    enabled = !empty && FilePicker.available && !busy && making == null,
                    onClick = {
                        if (!settings.pro) {
                            Paywall.open = true
                        } else {
                            val journal = LineRepository.journal
                            making = 0 to bookDays(journal).size
                            book = scope.launch {
                                val made = try {
                                    makeBook(journal, Cover.of(settings.cover).color) { n, total -> making = n to total }
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    false
                                } finally {
                                    // Here and not on the button: the drawing thread may still report
                                    // one last page after a cancel, and this runs only once it is done.
                                    making = null
                                }
                                if (!made) {
                                    failure = null to S.bookFailed
                                } else {
                                    // Made first, then asked where: the picker never waits on a book.
                                    FilePicker.exportFile(bookName(today()), PDF_MIME, { sink -> BookWriter.deliver(sink) }) { result ->
                                        BookWriter.discard()
                                        if (result == PickResult.Failed) failure = null to S.bookFailed
                                    }
                                }
                            }
                        }
                    },
                )
            }

            Section(S.sectionPro) {
                SettingRow(
                    title = S.proRow,
                    subtitle = if (settings.pro) S.proOwned else S.proSubtitle,
                    enabled = !settings.pro,
                    onClick = { Paywall.open = true },
                )
                // Both stores ask for this to be reachable without buying anything first.
                SettingRow(
                    title = S.restoreRow,
                    enabled = !restoring,
                    onClick = {
                        restoring = true
                        scope.launch {
                            val found = Billing.restore()
                            restoring = false
                            restored = if (found) S.restoreDone else S.restoreNothing
                        }
                    },
                )
            }

            val siblings = SIBLINGS.filter { it.storeUrl != null }
            if (siblings.isNotEmpty()) {
                Section(S.sectionMoreApps) {
                    siblings.forEach { app ->
                        SettingRow(app.name, app.tagline, onClick = { AppInfo.open(app.storeUrl!!) })
                    }
                }
            }

            Section(S.sectionAbout) {
                SettingRow(title = S.privacyRow, onClick = { AppInfo.open(PRIVACY_URL) })
                SettingRow(title = S.version(AppInfo.version))
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    restored?.let { Ask(null, it, S.ok, onConfirm = { restored = null }) }

    making?.let { (n, total) ->
        // Stopping is the only answer while the book is made, so closing the dialog stops it too.
        Ask(null, S.bookMaking(n, total), S.cancel, onConfirm = { book?.cancel() })
    }

    pending?.let { (result, delivered) ->
        Ask(
            title = S.importTitle,
            text = if (fromMood) S.importMoodCount(result.added, result.same) else S.importSummary(result.added, result.joined, result.same),
            // The button says so while the photos are copied and the diary rewritten (pantallas 9.2).
            confirm = if (applying) S.working else S.importAction,
            onConfirm = {
                if (!applying) {
                    applying = true
                    LineRepository.applyImport(result, delivered) {
                        applying = false
                        imported = result.added + result.joined
                        pending = null
                    }
                }
            },
            onDismiss = if (applying) {
                null
            } else {
                {
                    abandonImport()
                    pending = null
                }
            },
        )
    }

    failure?.let { (title, text) ->
        Ask(title = title, text = text, confirm = S.ok, onConfirm = { failure = null })
    }

    imported?.let { n ->
        Ask(title = S.importTitle, text = S.importDone(n), confirm = S.ok, onConfirm = { imported = null })
    }

    if (pickTime) {
        TimeDialog(settings.reminderHour, settings.reminderMinute, onDismiss = { pickTime = false }) { h, m ->
            LineRepository.updateSettings { it.copy(reminderHour = h, reminderMinute = m) }
            Reminder.sync(askPermission = false)
            pickTime = false
        }
    }
}

/**
 * A label and a card of rows. The rows sit 1 apart on the divider colour, which is what draws the
 * lines between them.
 */
@Composable
private fun Section(label: String, content: @Composable () -> Unit) {
    Spacer(Modifier.height(28.dp))
    Text(label.uppercase(), style = Styles.eyebrow, modifier = Modifier.padding(start = 4.dp))
    Spacer(Modifier.height(10.dp))
    Column(
        Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.outlineVariant),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) { content() }
}

/** A row of the list: title, subtitle and whatever sits at the end. The whole row answers. */
@Composable
private fun SettingRow(
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .heightIn(min = 60.dp)
            .then(if (onClick != null && enabled) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Faded, not hidden: the card keeps its colour and only what is written in it steps back.
        Column(Modifier.weight(1f).padding(vertical = 10.dp).alpha(if (enabled) 1f else 0.4f)) {
            Text(title, style = Styles.body)
            if (subtitle != null) Text(subtitle, style = Styles.secondary)
        }
        trailing?.invoke()
    }
}

@Composable
private fun SoftSwitch(checked: Boolean, enabled: Boolean = true, onChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onChange,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

/**
 * The eight pastels of the family, as the notebooks they colour: two rows of four, the spine on the
 * left. Sage is free; the rest carry a padlock until Pro (docs/tecnico.md 6.17).
 */
@Composable
private fun Covers(selected: String, pro: Boolean) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val gap = 12.dp
        val width = minOf(72.dp, (maxWidth - gap * 3) / 4)
        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            Cover.entries.chunked(4).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    row.forEach { cover ->
                        Notebook(cover, chosen = cover.id == selected, locked = !pro && cover != Cover.SAGE, width = width)
                    }
                }
            }
        }
    }
}

@Composable
private fun Notebook(cover: Cover, chosen: Boolean, locked: Boolean, width: Dp) {
    val shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 10.dp, bottomEnd = 10.dp)
    Box(
        Modifier.size(width, width * 4 / 3)
            .clip(shape)
            .background(cover.color)
            .border(2.dp, if (chosen) MaterialTheme.colorScheme.onBackground else Color.Transparent, shape)
            // A locked cover sells Pro instead of changing anything (docs/tecnico.md 6.17).
            .clickable(role = Role.Button) {
                if (locked) {
                    Paywall.open = true
                } else {
                    LineRepository.updateSettings { it.copy(cover = cover.id) }
                }
            }
            .semantics {
                contentDescription = S.coverName(cover.id) + if (chosen) ", ${S.a11ySelected}" else ""
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.align(Alignment.CenterStart).fillMaxHeight().width(7.dp).background(OnCover.copy(alpha = 0.08f)))
        when {
            chosen -> GlyphIcon(Glyph.CHECK, size = 22.dp, tint = OnCover)
            locked -> GlyphIcon(Glyph.LOCK, size = 18.dp, tint = OnCover.copy(alpha = 0.5f))
        }
    }
}

private fun importText(e: Throwable): String = when ((e as? ImportFailed)?.problem) {
    ImportProblem.NotBackup -> S.importNotBackup
    ImportProblem.TooNew -> S.importTooNew
    ImportProblem.Empty -> S.importEmpty
    ImportProblem.IsMoodTraker -> S.importIsMoodTraker
    ImportProblem.MoodNotBackup -> S.importMoodNotBackup
    // A file that broke while being read is damaged as far as the user is concerned.
    ImportProblem.Damaged, null -> S.importDamaged
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeDialog(hour: Int, minute: Int, onDismiss: () -> Unit, onPick: (Int, Int) -> Unit) {
    val state = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = true)
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { TimePicker(state) },
        confirmButton = { TextAction(S.ok, onClick = { onPick(state.hour, state.minute) }) },
        dismissButton = { TextAction(S.cancel, onClick = onDismiss) },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}
