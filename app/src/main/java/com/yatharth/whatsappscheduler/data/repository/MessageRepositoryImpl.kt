package com.yatharth.whatsappscheduler.data.repository

import com.yatharth.whatsappscheduler.data.local.dao.ExecutionLogDao
import com.yatharth.whatsappscheduler.data.local.dao.ScheduledMessageDao
import com.yatharth.whatsappscheduler.data.local.entity.ExecutionLogEntity
import com.yatharth.whatsappscheduler.data.local.entity.ScheduledMessageEntity
import com.yatharth.whatsappscheduler.domain.model.ExecutionLog
import com.yatharth.whatsappscheduler.domain.model.MessageStatus
import com.yatharth.whatsappscheduler.domain.model.ScheduledMessage
import com.yatharth.whatsappscheduler.domain.repository.MessageRepository
import com.yatharth.whatsappscheduler.scheduler.MessageScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val scheduledMessageDao: ScheduledMessageDao,
    private val executionLogDao: ExecutionLogDao,
    private val scheduler: MessageScheduler
) : MessageRepository {

    override fun getUpcomingMessages(): Flow<List<ScheduledMessage>> {
        return scheduledMessageDao.getUpcomingMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getHistoryMessages(): Flow<List<ScheduledMessage>> {
        return scheduledMessageDao.getHistoryMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMessagesByStatus(status: MessageStatus): Flow<List<ScheduledMessage>> {
        return scheduledMessageDao.getMessagesByStatus(status.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMessageById(id: Long): ScheduledMessage? {
        return scheduledMessageDao.getById(id)?.toDomain()
    }

    override suspend fun scheduleMessage(message: ScheduledMessage): Long {
        val entity = ScheduledMessageEntity.fromDomain(message)
        val insertedId = scheduledMessageDao.insert(entity)
        scheduler.schedule(insertedId, message.scheduledAt)
        return insertedId
    }

    override suspend fun updateMessage(message: ScheduledMessage) {
        val entity = ScheduledMessageEntity.fromDomain(message)
        scheduledMessageDao.update(entity)
        if (message.status == MessageStatus.SCHEDULED) {
            scheduler.schedule(message.id, message.scheduledAt)
        }
    }

    override suspend fun cancelMessage(id: Long): Boolean {
        val cancelledRows = scheduledMessageDao.cancelScheduledMessage(id)
        if (cancelledRows > 0) {
            scheduler.cancel(id)
            return true
        }
        return false
    }

    override suspend fun deleteMessage(id: Long) {
        scheduler.cancel(id)
        scheduledMessageDao.deleteById(id)
    }

    override suspend fun rescheduleMessage(id: Long, newTimestamp: Long): Boolean {
        val updatedRows = scheduledMessageDao.rescheduleMessage(id, newTimestamp)
        if (updatedRows > 0) {
            scheduler.schedule(id, newTimestamp)
            return true
        }
        return false
    }

    override suspend fun claimForProcessing(id: Long): Boolean {
        val rowsAffected = scheduledMessageDao.claimForProcessing(id)
        return rowsAffected > 0
    }

    override suspend fun recordExecutionResult(
        messageId: Long,
        finalStatus: MessageStatus,
        result: String,
        errorCode: String?,
        errorMessage: String?
    ) {
        scheduledMessageDao.updateExecutionResult(
            id = messageId,
            status = finalStatus.name,
            lastError = errorMessage
        )

        executionLogDao.insert(
            ExecutionLogEntity(
                scheduledMessageId = messageId,
                attemptedAt = System.currentTimeMillis(),
                result = result,
                errorCode = errorCode,
                errorMessage = errorMessage
            )
        )
    }

    override suspend fun getFutureScheduledMessages(): List<ScheduledMessage> {
        return scheduledMessageDao.getFutureScheduledMessages(System.currentTimeMillis()).map { it.toDomain() }
    }

    override fun getLogsForMessage(messageId: Long): Flow<List<ExecutionLog>> {
        return executionLogDao.getLogsForMessage(messageId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
