package com.aether.application.feature.chatbot.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    @SerialName("input_message") val inputMessage: String,
    @SerialName("output_message") val outputMessage: String,
)

@Serializable
data class SendMessageRequest(
    @SerialName("id_session") val idSession: String? = null,
    val input: String,
    @SerialName("id_external_user") val idExternalUser: Long,
    @SerialName("id_external_company") val idExternalCompany: Long,
)

@Serializable
data class SendMessageResponse(
    @SerialName("input_message") val inputMessage: String,
    @SerialName("output_message") val outputMessage: String,
    // TODO: the hub doesn't say which session a message went to, so a new conversation can't continue
    @SerialName("id_session") val idSession: String? = null,
)
