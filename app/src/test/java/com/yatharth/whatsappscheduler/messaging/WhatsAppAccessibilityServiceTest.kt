package com.yatharth.whatsappscheduler.messaging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WhatsAppAccessibilityServiceTest {

    @Test
    fun armAutoSend_setsPendingTimestamp() {
        val before = System.currentTimeMillis()
        WhatsAppAccessibilityService.armAutoSend()
        val after = System.currentTimeMillis()

        assertTrue(WhatsAppAccessibilityService.pendingAutoSendTimestamp >= before)
        assertTrue(WhatsAppAccessibilityService.pendingAutoSendTimestamp <= after)
    }

    @Test
    fun defaultState_isAutoSendEnabledIsTrue() {
        assertTrue(WhatsAppAccessibilityService.isAutoSendEnabled)
    }
}
