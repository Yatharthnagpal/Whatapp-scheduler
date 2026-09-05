package com.yatharth.whatsappscheduler.core.time

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeUtils {
    fun formatDateTime(epochMs: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }

    fun formatDateOnly(epochMs: Long): String {
        val sdf = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }

    fun formatTimeOnly(epochMs: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }

    fun getTimeZoneName(): String {
        return TimeZone.getDefault().displayName
    }
}
