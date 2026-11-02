package com.aether.application.feature.profile.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.feature.profile.domain.model.MAX_PROBLEM_DESCRIPTION_LENGTH
import com.aether.application.feature.profile.domain.model.ProblemException
import com.aether.application.feature.profile.domain.model.screenshotMimeType
import com.aether.application.feature.profile.domain.repository.ProblemRepository
import com.aether.application.feature.profile.presentation.state.ProblemEvent
import com.aether.application.feature.profile.presentation.state.ProblemUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ProblemViewModel"

class ProblemViewModel(
    private val problemRepository: ProblemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProblemUiState())
    val uiState: StateFlow<ProblemUiState> = _uiState.asStateFlow()

    private val _events = Channel<ProblemEvent>(Channel.BUFFERED)
    val events: Flow<ProblemEvent> = _events.receiveAsFlow()

    private var screenshotUri: Uri? = null

    init {
        loadCategories()
    }

    fun loadCategories() {
        _uiState.update { it.copy(isLoadingCategories = true, categoriesError = null) }
        viewModelScope.launch {
            try {
                val categories = problemRepository.getCategories()
                _uiState.update { it.copy(categories = categories, isLoadingCategories = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load categories", e)
                _uiState.update { it.copy(isLoadingCategories = false, categoriesError = e.userMessage()) }
            }
        }
    }

    fun onCategorySelected(label: String) {
        val category = _uiState.value.categories.firstOrNull { it.label == label } ?: return
        _uiState.update { it.copy(selectedCategory = category, errorMessage = null) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description.take(MAX_PROBLEM_DESCRIPTION_LENGTH)) }
    }

    fun onScreenshotSelected(uri: Uri) {
        val name = problemRepository.displayName(uri.toString())
        if (name == null || screenshotMimeType(name) == null) {
            _uiState.update { it.copy(errorMessage = "Selecione uma imagem PNG, JPG ou JPEG.") }
            return
        }
        screenshotUri = uri
        _uiState.update { it.copy(attachedFileName = name, errorMessage = null) }
    }

    fun onRemoveScreenshotClicked() {
        screenshotUri = null
        _uiState.update { it.copy(attachedFileName = null) }
    }

    fun onSubmitClicked() {
        val state = _uiState.value
        val category = state.selectedCategory
        if (state.isSubmitting) return
        if (category == null || state.description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Escolha a categoria e descreva o problema.") }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                problemRepository.reportProblem(
                    categoryCode = category.code,
                    description = state.description.trim(),
                    screenshotUri = screenshotUri?.toString(),
                    screenshotName = state.attachedFileName
                )
                _uiState.update { it.copy(isSubmitting = false) }
                _events.send(ProblemEvent.Submitted)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to report problem", e)
                // The form keeps what the user typed so a retry is one tap
                _uiState.update { it.copy(isSubmitting = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun Exception.userMessage(): String =
        (this as? ProblemException)?.message ?: "Algo deu errado. Tente novamente."
}
