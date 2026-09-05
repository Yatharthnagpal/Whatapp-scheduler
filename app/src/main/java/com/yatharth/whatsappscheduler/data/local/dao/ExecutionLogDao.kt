package com.yatharth.whatsappscheduler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yatharth.whatsappscheduler.data.local.entity.ExecutionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExecutionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ExecutionLogEntity): Long

    @Query("SELECT * FROM execution_logs WHERE scheduled_message_id = :messageId ORDER BY attempted_at DESC")
    fun getLogsForMessage(messageId: Long): Flow<List<ExecutionLogEntity>>
}
