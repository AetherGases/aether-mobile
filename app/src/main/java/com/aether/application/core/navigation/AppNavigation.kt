package com.aether.application.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.aether.application.BuildConfig
import com.aether.application.feature.auth.presentation.screen.LoginScreen
import com.aether.application.feature.auth.presentation.viewmodel.LoginEvent
import com.aether.application.feature.auth.presentation.viewmodel.LoginViewModel
import com.aether.application.feature.home.presentation.screen.EmployeeHomeScreen
import com.aether.application.feature.home.presentation.screen.ManagerHomeScreen
import com.aether.application.feature.qa.presentation.screen.ServerConfigScreen
import com.aether.application.feature.qa.presentation.viewmodel.ServerConfigEvent
import com.aether.application.feature.qa.presentation.viewmodel.ServerConfigViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = AuthGraph,
        modifier = modifier,
    ) {
        composable<ServerConfigRoute> {
            val viewModel = koinViewModel<ServerConfigViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ServerConfigEvent.Saved -> navController.popBackStack()
                    }
                }
            }

            ServerConfigScreen(
                domain = uiState.domainInput,
                onDomainChange = viewModel::onDomainInputChange,
                savedDomains = uiState.savedDomains,
                onSaveClick = viewModel::onSaveClick
            )
        }

        navigation<AuthGraph>(startDestination = LoginRoute) {
            composable<LoginRoute> {
                val viewModel = koinViewModel<LoginViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is LoginEvent.LoggedIn ->
                                navController.navigate(EmployeeGraph) {
                                    popUpTo<AuthGraph> { inclusive = true }
                                }
                        }
                    }
                }

                LoginScreen(
                    onLoginClick = viewModel::onLoginClick,
                    onForgotPasswordClick = { /* TODO */ },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onChangeServerClick = if (BuildConfig.DEBUG) {
                        { navController.navigate(ServerConfigRoute) }
                    } else {
                        null
                    },
                )
            }
        }
        navigation<EmployeeGraph>(startDestination = EmployeeHomeRoute) {
            composable<EmployeeHomeRoute> {
                EmployeeHomeScreen(
                    userName = TODO(),
                    userLastName = TODO(),
                    avatarUrl = TODO(),
                    hasUnreadNotifications = TODO(),
                    lastSubmittedLabel = TODO(),
                    reportingPeriodLabel = TODO(),
                    reportStatusLabel = TODO(),
                    reportsCount = TODO(),
                    unitEmissionsValue = TODO(),
                    unitEmissionsChangeLabel = TODO(),
                    sealLevelPercent = TODO(),
                    sealCriteriaLabel = TODO(),
                    quickActions = TODO(),
                    recentReports = TODO(),
                    onNotificationsClick = TODO(),
                    onSettingsClick = TODO(),
                    onViewHistoryClick = TODO(),
                    onSeeAllReportsClick = TODO(),
                    onReportMenuClick = TODO(),
                    onHomeClick = TODO(),
                    modifier = TODO()
                )
            }
        }

        navigation<ManagerGraph>(startDestination = ManagerHomeRoute) {
            composable<ManagerHomeRoute> {
                ManagerHomeScreen(
                    userName = TODO(),
                    userLastName = TODO(),
                    avatarUrl = TODO(),
                    hasUnreadNotifications = TODO(),
                    lastSubmittedLabel = TODO(),
                    totalEmissionsValue = TODO(),
                    reductionAchievedLabel = TODO(),
                    reviewedReportsCount = TODO(),
                    unitEmissionsValue = TODO(),
                    unitEmissionsChangeLabel = TODO(),
                    sealLevelPercent = TODO(),
                    sealCriteriaLabel = TODO(),
                    quickActions = TODO(),
                    recentReports = TODO(),
                    onNotificationsClick = TODO(),
                    onSettingsClick = TODO(),
                    onViewHistoryClick = TODO(),
                    onSeeAllReportsClick = TODO(),
                    onReportMenuClick = TODO(),
                    modifier = TODO()
                )
            }
        }
    }
}
