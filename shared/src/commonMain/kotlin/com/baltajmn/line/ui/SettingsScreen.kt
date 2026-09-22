package com.baltajmn.line.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.baltajmn.line.data.AppInfo
import com.baltajmn.line.data.Backup
import com.baltajmn.line.data.abandonImport
import com.baltajmn.line.data.FilePicker
import com.baltajmn.line.data.ImportFailed
import com.baltajmn.line.data.ImportProblem
import com.baltajmn.line.data.MergeResult
import com.baltajmn.line.data.PickResult
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.PRIVACY_URL
import com.baltajmn.line.data.Reminder
import com.baltajmn.line.data.merge
import com.baltajmn.line.data.readBackup
import com.baltajmn.line.data.startExport
import com.baltajmn.line.data.today
import com.baltajmn.line.data.SIBLINGS
import com.baltajmn.line.data.storeUrl
import com.baltajmn.line.i18n.S
import com.baltajmn.line.ui.theme.Cover
import com.baltajmn.line.ui.theme.MAX_CONTENT_WIDTH
import com.baltajmn.line.ui.theme.Styles
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
    // Title and text together: an export that fails is not an import that fails (pantallas 9.3).
    var failure by remember { mutableStateOf<Pair<String?, String>?>(null) }
    var imported by remember { mutableStateOf<Int?>(null) }

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = MAX_CONTENT_WIDTH).fillMaxWidth().padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                GlyphButton(Glyph.BACK, S.a11yBack, onBack, Modifier.padding(end = 8.dp))
                Text(S.settingsTitle, style = Styles.title)
            }

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

            Section(S.sectionPrivacy) {
                // ponytail: turning it on asks to authenticate first from #18; here it only stores.
                SettingRow(title = S.lockRow, subtitle = S.lockSubtitle) {
                    SoftSwitch(settings.lockOn) { on ->
                        LineRepository.updateSettings { it.copy(lockOn = on) }
                        // The lock decides whether a reminder may quote the diary, so the window
                        // is rebuilt without it.
                        Reminder.sync(askPermission = false)
                    }
                }
            }

            Section(S.sectionCover) {
                Covers(settings.cover, settings.pro)
                if (!settings.pro) {
                    Text(S.coverProHint, style = Styles.secondary, modifier = Modifier.padding(top = 12.dp))
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
                                    onSuccess = { pending = merge(LineRepository.journal, it.journal) to it.photos },
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
            }

            Section(S.sectionPro) {
                // ponytail: the paywall and the restore are #23.
                SettingRow(
                    title = S.proRow,
                    subtitle = if (settings.pro) S.proOwned else S.proSubtitle,
                    enabled = !settings.pro,
                )
                SettingRow(title = S.restoreRow)
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

    pending?.let { (result, delivered) ->
        Ask(
            title = S.importTitle,
            text = S.importSummary(result.added, result.joined, result.same),
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

@Composable
private fun Section(label: String, content: @Composable () -> Unit) {
    Spacer(Modifier.height(32.dp))
    Text(label.uppercase(), style = Styles.eyebrow)
    Spacer(Modifier.height(12.dp))
    content()
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
            .heightIn(min = 56.dp)
            .then(if (onClick != null && enabled) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .alpha(if (enabled) 1f else 0.4f),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
            Text(title, style = Styles.body)
            if (subtitle != null) Text(subtitle, style = Styles.secondary)
        }
        trailing?.invoke()
    }
}

@Composable
private fun SoftSwitch(checked: Boolean, onChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

/** The eight pastels of the family. Sage is free; the rest come with Pro (docs/tecnico.md 6.17). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Covers(selected: String, pro: Boolean) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Cover.entries.forEach { cover ->
            val chosen = cover.id == selected
            Box(
                Modifier.size(32.dp)
                    .clip(CircleShape)
                    .background(cover.color)
                    // ponytail: without Pro a locked cover opens the ProDialog of #23; for now it stays put.
                    .clickable(role = Role.Button, enabled = pro || cover == Cover.SAGE) {
                        LineRepository.updateSettings { it.copy(cover = cover.id) }
                    }
                    .semantics {
                        contentDescription = S.coverName(cover.id) + if (chosen) ", ${S.a11ySelected}" else ""
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (chosen) GlyphIcon(Glyph.CHECK, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            }
        }
    }
}

private fun importText(e: Throwable): String = when ((e as? ImportFailed)?.problem) {
    ImportProblem.NotBackup -> S.importNotBackup
    ImportProblem.TooNew -> S.importTooNew
    ImportProblem.Empty -> S.importEmpty
    ImportProblem.IsMoodTraker -> S.importIsMoodTraker
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
