package com.aether.application.feature.chatbot.data.remote

import com.aether.application.feature.chatbot.data.remote.dto.ChatMessageDto
import com.aether.application.feature.chatbot.data.remote.dto.SendMessageRequest
import com.aether.application.feature.chatbot.data.remote.dto.SendMessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// ms-aeko-hub, routed by the gateway under /hub
private const val HUB = "hub/ai"

interface ChatbotApi {

    @GET("$HUB/session/{sessionId}/messages")
    suspend fun getMessages(@Path("sessionId") sessionId: String): List<ChatMessageDto>

    @POST("$HUB/user/session/message")
    suspend fun sendMessage(@Body request: SendMessageRequest): SendMessageResponse
}
