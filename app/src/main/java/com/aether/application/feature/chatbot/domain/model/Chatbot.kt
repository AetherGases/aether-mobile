package com.aether.application.feature.chatbot.domain.model

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

/**
 * The assistant's answer to a message. [conversationId] is null when the backend didn't say
 * which session the message was stored in (new conversations, see ChatbotApi).
 */
data class ChatReply(
    val conversationId: String?,
    val turn: ChatTurn,
)

/** A failure with a message that is safe to show to the user. */
class ChatbotException(message: String, cause: Throwable? = null) : Exception(message, cause)
