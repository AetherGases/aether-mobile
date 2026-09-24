package com.aether.application.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.feature.auth.domain.usecase.RequestPasswordRecoveryUseCase
import com.aether.application.feature.auth.domain.usecase.VerifyCodeUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerificationViewModel(
    private val email: String,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val requestPasswordRecoveryUseCase: RequestPasswordRecoveryUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private val _events = Channel<VerificationEvent>(Channel.BUFFERED)
    val events: Flow<VerificationEvent> = _events.receiveAsFlow()

    fun onCodeChange(code: List<String>) {
        _uiState.update { it.copy(code = code) }
    }

    fun onVerifyClick(code: String = _uiState.value.code.joinToString(separator = "")) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            verifyCodeUseCase.invoke(email, code)
                .onSuccess { response ->
                    _events.send(VerificationEvent.Verified(response.key))
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Erro inesperado, tente novamente!")
                    }
                }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onResendClick() {
        viewModelScope.launch {
            requestPasswordRecoveryUseCase.invoke(email)
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.message ?: "Erro inesperado, tente novamente!")
                    }
                }
        }
    }
}

data class VerificationUiState(
    val code: List<String> = List(6) { "" },
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface VerificationEvent {
    data class Verified(val key: String) : VerificationEvent
}
