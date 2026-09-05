package com.yatharth.whatsappscheduler.di

import android.content.Context
import androidx.room.Room
import com.yatharth.whatsappscheduler.data.contacts.ContactRepositoryImpl
import com.yatharth.whatsappscheduler.data.local.AppDatabase
import com.yatharth.whatsappscheduler.data.local.dao.ExecutionLogDao
import com.yatharth.whatsappscheduler.data.local.dao.ScheduledMessageDao
import com.yatharth.whatsappscheduler.data.repository.MessageRepositoryImpl
import com.yatharth.whatsappscheduler.domain.repository.ContactRepository
import com.yatharth.whatsappscheduler.domain.repository.MessageRepository
import com.yatharth.whatsappscheduler.messaging.MessageSender
import com.yatharth.whatsappscheduler.messaging.WhatsAppMessageSenderImpl
import com.yatharth.whatsappscheduler.scheduler.AlarmManagerSchedulerImpl
import com.yatharth.whatsappscheduler.scheduler.MessageScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideScheduledMessageDao(database: AppDatabase): ScheduledMessageDao {
        return database.scheduledMessageDao()
    }

    @Provides
    fun provideExecutionLogDao(database: AppDatabase): ExecutionLogDao {
        return database.executionLogDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulerModule {

    @Binds
    @Singleton
    abstract fun bindMessageScheduler(impl: AlarmManagerSchedulerImpl): MessageScheduler

    @Binds
    @Singleton
    abstract fun bindMessageSender(impl: WhatsAppMessageSenderImpl): MessageSender
}
