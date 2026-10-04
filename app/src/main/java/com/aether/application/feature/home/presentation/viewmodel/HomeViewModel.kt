package com.aether.application.feature.home.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.R
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.domain.model.AppPermission
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.repository.HomeRepository
import com.aether.application.feature.home.presentation.components.QuickAction
import com.aether.application.feature.home.presentation.components.RecentReport
import com.aether.application.feature.home.presentation.model.toReportStatus
import com.aether.core.ui.components.EmployeeHeroCard
import com.aether.core.ui.components.HomeHeroCard
import com.aether.core.ui.components.ManagerHeroCard
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HomeUiState(
    val userName: String = "",
    val userLastName: String = "",
    val avatarUrl: String = "",
    val hasUnreadNotifications: Boolean = false, //TODO()
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

sealed interface HomeEvent {
    data object NavigateToHistory : HomeEvent
    data object NavigateToCalculator : HomeEvent
    data object NavigateToChatBot : HomeEvent
    data object NavigateToCreateReport : HomeEvent
    data class NavigateToReport(val reportId: Long) : HomeEvent
}

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val sessionManager: SessionManager
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Populate uiState
    init {
        viewModelScope.launch{
            val session = sessionManager.getSession()
                ?: throw Exception("nah man, u need to be authed")

            val (userName, userLastName) = if (" " in session.name) {
                session.name.split(" ", limit = 2)
            } else {
                listOf(session.name, "")
            }

            val avatar = session.avatar
            val unitEmissionsValue = homeRepository.getMonthlyUnitEmission()
            val unitEmissionsChangeLabel = homeRepository.getMonthlyUnitChange()
            val homeHeroCard = getHomeHeroCard()
            val recentReports = getRecentReports(
                userName = "$userName $userLastName",
                avatarUrl = avatar
            )
            val quickActions = getQuickActions()

            _uiState.update { it.copy(
                userName = userName,
                userLastName = userLastName,
                avatarUrl = avatar,
                heroCard = homeHeroCard,
                unitEmissionsValue = "$unitEmissionsValue",
                unitEmissionsChangeLabel = "$unitEmissionsChangeLabel% ↗ em relação ao período anterior",
                quickActions = quickActions,
                recentReports = recentReports
            ) }
        }
    }

    fun onReportClick(recentReport: RecentReport) {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToReport(recentReport.id))
        }
    }

    private suspend fun getHomeHeroCard(): HomeHeroCard {
        if (sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)) {
            val lastReviewedDate = homeRepository.getLastReviewedInventory().createdAt
                .format(dateFormatter)
            val totalEmissions = homeRepository.getTotalEmissions()
            val reductionAchieved = homeRepository.getTotalReduction()
            val countReviewedInventories = homeRepository.countReviewedInventories()

            return ManagerHeroCard(
                    lastSubmittedLabel = lastReviewedDate,
                    totalEmissions = "$totalEmissions",
                    reductionAchieved = "$reductionAchieved%",
                    summaryCount = "$countReviewedInventories"
                )
        } else {
            val lastSubmittedInventory = homeRepository.getLastSubmittedInventory()
            val countSubmittedInventories = homeRepository.countAllSubmittedInvetories()
            val monthFormatter = DateTimeFormatter
                .ofPattern(
                    "MMM",
                    Locale.forLanguageTag("pt-BR")
                )
            val reportingPeriodLabel = buildString {
                append(lastSubmittedInventory.period.start.format(monthFormatter))
                append("-")
                append(lastSubmittedInventory.period.end.format(monthFormatter))
                append(" ")
                if (lastSubmittedInventory.period.start.year == lastSubmittedInventory.period.end.year) {
                    append(lastSubmittedInventory.period.end.year)
                } else {
                    append(lastSubmittedInventory.period.start.year)
                    append("-")
                    append(lastSubmittedInventory.period.end.year)
                }
            }

            return EmployeeHeroCard(
                lastSubmittedLabel = lastSubmittedInventory.createdAt.format(dateFormatter),
                reportingPeriodLabel = reportingPeriodLabel,
                statusLabel = lastSubmittedInventory.status.toReportStatus().name,
                summaryCount = "$countSubmittedInventories"
            )
        }
    }

    private suspend fun getRecentReports(userName: String, avatarUrl: String): List<RecentReport> {
        val recentInventories = mutableListOf<Inventory>()

        // Get creator permission inventories
        if (sessionManager.hasPermission(AppPermission.INVENTORY_CREATING)) {
            recentInventories.addAll(elements = homeRepository.getMyInventories(take = 5))
        }

        // Get analyst permission inventories
        if (sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)) {
            recentInventories.addAll(elements = homeRepository.getPendingInventories(take = 5))
        }

        return recentInventories
            .sortedByDescending { it.createdAt }
            .take(5)
            .map { inventory ->
                RecentReport(
                    id = inventory.id,
                    name = inventory.name,
                    ownerName = inventory.author ?: userName,
                    ownerAvatarUrl = inventory.authorImageUrl ?: avatarUrl,
                    createdAt = inventory.createdAt.format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ),
                    status = inventory.status.toReportStatus()
                )
            }
    }

    private suspend fun getQuickActions(): List<QuickAction> {
        val quickActions = mutableListOf<QuickAction>()
        if (sessionManager.hasPermission(AppPermission.INVENTORY_VIEW)) {
            quickActions.add(
                QuickAction(
                    label = "Histórico",
                    iconRes = R.drawable.ic_history_green,
                    iconSize = 60.dp,
                    offsetX = 3.dp,
                    offsetY = 0.dp,
                    backgroundColor = Color(0xFFECE2FD),
                    onClick = { viewModelScope.launch {
                        _events.send(HomeEvent.NavigateToHistory)
                    }}
                )
            )
        }
        if (sessionManager.hasPermission(AppPermission.INVENTORY_CREATING)) {
            quickActions.add(
                QuickAction(
                    label = "Criar relatório",
                    iconRes = R.drawable.ic_create_report,
                    iconSize = 60.dp,
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    backgroundColor = Color(0xFFE7FAF2),
                    onClick = { viewModelScope.launch {
                        _events.send(HomeEvent.NavigateToCreateReport)
                    }}
                )
            )
        }
        if (sessionManager.hasPermission(AppPermission.CALCULATOR)) {
            quickActions.add(
                QuickAction(
                    label = "Calcular CO2",
                    iconRes = R.drawable.ic_calculator_purple,
                    iconSize = 50.dp,
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    backgroundColor = Color(0xFFECE2FD),
                    onClick = { viewModelScope.launch {
                        _events.send(HomeEvent.NavigateToCalculator)
                    }}
                )
            )
        }
        if (sessionManager.hasPermission(AppPermission.CHATBOT)) {
            quickActions.add(
                QuickAction(
                    label = "Chatbot",
                    iconRes = R.drawable.ic_aeko,
                    iconSize = 90.dp,
                    offsetX = 3.dp,
                    offsetY = 10.dp,
                    backgroundColor = Color(0xFFECE2FD),
                    onClick = { viewModelScope.launch {
                        _events.send(HomeEvent.NavigateToChatBot)
                    }}
                )
            )
        }
        return quickActions
    }
}