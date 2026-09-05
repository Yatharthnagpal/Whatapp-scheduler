package com.yatharth.whatsappscheduler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yatharth.whatsappscheduler.data.local.dao.ExecutionLogDao
import com.yatharth.whatsappscheduler.data.local.dao.ScheduledMessageDao
import com.yatharth.whatsappscheduler.data.local.entity.ExecutionLogEntity
import com.yatharth.whatsappscheduler.data.local.entity.ScheduledMessageEntity

@Database(
    entities = [
        ScheduledMessageEntity::class,
        ExecutionLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduledMessageDao(): ScheduledMessageDao
    abstract fun executionLogDao(): ExecutionLogDao

    companion object {
        const val DATABASE_NAME = "whatsapp_scheduler.db"
    }
}
