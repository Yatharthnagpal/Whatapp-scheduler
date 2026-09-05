package com.yatharth.whatsappscheduler.messaging

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.yatharth.whatsappscheduler.domain.model.Contact
import com.yatharth.whatsappscheduler.domain.model.SendResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface MessageSender {
    suspend fun send(recipient: Contact, message: String): SendResult
    fun createWhatsAppIntent(phoneNumber: String, message: String): Intent
}

@Singleton
class WhatsAppMessageSenderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MessageSender {

    override suspend fun send(recipient: Contact, message: String): SendResult {
        val intent = createWhatsAppIntent(recipient.phoneNumber, message)
        
        // Check if WhatsApp is installed
        val packageManager = context.packageManager
        if (intent.resolveActivity(packageManager) == null) {
            return SendResult.Failure("WhatsApp application is not installed on this device.")
        }

        // Check if Accessibility Auto-Send service is enabled and active
        val accessibilityEnabled = WhatsAppAccessibilityService.isAccessibilityServiceEnabled(context)
        if (accessibilityEnabled && WhatsAppAccessibilityService.isAutoSendEnabled) {
            WhatsAppAccessibilityService.armAutoSend()
            try {
                context.startActivity(intent)
                return SendResult.Success
            } catch (e: Exception) {
                return SendResult.Failure("Failed to auto-launch WhatsApp intent: ${e.message}")
            }
        }

        // Returning RequiresUserAction prompts notification dispatch when Accessibility Auto-Send is disabled
        return SendResult.RequiresUserAction("Accessibility Service inactive. Tap notification to complete dispatch.")
    }

    override fun createWhatsAppIntent(phoneNumber: String, message: String): Intent {
        val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
        val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}"
        return Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.whatsapp")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}

