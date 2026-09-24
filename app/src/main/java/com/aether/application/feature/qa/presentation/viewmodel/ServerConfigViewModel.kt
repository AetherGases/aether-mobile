package com.aether.application.feature.qa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.core.network.ServerConfigStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServerConfigUiState(
    val domainInput: String = "",
    val savedDomains: List<String> = emptyList()
)

sealed interface ServerConfigEvent {
    data object Saved : ServerConfigEvent
}

class ServerConfigViewModel(
    private val serverConfigStorage: ServerConfigStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServerConfigUiState())
    val uiState: StateFlow<ServerConfigUiState> = _uiState.asStateFlow()

    private val _events = Channel<ServerConfigEvent>(Channel.BUFFERED)
    val events: Flow<ServerConfigEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val savedDomains = serverConfigStorage.getSavedDomains()
            _uiState.update {
                it.copy(
                    domainInput = savedDomains.firstOrNull() ?: "",
                    savedDomains = savedDomains
                )
            }
        }
    }

    fun onDomainInputChange(domain: String) {
        _uiState.update { it.copy(domainInput = domain) }
    }

    fun onSaveClick() {
        val domain = _uiState.value.domainInput.trim()
        if (domain.isBlank()) return

        viewModelScope.launch {
            serverConfigStorage.saveDomain(domain)
            _uiState.update { it.copy(savedDomains = serverConfigStorage.getSavedDomains()) }
            _events.send(ServerConfigEvent.Saved)
        }
    }
}
