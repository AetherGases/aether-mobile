package com.aether.application.core.navigation

import android.util.Log
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
import com.aether.application.feature.auth.presentation.screen.LoginScreen
import com.aether.application.feature.auth.presentation.screen.PasswordRecoveryScreen
import com.aether.application.feature.auth.presentation.screen.VerificationScreen
import com.aether.application.feature.auth.presentation.viewmodel.LoginEvent
import com.aether.application.feature.auth.presentation.viewmodel.LoginViewModel
import com.aether.application.feature.auth.presentation.viewmodel.PasswordRecoveryViewModel
import com.aether.application.feature.auth.presentation.viewmodel.SendCodeEvent
import com.aether.application.feature.auth.presentation.viewmodel.VerificationEvent
import com.aether.application.feature.auth.presentation.viewmodel.VerificationViewModel
import com.aether.application.feature.home.presentation.screen.EmployeeHomeScreen
import com.aether.application.feature.home.presentation.screen.ManagerHomeScreen
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
        navigation<AuthGraph>(startDestination = LoginRoute) {
            composable<LoginRoute> {
                val viewModel = koinViewModel<LoginViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val passwordRecoveryViewModel = koinViewModel<PasswordRecoveryViewModel>()

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

                LaunchedEffect(Unit) {
                    passwordRecoveryViewModel.events.collect { event ->
                        when (event) {
                            is SendCodeEvent.CodeSent ->
                                navController.navigate(ValidateRecoveryCodeRoute)
                        }
                    }
                }

                LoginScreen(
                    onLoginClick = viewModel::onLoginClick,
                    onForgotPasswordClick = { email ->
                        if (email.isNotBlank()) {
                            passwordRecoveryViewModel.onSendCodeClick(email)
                        } else {
                            navController.navigate(PasswordRecoveryRoute)
                        }
                    },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                )
            }

            composable<PasswordRecoveryRoute> {
                val viewModel = koinViewModel<PasswordRecoveryViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is SendCodeEvent.CodeSent ->
                                navController.navigate(ValidateRecoveryCodeRoute)
                        }
                    }
                }

                PasswordRecoveryScreen(
                    onBackClick = { navController.popBackStack() },
                    onBackToLoginClick = { navController.navigate(LoginRoute) },
                    onSendCodeClick = viewModel::onSendCodeClick,
                    errorMessage = uiState.errorMessage
                )
            }

            composable<ValidateRecoveryCodeRoute> {
                val viewModel = koinViewModel<VerificationViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is VerificationEvent.Verified ->
                                navController.navigate(ChangePasswordRoute)
                        }
                    }
                }

                VerificationScreen(
                    onBackClick = { navController.popBackStack() },
                    onVerifyClick = viewModel::onVerifyClick,
                    onResendClick = viewModel::onResendClick,
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