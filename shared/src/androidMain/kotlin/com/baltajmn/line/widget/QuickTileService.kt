package com.baltajmn.line.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.baltajmn.line.data.AndroidContext
import com.baltajmn.line.data.readWidgetState
import com.baltajmn.line.data.today
import com.baltajmn.line.data.widgetView
import com.baltajmn.line.i18n.S

/**
 * One tap from the shade and the keyboard is up on today's line (docs/tecnico.md 12.4). It reads
 * widget.json like the widgets do, so it only knows whether today is written: the shade can be
 * pulled down on the lock screen, and nothing of the diary may show there.
 */
class QuickTileService : TileService() {

    private fun state() = readWidgetState()?.let { widgetView(it, today()) }

    override fun onStartListening() {
        AndroidContext.init(applicationContext)
        val tile = qsTile ?: return
        val written = state()?.written == true
        tile.state = if (written) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = S.tileLabel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (written) S.tileWritten else S.tileNotWritten
        }
        tile.updateTile()
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        AndroidContext.init(applicationContext)
        // Without Pro the tile still answers, with the paywall, which is where every Pro door leads.
        val pro = state()?.pro == true
        val open = Intent()
            .setComponent(ComponentName(packageName, "com.baltajmn.line.MainActivity"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra("screen", if (pro) "today" else "pro")
            .putExtra("focus", pro)
        // unlockAndRun first: on the lock screen the shade would otherwise open the diary behind it.
        unlockAndRun {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val pending = PendingIntent.getActivity(this, 0, open, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                startActivityAndCollapse(pending)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(open)
            }
        }
    }
}
