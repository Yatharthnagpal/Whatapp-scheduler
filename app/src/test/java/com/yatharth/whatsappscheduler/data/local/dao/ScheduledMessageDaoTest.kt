package com.yatharth.whatsappscheduler.data.local.dao

import com.yatharth.whatsappscheduler.data.local.entity.ScheduledMessageEntity
import com.yatharth.whatsappscheduler.domain.model.MessageStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ScheduledMessageDaoTest {

    @Test
    fun testEntityStatusConversion() {
        val entity = ScheduledMessageEntity(
            id = 1L,
            contactName = "Alice",
            phoneNumber = "+1234567890",
            message = "Hello Alice",
            scheduledAt = System.currentTimeMillis() + 100000L,
            status = "SCHEDULED"
        )

        val domain = entity.toDomain()
        assertEquals(MessageStatus.SCHEDULED, domain.status)
        assertEquals("Alice", domain.contactName)
        assertEquals("+1234567890", domain.phoneNumber)

        val convertedEntity = ScheduledMessageEntity.fromDomain(domain)
        assertEquals("SCHEDULED", convertedEntity.status)
    }
}
