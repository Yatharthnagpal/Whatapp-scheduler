package com.yatharth.whatsappscheduler.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.yatharth.whatsappscheduler.domain.model.ExecutionLog
import com.yatharth.whatsappscheduler.domain.model.MessageStatus
import com.yatharth.whatsappscheduler.domain.model.ScheduledMessage

@Entity(
    tableName = "scheduled_messages",
    indices = [
        Index(value = ["scheduled_at"]),
        Index(value = ["status"]),
        Index(value = ["status", "scheduled_at"])
    ]
)
data class ScheduledMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "contact_name")
    val contactName: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "scheduled_at")
    val scheduledAt: Long,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "execution_attempts", defaultValue = "0")
    val executionAttempts: Int = 0,

    @ColumnInfo(name = "last_error")
    val lastError: String? = null
) {
    fun toDomain(): ScheduledMessage {
        return ScheduledMessage(
            id = id,
            contactName = contactName,
            phoneNumber = phoneNumber,
            message = message,
            scheduledAt = scheduledAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
            status = try { MessageStatus.valueOf(status) } catch (e: Exception) { MessageStatus.FAILED },
            executionAttempts = executionAttempts,
            lastError = lastError
        )
    }

    companion object {
        fun fromDomain(domain: ScheduledMessage): ScheduledMessageEntity {
            return ScheduledMessageEntity(
                id = domain.id,
                contactName = domain.contactName,
                phoneNumber = domain.phoneNumber,
                message = domain.message,
                scheduledAt = domain.scheduledAt,
                createdAt = domain.createdAt,
                updatedAt = domain.updatedAt,
                status = domain.status.name,
                executionAttempts = domain.executionAttempts,
                lastError = domain.lastError
            )
        }
    }
}

@Entity(
    tableName = "execution_logs",
    foreignKeys = [
        ForeignKey(
            entity = ScheduledMessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["scheduled_message_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scheduled_message_id"])
    ]
)
data class ExecutionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "scheduled_message_id")
    val scheduledMessageId: Long,

    @ColumnInfo(name = "attempted_at")
    val attemptedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "result")
    val result: String,

    @ColumnInfo(name = "error_code")
    val errorCode: String? = null,

    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null
) {
    fun toDomain(): ExecutionLog {
        return ExecutionLog(
            id = id,
            scheduledMessageId = scheduledMessageId,
            attemptedAt = attemptedAt,
            result = result,
            errorCode = errorCode,
            errorMessage = errorMessage
        )
    }

    companion object {
        fun fromDomain(domain: ExecutionLog): ExecutionLogEntity {
            return ExecutionLogEntity(
                id = domain.id,
                scheduledMessageId = domain.scheduledMessageId,
                attemptedAt = domain.attemptedAt,
                result = domain.result,
                errorCode = domain.errorCode,
                errorMessage = domain.errorMessage
            )
        }
    }
}
