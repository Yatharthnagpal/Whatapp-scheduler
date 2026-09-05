package com.yatharth.whatsappscheduler.domain.model

enum class MessageStatus {
    SCHEDULED,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    MISSED,
    REQUIRES_USER_ACTION
}

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String
)

sealed class SendResult {
    data object Success : SendResult()
    data class RequiresUserAction(val reason: String) : SendResult()
    data class Failure(val reason: String, val errorCode: String? = null) : SendResult()
}

data class ScheduledMessage(
    val id: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val message: String,
    val scheduledAt: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SCHEDULED,
    val executionAttempts: Int = 0,
    val lastError: String? = null
)

data class ExecutionLog(
    val id: Long = 0,
    val scheduledMessageId: Long,
    val attemptedAt: Long = System.currentTimeMillis(),
    val result: String,
    val errorCode: String? = null,
    val errorMessage: String? = null
)
