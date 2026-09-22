package com.baltajmn.line.share

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.baltajmn.line.i18n.S
import com.baltajmn.line.model.Journal
import com.baltajmn.line.model.isoKey
import com.baltajmn.line.model.longestStreak
import kotlinx.datetime.LocalDate

/**
 * The two cards, at the same 1080x1350 as the sisters. Always in light: a card is read in someone
 * else's timeline, not in this app, so it does not follow the phone's theme.
 *
 * The year card carries shape only. The line card carries one line, and only because the person
 * opened that day and asked for it (SPEC 5).
 */
private const val CARD_W = 1080
private const val CARD_H = 1350

private val Cream = Color(0xFFFBF8F3)
private val Ink = Color(0xFF39352E)
private val Muted = Color(0xFF8B8479)
private val Empty = Color(0xFFEDE7DC)

fun renderYearCard(
    journal: Journal,
    year: Int,
    today: LocalDate,
    cover: Color,
    literata: FontFamily,
    measurer: TextMeasurer,
): ImageBitmap = card { scope ->
    scope.text(measurer, year.toString(), TextStyle(fontFamily = literata, fontSize = 140.sp, color = Ink), 108f, 250f)
    val written = journal.keys.count { it.startsWith("$year-") }
    scope.text(measurer, S.cardLines(written), TextStyle(fontSize = 42.sp, color = Muted), 108f, 330f)
    scope.text(
        measurer,
        S.cardLongestStreak(longestStreak(journal, year)),
        TextStyle(fontSize = 42.sp, color = Muted),
        108f,
        390f,
    )
    scope.grid(journal, year, today, cover)
    scope.footer(measurer, literata, cover)
}

fun renderLineCard(
    date: LocalDate,
    text: String,
    cover: Color,
    literata: FontFamily,
    measurer: TextMeasurer,
): ImageBitmap = card { scope ->
    scope.drawCircle(cover, radius = 18f, center = Offset(126f, 150f))
    scope.text(
        measurer,
        S.longDateWithYear(date).uppercase(),
        TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Medium, color = Muted, letterSpacing = 4.sp),
        168f,
        162f,
    )
    scope.line(measurer, text, literata)
    scope.footer(measurer, literata, cover)
}

/** Density 1: a size in sp is a size in px, which is what the fixed layout of the card is written in. */
private fun card(draw: (DrawScope) -> Unit): ImageBitmap {
    val bitmap = ImageBitmap(CARD_W, CARD_H)
    CanvasDrawScope().draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(bitmap),
        size = Size(CARD_W.toFloat(), CARD_H.toFloat()),
    ) {
        drawRect(Cream)
        draw(this)
    }
    return bitmap
}

/** The positions of docs/pantallas.md 10 are baselines, which is where the eye lines text up. */
private fun DrawScope.text(
    measurer: TextMeasurer,
    value: String,
    style: TextStyle,
    x: Float,
    baseline: Float,
): TextLayoutResult {
    val laid = measurer.measure(value, style)
    drawText(laid, topLeft = Offset(x, baseline - laid.firstBaseline))
    return laid
}

private const val CELL = 22f
private const val GAP = 6f
private const val GRID_X = 109f
private const val GRID_Y = 520f

/** Twelve rows of thirty-one: the year as a block, without the month initials of the screen. */
private fun DrawScope.grid(journal: Journal, year: Int, today: LocalDate, cover: Color) {
    val radius = CornerRadius(5f)
    val box = Size(CELL, CELL)
    for (month in 1..12) {
        for (day in 1..31) {
            val date = runCatching { LocalDate(year, month, day) }.getOrNull() ?: continue
            val at = Offset(GRID_X + (CELL + GAP) * (day - 1), GRID_Y + (CELL + GAP) * (month - 1))
            val color = when {
                date.isoKey() in journal -> cover
                date > today -> Empty.copy(alpha = 0.5f)
                else -> Empty
            }
            drawRoundRect(color, at, box, radius)
        }
    }
}

/** Starts at 64 and comes down in fours until it fits; at 36 it stops coming down and cuts. */
private fun DrawScope.line(measurer: TextMeasurer, text: String, literata: FontFamily) {
    val width = 864
    val height = 820
    var size = 64
    var laid = measurer.measure(text, lineStyle(literata, size), constraints = Constraints(maxWidth = width))
    while (laid.size.height > height && size > 36) {
        size -= 4
        laid = measurer.measure(text, lineStyle(literata, size), constraints = Constraints(maxWidth = width))
    }
    if (laid.size.height > height) {
        laid = measurer.measure(
            text,
            lineStyle(literata, size),
            overflow = TextOverflow.Ellipsis,
            constraints = Constraints(maxWidth = width, maxHeight = height),
        )
    }
    drawText(laid, topLeft = Offset(108f, 260f))
}

private fun lineStyle(literata: FontFamily, size: Int) =
    TextStyle(fontFamily = literata, fontSize = size.sp, lineHeight = (size * 1.4f).sp, color = Ink)

/** The same on both cards: the name, what the app is, and the cover as a dot. */
private fun DrawScope.footer(measurer: TextMeasurer, literata: FontFamily, cover: Color) {
    text(measurer, "Purl", TextStyle(fontFamily = literata, fontSize = 52.sp, color = Ink), 108f, 1210f)
    text(measurer, S.cardTagline, TextStyle(fontSize = 34.sp, color = Muted), 108f, 1262f)
    drawCircle(cover, radius = 18f, center = Offset(954f, 1196f))
}
