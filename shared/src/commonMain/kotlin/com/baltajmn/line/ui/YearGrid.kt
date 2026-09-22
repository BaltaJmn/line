package com.baltajmn.line.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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

/**
 * The year at a glance: written or not, and nothing else. No colour scale and no red, because a
 * blank day is not a failure. Painted in one Canvas; the taps and the screen reader ride on top,
 * one node per day that can be opened (pantallas 5, 14).
 */
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
