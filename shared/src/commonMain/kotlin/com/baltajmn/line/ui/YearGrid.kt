package com.baltajmn.line.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.layout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.clampCodePoints
import com.baltajmn.line.model.codePointCount
import com.baltajmn.line.model.isoKey
import com.baltajmn.line.ui.theme.Cover
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
private val BUBBLE_MAX = 240.dp
const val PREVIEW_CODE_POINTS = 60

/**
 * The year at a glance: written or not, and nothing else. No colour scale and no red, because a
 * blank day is not a failure. Painted in one Canvas; the taps and the screen reader ride on top,
 * one node per day that can be opened (pantallas 5, 14).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YearGrid(
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

    // The day under a long press. One interaction source for every cell: whichever is let go, the
    // bubble goes, which is all "until release" needs (pantallas 6.2).
    var peek by remember(year) { mutableStateOf<LocalDate?>(null) }
    val presses = remember { MutableInteractionSource() }
    LaunchedEffect(presses) {
        presses.interactions.collect { if (it is PressInteraction.Release || it is PressInteraction.Cancel) peek = null }
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
                        .combinedClickable(
                            interactionSource = presses,
                            indication = LocalIndication.current,
                            role = Role.Button,
                            // Only a written day has words to show; a blank one just stays put.
                            onLongClick = { if (date.isoKey() in journal) peek = date },
                            onClick = { onOpenDay(date) },
                        )
                        .semantics { contentDescription = S.a11yDay(date, date.isoKey() in journal) },
                )
            }
        }

        peek?.let { date ->
            val entry = journal[date.isoKey()] ?: return@let
            val gap = 8.dp
            Bubble(
                date,
                entry.text,
                Modifier.layout { measurable, constraints ->
                    val bubble = measurable.measure(constraints.copy(minWidth = 0, maxWidth = minOf(constraints.maxWidth, BUBBLE_MAX.roundToPx())))
                    layout(bubble.width, bubble.height) {
                        val cellLeft = x(date.month.ordinal + 1).roundToPx()
                        val left = (cellLeft + cell.roundToPx() / 2 - bubble.width / 2).coerceIn(0, constraints.maxWidth - bubble.width)
                        val above = y(date.day).roundToPx() - gap.roundToPx() - bubble.height
                        // Above the cell, where the finger does not hide it; below only in the first rows.
                        val top = if (above >= 0) above else (y(date.day) + cell + gap).roundToPx()
                        bubble.place(left, top)
                    }
                },
            )
        }
    }
}

@Composable
private fun Bubble(date: LocalDate, text: String, modifier: Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier.background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(12.dp),
    ) {
        Text(S.shortDate(date).uppercase(), style = Styles.eyebrow)
        if (text.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            val cut = text.clampCodePoints(PREVIEW_CODE_POINTS)
            Text(if (cut.codePointCount() < text.codePointCount()) "$cut..." else cut, style = Styles.userSmall)
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
