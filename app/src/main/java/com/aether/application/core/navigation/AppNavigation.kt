package com.aether.application.core.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.aether.application.BuildConfig
import com.aether.application.core.auth.storage.SessionManager
import androidx.navigation.toRoute
import com.aether.application.feature.auth.presentation.screen.ChangePasswordScreen
import com.aether.application.feature.auth.presentation.screen.LoginScreen
import com.aether.application.feature.auth.presentation.screen.PasswordRecoveryScreen
import com.aether.application.feature.auth.presentation.screen.VerificationScreen
import com.aether.application.feature.auth.presentation.viewmodel.ChangePasswordEvent
import com.aether.application.feature.auth.presentation.viewmodel.ChangePasswordViewModel
import com.aether.application.feature.auth.presentation.viewmodel.LoginEvent
import com.aether.application.feature.auth.presentation.viewmodel.LoginViewModel
import com.aether.application.feature.home.presentation.screen.HomeScreen
import com.aether.application.feature.auth.presentation.viewmodel.SplashEvent
import com.aether.application.feature.auth.presentation.viewmodel.SplashViewModel
import com.aether.application.feature.auth.presentation.screen.SplashScreen
import com.aether.application.feature.qa.presentation.screen.ServerConfigScreen
import com.aether.application.feature.qa.presentation.viewmodel.ServerConfigEvent
import com.aether.application.feature.qa.presentation.viewmodel.ServerConfigViewModel
import org.koin.compose.koinInject
import com.aether.application.feature.auth.presentation.viewmodel.PasswordRecoveryViewModel
import com.aether.application.feature.auth.presentation.viewmodel.SendCodeEvent
import com.aether.application.feature.auth.presentation.viewmodel.VerificationEvent
import com.aether.application.feature.auth.presentation.viewmodel.VerificationViewModel
import com.aether.application.feature.home.presentation.viewmodel.HomeEvent
import com.aether.application.feature.settings.presentation.screen.PrivacyPolicyScreen
import com.aether.application.feature.settings.presentation.screen.SettingsScreen
import com.aether.application.feature.settings.presentation.screen.ThemePickerDialog
import com.aether.application.feature.settings.presentation.screen.label
import com.aether.application.feature.settings.presentation.viewmodel.SettingsViewModel
import com.aether.application.feature.settings.presentation.screen.TermsOfUseScreen
import com.aether.application.feature.home.presentation.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aether.application.feature.proximity.presentation.screen.ProximityGateScreen
import com.aether.application.feature.proximity.presentation.viewmodel.ProximityViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isInAppGraph = backStackEntry?.destination?.hierarchy?.any { it.hasRoute<AppGraph>() } == true
    val proximityViewModel = koinViewModel<ProximityViewModel>()
    val proximityState by proximityViewModel.uiState.collectAsStateWithLifecycle()

    // The app can only be used around the workplace: checked on entering the app and on every return to it
    LaunchedEffect(isInAppGraph) {
        if (isInAppGraph) proximityViewModel.check() else proximityViewModel.reset()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        if (isInAppGraph) proximityViewModel.check()
    }

    Box(modifier = modifier) {
        AppNavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController
        )

        if (isInAppGraph && (proximityState.status == null || proximityState.isBlocked)) {
            ProximityGateScreen(
                status = proximityState.status,
                isChecking = proximityState.isChecking,
                onRetry = proximityViewModel::check
            )
        }
    }
}

@Composable
private fun AppNavHost(
    modifier: Modifier,
    navController: NavHostController,
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
                                navController.navigate(AppGraph) {
                                    popUpTo<AuthGraph> { inclusive = true }
                                }
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
                                navController.navigate(AppGraph) {
                                    popUpTo<AuthGraph> { inclusive = true }
                                }
                        }
                    }
                }

                LoginScreen(
                    email = uiState.email,
                    onEmailChange = viewModel::onEmailChange,
                    password = uiState.password,
                    onPasswordChange = viewModel::onPasswordChange,
                    rememberMe = uiState.rememberMe,
                    onRememberMeChange = viewModel::onRememberMeChange,
                    onLoginClick = viewModel::onLoginClick,
                    onForgotPasswordClick = {  navController.navigate(PasswordRecoveryRoute(email = uiState.email)) },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onChangeServerClick = if (BuildConfig.DEBUG) {
                        { navController.navigate(ServerConfigRoute) }
                    } else {
                        null
                    },
                )
            }

            composable<PasswordRecoveryRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<PasswordRecoveryRoute>()
                val viewModel = koinViewModel<PasswordRecoveryViewModel> {
                    parametersOf(route.email)
                }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is SendCodeEvent.CodeSent ->
                                navController.navigate(ValidateRecoveryCodeRoute(email = event.email))
                        }
                    }
                }

                PasswordRecoveryScreen(
                    email = uiState.email,
                    isLoading = uiState.isLoading,
                    onEmailChange = viewModel::onEmailChange,
                    onBackToLoginClick = navController::popBackStack,
                    onSendCodeClick = viewModel::onSendCodeClick,
                    errorMessage = uiState.errorMessage
                )
            }

            composable<ValidateRecoveryCodeRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ValidateRecoveryCodeRoute>()
                val viewModel = koinViewModel<VerificationViewModel> {
                    parametersOf(route.email)
                }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is VerificationEvent.Verified ->
                                navController.navigate(
                                    ChangePasswordRoute(email = route.email, key = event.key)
                                )
                        }
                    }
                }

                VerificationScreen(
                    email = route.email,
                    code = uiState.code,
                    onCodeChange = viewModel::onCodeChange,
                    onBackClick = { navController.popBackStack() },
                    onVerifyClick = viewModel::onVerifyClick,
                    onResendClick = viewModel::onResendClick,
                    isLoading = uiState.isLoading,
                    isResending = uiState.isResending,
                    resendCooldownSeconds = uiState.resendCooldownSeconds,
                    errorMessage = uiState.errorMessage,
                )
            }

            composable<ChangePasswordRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ChangePasswordRoute>()
                val viewModel = koinViewModel<ChangePasswordViewModel> {
                    parametersOf(route.email, route.key)
                }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                BackHandler(enabled = uiState.isConfirmStep, onBack = viewModel::onBackClick)

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is ChangePasswordEvent.PasswordChanged ->
                                navController.navigate(LoginRoute) {
                                    popUpTo<LoginRoute> { inclusive = true }
                                }
                            is ChangePasswordEvent.NavigateBack ->
                                navController.popBackStack()
                        }
                    }
                }

                ChangePasswordScreen(
                    password = uiState.password,
                    onPasswordChange = viewModel::onPasswordChange,
                    confirmPassword = uiState.confirmPassword,
                    onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                    isConfirmStep = uiState.isConfirmStep,
                    onBackClick = viewModel::onBackClick,
                    onSubmitClick = viewModel::onSubmitClick,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                )
            }
        }

        navigation<AppGraph>(startDestination = HomeRoute) {
            composable<HomeRoute> {
                val viewModel = koinViewModel<HomeViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            HomeEvent.NavigateToCalculator ->
                                navController.navigate(CalculatorRoute)
                            HomeEvent.NavigateToChatBot ->
                                navController.navigate(ChatbotRoute)
                            HomeEvent.NavigateToCreateReport ->
                                navController.navigate(CreateReportRoute)
                            HomeEvent.NavigateToHistory ->
                                navController.navigate(ReportHistoryRoute)
                            is HomeEvent.NavigateToReport ->
                                navController.navigate(ReportAnalysisRoute(event.reportId))
                        }
                    }
                }

                HomeScreen(
                    userName = uiState.userName,
                    userLastName = uiState.userLastName,
                    avatarUrl = uiState.avatarUrl,
                    hasUnreadNotifications = uiState.hasUnreadNotifications,
                    cards = uiState.cards,
                    quickActions = uiState.quickActions,
                    recentReports = uiState.recentReports,
                    cardsFailed = uiState.cardsFailed,
                    quickActionsFailed = uiState.quickActionsFailed,
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = viewModel::refresh,
                    onNotificationsClick = { navController.navigate(NotificationsRoute) },
                    onSettingsClick = { navController.navigate(SettingsRoute) },
                    onSeeAllReportsClick = { navController.navigate(ReportHistoryRoute) },
                    onReportClick = viewModel::onReportClick,
                )
            }

            composable<ReportAnalysisRoute> {
                TODO()
            }

            composable<ReportHistoryRoute> {
                TODO()
            }

            composable<NotificationsRoute> {
                TODO()
            }

            composable<SettingsRoute> {
                val viewModel = koinViewModel<SettingsViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showThemePicker by remember { mutableStateOf(false) }
                val context = LocalContext.current

                LaunchedEffect(uiState.avatarErrorMessage) {
                    uiState.avatarErrorMessage?.let { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        viewModel.onAvatarErrorShown()
                    }
                }

                SettingsScreen(
                    userName = uiState.userName,
                    avatarUrl = uiState.avatarUrl,
                    isUploadingAvatar = uiState.isUploadingAvatar,
                    onAvatarPicked = viewModel::onAvatarPicked,
                    reportsCount = uiState.reportsCount,
                    entryDate = uiState.entryDate,
                    notificationsEnabled = uiState.notificationsEnabled,
                    emailNotificationsEnabled = uiState.emailNotificationsEnabled,
                    themeLabel = uiState.theme.label(),
                    languageLabel = uiState.languageLabel,
                    onNotificationsChanged = viewModel::onNotificationsChanged,
                    onEmailNotificationsChanged = viewModel::onEmailNotificationsChanged,
                    onThemeClick = { showThemePicker = true },
                    onLanguageClick = { /* TODO: no translated resources yet */ },
                    onTalkToAekoClick = { navController.navigate(ChatbotRoute) },
                    onReportProblemClick = { /* TODO: ReportProblem route */ },
                    onTermsOfUseClick = { navController.navigate(TermsOfUseRoute) },
                    onPrivacyPolicyClick = { navController.navigate(PrivacyPolicyRoute) },
                    onLogoutClick = viewModel::onLogoutClick,
                    onBackClick = { navController.popBackStack() }
                )

                if (showThemePicker) {
                    ThemePickerDialog(
                        selected = uiState.theme,
                        onSelect = {
                            viewModel.onThemeSelected(it)
                            showThemePicker = false
                        },
                        onDismiss = { showThemePicker = false }
                    )
                }
            }

            composable<TermsOfUseRoute> {
                TermsOfUseScreen(onBackClick = { navController.popBackStack() })
            }

            composable<PrivacyPolicyRoute> {
                PrivacyPolicyScreen(onBackClick = { navController.popBackStack() })
            }

            composable<CalculatorRoute> {
                TODO()
            }

            composable<ChatbotRoute> {
                TODO()
            }
        }
    }
}