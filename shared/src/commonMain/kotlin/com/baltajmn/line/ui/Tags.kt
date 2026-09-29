package com.baltajmn.line.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.LineEntry
import com.baltajmn.line.model.TAGS_PER_ENTRY
import com.baltajmn.line.model.addTag
import com.baltajmn.line.model.tagSuggestions
import com.baltajmn.line.ui.theme.Styles
import kotlinx.datetime.LocalDate

/**
 * The tags of a day (docs/pantallas.md 15.2). Only on a day that has an entry: a tag describes a
 * line, it does not make a day written. Tapping a tag takes it off; the suggestions are the ones
 * the diary uses most, so after a month tagging costs a tap and no typing.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagRow(date: LocalDate, entry: LineEntry?) {
    entry ?: return
    var typing by remember(date) { mutableStateOf(false) }
    var draft by remember(date) { mutableStateOf("") }
    val tags = entry.tags

    fun add(raw: String) {
        LineRepository.setTags(date, addTag(tags, raw))
        draft = ""
    }

    Spacer(Modifier.height(12.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tags.forEach { tag ->
            Chip("#$tag", Modifier.semantics { contentDescription = S.a11yRemoveTag(tag) }, quiet = false) {
                LineRepository.setTags(date, tags - tag)
            }
        }
        if (tags.size < TAGS_PER_ENTRY) {
            if (typing) {
                TagField(
                    draft,
                    onChange = { draft = it },
                    onDone = {
                        if (draft.isBlank()) typing = false else add(draft)
                    },
                    onLeave = {
                        // Leaving the field keeps what was typed: nothing written is thrown away.
                        if (draft.isNotBlank()) add(draft)
                        typing = false
                    },
                )
            } else {
                Chip(S.addTag) { typing = true }
            }
        }
    }

    if (typing && tags.size < TAGS_PER_ENTRY) {
        val suggestions = remember(tags, LineRepository.journal) { tagSuggestions(LineRepository.journal, tags) }
        if (suggestions.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            FlowRow(
                Modifier.alpha(0.6f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                suggestions.forEach { tag -> Chip("#$tag") { add(tag) } }
            }
        }
    }
}

private val ChipShape = RoundedCornerShape(16.dp)

/**
 * A tag on the page: paper on the cover's colour, like the photo button, with no border to draw.
 * A tag reads in ink; the add chip and the suggestions stay [quiet].
 */
@Composable
private fun Chip(label: String, modifier: Modifier = Modifier, quiet: Boolean = true, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier
            .heightIn(min = 32.dp)
            .clip(ChipShape)
            .background(colors.background)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = Styles.secondary.copy(color = if (quiet) colors.onSurfaceVariant else colors.onBackground))
    }
}

@Composable
private fun TagField(text: String, onChange: (String) -> Unit, onDone: () -> Unit, onLeave: () -> Unit) {
    val focus = remember { FocusRequester() }
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { focus.requestFocus() }
    Box(
        Modifier.heightIn(min = 32.dp).widthIn(min = 96.dp)
            .clip(ChipShape)
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.primary, ChipShape)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = text,
            onValueChange = { onChange(it.replace("\n", "")) },
            singleLine = true,
            textStyle = Styles.body.copy(fontSize = 13.sp),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier.widthIn(min = 72.dp, max = 200.dp).focusRequester(focus).onFocusChanged {
                if (focused && !it.isFocused) onLeave()
                focused = it.isFocused
            },
        )
        if (text.isEmpty()) Text("#", style = Styles.secondary.copy(fontSize = 13.sp))
    }
}
