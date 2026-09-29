package com.baltajmn.line.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The glyphs the app needs, drawn rather than typed: iOS renders characters like a gear or an
 * angle quote as colour emoji and Android as plain text, so typed glyphs ship two icon sets.
 * Coordinates are fractions of the side (docs/pantallas.md 2).
 */
enum class Glyph { BACK, FORWARD, SHARE, SETTINGS, YEAR, CLOSE, PHOTO, TRASH, SEARCH, CHECK, LOCK }

/**
 * The stitch of the icon, and of everything that counts days: a capsule turned a few degrees, one
 * way on one row and the other way on the next, like the purl side of a knit (pantallas 1.5).
 */
const val STITCH_TILT = 8f

/** Row 0 turns anticlockwise, as the top row of the icon does. */
fun stitchTilt(row: Int): Float = if (row % 2 == 0) -STITCH_TILT else STITCH_TILT

fun DrawScope.stitch(center: Offset, width: Float, height: Float, tilt: Float, color: Color, style: DrawStyle = Fill) =
    rotate(tilt, center) {
        drawRoundRect(
            color,
            topLeft = Offset(center.x - width / 2, center.y - height / 2),
            size = Size(width, height),
            cornerRadius = CornerRadius(height / 2),
            style = style,
        )
    }

/**
 * Rows of stitches as the icon draws them (pantallas 13): each row turns its own way, and a null is
 * a stitch not yet made, drawn as an outline.
 */
@Composable
fun Swatch(rows: List<List<Color?>>, stitchWidth: Dp, modifier: Modifier = Modifier) {
    val empty = MaterialTheme.colorScheme.onSurfaceVariant
    val columns = rows.maxOf { it.size }
    val gap = stitchWidth * 0.18f
    val step = stitchWidth * 0.91f
    Canvas(modifier.size(stitchWidth * columns + gap * (columns - 1), step * rows.size)) {
        val width = stitchWidth.toPx()
        val height = width * 0.545f
        rows.forEachIndexed { r, row ->
            row.forEachIndexed { c, color ->
                val center = Offset(c * (width + gap.toPx()) + width / 2, (r + 0.5f) * step.toPx())
                if (color != null) {
                    stitch(center, width, height, stitchTilt(r), color)
                } else {
                    stitch(center, width, height, stitchTilt(r), empty, Stroke(1.5.dp.toPx()))
                }
            }
        }
    }
}

/** One stitch on its own, 20 by 12: the mark of a year, of a line, of a Pro perk. */
@Composable
fun StitchMark(color: Color, modifier: Modifier = Modifier, tilt: Float = -STITCH_TILT, filled: Boolean = true) {
    Canvas(modifier.size(20.dp, 12.dp)) {
        val width = size.width * 0.88f
        val style = if (filled) Fill else Stroke(1.2.dp.toPx())
        stitch(center, width, width * 0.52f, tilt, color, style)
    }
}

/** A 48dp tap target with no background: the glyph is the whole control. */
@Composable
fun GlyphButton(glyph: Glyph, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(48.dp)
            .clip(CircleShape)
            .semantics { contentDescription = label }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { GlyphIcon(glyph) }
}

@Composable
fun GlyphIcon(glyph: Glyph, size: Dp = 20.dp, tint: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Canvas(Modifier.size(size)) {
        val side = this.size.width
        val stroke = Stroke(width = side * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun at(x: Float, y: Float) = Offset(x * side, y * side)
        fun line(vararg points: Pair<Float, Float>) = drawPath(
            Path().apply {
                points.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x * side, y * side) else lineTo(x * side, y * side) }
            },
            tint,
            style = stroke,
        )
        // Filled: a stroked ring this small is just the line crossing itself.
        fun dot(x: Float, y: Float, r: Float) = drawCircle(tint, radius = r * side, center = at(x, y))

        when (glyph) {
            Glyph.BACK -> line(0.62f to 0.18f, 0.34f to 0.50f, 0.62f to 0.82f)
            Glyph.FORWARD -> line(0.40f to 0.18f, 0.68f to 0.50f, 0.40f to 0.82f)
            Glyph.SHARE -> {
                line(0.50f to 0.88f, 0.50f to 0.16f)
                line(0.26f to 0.40f, 0.50f to 0.16f, 0.74f to 0.40f)
            }
            // Two sliders, not a gear: a gear at 20dp is a smudge.
            Glyph.SETTINGS -> listOf(0.34f to 0.66f, 0.62f to 0.38f).forEach { (y, knob) ->
                line(0.14f to y, 0.86f to y)
                dot(knob, y, 0.11f)
            }
            // A swatch of the year grid: three rows of two stitches.
            Glyph.YEAR -> listOf(0.26f, 0.50f, 0.74f).forEachIndexed { row, y ->
                listOf(0.31f, 0.69f).forEach { x -> stitch(at(x, y), 0.34f * side, 0.17f * side, stitchTilt(row), tint) }
            }
            Glyph.CLOSE -> {
                line(0.24f to 0.24f, 0.76f to 0.76f)
                line(0.76f to 0.24f, 0.24f to 0.76f)
            }
            Glyph.PHOTO -> {
                drawRoundRect(
                    tint,
                    topLeft = at(0.14f, 0.24f),
                    size = Size(0.72f * side, 0.56f * side),
                    cornerRadius = CornerRadius(0.08f * side),
                    style = stroke,
                )
                dot(0.36f, 0.43f, 0.07f)
                line(0.14f to 0.72f, 0.40f to 0.52f, 0.58f to 0.66f, 0.70f to 0.57f, 0.86f to 0.70f)
            }
            Glyph.TRASH -> {
                line(0.18f to 0.28f, 0.82f to 0.28f)
                line(0.40f to 0.28f, 0.40f to 0.18f, 0.60f to 0.18f, 0.60f to 0.28f)
                line(0.26f to 0.28f, 0.31f to 0.84f, 0.69f to 0.84f, 0.74f to 0.28f)
            }
            Glyph.SEARCH -> {
                drawCircle(tint, radius = 0.24f * side, center = at(0.44f, 0.44f), style = stroke)
                line(0.62f to 0.62f, 0.84f to 0.84f)
            }
            Glyph.CHECK -> line(0.22f to 0.52f, 0.42f to 0.72f, 0.78f to 0.30f)
            Glyph.LOCK -> {
                drawRoundRect(
                    tint,
                    topLeft = at(0.22f, 0.44f),
                    size = Size(0.56f * side, 0.42f * side),
                    cornerRadius = CornerRadius(0.08f * side),
                    style = stroke,
                )
                drawPath(
                    Path().apply {
                        moveTo(0.34f * side, 0.44f * side)
                        lineTo(0.34f * side, 0.32f * side)
                        arcTo(Rect(0.34f * side, 0.16f * side, 0.66f * side, 0.48f * side), 180f, 180f, false)
                        lineTo(0.66f * side, 0.44f * side)
                    },
                    tint,
                    style = stroke,
                )
            }
        }
    }
}
