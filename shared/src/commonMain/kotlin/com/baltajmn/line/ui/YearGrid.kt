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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.DpSize
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

/** A stitch is half as tall as it is wide, and the rows sit closer than the columns, as in a knit. */
private const val STITCH_HEIGHT = 0.5f
private const val ROW_STEP = 0.8f

/**
 * The year at a glance, as a swatch: one stitch per day, filled when written, and nothing else. No
 * colour scale and no red, because a blank day is a stitch not yet made, not a failure. Painted in
 * one Canvas; the taps and the screen reader ride on top, one node per day that can be opened
 * (pantallas 5, 14).
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
        val row = cell * ROW_STEP
        fun x(month: Int) = GUTTER + step * (month - 1)
        fun y(day: Int) = head + row * (day - 1)

        Canvas(Modifier.fillMaxWidth().height(head + row * ROWS)) {
            val width = cell.toPx()
            val height = width * STITCH_HEIGHT
            val rowPx = row.toPx()
            val line = Stroke(1.dp.toPx())
            initials.forEachIndexed { i, label ->
                val laid = measurer.measure(label, eyebrow)
                drawText(
                    laid,
                    topLeft = Offset(x(i + 1).toPx() + (width - laid.size.width) / 2, head.toPx() - laid.size.height - 4.dp.toPx()),
                )
            }
            listOf(1, 10, 20, 30).forEach { day ->
                val laid = measurer.measure(day.toString(), eyebrow)
                drawText(
                    laid,
                    topLeft = Offset(GUTTER.toPx() - 4.dp.toPx() - laid.size.width, y(day).toPx() + (rowPx - laid.size.height) / 2),
                )
            }
            for (month in 1..12) {
                for (day in 1..monthDays[month - 1]) {
                    val date = LocalDate(year, month, day)
                    val center = Offset(x(month).toPx() + width / 2, y(day).toPx() + rowPx / 2)
                    val tilt = stitchTilt(day - 1)
                    when {
                        date > today -> stitch(center, width, height, tilt, outline.copy(alpha = 0.4f), line)
                        date.isoKey() in journal -> {
                            val dim = matching != null && date.isoKey() !in matching
                            stitch(center, width, height, tilt, cover.copy(alpha = if (dim) 0.25f else 1f))
                        }
                        else -> stitch(center, width, height, tilt, outline, line)
                    }
                    if (date == today) ringToday(center, width, height, tilt, ring)
                }
            }
        }

        // The days touch each other, so the 48 dp that small targets are stretched to would make
        // each one cover half of the one above: a finger is fine, but the screen reader, reading
        // what is under the finger, would name the day below. Here a day is exactly its box.
        val base = LocalViewConfiguration.current
        val exact = remember(base) {
            object : ViewConfiguration by base {
                override val minimumTouchTargetSize = DpSize.Zero
            }
        }
        CompositionLocalProvider(LocalViewConfiguration provides exact) {
            // Future days and days that do not exist are not nodes, so they get no box at all.
            for (month in 1..12) {
                for (day in 1..monthDays[month - 1]) {
                    val date = LocalDate(year, month, day)
                    if (date > today) continue
                    Box(
                        Modifier.offset { IntOffset(x(month).roundToPx(), y(day).roundToPx()) }
                            .size(cell, row)
                            .clickable(role = Role.Button) { onOpenDay(date) }
                            .semantics { contentDescription = S.a11yDay(date, date.isoKey() in journal) },
                    )
                }
            }
        }
    }
}

/** Today: a ring 2.5 out from its stitch, turned with it. */
private fun DrawScope.ringToday(center: Offset, width: Float, height: Float, tilt: Float, color: Color) {
    val out = 2.5.dp.toPx() * 2
    stitch(center, width + out, height + out, tilt, color, Stroke(1.5.dp.toPx()))
}
