package com.baltajmn.line.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
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
import kotlinx.datetime.LocalDate

class TodayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = TodayWidget()
}

/**
 * Free, and it only ever knows what widget.json says: the date, whether the day is written and
 * whether there is a memory. There is no path from here to the diary.
 */
class TodayWidget : GlanceAppWidget() {

    // Two shapes and no more: the smallest cell the launcher allows, and anything taller. A half
    // painted line of text is worse than one line less.
    override val sizeMode = SizeMode.Responsive(setOf(DpSize(110.dp, 110.dp), DpSize(110.dp, 150.dp)))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // The file may be days old if the app has not run: widgetView is what makes it right anyway.
        val state = readWidgetState()?.let { widgetView(it, today()) }
        provideContent { Today(state) }
    }
}

@androidx.compose.runtime.Composable
private fun Today(state: WidgetState?) {
    val context = LocalContext.current
    val date = state?.date?.let(LocalDate::parse) ?: today()
    val cover = Cover.of(state?.cover).color.toArgb()
    // Glance 1.1.1 has no day/night ColorProvider and a bitmap could not follow one anyway, so the
    // scheme is read from the host's own configuration and everything is painted from it.
    val night = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
        Configuration.UI_MODE_NIGHT_YES
    val scheme = if (night) Dark else Light
    val background = ColorProvider(scheme.background)
    val onBackground = ColorProvider(scheme.onBackground)
    val muted = ColorProvider(scheme.onSurfaceVariant)
    val outline = scheme.outline.toArgb()
    // The widget lives in the app's own package, which is the only Activity it may name.
    val open = Intent()
        .setComponent(ComponentName(context.packageName, "com.baltajmn.line.MainActivity"))
        .putExtra("screen", "today")

    Column(
        // 8 and not 16: from Android 12 the host already insets the widget, and the two paddings
        // together left the shortest label wrapping inside a 2x2.
        modifier = GlanceModifier.fillMaxSize().background(background).padding(8.dp)
            .clickable(actionStartActivity(open)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            S.widgetDate(date),
            style = TextStyle(color = muted, fontSize = 12.sp, fontWeight = FontWeight.Medium),
        )
        Spacer(GlanceModifier.height(8.dp))
        // Glance has no border and no canvas of its own, so the circle is drawn into a bitmap.
        Image(ImageProvider(circle(28, cover, outline, filled = state?.written == true)), null)
        Spacer(GlanceModifier.height(6.dp))
        Text(
            if (state?.written == true) S.widgetWritten else S.widgetNotWritten,
            style = TextStyle(color = onBackground, fontSize = 15.sp, fontWeight = FontWeight.Medium),
        )
        if (state?.memory == true && LocalSize.current.height >= 150.dp) {
            Spacer(GlanceModifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(ImageProvider(circle(6, cover, outline, filled = true)), null)
                Spacer(GlanceModifier.width(6.dp))
                // English needs two lines at 2x2; without the weight the Row would cut it at one.
                Text(
                    S.widgetMemory,
                    maxLines = 2,
                    style = TextStyle(color = muted, fontSize = 12.sp),
                    modifier = GlanceModifier.defaultWeight(),
                )
            }
        }
    }
}

/** Filled when the day is written, a ring of 2 when it is not. Sizes are in dp, drawn at 3x. */
private fun circle(dp: Int, color: Int, outline: Int, filled: Boolean): Bitmap {
    val size = dp * 3
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
    val canvas = Canvas(bitmap)
    if (filled) {
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    } else {
        val stroke = 2f * 3
        paint.color = outline
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = stroke
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - stroke / 2f, paint)
    }
    return bitmap
}
