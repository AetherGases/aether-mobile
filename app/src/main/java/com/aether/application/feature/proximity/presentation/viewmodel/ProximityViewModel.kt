package com.aether.application.feature.proximity.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.feature.proximity.domain.ProximityStatus
import com.aether.application.feature.proximity.domain.WorkplaceProximityChecker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ProximityViewModel"

data class ProximityUiState(
    /** Null until the first check finishes. */
    val status: ProximityStatus? = null,
    val isChecking: Boolean = false,
) {
    val isBlocked: Boolean
        get() = status != null && status != ProximityStatus.Allowed
}

class ProximityViewModel(
    private val proximityChecker: WorkplaceProximityChecker
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProximityUiState())
    val uiState: StateFlow<ProximityUiState> = _uiState.asStateFlow()

    private var checkJob: Job? = null

    fun check() {
        if (checkJob?.isActive == true) return
        checkJob = viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true) }
            val status = try {
                proximityChecker.check()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check workplace proximity", e)
                ProximityStatus.LocationUnavailable
            }
            _uiState.update { it.copy(status = status, isChecking = false) }
        }
    }

    /** Forgets the last result, e.g. after logging out. */
    fun reset() {
        checkJob?.cancel()
        _uiState.value = ProximityUiState()
    }
}
