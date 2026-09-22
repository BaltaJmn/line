package com.baltajmn.line.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.baltajmn.line.i18n.S
import com.baltajmn.line.shared.R

private const val CHANNEL_ID = "line-daily"
private const val NOTIFICATION_ID = 1

/** Fires the daily nudge unless the day already has its line, then books the next one. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AndroidContext.init(context)
        LineRepository.load()

        val day = today()
        if (LineRepository.entryOn(day) == null) {
            val memory = if (LineRepository.settings.lockOn) null else memorySnippet(LineRepository.journal, day)
            notify(context, if (memory == null) S.reminderTitle else S.reminderMemoryTitle, memory)
        }
        Reminder.sync(askPermission = false)
    }

    private fun notify(context: Context, title: String, body: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, S.reminderRow, NotificationManager.IMPORTANCE_DEFAULT),
        )

        val open = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val tap = PendingIntent.getActivity(
            context,
            0,
            open,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(Color.parseColor("#6FAE9B"))
            .setContentTitle(title)
            .apply { if (body != null) setContentText(body) }
            .setContentIntent(tap)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}

/** Alarms do not survive a reboot, a reinstall or a change of clock, so book it again. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AndroidContext.init(context)
        LineRepository.load()
        Reminder.sync(askPermission = false)
    }
}
