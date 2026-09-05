package com.yatharth.whatsappscheduler.domain.usecase

import com.yatharth.whatsappscheduler.domain.model.ScheduledMessage
import com.yatharth.whatsappscheduler.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeMessageRepository : MessageRepository {
    val messages = mutableListOf<ScheduledMessage>()

    override fun getUpcomingMessages(): Flow<List<ScheduledMessage>> = flowOf(messages)
    override fun getHistoryMessages(): Flow<List<ScheduledMessage>> = flowOf(messages)
    override fun getMessagesByStatus(status: com.yatharth.whatsappscheduler.domain.model.MessageStatus): Flow<List<ScheduledMessage>> = flowOf(messages)
    override suspend fun getMessageById(id: Long): ScheduledMessage? = messages.find { it.id == id }
    override suspend fun scheduleMessage(message: ScheduledMessage): Long {
        val id = (messages.size + 1).toLong()
        val msgWithId = message.copy(id = id)
        messages.add(msgWithId)
        return id
    }
    override suspend fun updateMessage(message: ScheduledMessage) {}
    override suspend fun cancelMessage(id: Long): Boolean = true
    override suspend fun deleteMessage(id: Long) { messages.removeIf { it.id == id } }
    override suspend fun rescheduleMessage(id: Long, newTimestamp: Long): Boolean = true
    override suspend fun claimForProcessing(id: Long): Boolean = true
    override suspend fun recordExecutionResult(
        messageId: Long,
        finalStatus: com.yatharth.whatsappscheduler.domain.model.MessageStatus,
        result: String,
        errorCode: String?,
        errorMessage: String?
    ) {}
    override suspend fun getFutureScheduledMessages(): List<ScheduledMessage> = emptyList()
    override fun getLogsForMessage(messageId: Long): Flow<List<com.yatharth.whatsappscheduler.domain.model.ExecutionLog>> = flowOf(emptyList())
}

class UseCasesTest {

    @Test
    fun scheduleMessageUseCase_rejectsPastTime() = runTest {
        val repo = FakeMessageRepository()
        val useCase = ScheduleMessageUseCase(repo)

        val result = useCase(
            contactName = "John",
            phoneNumber = "+1999888777",
            messageText = "Past test",
            scheduledAt = System.currentTimeMillis() - 1000L
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun scheduleMessageUseCase_acceptsFutureTime() = runTest {
        val repo = FakeMessageRepository()
        val useCase = ScheduleMessageUseCase(repo)

        val result = useCase(
            contactName = "John",
            phoneNumber = "+1999888777",
            messageText = "Future test",
            scheduledAt = System.currentTimeMillis() + 60000L
        )

        assertTrue(result.isSuccess)
    }
}
