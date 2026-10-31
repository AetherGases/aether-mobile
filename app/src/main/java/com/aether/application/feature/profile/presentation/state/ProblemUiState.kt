package com.aether.application.feature.profile.presentation.state

import com.aether.application.feature.profile.domain.model.ProblemCategory

data class ProblemUiState(
    val categories: List<ProblemCategory> = emptyList(),
    val isLoadingCategories: Boolean = true,
    /** Set when the categories failed to load; the form can't be submitted until a retry works. */
    val categoriesError: String? = null,
    val selectedCategory: ProblemCategory? = null,
    val description: String = "",
    val attachedFileName: String? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val canSubmit: Boolean
        get() = selectedCategory != null && description.isNotBlank() && !isSubmitting
}

sealed interface ProblemEvent {
    data object Submitted : ProblemEvent
}
