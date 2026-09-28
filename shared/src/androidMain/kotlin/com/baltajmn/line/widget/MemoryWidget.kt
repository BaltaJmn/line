package com.baltajmn.line.widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.baltajmn.line.data.AndroidContext
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.WidgetState
import com.baltajmn.line.data.readWidgetState
import com.baltajmn.line.data.today
import com.baltajmn.line.data.widgetView
import com.baltajmn.line.i18n.S
import com.baltajmn.line.ui.theme.Dark
import com.baltajmn.line.ui.theme.Light
import kotlinx.datetime.LocalDate

/**
 * Placing or removing the last memory widget changes what widget.json may carry, so the diary is
 * read once more and the state rewritten: refreshWidgets is where the placement is looked up.
 */
class MemoryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = MemoryWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        resync(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        resync(context)
    }

    private fun resync(context: Context) {
        AndroidContext.init(context)
        LineRepository.load()
    }
}

/**
 * Pro, and the one surface that shows words of the diary: the line of a year ago. Putting it on
 * the home screen is the consent; without it, with the lock on or without Pro, widget.json carries
 * no line and this widget says why instead (docs/pantallas.md 11.3).
 */
class MemoryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = readWidgetState()?.let { widgetView(it, today()) }
        provideContent { Memory(state) }
    }
}

@androidx.compose.runtime.Composable
private fun Memory(state: WidgetState?) {
    val context = LocalContext.current
    val day = state?.date?.let(LocalDate::parse) ?: today()
    val pro = state?.pro == true
    val line = state?.line.takeIf { pro && state?.locked != true }
    val night = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
        Configuration.UI_MODE_NIGHT_YES
    val scheme = if (night) Dark else Light
    val open = Intent()
        .setComponent(ComponentName(context.packageName, "com.baltajmn.line.MainActivity"))
        .putExtra("screen", if (!pro) "pro" else if (line != null) "memory" else "today")
    val muted = TextStyle(color = ColorProvider(scheme.onSurfaceVariant), fontSize = 13.sp)

    Column(
        modifier = GlanceModifier.fillMaxSize().background(ColorProvider(scheme.background)).padding(8.dp)
            .clickable(actionStartActivity(open)),
        verticalAlignment = if (line != null) Alignment.Top else Alignment.CenterVertically,
        horizontalAlignment = if (line != null) Alignment.Start else Alignment.CenterHorizontally,
    ) {
        when {
            !pro -> {
                Text(
                    S.proTitle,
                    style = TextStyle(color = ColorProvider(scheme.onBackground), fontSize = 13.sp, fontWeight = FontWeight.Medium),
                )
                Text(S.widgetUnlock, style = muted)
            }
            state?.locked == true -> Text(S.widgetLocked, style = muted)
            line == null -> Text(S.widgetNoMemory, style = muted)
            else -> {
                Text(
                    S.widgetMemoryLabel(day.year - 1).uppercase(),
                    style = TextStyle(color = ColorProvider(scheme.onSurfaceVariant), fontSize = 11.sp, fontWeight = FontWeight.Medium),
                )
                Spacer(GlanceModifier.height(6.dp))
                Text(
                    line,
                    style = TextStyle(color = ColorProvider(scheme.onBackground), fontSize = 15.sp, fontFamily = FontFamily.Serif),
                    maxLines = 5,
                )
            }
        }
    }
}
