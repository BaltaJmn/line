package com.baltajmn.line.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.search
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.isoKey
import com.baltajmn.line.ui.theme.Cover
import com.baltajmn.line.ui.theme.MAX_CONTENT_WIDTH
import com.baltajmn.line.ui.theme.Styles
import kotlin.math.min
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

private val GUTTER = 20.dp
private val GAP = 4.dp
private val CELL_MAX = 24.dp
private const val ROWS = 31

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

/**
 * The year at a glance: written or not, and nothing else. No colour scale and no red, because a
 * blank day is not a failure. Painted in one Canvas; the taps and the screen reader ride on top,
 * one node per day that can be opened (pantallas 5, 14).
 */
@Composable
private fun YearGrid(
    year: Int,
    journal: Journal,
    today: LocalDate,
    matching: Set<String>?,
    onOpenDay: (LocalDate) -> Unit,
) {
    val cover = Cover.of(LineRepository.settings.cover).color
    val outline = MaterialTheme.colorScheme.outline
    val ring = MaterialTheme.colorScheme.onBackground
    val measurer = rememberTextMeasurer()
    val eyebrow = Styles.eyebrow
    val initials = remember { S.monthInitials() }
    val monthDays = remember(year) {
        (1..12).map { m -> LocalDate(year, m, 1).plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day }
    }

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val cell = min(CELL_MAX.value, (maxWidth.value - GUTTER.value - 11 * GAP.value) / 12).dp
        val head = 20.dp
        val step = cell + GAP
        fun x(month: Int) = GUTTER + step * (month - 1)
        fun y(day: Int) = head + step * (day - 1)

        Canvas(Modifier.fillMaxWidth().height(head + cell * ROWS + GAP * (ROWS - 1))) {
            val side = cell.toPx()
            val radius = CornerRadius(4.dp.toPx())
            initials.forEachIndexed { i, label ->
                val laid = measurer.measure(label, eyebrow)
                drawText(
                    laid,
                    topLeft = Offset(x(i + 1).toPx() + (side - laid.size.width) / 2, head.toPx() - laid.size.height - 4.dp.toPx()),
                )
            }
            listOf(1, 10, 20, 30).forEach { day ->
                val laid = measurer.measure(day.toString(), eyebrow)
                drawText(
                    laid,
                    topLeft = Offset(GUTTER.toPx() - 4.dp.toPx() - laid.size.width, y(day).toPx() + (side - laid.size.height) / 2),
                )
            }
            for (month in 1..12) {
                for (day in 1..monthDays[month - 1]) {
                    val date = LocalDate(year, month, day)
                    val at = Offset(x(month).toPx(), y(day).toPx())
                    val box = Size(side, side)
                    when {
                        date > today -> drawRoundRect(outline.copy(alpha = 0.4f), at, box, radius, Stroke(1.dp.toPx()))
                        date.isoKey() in journal -> {
                            val dim = matching != null && date.isoKey() !in matching
                            drawRoundRect(cover.copy(alpha = if (dim) 0.25f else 1f), at, box, radius)
                        }
                        else -> drawRoundRect(outline, at, box, radius, Stroke(1.dp.toPx()))
                    }
                    if (date == today) ringToday(at, side, ring)
                }
            }
        }

        // Future days and days that do not exist are not nodes, so they get no box at all.
        for (month in 1..12) {
            for (day in 1..monthDays[month - 1]) {
                val date = LocalDate(year, month, day)
                if (date > today) continue
                Box(
                    Modifier.offset { IntOffset(x(month).roundToPx(), y(day).roundToPx()) }
                        .size(cell)
                        .clickable(role = Role.Button) { onOpenDay(date) }
                        .semantics { contentDescription = S.a11yDay(date, date.isoKey() in journal) },
                )
            }
        }
    }
}

private fun DrawScope.ringToday(at: Offset, side: Float, color: Color) {
    val out = 2.dp.toPx()
    drawRoundRect(
        color,
        Offset(at.x - out, at.y - out),
        Size(side + out * 2, side + out * 2),
        CornerRadius(6.dp.toPx()),
        Stroke(1.5.dp.toPx()),
    )
}
