package com.yatharth.whatsappscheduler.domain.usecase

import com.yatharth.whatsappscheduler.core.notifications.NotificationHelper
import com.yatharth.whatsappscheduler.core.security.SanitizedLogger
import com.yatharth.whatsappscheduler.domain.model.Contact
import com.yatharth.whatsappscheduler.domain.model.ExecutionLog
import com.yatharth.whatsappscheduler.domain.model.MessageStatus
import com.yatharth.whatsappscheduler.domain.model.ScheduledMessage
import com.yatharth.whatsappscheduler.domain.model.SendResult
import com.yatharth.whatsappscheduler.domain.repository.ContactRepository
import com.yatharth.whatsappscheduler.domain.repository.MessageRepository
import com.yatharth.whatsappscheduler.messaging.MessageSender
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(
        contactName: String,
        phoneNumber: String,
        messageText: String,
        scheduledAt: Long
    ): Result<Long> {
        if (contactName.isBlank()) return Result.failure(IllegalArgumentException("Contact name cannot be empty."))
        if (phoneNumber.isBlank()) return Result.failure(IllegalArgumentException("Phone number cannot be empty."))
        if (messageText.isBlank()) return Result.failure(IllegalArgumentException("Message content cannot be empty."))
        if (scheduledAt <= System.currentTimeMillis()) return Result.failure(IllegalArgumentException("Scheduled time must be in the future."))

        val message = ScheduledMessage(
            contactName = contactName.trim(),
            phoneNumber = phoneNumber.trim(),
            message = messageText.trim(),
            scheduledAt = scheduledAt,
            status = MessageStatus.SCHEDULED
        )

        return try {
            val id = repository.scheduleMessage(message)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Singleton
class CancelMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(id: Long): Boolean {
        return repository.cancelMessage(id)
    }
}

@Singleton
class RescheduleMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(id: Long, newTimestamp: Long): Result<Boolean> {
        if (newTimestamp <= System.currentTimeMillis()) {
            return Result.failure(IllegalArgumentException("New scheduled time must be in the future."))
        }
        val success = repository.rescheduleMessage(id, newTimestamp)
        return Result.success(success)
    }
}

@Singleton
class DeleteMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteMessage(id)
    }
}

@Singleton
class GetUpcomingMessagesUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(): Flow<List<ScheduledMessage>> = repository.getUpcomingMessages()
}

@Singleton
class GetHistoryMessagesUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(): Flow<List<ScheduledMessage>> = repository.getHistoryMessages()
}

@Singleton
class GetContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(query: String = ""): List<Contact> = repository.getContacts(query)
}

@Singleton
class ExecuteScheduledMessageUseCase @Inject constructor(
    private val repository: MessageRepository,
    private val messageSender: MessageSender,
    private val notificationHelper: NotificationHelper
) {
    companion object {
        const val MISSED_TOLERANCE_MS = 15 * 60 * 1000L // 15 minutes tolerance
    }

    suspend operator fun invoke(messageId: Long) {
        SanitizedLogger.d("Executing scheduled message ID: $messageId")
        val message = repository.getMessageById(messageId) ?: run {
            SanitizedLogger.w("Message record not found for ID: $messageId")
            return
        }

        if (message.status != MessageStatus.SCHEDULED) {
            SanitizedLogger.w("Message ID $messageId status is ${message.status}, ignoring execution.")
            return
        }

        val now = System.currentTimeMillis()
        if (now > message.scheduledAt + MISSED_TOLERANCE_MS) {
            SanitizedLogger.w("Message ID $messageId missed its execution window.")
            repository.recordExecutionResult(
                messageId = messageId,
                finalStatus = MessageStatus.MISSED,
                result = "MISSED",
                errorCode = "SCHEDULE_MISSED",
                errorMessage = "Execution window exceeded due to OS delay or device power off."
            )
            notificationHelper.showMissedNotification(message)
            return
        }

        // Atomic claim
        val claimed = repository.claimForProcessing(messageId)
        if (!claimed) {
            SanitizedLogger.w("Failed atomic claim for message ID $messageId. Aborting duplicate execution.")
            return
        }

        val recipient = Contact(id = "", name = message.contactName, phoneNumber = message.phoneNumber)
        when (val result = messageSender.send(recipient, message.message)) {
            is SendResult.Success -> {
                repository.recordExecutionResult(
                    messageId = messageId,
                    finalStatus = MessageStatus.COMPLETED,
                    result = "SUCCESS"
                )
            }
            is SendResult.RequiresUserAction -> {
                repository.recordExecutionResult(
                    messageId = messageId,
                    finalStatus = MessageStatus.REQUIRES_USER_ACTION,
                    result = "REQUIRES_USER_ACTION",
                    errorMessage = result.reason
                )
                val waIntent = messageSender.createWhatsAppIntent(message.phoneNumber, message.message)
                notificationHelper.showActionRequiredNotification(message, waIntent)
            }
            is SendResult.Failure -> {
                repository.recordExecutionResult(
                    messageId = messageId,
                    finalStatus = MessageStatus.FAILED,
                    result = "FAILURE",
                    errorCode = result.errorCode,
                    errorMessage = result.reason
                )
            }
        }
    }
}

@Singleton
class RestoreAlarmsUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke() {
        SanitizedLogger.i("Restoring alarms after system reboot...")
        val futureMessages = repository.getFutureScheduledMessages()
        futureMessages.forEach { msg ->
            repository.scheduleMessage(msg)
        }
        SanitizedLogger.i("Restored ${futureMessages.size} future scheduled alarms.")
    }
}
