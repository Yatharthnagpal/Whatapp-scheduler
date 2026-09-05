package com.yatharth.whatsappscheduler.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatharth.whatsappscheduler.domain.model.ExecutionLog
import com.yatharth.whatsappscheduler.domain.model.ScheduledMessage
import com.yatharth.whatsappscheduler.domain.usecase.CancelMessageUseCase
import com.yatharth.whatsappscheduler.domain.usecase.DeleteMessageUseCase
import com.yatharth.whatsappscheduler.domain.usecase.RescheduleMessageUseCase
import com.yatharth.whatsappscheduler.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessageDetailsUiState(
    val message: ScheduledMessage? = null,
    val logs: List<ExecutionLog> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class MessageDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MessageRepository,
    private val cancelMessageUseCase: CancelMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val rescheduleMessageUseCase: RescheduleMessageUseCase
) : ViewModel() {

    private val messageId: Long = checkNotNull(savedStateHandle["messageId"])

    private val _uiState = MutableStateFlow(MessageDetailsUiState())
    val uiState: StateFlow<MessageDetailsUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    fun loadDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val msg = repository.getMessageById(messageId)
            if (msg == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Message record not found.") }
                return@launch
            }

            repository.getLogsForMessage(messageId).collect { logsList ->
                _uiState.update { it.copy(message = msg, logs = logsList, isLoading = false) }
            }
        }
    }

    fun cancelSchedule() {
        viewModelScope.launch {
            cancelMessageUseCase(messageId)
            loadDetails()
        }
    }

    fun deleteSchedule(onDeleted: () -> Unit) {
        viewModelScope.launch {
            deleteMessageUseCase(messageId)
            onDeleted()
        }
    }

    fun reschedule(newTimestamp: Long) {
        viewModelScope.launch {
            rescheduleMessageUseCase(messageId, newTimestamp)
            loadDetails()
        }
    }
}
