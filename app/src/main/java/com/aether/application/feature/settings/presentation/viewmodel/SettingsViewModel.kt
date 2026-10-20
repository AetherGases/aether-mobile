package com.aether.application.feature.settings.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.domain.model.AppPermission
import com.aether.application.feature.home.domain.repository.HomeRepository
import com.aether.application.feature.settings.domain.model.ThemePreference
import com.aether.application.feature.settings.domain.repository.SettingsRepository
import com.aether.application.feature.settings.presentation.state.SettingsUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "SettingsViewModel"
private const val NO_ENTRY_DATE = "—"

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val homeRepository: HomeRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val entryDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(
                    notificationsEnabled = settings.notificationsEnabled,
                    emailNotificationsEnabled = settings.emailNotificationsEnabled,
                    theme = settings.theme
                ) }
            }
        }

        viewModelScope.launch {
            val session = sessionManager.getSession()
            _uiState.update { it.copy(
                userName = session?.name.orEmpty(),
                avatarUrl = session?.avatar?.takeIf { avatar -> avatar.isNotEmpty() },
                entryDate = session?.createdAt
                    ?.atZone(ZoneId.systemDefault())
                    ?.format(entryDateFormatter)
                    ?: NO_ENTRY_DATE
            ) }

            try {
                val count = homeRepository.getInventoryCount()
                val reportsCount = if (sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)) {
                    count.reviewedCount
                } else {
                    count.submittedCount
                }
                _uiState.update { it.copy(reportsCount = reportsCount) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load reports count", e)
            }
        }
    }

    fun onNotificationsChanged(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationsEnabled(enabled) }
    }

    fun onEmailNotificationsChanged(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setEmailNotificationsEnabled(enabled) }
    }

    fun onThemeSelected(theme: ThemePreference) {
        viewModelScope.launch { settingsRepository.setTheme(theme) }
    }

    // AppNavigation reacts to the auth state and goes back to the auth graph
    fun onLogoutClick() {
        viewModelScope.launch { sessionManager.logout() }
    }
}
