package com.aether.application.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.aether.application.BuildConfig
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.feature.auth.presentation.screen.LoginScreen
import com.aether.application.feature.auth.presentation.viewmodel.LoginEvent
import com.aether.application.feature.auth.presentation.viewmodel.LoginViewModel
import com.aether.application.feature.home.presentation.screen.HomeScreen
import com.aether.core.ui.components.EmployeeHeroCard
import com.aether.core.ui.components.ManagerHeroCard
import com.aether.application.feature.auth.presentation.viewmodel.SplashEvent
import com.aether.application.feature.auth.presentation.viewmodel.SplashViewModel
import com.aether.application.feature.auth.presentation.screen.SplashScreen
import com.aether.application.feature.qa.presentation.screen.ServerConfigScreen
import com.aether.application.feature.qa.presentation.viewmodel.ServerConfigEvent
import com.aether.application.feature.qa.presentation.viewmodel.ServerConfigViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val sessionManager = koinInject<SessionManager>()
    val isAuthenticated by sessionManager.authState.collectAsStateWithLifecycle()
    var wasAuthenticated by remember { mutableStateOf(isAuthenticated) }

    LaunchedEffect(isAuthenticated) {
        if (wasAuthenticated && !isAuthenticated) {
            navController.navigate(AuthGraph) {
                popUpTo(0) { inclusive = true }
            }
        }
        wasAuthenticated = isAuthenticated
    }

    NavHost(
        navController = navController,
        startDestination = AuthGraph,
        modifier = modifier,
    ) {
        navigation<AuthGraph>(startDestination = SplashRoute) {
            composable<SplashRoute> {
                val viewModel = koinViewModel<SplashViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is SplashEvent.NavigateToAuth ->
                                navController.navigate(LoginRoute) {
                                    popUpTo<SplashRoute> { inclusive = true }
                                }
                            is SplashEvent.NavigateToHome ->
                                TODO()
                        }
                    }
                }

                SplashScreen(
                    circleDurationMillis = viewModel.circleDurationMillis,
                    onIrisOpened = viewModel::onIrisOpened,
                    greenClosing = uiState.greenClosing,
                    whiteClosing = uiState.whiteClosing,
                    showLogo = uiState.showLogo,
                    player = viewModel.player
                )
            }

            if (BuildConfig.DEBUG) {
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
            }

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
                HomeScreen(
                    userName = TODO(),
                    userLastName = TODO(),
                    avatarUrl = TODO(),
                    hasUnreadNotifications = TODO(),
                    heroCard = EmployeeHeroCard(
                        lastSubmittedLabel = TODO(),
                        reportingPeriodLabel = TODO(),
                        statusLabel = TODO(),
                        summaryCount = TODO()
                    ),
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

        navigation<ManagerGraph>(startDestination = ManagerHomeRoute) {
            composable<ManagerHomeRoute> {
                HomeScreen(
                    userName = TODO(),
                    userLastName = TODO(),
                    avatarUrl = TODO(),
                    hasUnreadNotifications = TODO(),
                    heroCard = ManagerHeroCard(
                        lastSubmittedLabel = TODO(),
                        totalEmissions = TODO(),
                        reductionAchieved = TODO(),
                        summaryCount = TODO()
                    ),
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