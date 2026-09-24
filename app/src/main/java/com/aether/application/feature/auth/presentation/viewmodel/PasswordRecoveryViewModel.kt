package com.aether.application.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.feature.auth.domain.repository.RecoveryCodeStorage
import com.aether.application.feature.auth.domain.usecase.RequestPasswordRecoveryUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class PasswordRecoveryViewModel(
    private val requestPasswordRecoveryUseCase: RequestPasswordRecoveryUseCase,
    private val recoveryCodeStorage: RecoveryCodeStorage
): ViewModel() {
    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState.asStateFlow()

    private val _events = Channel<SendCodeEvent>(Channel.BUFFERED)
    val events: Flow<SendCodeEvent> = _events.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onSendCodeClick(email: String = _uiState.value.email) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            requestPasswordRecoveryUseCase.invoke(email)
                .onSuccess {
                    recoveryCodeStorage.saveEmail(email.trim())
                    _events.send(SendCodeEvent.CodeSent)
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Erro inesperado, tente novamente!")
                    }
                }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

data class RecoveryUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SendCodeEvent {
    data object CodeSent : SendCodeEvent
}
