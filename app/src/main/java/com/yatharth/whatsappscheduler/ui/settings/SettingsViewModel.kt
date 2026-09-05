package com.yatharth.whatsappscheduler.ui.settings

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

import com.yatharth.whatsappscheduler.messaging.WhatsAppAccessibilityService

data class SettingsUiState(
    val hasContactsPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val canScheduleExactAlarms: Boolean = true,
    val hasAccessibilityPermission: Boolean = false,
    val timeZone: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun checkPermissions() {
        val contactsGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        val notifGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        val exactAlarmCapable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true

        val accessibilityActive = WhatsAppAccessibilityService.isAccessibilityServiceEnabled(context)

        _uiState.update {
            it.copy(
                hasContactsPermission = contactsGranted,
                hasNotificationPermission = notifGranted,
                canScheduleExactAlarms = exactAlarmCapable,
                hasAccessibilityPermission = accessibilityActive,
                timeZone = java.util.TimeZone.getDefault().displayName
            )
        }
    }
}

