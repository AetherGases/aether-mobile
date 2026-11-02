package com.aether.application.feature.chatbot.domain.model

import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.UserFacingException


/** A stored chat session with the assistant. */
data class Conversation(
    val id: String,
    val name: String,
)

/** One exchange: what the user asked and what the assistant answered. */
data class ChatTurn(
    val input: String,
    val output: String,
)

/** The assistant's answer to a message and the session it was stored in, with the name the hub gave it. */
data class ChatReply(
    val conversationId: String,
    val conversationName: String,
    val turn: ChatTurn,
)

/** A failure with a message that is safe to show to the user. */
class ChatbotException(uiText: UiText, cause: Throwable? = null) : UserFacingException(uiText, cause)
