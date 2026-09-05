package com.yatharth.whatsappscheduler.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yatharth.whatsappscheduler.domain.model.MessageStatus
import com.yatharth.whatsappscheduler.domain.model.ScheduledMessage
import com.yatharth.whatsappscheduler.domain.usecase.CancelMessageUseCase
import com.yatharth.whatsappscheduler.domain.usecase.GetHistoryMessagesUseCase
import com.yatharth.whatsappscheduler.domain.usecase.GetUpcomingMessagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val upcomingMessages: List<ScheduledMessage> = emptyList(),
    val historyMessages: List<ScheduledMessage> = emptyList(),
    val selectedTab: Int = 0, // 0 = Upcoming, 1 = History
    val selectedStatusFilter: MessageStatus? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getUpcomingMessagesUseCase: GetUpcomingMessagesUseCase,
    getHistoryMessagesUseCase: GetHistoryMessagesUseCase,
    private val cancelMessageUseCase: CancelMessageUseCase
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    private val _statusFilter = MutableStateFlow<MessageStatus?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        getUpcomingMessagesUseCase(),
        getHistoryMessagesUseCase(),
        _selectedTab,
        _statusFilter
    ) { upcoming, history, tab, filter ->
        val filteredHistory = if (filter != null) {
            history.filter { it.status == filter }
        } else {
            history
        }
        HomeUiState(
            upcomingMessages = upcoming,
            historyMessages = filteredHistory,
            selectedTab = tab,
            selectedStatusFilter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun selectStatusFilter(status: MessageStatus?) {
        _statusFilter.value = status
    }

    fun cancelSchedule(id: Long) {
        viewModelScope.launch {
            cancelMessageUseCase(id)
        }
    }
}
