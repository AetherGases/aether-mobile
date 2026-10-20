package com.aether.application.feature.chatbot.data.repository

import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.network.readErrorMessage
import com.aether.application.feature.auth.data.remote.ProfileApi
import com.aether.application.feature.chatbot.data.remote.ChatbotApi
import com.aether.application.feature.chatbot.data.remote.dto.SendMessageRequest
import com.aether.application.feature.chatbot.domain.model.ChatReply
import com.aether.application.feature.chatbot.domain.model.ChatTurn
import com.aether.application.feature.chatbot.domain.model.ChatbotException
import com.aether.application.feature.chatbot.domain.model.Conversation
import com.aether.application.feature.chatbot.domain.repository.ChatbotRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection.HTTP_BAD_GATEWAY
import java.net.HttpURLConnection.HTTP_NOT_FOUND

class ChatbotRepositoryImpl(
    private val chatbotApi: ChatbotApi,
    private val profileApi: ProfileApi,
    private val sessionManager: SessionManager,
) : ChatbotRepository {

    private class Identity(val email: String?, val userId: Long, val companyId: Long)

    private val identityLock = Mutex()
    private var identity: Identity? = null

    override suspend fun getConversations(): List<Conversation> = call {
        val identity = identity()
        try {
            chatbotApi.getSessions(identity.userId.toString()).map { Conversation(it.id, it.name) }
        } catch (e: HttpException) {
            // The hub answers 404 instead of an empty list for a user without sessions
            if (e.code() == HTTP_NOT_FOUND) emptyList() else throw e
        }
    }

    override suspend fun getMessages(conversationId: String): List<ChatTurn> = call {
        chatbotApi.getMessages(conversationId).map {
            ChatTurn(input = it.inputMessage, output = it.outputMessage)
        }
    }

    override suspend fun sendMessage(conversationId: String?, input: String): ChatReply = call {
        val identity = identity()
        val response = chatbotApi.sendMessage(
            SendMessageRequest(
                idSession = conversationId,
                input = input,
                idExternalUser = identity.userId,
                idExternalCompany = identity.companyId,
            )
        )
        ChatReply(
            conversationId = response.idSession,
            turn = ChatTurn(input = response.inputMessage, output = response.outputMessage),
        )
    }

    // The hub needs the ids from the user's profile and checks them against the access token.
    // The cache is tied to the logged-in account so a new login doesn't reuse the previous ids.
    private suspend fun identity(): Identity = identityLock.withLock {
        val email = sessionManager.getSession()?.email
        identity?.takeIf { it.email == email } ?: run {
            val profile = profileApi.getUserProfile()
            val companyId = profile.enterprise?.id
                ?: throw ChatbotException("Não foi possível identificar a empresa do usuário.")
            Identity(email = email, userId = profile.id, companyId = companyId).also { identity = it }
        }
    }

    private suspend fun <T> call(block: suspend () -> T): T =
        try {
            block()
        } catch (e: ChatbotException) {
            throw e
        } catch (e: HttpException) {
            throw ChatbotException(e.userMessage(), e)
        } catch (e: IOException) {
            throw ChatbotException("Sem conexão com o servidor. Tente novamente.", e)
        }

    private fun HttpException.userMessage(): String =
        if (code() == HTTP_BAD_GATEWAY) {
            "A Aeko não conseguiu responder. Tente reformular a pergunta."
        } else {
            readErrorMessage() ?: "Não foi possível falar com a Aeko. Tente novamente."
        }
}
