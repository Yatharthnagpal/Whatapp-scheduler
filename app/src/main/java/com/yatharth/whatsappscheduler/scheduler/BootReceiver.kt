package com.yatharth.whatsappscheduler.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yatharth.whatsappscheduler.core.security.SanitizedLogger
import com.yatharth.whatsappscheduler.domain.usecase.RestoreAlarmsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var restoreAlarmsUseCase: RestoreAlarmsUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        SanitizedLogger.i("BootReceiver received action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    restoreAlarmsUseCase()
                } catch (e: Exception) {
                    SanitizedLogger.e("Failed to restore alarms in BootReceiver", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
