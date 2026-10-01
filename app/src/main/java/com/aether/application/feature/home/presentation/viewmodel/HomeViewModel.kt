package com.aether.application.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.aether.application.feature.home.presentation.screen.QuickAction
import com.aether.application.feature.home.presentation.screen.RecentReport
import com.aether.core.ui.components.EmployeeHeroCard
import com.aether.core.ui.components.HomeHeroCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val userName: String = "",
    val userLastName: String = "",
    val avatarUrl: String = "",
    val hasUnreadNotifications: Boolean = false,
    val heroCard: HomeHeroCard = EmployeeHeroCard(
        lastSubmittedLabel = "",
        reportingPeriodLabel = "",
        statusLabel = "",
        summaryCount = "",
    ),
    val unitEmissionsValue: String = "",
    val unitEmissionsChangeLabel: String = "",
    val sealLevelPercent: String = "",
    val sealCriteriaLabel: String = "",
    val quickActions: List<QuickAction> = emptyList(),
    val recentReports: List<RecentReport> = emptyList(),
)

class HomeViewModel(

): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        TODO("Adicionar integração com backend")
    }

    fun onReportMenuClick(recentReport: RecentReport) {

    }

    fun onSeeAllReportsClick() {

    }

    fun onViewHistoryClick() {

    }

    fun onSettingsClick() {

    }

    fun onNotificationsClick() {

    }
}