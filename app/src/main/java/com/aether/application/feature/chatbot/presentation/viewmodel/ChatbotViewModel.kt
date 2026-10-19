package com.aether.application.feature.chatbot.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.domain.model.AppPermission
import com.aether.application.feature.chatbot.domain.model.ChatbotException
import com.aether.application.feature.chatbot.domain.model.Conversation
import com.aether.application.feature.chatbot.domain.repository.ChatbotRepository
import com.aether.application.feature.chatbot.domain.repository.SavedConversationsRepository
import com.aether.application.feature.chatbot.presentation.state.ChatbotUiState
import com.aether.application.feature.chatbot.presentation.state.NEW_CONVERSATION_TITLE
import com.aether.core.ui.components.ChatMessage
import com.aether.core.ui.components.ChatSender
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ChatbotViewModel"
private const val TITLE_MAX_LENGTH = 60

private val SUGGESTIONS = listOf(
    "Como funciona o Aether?",
    "O que é o Selo Paraná?",
    "Como reduzir minhas emissões?",
    "Como criar um relatório?",
)

class ChatbotViewModel(
    private val chatbotRepository: ChatbotRepository,
    private val savedConversationsRepository: SavedConversationsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatbotUiState(suggestions = SUGGESTIONS))
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    private var nextMessageId = 0L

    init {
        viewModelScope.launch {
            val session = sessionManager.getSession()
            val role = if (sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)) {
                "Gerente"
            } else {
                "Funcionário"
            }
            _uiState.update {
                it.copy(
                    userName = session?.name.orEmpty(),
                    userRole = role,
                    avatarUrl = session?.avatar
                )
            }
            loadSavedConversations()
        }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onConversationSearchQueryChanged(query: String) {
        _uiState.update { it.copy(conversationSearchQuery = query) }
    }

    fun onSendClicked() = send(_uiState.value.inputText)

    fun onSuggestionClicked(suggestion: String) = send(suggestion)

    fun onNewConversationClick() {
        if (_uiState.value.isSending) return
        _uiState.update {
            it.copy(
                title = NEW_CONVERSATION_TITLE,
                messages = emptyList(),
                activeConversationId = null,
                inputText = ""
            )
        }
    }

    fun onConversationClick(conversationId: String) {
        val state = _uiState.value
        if (state.isSending || state.isLoadingConversation) return
        if (conversationId == state.activeConversationId) return

        val conversation = state.savedConversations.firstOrNull { it.id == conversationId } ?: return
        _uiState.update { it.copy(isLoadingConversation = true) }
        viewModelScope.launch {
            try {
                val turns = chatbotRepository.getMessages(conversationId)
                _uiState.update {
                    it.copy(
                        title = conversation.name,
                        activeConversationId = conversationId,
                        messages = turns.flatMap { turn ->
                            listOf(
                                newMessage(ChatSender.USER, turn.input),
                                newMessage(ChatSender.AEKO, turn.output)
                            )
                        },
                        isLoadingConversation = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load conversation $conversationId", e)
                _uiState.update {
                    it.copy(isLoadingConversation = false, errorMessage = e.userMessage())
                }
            }
        }
    }

    fun onBookmarkClicked() {
        val state = _uiState.value
        val conversationId = state.activeConversationId
        if (conversationId == null) {
            val reason = if (state.messages.isEmpty()) {
                "Envie uma mensagem para poder salvar a conversa."
            } else {
                "Não foi possível salvar esta conversa agora."
            }
            _uiState.update { it.copy(errorMessage = reason) }
            return
        }

        viewModelScope.launch {
            try {
                if (state.isConversationSaved) {
                    savedConversationsRepository.remove(conversationId)
                } else {
                    savedConversationsRepository.save(Conversation(conversationId, state.title))
                }
                loadSavedConversations()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update saved conversation $conversationId", e)
                _uiState.update { it.copy(errorMessage = "Não foi possível atualizar a conversa salva.") }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun send(rawText: String) {
        val text = rawText.trim()
        val state = _uiState.value
        if (text.isEmpty() || state.isSending || state.isLoadingConversation) return

        val conversationId = state.activeConversationId
        val userMessage = newMessage(ChatSender.USER, text)
        _uiState.update {
            it.copy(messages = it.messages + userMessage, inputText = "", isSending = true)
        }

        viewModelScope.launch {
            try {
                val reply = chatbotRepository.sendMessage(conversationId, text)
                _uiState.update {
                    it.copy(
                        messages = it.messages + newMessage(ChatSender.AEKO, reply.turn.output),
                        activeConversationId = reply.conversationId,
                        title = if (conversationId == null) text.toTitle() else it.title,
                        isSending = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send message", e)
                // Take the message back so the user can retry without duplicating it
                _uiState.update {
                    it.copy(
                        messages = it.messages - userMessage,
                        inputText = it.inputText.ifEmpty { text },
                        isSending = false,
                        errorMessage = e.userMessage()
                    )
                }
            }
        }
    }

    private suspend fun loadSavedConversations() {
        try {
            val saved = savedConversationsRepository.getSaved()
            _uiState.update { it.copy(savedConversations = saved) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load saved conversations", e)
        }
    }

    private fun newMessage(sender: ChatSender, text: String) =
        ChatMessage(id = "${nextMessageId++}", sender = sender, text = text)

    private fun String.toTitle(): String {
        val title = split(Regex("\\s+")).joinToString(" ")
        return if (title.length <= TITLE_MAX_LENGTH) title else title.take(TITLE_MAX_LENGTH).trimEnd() + "..."
    }

    private fun Exception.userMessage(): String =
        (this as? ChatbotException)?.message ?: "Algo deu errado. Tente novamente."
}
