package com.yatharth.whatsappscheduler.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yatharth.whatsappscheduler.core.security.SanitizedLogger
import com.yatharth.whatsappscheduler.domain.usecase.ExecuteScheduledMessageUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScheduledMessageReceiver : BroadcastReceiver() {

    @Inject
    lateinit var executeScheduledMessageUseCase: ExecuteScheduledMessageUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val messageId = intent.getLongExtra(AlarmManagerSchedulerImpl.EXTRA_MESSAGE_ID, -1L)

        SanitizedLogger.d("ScheduledMessageReceiver triggered for messageId: $messageId")

        if (messageId == -1L) {
            SanitizedLogger.w("Invalid messageId received in ScheduledMessageReceiver.")
            pendingResult.finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                executeScheduledMessageUseCase(messageId)
            } catch (e: Exception) {
                SanitizedLogger.e("Error executing scheduled message in receiver", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
