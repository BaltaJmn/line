package com.baltajmn.line.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.baltajmn.line.data.WidgetState
import com.baltajmn.line.data.readWidgetState
import com.baltajmn.line.data.today
import com.baltajmn.line.data.widgetView
import com.baltajmn.line.i18n.S
import com.baltajmn.line.ui.theme.Cover
import com.baltajmn.line.ui.theme.Dark
import com.baltajmn.line.ui.theme.Light
import kotlin.math.min
import kotlinx.datetime.LocalDate

class YearWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = YearWidget()
}

/**
 * Pro. The year as written or not written, and nothing else: no text of the diary reaches this
 * widget either, only the 366 characters of widget.json (docs/tecnico.md 4.2).
 */
class YearWidget : GlanceAppWidget() {

    // Exact and not Responsive: the grid is a bitmap and it is painted for the size it gets.
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = readWidgetState()?.let { widgetView(it, today()) }
        provideContent { Year(state) }
    }
}

@androidx.compose.runtime.Composable
private fun Year(state: WidgetState?) {
    val context = LocalContext.current
    val day = state?.date?.let(LocalDate::parse) ?: today()
    val year = state?.year ?: day.year
    val pro = state?.pro == true
    val night = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
        Configuration.UI_MODE_NIGHT_YES
    val scheme = if (night) Dark else Light
    val written = state?.days?.count { it == '1' } ?: 0
    // The widget lives in the app's own package, which is the only Activity it may name. Without
    // Pro it opens the paywall: a locked grid that opened the year screen would explain nothing.
    val open = Intent()
        .setComponent(ComponentName(context.packageName, "com.baltajmn.line.MainActivity"))
        .putExtra("screen", if (pro) "year" else "pro")

    val density = context.resources.displayMetrics.density
    val size = LocalSize.current
    val gridWidth = ((size.width.value - 16) * density).toInt()
    val gridHeight = ((size.height.value - 16 - 20) * density).toInt()

    Column(
        modifier = GlanceModifier.fillMaxSize().background(ColorProvider(scheme.background)).padding(8.dp)
            .clickable(actionStartActivity(open)),
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                year.toString(),
                style = TextStyle(
                    color = ColorProvider(scheme.onBackground),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            Spacer(GlanceModifier.defaultWeight())
            if (pro) {
                Text(
                    S.yearCount(written, year),
                    style = TextStyle(color = ColorProvider(scheme.onSurfaceVariant), fontSize = 12.sp),
                )
            }
        }
        Spacer(GlanceModifier.height(6.dp))
        Box(modifier = GlanceModifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                ImageProvider(
                    yearBitmap(
                        width = gridWidth,
                        height = gridHeight,
                        days = state?.days.takeIf { pro },
                        year = year,
                        today = day,
                        cover = Cover.of(state?.cover).color.toArgb(),
                        outline = scheme.outline.toArgb(),
                    ),
                ),
                contentDescription = null,
            )
            if (!pro) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        S.proTitle,
                        style = TextStyle(
                            color = ColorProvider(scheme.onBackground),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                    Text(
                        S.widgetUnlock,
                        style = TextStyle(color = ColorProvider(scheme.onSurfaceVariant), fontSize = 12.sp),
                    )
                }
            }
        }
    }
}

private const val MONTHS = 12
private const val DAYS = 31

/**
 * Twelve rows of thirty-one, like the share card: the widget is wider than it is tall. A null
 * [days] paints the grid empty, which is what a locked widget shows.
 *
 * The cells are square, so the grid keeps its 31:12 shape and the bitmap is only as big as the
 * grid, never as big as the widget: a tall widget centres it and pays nothing for the space.
 */
private fun yearBitmap(
    width: Int,
    height: Int,
    days: String?,
    year: Int,
    today: LocalDate,
    cover: Int,
    outline: Int,
): Bitmap {
    val w = width.coerceAtLeast(DAYS)
    val h = height.coerceAtLeast(MONTHS)
    val gap = 2f
    val side = min((w - (DAYS - 1) * gap) / DAYS, (h - (MONTHS - 1) * gap) / MONTHS)
    val step = side + gap
    val bitmap = Bitmap.createBitmap(
        (step * DAYS - gap).toInt().coerceAtLeast(DAYS),
        (step * MONTHS - gap).toInt().coerceAtLeast(MONTHS),
        Bitmap.Config.ARGB_8888,
    )
    val canvas = Canvas(bitmap)
    val left = 0f
    val top = 0f
    val radius = side / 5

    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = cover }
    val empty = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = outline
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }
    val ahead = Paint(empty).apply { alpha = 100 }

    for (month in 1..MONTHS) {
        for (day in 1..DAYS) {
            val date = runCatching { LocalDate(year, month, day) }.getOrNull() ?: continue
            val x = left + step * (day - 1)
            val y = top + step * (month - 1)
            val box = RectF(x, y, x + side, y + side)
            when {
                days?.getOrNull(date.dayOfYear - 1) == '1' -> canvas.drawRoundRect(box, radius, radius, fill)
                date > today -> canvas.drawRoundRect(box, radius, radius, ahead)
                else -> canvas.drawRoundRect(box, radius, radius, empty)
            }
        }
    }
    return bitmap
}
