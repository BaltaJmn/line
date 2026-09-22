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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.search
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.isoKey
import com.baltajmn.line.ui.theme.MAX_CONTENT_WIDTH
import com.baltajmn.line.ui.theme.Styles
import kotlinx.datetime.LocalDate

@Composable
fun YearScreen(today: LocalDate, onBack: () -> Unit, onOpenDay: (LocalDate) -> Unit) {
    val journal = LineRepository.journal
    var year by remember { mutableStateOf(today.year) }
    var query by remember { mutableStateOf("") }
    val firstYear = remember(journal) { journal.keys.minOrNull()?.take(4)?.toInt() ?: today.year }
    val results = search(journal, query)
    val matching = if (query.isBlank()) null else results.mapTo(mutableSetOf()) { it.first.isoKey() }

    Column(
        Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = MAX_CONTENT_WIDTH).fillMaxWidth().padding(horizontal = 24.dp)) {
            Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                GlyphButton(Glyph.BACK, S.a11yBack, onBack)
            }

            Row(
                Modifier.fillMaxWidth().height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                YearArrow(Glyph.BACK, S.a11yPreviousYear, year > firstYear) { year-- }
                Text(year.toString(), style = Styles.title, modifier = Modifier.padding(horizontal = 16.dp))
                YearArrow(Glyph.FORWARD, S.a11yNextYear, year < today.year) { year++ }
            }

            Spacer(Modifier.height(16.dp))
            SearchField(query) { query = it }

            Spacer(Modifier.height(24.dp))
            YearGrid(year, journal, today, matching, onOpenDay)

            Spacer(Modifier.height(16.dp))
            val lines = journal.keys.count { it.startsWith("$year-") }
            Text(
                if (journal.isEmpty()) S.yearEmpty else S.yearCount(lines, year),
                style = Styles.secondary,
            )

            if (query.isNotBlank()) {
                Spacer(Modifier.height(24.dp))
                if (results.isEmpty()) {
                    Text(S.noResults, style = Styles.secondary)
                } else {
                    Text(S.resultsCount(results.size).uppercase(), style = Styles.eyebrow)
                    results.forEach { (date, entry) ->
                        Column(
                            Modifier.fillMaxWidth()
                                .padding(top = 16.dp)
                                .clickable(role = Role.Button) { onOpenDay(date) }
                                .semantics { contentDescription = S.a11yOpenDay },
                        ) {
                            Text(S.longDateWithYear(date).uppercase(), style = Styles.eyebrow)
                            Spacer(Modifier.height(4.dp))
                            Text(entry.text, style = Styles.userSmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

/** At the ends of the diary the arrow stays, faded and deaf: the year does not wrap around. */
@Composable
private fun YearArrow(glyph: Glyph, label: String, enabled: Boolean, onClick: () -> Unit) {
    if (enabled) {
        GlyphButton(glyph, label, onClick)
    } else {
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            GlyphIcon(glyph, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
        }
    }
}

@Composable
private fun SearchField(query: String, onChange: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .height(48.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlyphIcon(Glyph.SEARCH)
        BasicTextField(
            value = query,
            onValueChange = onChange,
            modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
            textStyle = Styles.body,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { inner ->
                Box {
                    if (query.isEmpty()) {
                        Text(S.searchPlaceholder, style = Styles.body.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                    inner()
                }
            },
        )
        if (query.isNotEmpty()) {
            Box(
                Modifier.size(24.dp).clickable(role = Role.Button) { onChange("") }
                    .semantics { contentDescription = S.a11yClose },
                contentAlignment = Alignment.Center,
            ) { GlyphIcon(Glyph.CLOSE) }
        }
    }
}

