package com.aether.application.feature.reports

import com.aether.application.feature.reports.data.remote.dto.RegisterReportRequest
import com.aether.application.feature.reports.domain.model.ConsolidationApproach
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class RegisterReportRequestTest {

    private fun approachSentFor(approach: ConsolidationApproach): String {
        val request = RegisterReportRequest(
            name = "Relatório de emissões",
            consolidationApproach = approach,
            fileName = "inventario.xlsx",
            path = "https://res.cloudinary.com/demo/raw/upload/inventario.xlsx"
        )
        val json = Json.parseToJsonElement(Json.encodeToString(request)).jsonObject
        return json.getValue("consolidationApproach").jsonPrimitive.content
    }

    @Test
    fun `each approach is sent with its fixed text`() {
        assertEquals("Participação societária", approachSentFor(ConsolidationApproach.EQUITY_SHARE))
        assertEquals("Controle financeiro", approachSentFor(ConsolidationApproach.FINANCIAL_CONTROL))
        assertEquals("Controle operacional", approachSentFor(ConsolidationApproach.OPERATIONAL_CONTROL))
    }

    @Test
    fun `there are exactly three approaches`() {
        assertEquals(3, ConsolidationApproach.entries.size)
    }

    @Test
    fun `every sent text fits the backend limit of 3 to 100 characters`() {
        ConsolidationApproach.entries.forEach {
            assertEquals(true, approachSentFor(it).length in 3..100)
        }
    }
}
