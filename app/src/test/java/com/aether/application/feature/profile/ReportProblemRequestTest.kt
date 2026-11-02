package com.aether.application.feature.profile

import com.aether.application.core.network.ApiClient
import com.aether.application.feature.profile.data.remote.dto.ProblemCategoryResponse
import com.aether.application.feature.profile.data.remote.dto.ReportProblemRequest
import com.aether.application.feature.profile.data.remote.dto.ReportProblemResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class ReportProblemRequestTest {

    @Test
    fun `the report body carries the screen next to category and description`() {
        val body = ApiClient.json.encodeToString(
            ReportProblemRequest(category = "BUG", screen = "Histórico de relatórios", description = "Travou ao abrir")
        )

        val fields = Json.parseToJsonElement(body).jsonObject
        assertEquals("BUG", fields.getValue("category").jsonPrimitive.content)
        assertEquals("Histórico de relatórios", fields.getValue("screen").jsonPrimitive.content)
        assertEquals("Travou ao abrir", fields.getValue("description").jsonPrimitive.content)
    }

    @Test
    fun `decodes the support response ignoring the echoed fields`() {
        val body = """
            { "id": 12, "category": "BUG", "screen": "Início", "description": "x", "image": null }
        """.trimIndent()

        assertEquals(12L, ApiClient.json.decodeFromString<ReportProblemResponse>(body).id)
    }

    @Test
    fun `decodes the categories list`() {
        val body = """[{ "code": "LOGIN_AND_ACCOUNT", "label": "Login e conta" }]"""

        val categories = ApiClient.json.decodeFromString<List<ProblemCategoryResponse>>(body)

        assertEquals("LOGIN_AND_ACCOUNT", categories.single().code)
        assertEquals("Login e conta", categories.single().label)
    }
}
