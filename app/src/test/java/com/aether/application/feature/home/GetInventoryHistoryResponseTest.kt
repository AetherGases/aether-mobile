package com.aether.application.feature.home

import com.aether.application.core.network.ApiClient
import com.aether.application.feature.home.data.remote.dto.GetInventoryHistoryResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class GetInventoryHistoryResponseTest {

    @Test
    fun `decodes the history page with author and totalCount`() {
        val body = """
            {
              "inventories": [
                {
                  "id": 7,
                  "name": "Inventário Q1",
                  "createdAt": "2026-09-11T10:00:00",
                  "author": "Maria",
                  "authorImageUrl": "https://img/maria.png",
                  "status": "UNDER_REVIEW",
                  "type": "INPUT"
                }
              ],
              "totalCount": 42
            }
        """.trimIndent()

        val page = ApiClient.json.decodeFromString<GetInventoryHistoryResponse>(body).toDomain()

        assertEquals(42L, page.totalCount)
        assertEquals(1, page.reports.size)
        assertEquals(7L, page.reports[0].id)
        assertEquals("Maria", page.reports[0].author)
        assertEquals("https://img/maria.png", page.reports[0].authorImageUrl)
    }
}
