package com.yatharth.whatsappscheduler.messaging

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.yatharth.whatsappscheduler.core.security.SanitizedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WhatsAppAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var isAutoSendEnabled: Boolean = true

        @Volatile
        var pendingAutoSendTimestamp: Long = 0L

        private const val PENDING_TIMEOUT_MS = 25_000L // 25 seconds window after intent launch

        fun armAutoSend() {
            pendingAutoSendTimestamp = System.currentTimeMillis()
            SanitizedLogger.d("Armed Accessibility Auto-Send for next WhatsApp launch.")
        }

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            val expectedComponentName = "${context.packageName}/${WhatsAppAccessibilityService::class.java.canonicalName}"
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val stringSplitter = TextUtils.SimpleStringSplitter(':')
            stringSplitter.setString(enabledServicesSetting)

            while (stringSplitter.hasNext()) {
                val componentName = stringSplitter.next()
                if (componentName.equals(expectedComponentName, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || !isAutoSendEnabled) return

        val now = System.currentTimeMillis()
        if (pendingAutoSendTimestamp == 0L || (now - pendingAutoSendTimestamp) > PENDING_TIMEOUT_MS) {
            return
        }

        val packageName = event.packageName?.toString() ?: return
        if (packageName != "com.whatsapp" && packageName != "com.whatsapp.w4b") return

        val rootNode = rootInActiveWindow ?: return

        // Attempt to find and click the Send button
        val clicked = findAndClickSendButton(rootNode, packageName)
        if (clicked) {
            SanitizedLogger.i("Accessibility Auto-Send successfully clicked WhatsApp send button.")
            pendingAutoSendTimestamp = 0L // Disarm after click

            // Auto-navigate back after sending
            CoroutineScope(Dispatchers.Main).launch {
                delay(800)
                performGlobalAction(GLOBAL_ACTION_BACK)
            }
        }
    }

    private fun findAndClickSendButton(rootNode: AccessibilityNodeInfo, packageName: String): Boolean {
        // Strategy 1: Find by known resource IDs
        val knownResourceIds = listOf(
            "$packageName:id/send",
            "$packageName:id/send_button",
            "com.whatsapp:id/send"
        )

        for (id in knownResourceIds) {
            val nodes = rootNode.findAccessibilityNodeInfosByViewId(id)
            if (!nodes.isNullOrEmpty()) {
                for (node in nodes) {
                    if (node.isClickable && tryClickNode(node)) {
                        return true
                    }
                }
            }
        }

        // Strategy 2: Find by content description "Send"
        val nodesByText = rootNode.findAccessibilityNodeInfosByText("Send")
        if (!nodesByText.isNullOrEmpty()) {
            for (node in nodesByText) {
                if (node.isClickable && tryClickNode(node)) {
                    return true
                }
                // Check parent if container is clickable
                var parent = node.parent
                while (parent != null) {
                    if (parent.isClickable && tryClickNode(parent)) {
                        return true
                    }
                    parent = parent.parent
                }
            }
        }

        // Strategy 3: Deep search for ImageButton with Send content description
        return searchRecursive(rootNode)
    }

    private fun searchRecursive(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false

        val description = node.contentDescription?.toString()
        if (!description.isNullOrEmpty() && description.contains("Send", ignoreCase = true)) {
            if (node.isClickable && tryClickNode(node)) {
                return true
            }
            var parent = node.parent
            while (parent != null) {
                if (parent.isClickable && tryClickNode(parent)) {
                    return true
                }
                parent = parent.parent
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (searchRecursive(child)) {
                return true
            }
        }

        return false
    }

    private fun tryClickNode(node: AccessibilityNodeInfo): Boolean {
        return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    override fun onInterrupt() {
        SanitizedLogger.w("WhatsAppAccessibilityService interrupted.")
    }
}
