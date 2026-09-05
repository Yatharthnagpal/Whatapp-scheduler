package com.yatharth.whatsappscheduler.domain.repository

import com.yatharth.whatsappscheduler.domain.model.Contact
import com.yatharth.whatsappscheduler.domain.model.ExecutionLog
import com.yatharth.whatsappscheduler.domain.model.MessageStatus
import com.yatharth.whatsappscheduler.domain.model.ScheduledMessage
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getUpcomingMessages(): Flow<List<ScheduledMessage>>
    fun getHistoryMessages(): Flow<List<ScheduledMessage>>
    fun getMessagesByStatus(status: MessageStatus): Flow<List<ScheduledMessage>>
    suspend fun getMessageById(id: Long): ScheduledMessage?
    suspend fun scheduleMessage(message: ScheduledMessage): Long
    suspend fun updateMessage(message: ScheduledMessage)
    suspend fun cancelMessage(id: Long): Boolean
    suspend fun deleteMessage(id: Long)
    suspend fun rescheduleMessage(id: Long, newTimestamp: Long): Boolean
    suspend fun claimForProcessing(id: Long): Boolean
    suspend fun recordExecutionResult(
        messageId: Long,
        finalStatus: MessageStatus,
        result: String,
        errorCode: String? = null,
        errorMessage: String? = null
    )
    suspend fun getFutureScheduledMessages(): List<ScheduledMessage>
    fun getLogsForMessage(messageId: Long): Flow<List<ExecutionLog>>
}

interface ContactRepository {
    suspend fun getContacts(query: String = ""): List<Contact>
}
