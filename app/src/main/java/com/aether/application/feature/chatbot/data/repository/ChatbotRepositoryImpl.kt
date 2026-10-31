package com.aether.application.feature.chatbot.data.repository

import android.util.Log
import com.aether.application.R
import com.aether.application.core.database.ProfileLocalDataSource
import com.aether.application.core.network.readErrorMessage
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.uiText
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
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_NOT_FOUND

private const val TAG = "ChatbotRepository"

// Not in HttpURLConnection
private const val HTTP_UNPROCESSABLE_ENTITY = 422

class ChatbotRepositoryImpl(
    private val chatbotApi: ChatbotApi,
    private val profileApi: ProfileApi,
    private val profileLocalDataSource: ProfileLocalDataSource,
) : ChatbotRepository {

    private class Identity(val userId: Long, val companyId: Long)

    private val identityLock = Mutex()

    override suspend fun getConversations(): List<Conversation> = call {
        val identity = identity()
        try {
            chatbotApi.getSessions(identity.userId.toString()).map { Conversation(it.id, it.name) }
        } catch (e: HttpException) {
            // The hub answers 404 instead of an empty list for a user without sessions
            if (e.code() != HTTP_NOT_FOUND) throw e
            Log.d(TAG, "No conversations yet (404)")
            emptyList()
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
            conversationName = response.sessionName,
            turn = ChatTurn(input = response.inputMessage, output = response.outputMessage),
        )
    }

    // The hub needs the ids from the user's profile and checks them against the access token.
    // Both are stored at login; sessions from before the enterprise was stored fetch it once.
    private suspend fun identity(): Identity = identityLock.withLock {
        val userId = profileLocalDataSource.getProfile()?.id
        val companyId = profileLocalDataSource.getEnterprise()?.id
        if (userId != null && companyId != null) return@withLock Identity(userId, companyId)

        val profile = profileApi.getUserProfile()
        val enterprise = profile.toLocalEnterprise()
            ?: throw ChatbotException(uiText(R.string.chatbot_error_company_not_found))
        profileLocalDataSource.saveEnterprise(enterprise)
        Identity(userId = profile.id, companyId = enterprise.id)
    }

    private suspend fun <T> call(block: suspend () -> T): T =
        try {
            block()
        } catch (e: ChatbotException) {
            throw e
        } catch (e: HttpException) {
            Log.e(TAG, "Hub request failed (${e.code()})", e)
            throw ChatbotException(e.userMessage(), e)
        } catch (e: IOException) {
            Log.e(TAG, "Hub unreachable", e)
            throw ChatbotException(uiText(R.string.common_error_no_connection_retry), e)
        }

    // The hub's `detail` texts are in English, so these statuses get their own messages
    private fun HttpException.userMessage(): UiText = when (code()) {
        HTTP_BAD_GATEWAY, HTTP_UNPROCESSABLE_ENTITY ->
            uiText(R.string.chatbot_error_no_answer)
        HTTP_BAD_REQUEST -> uiText(R.string.chatbot_error_bad_message)
        else -> readErrorMessage()?.let(::uiText) ?: uiText(R.string.chatbot_error_generic)
    }
}
