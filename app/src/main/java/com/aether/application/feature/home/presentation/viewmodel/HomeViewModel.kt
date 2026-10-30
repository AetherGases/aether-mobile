package com.aether.application.feature.home.presentation.viewmodel

import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.application.R
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.domain.model.AppPermission
import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.home.domain.repository.HomeRepository
import com.aether.application.feature.home.presentation.components.QuickAction
import com.aether.application.feature.home.presentation.components.QuickActionContainer
import com.aether.application.feature.home.presentation.components.RecentReport
import com.aether.application.feature.home.presentation.model.toReportStatus
import com.aether.application.feature.profile.domain.repository.ProfileRepository
import com.aether.core.ui.components.EmployeeHeroCard
import com.aether.core.ui.components.HomeHeroCard
import com.aether.core.ui.components.ManagerHeroCard
import com.aether.core.ui.components.ReportsSummaryHomeCard
import com.aether.core.ui.components.SealProgressHomeCard
import com.aether.core.ui.components.UnitEmissionsHomeCard
import kotlinx.coroutines.CancellationException
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

private const val TAG = "HomeViewModel"

// Shown on a card while the user has no inventory yet
private const val NO_VALUE = "—"

enum class HomeLoadPhase { LOADING, READY, NO_PERMISSIONS }

data class HomeUiState(
    val phase: HomeLoadPhase = HomeLoadPhase.LOADING,
    val userName: String = "",
    val userLastName: String = "",
    val avatarUrl: String = "",
    val hasUnreadNotifications: Boolean = false, //TODO()
    val cards: List<HomeHeroCard> = emptyList(),
    val quickActions: List<QuickAction> = emptyList(),
    val recentReports: List<RecentReport> = emptyList(),
    val cardsFailed: Boolean = false,
    val quickActionsFailed: Boolean = false,
    val isRefreshing: Boolean = false,
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
    private val profileRepository: ProfileRepository,
    private val sessionManager: SessionManager
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    init {
        loadFirstTime()
    }

    // Home only shows up once the profile and its permissions are loaded
    fun loadFirstTime() {
        viewModelScope.launch {
            _uiState.update { it.copy(phase = HomeLoadPhase.LOADING) }
            try {
                profileRepository.refreshProfile()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load the profile, back to login", e)
                // AppNavigation reacts to the auth state and goes back to the auth graph
                sessionManager.logout()
                return@launch
            }

            if (AppPermission.entries.none { sessionManager.hasPermission(it) }) {
                _uiState.update { it.copy(phase = HomeLoadPhase.NO_PERMISSIONS) }
                return@launch
            }

            loadHome()
            _uiState.update { it.copy(phase = HomeLoadPhase.READY) }
        }
    }

    fun refresh() {
        if (_uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                loadHome()
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    // Sections load independently: one failing keeps its previous data and doesn't block the others
    private suspend fun loadHome() {
        val session = try {
            sessionManager.getSession()
                ?: throw Exception("nah man, u need to be authed")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load home", e)
            return
        }

        val (userName, userLastName) = if (" " in session.name) {
            session.name.split(" ", limit = 2)
        } else {
            listOf(session.name, "")
        }
        val avatar = session.avatar

        val cards = loadSection("cards") { getHomeCards() }
        val recentReports = loadSection("recent reports") {
            getRecentReports(userName = "$userName $userLastName", avatarUrl = avatar)
        }
        val quickActions = loadSection("quick actions") { getQuickActions() }

        _uiState.update { it.copy(
            userName = userName,
            userLastName = userLastName,
            avatarUrl = avatar,
            cards = cards ?: it.cards,
            cardsFailed = cards == null,
            quickActions = quickActions ?: it.quickActions,
            quickActionsFailed = quickActions == null,
            recentReports = recentReports ?: it.recentReports
        ) }
    }

    private suspend fun <T> loadSection(name: String, block: suspend () -> T): T? =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load home $name", e)
            null
        }

    // AppNavigation reacts to the auth state and goes back to the auth graph
    fun onLogoutClick() {
        viewModelScope.launch { sessionManager.logout() }
    }

    fun onReportClick(recentReport: RecentReport) {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToReport(recentReport.id))
        }
    }

    // Cards load independently: one failing is left out and the others still show
    private suspend fun getHomeCards(): List<HomeHeroCard> {
        val isManager = sessionManager.hasPermission(AppPermission.INVENTORY_ANALYSIS)
        // The hero and summary cards share these, so they are fetched once
        val summary = loadSection("inventory summary") {
            val lastInventory = if (isManager) {
                homeRepository.getLastReviewedInventory()
            } else {
                homeRepository.getLastSubmittedInventory()
            }
            val inventoryCount = homeRepository.getInventoryCount().let {
                if (isManager) it.reviewedCount else it.submittedCount
            }
            lastInventory to inventoryCount
        }

        val cards = listOfNotNull(
            summary?.let { (lastInventory, inventoryCount) ->
                loadSection("hero card") { getHeroCard(isManager, lastInventory, inventoryCount) }
            },
            summary?.let { (lastInventory, inventoryCount) ->
                getReportsSummaryCard(isManager, lastInventory, inventoryCount)
            },
            loadSection("unit emissions card") { getUnitEmissionsCard() },
            getSealProgressCard()
        )
        // Nothing loaded counts as a failure, so the previous cards are kept
        check(cards.isNotEmpty()) { "No home card loaded" }
        return cards
    }

    private suspend fun getHeroCard(
        isManager: Boolean,
        lastInventory: Inventory?,
        inventoryCount: Int
    ): HomeHeroCard? {
        val lastDate = lastInventory?.createdAt?.format(dateFormatter) ?: NO_VALUE

        if (isManager) {
            val enterpriseInfo = homeRepository.getEnterpriseInfos()

            return ManagerHeroCard(
                lastSubmittedLabel = lastDate,
                totalEmissions = "${enterpriseInfo.totalEmissions}",
                reductionAchieved = "${enterpriseInfo.totalReductions}",
                summaryCount = "$inventoryCount"
            )
        }

        val monthFormatter = DateTimeFormatter
            .ofPattern(
                "MMM",
                Locale.forLanguageTag("pt-BR")
            )
        val reportingPeriodLabel = lastInventory?.period?.let { period ->
            buildString {
                append(period.start.format(monthFormatter))
                append("-")
                append(period.end.format(monthFormatter))
                append(" ")
                if (period.start.year == period.end.year) {
                    append(period.end.year)
                } else {
                    append(period.start.year)
                    append("-")
                    append(period.end.year)
                }
            }
        } ?: "Período não informado"

        return EmployeeHeroCard(
            lastSubmittedLabel = lastDate,
            reportingPeriodLabel = reportingPeriodLabel,
            statusLabel = lastInventory?.status?.toReportStatus()?.name ?: NO_VALUE,
            summaryCount = "$inventoryCount"
        )
    }

    private fun getReportsSummaryCard(
        isManager: Boolean,
        lastInventory: Inventory?,
        inventoryCount: Int
    ): HomeHeroCard? =
        ReportsSummaryHomeCard(
            title = if (isManager) "Relatórios analisados" else "Relatórios realizados",
            lastSubmittedLabel = lastInventory?.createdAt?.format(dateFormatter) ?: NO_VALUE,
            count = "$inventoryCount",
            onViewHistoryClick = {
                viewModelScope.launch { _events.send(HomeEvent.NavigateToHistory) }
            }
        )

    private suspend fun getUnitEmissionsCard(): HomeHeroCard? {
        val unitInfo = homeRepository.getMonthlyUnitInfos()
        return UnitEmissionsHomeCard(
            value = "${unitInfo.emissions}",
            unitLabel = "tCO₂e/mês",
            changeLabel = "${unitInfo.reductionPercentage}% ↗ em relação ao período anterior"
        )
    }

    // Optional card: if it fails to load the other cards still show
    private suspend fun getSealProgressCard(): HomeHeroCard? {
        val seal = try {
            homeRepository.getSealProgress()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load seal progress", e)
            return null
        }
        return SealProgressHomeCard(
            levelPercent = "${seal.percent}%",
            criteriaLabel = "${seal.criteriaMet} de ${seal.criteriaTotal} critérios atendidos"
        )
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
                    container = QuickActionContainer.PRIMARY,
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
                    container = QuickActionContainer.SECONDARY,
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
                    container = QuickActionContainer.PRIMARY,
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
                    container = QuickActionContainer.PRIMARY,
                    onClick = { viewModelScope.launch {
                        _events.send(HomeEvent.NavigateToChatBot)
                    }}
                )
            )
        }
        return quickActions
    }
}