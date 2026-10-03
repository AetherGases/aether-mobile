package com.aether.application.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.core.utils.userMessage
import com.aether.application.feature.auth.domain.usecase.ResendRecoveryCodeUseCase
import com.aether.application.feature.auth.domain.usecase.VerifyCodeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private const val RESEND_COOLDOWN_SECONDS = 60

class VerificationViewModel(
    private val email: String,
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val resendRecoveryCodeUseCase: ResendRecoveryCodeUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private val _events = Channel<VerificationEvent>(Channel.BUFFERED)
    val events: Flow<VerificationEvent> = _events.receiveAsFlow()

    private var resendCooldownJob: Job? = null

    init {
        startResendCooldown()
    }

    fun onCodeChange(code: List<String>) {
        _uiState.update { it.copy(code = code) }
    }

    fun onVerifyClick(code: String = _uiState.value.code.joinToString(separator = "")) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            verifyCodeUseCase.invoke(email, code)
                .onSuccess { key ->
                    _events.send(VerificationEvent.Verified(key))
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.userMessage())
                    }
                }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onResendClick() {
        val state = _uiState.value
        if (state.isResending || state.resendCooldownSeconds > 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true, errorMessage = null) }

            resendRecoveryCodeUseCase.invoke(email)
                .onSuccess { startResendCooldown() }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(errorMessage = throwable.userMessage())
                    }
                }

            _uiState.update { it.copy(isResending = false) }
        }
    }

    private fun startResendCooldown() {
        resendCooldownJob?.cancel()
        resendCooldownJob = viewModelScope.launch {
            for (secondsLeft in RESEND_COOLDOWN_SECONDS downTo 0) {
                _uiState.update { it.copy(resendCooldownSeconds = secondsLeft) }
                if (secondsLeft > 0) delay(1.seconds)
            }
        }
    }
}

data class VerificationUiState(
    val code: List<String> = List(6) { "" },
    val isLoading: Boolean = false,
    val isResending: Boolean = false,
    val resendCooldownSeconds: Int = 60,
    val errorMessage: String? = null
)

sealed interface VerificationEvent {
    data class Verified(val key: String) : VerificationEvent
}
