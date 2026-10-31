package com.aether.application.feature.chatbot.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.R
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.utils.UiText
import com.aether.application.core.utils.uiText
import com.aether.application.feature.auth.domain.model.AppPermission
import com.aether.application.feature.chatbot.domain.model.ChatbotException
import com.aether.application.feature.chatbot.domain.repository.ChatbotRepository
import com.aether.application.feature.chatbot.domain.repository.SavedConversationsRepository
import com.aether.application.feature.chatbot.presentation.state.ChatbotUiState
import com.aether.core.ui.components.ChatMessage
import com.aether.core.ui.components.ChatSender
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ChatbotViewModel"

// The hub rejects longer messages
private const val MAX_INPUT_LENGTH = 8192

private val SUGGESTIONS = listOf(
    R.string.chatbot_suggestion_how_it_works,
    R.string.chatbot_suggestion_seal,
    R.string.chatbot_suggestion_reduce,
    R.string.chatbot_suggestion_create_report,
)

class ChatbotViewModel(
    private val chatbotRepository: ChatbotRepository,
    private val savedConversationsRepository: SavedConversationsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatbotUiState(suggestionRes = SUGGESTIONS))
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    private var nextMessageId = 0L

    init {
        viewModelScope.launch {
            val session = sessionManager.getSession()
            val role = if (sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)) {
                uiText(R.string.chatbot_role_manager)
            } else {
                uiText(R.string.chatbot_role_employee)
            }
            _uiState.update {
                it.copy(
                    userName = session?.name.orEmpty(),
                    userRole = role,
                    avatarUrl = session?.avatar
                )
            }
            loadPinnedConversations()
            loadConversations()
        }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text.take(MAX_INPUT_LENGTH), errorMessage = null) }
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
                title = uiText(R.string.chatbot_new_conversation),
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

        val conversation = state.conversations.firstOrNull { it.id == conversationId } ?: return
        _uiState.update { it.copy(isLoadingConversation = true) }
        viewModelScope.launch {
            try {
                val turns = chatbotRepository.getMessages(conversationId)
                _uiState.update {
                    it.copy(
                        title = uiText(conversation.name),
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
            _uiState.update { it.copy(errorMessage = uiText(R.string.chatbot_error_save_needs_message)) }
            return
        }

        viewModelScope.launch {
            try {
                if (state.isConversationPinned) {
                    savedConversationsRepository.unpin(conversationId)
                } else {
                    savedConversationsRepository.pin(conversationId)
                }
                loadPinnedConversations()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update pinned conversation $conversationId", e)
                _uiState.update { it.copy(errorMessage = uiText(R.string.chatbot_error_update_saved)) }
            }
        }
    }

    private fun send(rawText: String) {
        val text = rawText.trim().take(MAX_INPUT_LENGTH)
        val state = _uiState.value
        if (text.isEmpty() || state.isSending || state.isLoadingConversation) return

        val conversationId = state.activeConversationId
        val userMessage = newMessage(ChatSender.USER, text)
        _uiState.update {
            it.copy(messages = it.messages + userMessage, inputText = "", isSending = true, errorMessage = null)
        }

        viewModelScope.launch {
            try {
                val reply = chatbotRepository.sendMessage(conversationId, text)
                _uiState.update {
                    it.copy(
                        messages = it.messages + newMessage(ChatSender.AEKO, reply.turn.output),
                        activeConversationId = reply.conversationId,
                        title = uiText(reply.conversationName),
                        isSending = false
                    )
                }
                // The hub just created the session, list it in the drawer
                if (conversationId == null) loadConversations()
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

    private suspend fun loadConversations() {
        try {
            val conversations = chatbotRepository.getConversations()
            _uiState.update { state ->
                val activeName = conversations.firstOrNull { it.id == state.activeConversationId }?.name
                state.copy(conversations = conversations, title = activeName?.let(::uiText) ?: state.title)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load conversations", e)
        }
    }

    private suspend fun loadPinnedConversations() {
        try {
            val pinned = savedConversationsRepository.getPinnedIds()
            _uiState.update { it.copy(pinnedConversationIds = pinned) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load pinned conversations", e)
        }
    }

    private fun newMessage(sender: ChatSender, text: String) =
        ChatMessage(id = "${nextMessageId++}", sender = sender, text = text)

    private fun Exception.userMessage(): UiText =
        (this as? ChatbotException)?.uiText ?: uiText(R.string.common_something_went_wrong)
}
