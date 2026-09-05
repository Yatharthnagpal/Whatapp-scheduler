package com.yatharth.whatsappscheduler.ui.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatharth.whatsappscheduler.domain.usecase.ScheduleMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class ScheduleUiState(
    val contactName: String = "",
    val phoneNumber: String = "",
    val messageText: String = "",
    val selectedDateMs: Long = System.currentTimeMillis() + 3600_000L, // Default 1 hour in future
    val selectedTimeHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    val selectedTimeMinute: Int = Calendar.getInstance().get(Calendar.MINUTE),
    val isScheduling: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scheduleMessageUseCase: ScheduleMessageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        val initialName = savedStateHandle.get<String>("contactName") ?: ""
        val initialPhone = savedStateHandle.get<String>("phoneNumber") ?: ""
        if (initialName.isNotBlank() || initialPhone.isNotBlank()) {
            _uiState.update { it.copy(contactName = initialName, phoneNumber = initialPhone) }
        }
    }

    fun updateContact(name: String, phone: String) {
        _uiState.update { it.copy(contactName = name, phoneNumber = phone, errorMessage = null) }
    }

    fun updateMessage(text: String) {
        _uiState.update { it.copy(messageText = text, errorMessage = null) }
    }

    fun updateDate(dateMs: Long) {
        _uiState.update { it.copy(selectedDateMs = dateMs, errorMessage = null) }
    }

    fun updateTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(selectedTimeHour = hour, selectedTimeMinute = minute, errorMessage = null) }
    }

    fun scheduleMessage() {
        val currentState = _uiState.value
        val cal = Calendar.getInstance().apply {
            timeInMillis = currentState.selectedDateMs
            set(Calendar.HOUR_OF_DAY, currentState.selectedTimeHour)
            set(Calendar.MINUTE, currentState.selectedTimeMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetTimestamp = cal.timeInMillis

        if (targetTimestamp <= System.currentTimeMillis()) {
            _uiState.update { it.copy(errorMessage = "Scheduled time must be in the future.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isScheduling = true, errorMessage = null) }
            val result = scheduleMessageUseCase(
                contactName = currentState.contactName,
                phoneNumber = currentState.phoneNumber,
                messageText = currentState.messageText,
                scheduledAt = targetTimestamp
            )

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isScheduling = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isScheduling = false, errorMessage = error.message ?: "Failed to schedule message.") }
                }
            )
        }
    }
}
