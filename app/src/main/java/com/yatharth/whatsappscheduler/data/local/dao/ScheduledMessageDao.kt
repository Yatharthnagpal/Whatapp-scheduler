package com.yatharth.whatsappscheduler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yatharth.whatsappscheduler.data.local.entity.ScheduledMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ScheduledMessageEntity): Long

    @Update
    suspend fun update(message: ScheduledMessageEntity)

    @Query("SELECT * FROM scheduled_messages WHERE id = :id")
    suspend fun getById(id: Long): ScheduledMessageEntity?

    @Query("SELECT * FROM scheduled_messages WHERE status = 'SCHEDULED' ORDER BY scheduled_at ASC")
    fun getUpcomingMessages(): Flow<List<ScheduledMessageEntity>>

    @Query("SELECT * FROM scheduled_messages WHERE status != 'SCHEDULED' ORDER BY scheduled_at DESC")
    fun getHistoryMessages(): Flow<List<ScheduledMessageEntity>>

    @Query("SELECT * FROM scheduled_messages WHERE status = :status ORDER BY scheduled_at DESC")
    fun getMessagesByStatus(status: String): Flow<List<ScheduledMessageEntity>>

    @Query("SELECT * FROM scheduled_messages WHERE status = 'SCHEDULED' AND scheduled_at > :now")
    suspend fun getFutureScheduledMessages(now: Long): List<ScheduledMessageEntity>

    @Query("DELETE FROM scheduled_messages WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("""
        UPDATE scheduled_messages
        SET status = 'PROCESSING', execution_attempts = execution_attempts + 1, updated_at = :updatedAt
        WHERE id = :id AND status = 'SCHEDULED'
    """)
    suspend fun claimForProcessing(id: Long, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("""
        UPDATE scheduled_messages
        SET status = :status, last_error = :lastError, updated_at = :updatedAt
        WHERE id = :id
    """)
    suspend fun updateExecutionResult(
        id: Long,
        status: String,
        lastError: String?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE scheduled_messages
        SET status = 'CANCELLED', updated_at = :updatedAt
        WHERE id = :id AND status = 'SCHEDULED'
    """)
    suspend fun cancelScheduledMessage(id: Long, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("""
        UPDATE scheduled_messages
        SET scheduled_at = :newTimestamp, status = 'SCHEDULED', execution_attempts = 0, last_error = NULL, updated_at = :updatedAt
        WHERE id = :id
    """)
    suspend fun rescheduleMessage(id: Long, newTimestamp: Long, updatedAt: Long = System.currentTimeMillis()): Int
}
