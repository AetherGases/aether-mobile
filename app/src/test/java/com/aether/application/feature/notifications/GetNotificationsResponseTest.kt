package com.aether.application.feature.notifications

import com.aether.application.core.network.ApiClient
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.application.feature.notifications.data.remote.dto.GetNotificationsResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetNotificationsResponseTest {

    @Test
    fun `decodes the ms-auth page and takes the report id from the inventory`() {
        val body = """
            {
              "notifications": [
                {
                  "id": "65f1c0ffee",
                  "title": "Inventário aprovado",
                  "description": "O inventário Q1 foi aprovado por Ana.",
                  "inventory": { "id": 42, "status": "APPROVED", "type": "INPUT" },
                  "createdAt": "2026-11-01T12:00:00Z"
                }
              ],
              "total": 1,
              "skip": 0,
              "take": 20
            }
        """.trimIndent()

        val notification = ApiClient.json.decodeFromString<GetNotificationsResponse>(body)
            .notifications.single().toDomain()

        assertEquals(42L, notification.reportId)
        assertEquals(InventoryStatus.Input.APPROVED, notification.status)
        assertEquals("Inventário aprovado", notification.title)
    }

    @Test
    fun `a notification without inventory has no report to open`() {
        val body = """
            {
              "notifications": [
                { "id": "1", "title": "t", "description": "d", "createdAt": "2026-11-01T12:00:00Z" }
              ],
              "total": 1, "skip": 0, "take": 20
            }
        """.trimIndent()

        val notification = ApiClient.json.decodeFromString<GetNotificationsResponse>(body)
            .notifications.single().toDomain()

        assertNull(notification.reportId)
        assertEquals(InventoryStatus.Input.UNDER_REVIEW, notification.status)
    }
}
