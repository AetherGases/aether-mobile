package com.aether.application.feature.settings.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aether.application.feature.profile.presentation.state.ProblemEvent
import com.aether.application.feature.profile.presentation.viewmodel.ProblemViewModel

private val screenshotMimeTypes = arrayOf("image/png", "image/jpeg")

/** Connects [ReportProblemScreen] to its [ProblemViewModel] and the screenshot picker. */
@Composable
fun ReportProblemRoot(
    viewModel: ProblemViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ProblemEvent.Submitted -> onBackClick()
            }
        }
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let(viewModel::onScreenshotSelected)
    }

    ReportProblemScreen(
        category = state.selectedCategory?.label.orEmpty(),
        categoryOptions = state.categories.map { it.label },
        description = state.description,
        attachedFileName = state.attachedFileName,
        onCategoryChanged = viewModel::onCategorySelected,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onSelectFileClicked = { picker.launch(screenshotMimeTypes) },
        onRemoveFileClicked = viewModel::onRemoveScreenshotClicked,
        onBackClicked = onBackClick,
        onSubmitClicked = viewModel::onSubmitClicked,
        categoriesLoading = state.isLoadingCategories,
        categoriesError = state.categoriesError,
        onRetryCategoriesClicked = viewModel::loadCategories,
        errorMessage = state.errorMessage,
        isSubmitting = state.isSubmitting,
        canSubmit = state.canSubmit && state.categoriesError == null
    )
}
