package com.aether.application.core.navigation

import androidx.core.content.ContextCompat
import com.aether.application.feature.settings.presentation.screen.LanguagePickerDialog
import com.aether.application.core.utils.AppLocale
import androidx.compose.ui.platform.LocalContext
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import android.os.Build
import android.content.pm.PackageManager
import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
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
import com.aether.application.feature.home.presentation.screen.HomeLoadingScreen
import com.aether.application.feature.home.presentation.screen.HomeNoPermissionsScreen
import com.aether.application.feature.home.presentation.screen.HomeScreen
import com.aether.application.feature.home.presentation.viewmodel.HomeLoadPhase
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
import com.aether.application.feature.reports.presentation.screen.manager.AetherAnalysisScreen
import com.aether.application.feature.reports.presentation.screen.shared.ReportReviewScreen
import com.aether.application.feature.reports.presentation.viewmodel.manager.ReportAnalysisViewModel
import com.aether.application.feature.reports.presentation.viewmodel.shared.ReportReviewViewModel
import com.aether.application.feature.settings.presentation.screen.PrivacyPolicyScreen
import com.aether.application.feature.settings.presentation.screen.SettingsScreen
import com.aether.application.feature.settings.presentation.screen.ThemePickerDialog
import com.aether.application.feature.settings.presentation.screen.label
import com.aether.application.feature.notifications.presentation.screen.NotificationsScreen
import com.aether.application.feature.notifications.presentation.viewmodel.NotificationsViewModel
import com.aether.application.feature.settings.presentation.viewmodel.SettingsViewModel
import com.aether.application.feature.settings.presentation.screen.TermsOfUseScreen
import com.aether.application.feature.chatbot.presentation.screen.AekoScreen
import com.aether.application.feature.calculator.presentation.screen.CalculatorFormScreen
import com.aether.application.feature.calculator.presentation.viewmodel.CalculatorScreenViewModel
import com.aether.application.feature.chatbot.presentation.viewmodel.ChatbotViewModel
import com.aether.application.feature.home.presentation.viewmodel.HomeViewModel
import com.aether.application.feature.reports.presentation.screen.ReportHistoryScreen
import com.aether.application.feature.reports.presentation.viewmodel.ReportHistoryViewModel
import com.aether.application.feature.report.presentation.screen.ConsolidationApproachScreen
import com.aether.application.feature.reports.domain.model.ConsolidationApproach
import com.aether.application.feature.reports.presentation.screen.CreateReportUploadScreen
import com.aether.application.feature.reports.presentation.state.NewReportEvent
import com.aether.application.feature.reports.presentation.viewmodel.employee.NewReportViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aether.application.feature.proximity.presentation.qa.QaProximityBypass
import com.aether.application.feature.proximity.presentation.screen.ProximityGateScreen
import com.aether.application.feature.proximity.presentation.viewmodel.ProximityViewModel

private const val REPORT_REVIEWED_KEY = "report_reviewed"

@Composable
fun AppNavigation(
    pendingReportId: Long? = null,
    onPendingReportConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isInAppGraph = backStackEntry?.destination?.hierarchy?.any { it.hasRoute<AppGraph>() } == true
    val proximityViewModel = koinViewModel<ProximityViewModel>()
    val proximityState by proximityViewModel.uiState.collectAsStateWithLifecycle()
    val isQaBypassed by QaProximityBypass.isActive.collectAsStateWithLifecycle()

    // The app can only be used around the workplace: checked on entering the app and on every return to it
    LaunchedEffect(isInAppGraph) {
        if (isInAppGraph) {
            proximityViewModel.check()
        } else {
            proximityViewModel.reset()
            QaProximityBypass.reset()
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        if (isInAppGraph) proximityViewModel.check()
    }

    // A tapped push waits for the login (and the app graph) before opening its report
    LaunchedEffect(pendingReportId, isInAppGraph) {
        if (pendingReportId != null && isInAppGraph) {
            navController.navigate(ReportReviewRoute(pendingReportId))
            onPendingReportConsumed()
        }
    }

    Box(modifier = modifier) {
        AppNavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController
        )

        if (isInAppGraph && !isQaBypassed && (proximityState.status == null || proximityState.isBlocked)) {
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
                val context = LocalContext.current
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {}

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

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
                                navController.navigate(ReportReviewRoute(event.reportId))
                        }
                    }
                }

                when (uiState.phase) {
                    HomeLoadPhase.LOADING -> HomeLoadingScreen()
                    HomeLoadPhase.NO_PERMISSIONS -> HomeNoPermissionsScreen(
                        onRetryClick = viewModel::loadFirstTime,
                        onLogoutClick = viewModel::onLogoutClick
                    )
                    HomeLoadPhase.READY -> HomeScreen(
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
            }

            composable<ReportReviewRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ReportReviewRoute>()
                val viewModel = koinViewModel<ReportReviewViewModel> {
                    parametersOf(route.reportId)
                }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val wasReviewed by backStackEntry.savedStateHandle
                    .getStateFlow(REPORT_REVIEWED_KEY, false)
                    .collectAsStateWithLifecycle()

                // Coming back from the analysis after approving/rejecting: show the new status
                LaunchedEffect(wasReviewed) {
                    if (wasReviewed) {
                        backStackEntry.savedStateHandle[REPORT_REVIEWED_KEY] = false
                        viewModel.load()
                    }
                }

                ReportReviewScreen(
                    report = uiState.report,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onRetry = viewModel::load,
                    onBackClick = { navController.popBackStack() },
                    onProceedClick = { navController.navigate(ReportAnalysisRoute(route.reportId)) }
                )
            }

            composable<CreateReportRoute> {
                val viewModel = koinViewModel<NewReportViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                CreateReportUploadScreen(
                    selectedFileName = uiState.selectedFileName,
                    uploadProgress = uiState.uploadProgress,
                    reportName = uiState.reportName,
                    reportNameError = uiState.reportNameError,
                    errorMessage = uiState.errorMessage,
                    onReportNameChanged = viewModel::onReportNameChanged,
                    onFileSelected = viewModel::onFileSelected,
                    onRemoveFileClicked = viewModel::onRemoveFileClicked,
                    onConfirmSubmit = {
                        viewModel.clearError()
                        navController.navigate(ConsolidationApproachRoute)
                    },
                    onBackClicked = { navController.popBackStack() }
                )
            }

            composable<ConsolidationApproachRoute> { backStackEntry ->
                // Shares the state of the upload step, which holds the file and the name
                val uploadEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<CreateReportRoute>()
                }
                val viewModel = koinViewModel<NewReportViewModel>(viewModelStoreOwner = uploadEntry)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                // Covers the back button and the system back gesture alike
                DisposableEffect(Unit) {
                    onDispose { viewModel.clearError() }
                }

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is NewReportEvent.Created ->
                                navController.popBackStack<HomeRoute>(inclusive = false)
                        }
                    }
                }

                ConsolidationApproachScreen(
                    isSubmitting = uiState.isSubmitting,
                    errorMessage = uiState.errorMessage,
                    onBackClick = { navController.popBackStack() },
                    onEquityShareClick = { viewModel.onApproachSelected(ConsolidationApproach.EQUITY_SHARE) },
                    onFinancialControlClick = { viewModel.onApproachSelected(ConsolidationApproach.FINANCIAL_CONTROL) },
                    onOperationalControlClick = { viewModel.onApproachSelected(ConsolidationApproach.OPERATIONAL_CONTROL) },
                    onConsultAekoClick = { navController.navigate(ChatbotRoute) }
                )
            }

            composable<ReportAnalysisRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ReportAnalysisRoute>()
                val viewModel = koinViewModel<ReportAnalysisViewModel> {
                    parametersOf(route.reportId)
                }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(uiState.decision) {
                    if (uiState.decision != null) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(REPORT_REVIEWED_KEY, true)
                    }
                }

                AetherAnalysisScreen(
                    analysis = uiState.analysis,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    onRetry = viewModel::load,
                    canDecide = uiState.canDecide,
                    decision = uiState.decision,
                    isSubmitting = uiState.isSubmitting,
                    submitErrorMessage = uiState.submitErrorMessage,
                    onBackClick = { navController.popBackStack() },
                    onDecisionSubmit = viewModel::onDecisionSubmit
                )
            }

            composable<ReportHistoryRoute> {
                val viewModel = koinViewModel<ReportHistoryViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                ReportHistoryScreen(
                    viewerRole = uiState.viewerRole,
                    currentWeek = uiState.currentWeek,
                    month = uiState.month,
                    reportDates = uiState.reportDates,
                    selectedDate = uiState.selectedDate,
                    reportItems = uiState.reportItems,
                    searchQuery = uiState.searchQuery,
                    selectedTimeFilters = uiState.selectedTimeFilters,
                    selectedStatusFilters = uiState.selectedStatusFilters,
                    isRefreshing = uiState.isRefreshing,
                    loadFailed = uiState.loadFailed,
                    isLoadingMore = uiState.isLoadingMore,
                    loadMoreFailed = uiState.loadMoreFailed,
                    hasMore = uiState.hasMore,
                    onRefresh = viewModel::refresh,
                    onLoadMore = viewModel::loadMore,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onTimeFilterToggled = viewModel::onTimeFilterToggled,
                    onStatusFilterToggled = viewModel::onStatusFilterToggled,
                    onDaySelected = viewModel::onDaySelected,
                    onMonthChanged = viewModel::onMonthChanged,
                    onReviewClick = { id -> navController.navigate(ReportReviewRoute(id.toLong())) },
                    onBackClicked = { navController.popBackStack() }
                )
            }

            composable<NotificationsRoute> {
                val viewModel = koinViewModel<NotificationsViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                NotificationsScreen(
                    sections = uiState.sections,
                    loadFailed = uiState.failed,
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = viewModel::onFilterSelected,
                    onBackClicked = { navController.popBackStack() },
                    onNotificationClick = { reportId -> navController.navigate(ReportReviewRoute(reportId)) }
                )
            }

            composable<SettingsRoute> {
                val viewModel = koinViewModel<SettingsViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showThemePicker by remember { mutableStateOf(false) }
                var showLanguagePicker by remember { mutableStateOf(false) }
                val languageContext = LocalContext.current

                SettingsScreen(
                    userName = uiState.userName,
                    avatarUrl = uiState.avatarUrl,
                    isUploadingAvatar = uiState.isUploadingAvatar,
                    avatarErrorMessage = uiState.avatarErrorMessage,
                    onAvatarPicked = viewModel::onAvatarPicked,
                    reportsCount = uiState.reportsCount,
                    entryDate = uiState.entryDate,
                    notificationsEnabled = uiState.notificationsEnabled,
                    emailNotificationsEnabled = uiState.emailNotificationsEnabled,
                    themeLabel = uiState.theme.label(),
                    languageLabel = AppLocale.language.label(),
                    canTalkToAeko = uiState.canTalkToAeko,
                    onNotificationsChanged = viewModel::onNotificationsChanged,
                    onEmailNotificationsChanged = viewModel::onEmailNotificationsChanged,
                    onThemeClick = { showThemePicker = true },
                    onLanguageClick = { showLanguagePicker = true },
                    onTalkToAekoClick = { navController.navigate(ChatbotRoute) },
                    onReportProblemClick = { /* TODO: ReportProblem route */ },
                    onTermsOfUseClick = { navController.navigate(TermsOfUseRoute) },
                    onPrivacyPolicyClick = { navController.navigate(PrivacyPolicyRoute) },
                    onLogoutClick = viewModel::onLogoutClick,
                    onBackClick = { navController.popBackStack() }
                )

                if (showLanguagePicker) {
                    LanguagePickerDialog(
                        selected = AppLocale.language,
                        onSelect = {
                            showLanguagePicker = false
                            AppLocale.setLanguage(languageContext, it)
                        },
                        onDismiss = { showLanguagePicker = false }
                    )
                }

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
                val viewModel = koinViewModel<CalculatorScreenViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                CalculatorFormScreen(
                    uiState = uiState,
                    onPollutingGasChanged = viewModel::onPollutingGasChanged,
                    onMonthlyVolumeChanged = viewModel::onMonthlyVolumeChanged,
                    onScopeChanged = viewModel::onScopeChanged,
                    onGreenGasChanged = viewModel::onGreenGasChanged,
                    onDeadlineChanged = viewModel::onDeadlineChanged,
                    onNextClicked = viewModel::onNextClicked,
                    onBackClicked = {
                        if (!viewModel.onBackClicked()) navController.popBackStack()
                    },
                    onCalculateClicked = viewModel::onCalculateClicked,
                    onConsultAekoClicked = { navController.navigate(ChatbotRoute) },
                    onFinishClicked = {
                        viewModel.onFinishClicked()
                        navController.popBackStack()
                    }
                )
            }

            composable<ChatbotRoute> {
                val viewModel = koinViewModel<ChatbotViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val clipboard = LocalClipboardManager.current

                AekoScreen(
                    screenTitle = uiState.title.asString(),
                    messages = uiState.displayMessages,
                    suggestions = uiState.suggestionRes.map { stringResource(it) },
                    inputText = uiState.inputText,
                    isConversationPinned = uiState.isConversationPinned,
                    conversations = uiState.visibleConversations,
                    conversationSearchQuery = uiState.conversationSearchQuery,
                    userName = uiState.userName,
                    userRole = uiState.userRole.asString(),
                    avatarUrl = uiState.avatarUrl,
                    onInputTextChanged = viewModel::onInputTextChanged,
                    onSuggestionClicked = viewModel::onSuggestionClicked,
                    onSendClicked = viewModel::onSendClicked,
                    onBookmarkClicked = viewModel::onBookmarkClicked,
                    onCopyMessage = { id ->
                        uiState.messages.firstOrNull { it.id == id }?.let {
                            clipboard.setText(AnnotatedString(it.text))
                        }
                    },
                    onConversationSearchQueryChanged = viewModel::onConversationSearchQueryChanged,
                    onNewConversationClick = viewModel::onNewConversationClick,
                    onConversationClick = viewModel::onConversationClick,
                    onSettingsClick = { navController.navigate(SettingsRoute) },
                    onBackClicked = { navController.popBackStack() },
                    errorMessage = uiState.errorMessage
                )
            }
        }
    }
}