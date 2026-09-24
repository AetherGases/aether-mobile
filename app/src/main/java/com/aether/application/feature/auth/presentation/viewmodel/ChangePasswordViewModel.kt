package com.aether.application.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.feature.auth.domain.model.DefaultPasswordRules
import com.aether.application.feature.auth.domain.usecase.ChangePasswordUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val email: String,
    private val key: String,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    private val _events = Channel<ChangePasswordEvent>(Channel.BUFFERED)
    val events: Flow<ChangePasswordEvent> = _events.receiveAsFlow()

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    fun onSubmitClick() {
        if (_uiState.value.isConfirmStep) onChangePasswordClick() else onConfirmClick()
    }

    fun onBackClick() {
        if (_uiState.value.isConfirmStep) {
            _uiState.update { it.copy(isConfirmStep = false, confirmPassword = "", errorMessage = null) }
        } else {
            viewModelScope.launch { _events.send(ChangePasswordEvent.NavigateBack) }
        }
    }

    private fun onConfirmClick() {
        val password = _uiState.value.password

        if (DefaultPasswordRules.any { !it.isSatisfiedBy(password) }) {
            _uiState.update { it.copy(errorMessage = "A senha não atende aos requisitos!") }
            return
        }

        _uiState.update { it.copy(isConfirmStep = true, errorMessage = null) }
    }

    private fun onChangePasswordClick() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            changePasswordUseCase.invoke(email, key, state.password, state.confirmPassword)
                .onSuccess {
                    _events.send(ChangePasswordEvent.PasswordChanged)
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

data class ChangePasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val isConfirmStep: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ChangePasswordEvent {
    data object PasswordChanged : ChangePasswordEvent
    data object NavigateBack : ChangePasswordEvent
}
