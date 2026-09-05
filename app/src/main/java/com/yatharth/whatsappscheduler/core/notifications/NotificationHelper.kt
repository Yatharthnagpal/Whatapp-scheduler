package com.yatharth.whatsappscheduler.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yatharth.whatsappscheduler.MainActivity
import com.yatharth.whatsappscheduler.domain.model.ScheduledMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "whatsapp_scheduler_channel"
        const val CHANNEL_NAME = "WhatsApp Schedule Reminders"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for scheduled WhatsApp messages requiring dispatch"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showActionRequiredNotification(
        message: ScheduledMessage,
        whatsappIntent: Intent
    ) {
        createNotificationChannel()

        val appIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val appPendingIntent = PendingIntent.getActivity(
            context,
            message.id.toInt(),
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val waPendingIntent = PendingIntent.getActivity(
            context,
            (message.id + 10000).toInt(),
            whatsappIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Send Scheduled WhatsApp Message")
            .setContentText("Tap to dispatch message to ${message.contactName}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Schedule for ${message.contactName}:\n\"${message.message}\"\n\nTap 'Open WhatsApp' below to send.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(appPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_send,
                "Open WhatsApp",
                waPendingIntent
            )

        try {
            NotificationManagerCompat.from(context).notify(message.id.toInt(), builder.build())
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission missing
        }
    }

    fun showMissedNotification(message: ScheduledMessage) {
        createNotificationChannel()

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            message.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Missed WhatsApp Schedule")
            .setContentText("Schedule for ${message.contactName} was missed due to device delay.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify((message.id + 20000).toInt(), builder.build())
        } catch (e: SecurityException) {
            // Permission missing
        }
    }
}
