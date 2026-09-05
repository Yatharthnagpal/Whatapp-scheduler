package com.yatharth.whatsappscheduler.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.yatharth.whatsappscheduler.core.security.SanitizedLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface MessageScheduler {
    fun schedule(messageId: Long, timestamp: Long)
    fun cancel(messageId: Long)
}

@Singleton
class AlarmManagerSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MessageScheduler {

    companion object {
        const val EXTRA_MESSAGE_ID = "extra_scheduled_message_id"
        const val ACTION_EXECUTE_SCHEDULED_MESSAGE = "com.yatharth.whatsappscheduler.EXECUTE_SCHEDULE"
    }

    override fun schedule(messageId: Long, timestamp: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ScheduledMessageReceiver::class.java).apply {
            action = ACTION_EXECUTE_SCHEDULED_MESSAGE
            putExtra(EXTRA_MESSAGE_ID, messageId)
        }

        val pendingIntent = PendingIntent.getReceiver(
            context,
            messageId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
            }
            SanitizedLogger.d("Scheduled alarm registered for ID: $messageId at $timestamp")
        } catch (e: SecurityException) {
            SanitizedLogger.e("SecurityException when scheduling alarm for ID: $messageId", e)
        }
    }

    override fun cancel(messageId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ScheduledMessageReceiver::class.java).apply {
            action = ACTION_EXECUTE_SCHEDULED_MESSAGE
            putExtra(EXTRA_MESSAGE_ID, messageId)
        }

        val pendingIntent = PendingIntent.getReceiver(
            context,
            messageId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        SanitizedLogger.d("Cancelled alarm for ID: $messageId")
    }
}
