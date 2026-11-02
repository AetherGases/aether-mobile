package com.aether.application.feature.reports

import com.aether.application.core.network.ApiClient
import com.aether.application.feature.reports.data.remote.dto.RegisterReportResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RegisterReportResponseTest {

    @Test
    fun `decodes the response ms-inventory sends, which has no createdAt`() {
        val body = """
            {
              "id": 100,
              "name": "Relatório de emissões",
              "type": "INPUT",
              "status": "PROCESSING",
              "storageFile": {
                "id": 1,
                "fileName": "inventario.xlsx",
                "path": "https://res.cloudinary.com/demo/raw/upload/spreadsheets/inventario.xlsx"
              }
            }
        """.trimIndent()

        val response = ApiClient.json.decodeFromString<RegisterReportResponse>(body)

        assertEquals(100L, response.id)
        assertNull(response.createdAt)
        assertNull(response.storageFile?.createdAt)
    }

    @Test
    fun `ignores fields the app does not know yet`() {
        val body = """
            {"id": 1, "name": "Relatório", "type": "INPUT", "status": "PROCESSING", "consolidationApproach": "Controle financeiro"}
        """.trimIndent()

        assertEquals(1L, ApiClient.json.decodeFromString<RegisterReportResponse>(body).id)
    }
}
