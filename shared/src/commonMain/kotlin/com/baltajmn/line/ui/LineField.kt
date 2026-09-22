package com.baltajmn.line.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.baltajmn.line.model.limitEdit
import com.baltajmn.line.ui.theme.Styles

/**
 * The line of one day. The 280 limit lives here, not in the store: an imported text over it can be
 * shortened but not grown (docs/tecnico.md 6.4). Done closes the keyboard, it never adds a line.
 */
@Composable
fun LineField(
    text: String,
    onChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Styles.userLarge,
) {
    var value by remember { mutableStateOf(TextFieldValue(text, TextRange(text.length))) }
    var sent by remember { mutableStateOf(text) }
    // Someone else changed the text under the field (an import, a delete). A blank line typed here
    // is stored as no entry, so an empty text after it is not a change.
    LaunchedEffect(text) {
        if (text != sent && !(text.isEmpty() && sent.isBlank())) {
            sent = text
            value = TextFieldValue(text, TextRange(text.length))
        }
    }
    val focus = LocalFocusManager.current
    BasicTextField(
        value = value,
        onValueChange = { new ->
            val edit = limitEdit(value.text, new.text, new.selection.end)
            value = if (edit.text == new.text) new else TextFieldValue(edit.text, TextRange(edit.cursor))
            if (edit.text != sent) {
                sent = edit.text
                onChange(edit.text)
            }
        },
        modifier = modifier.heightIn(min = 96.dp),
        textStyle = style,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
        decorationBox = { inner ->
            Box {
                if (value.text.isEmpty()) {
                    Text(placeholder, style = style.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                inner()
            }
        },
    )
}
