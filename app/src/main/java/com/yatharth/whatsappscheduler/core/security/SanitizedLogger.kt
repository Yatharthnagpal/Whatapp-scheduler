package com.yatharth.whatsappscheduler.core.security

import android.util.Log

object SanitizedLogger {
    private const val TAG = "WhatsAppScheduler"

    fun d(message: String) {
        Log.d(TAG, sanitize(message))
    }

    fun i(message: String) {
        Log.i(TAG, sanitize(message))
    }

    fun w(message: String) {
        Log.w(TAG, sanitize(message))
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, sanitize(message), throwable)
    }

    fun sanitize(input: String): String {
        // Redact phone numbers (digits sequence length 7-15) and sanitize message contents
        return input.replace(Regex("\\+?\\d{7,15}"), "[REDACTED_PHONE]")
    }
}
