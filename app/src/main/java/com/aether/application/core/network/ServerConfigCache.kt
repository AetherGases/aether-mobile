package com.aether.application.core.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServerConfigCache(
    serverConfigStorage: ServerConfigStorage
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _selectedDomain = MutableStateFlow<String?>(null)
    val selectedDomain: StateFlow<String?> = _selectedDomain.asStateFlow()

    init {
        scope.launch {
            serverConfigStorage.observeSelectedDomain().collect { domain ->
                _selectedDomain.value = domain
            }
        }
    }
}
