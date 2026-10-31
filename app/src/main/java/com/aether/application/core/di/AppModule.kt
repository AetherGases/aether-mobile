package com.aether.application.core.di

import com.aether.application.core.auth.data.SessionStorage
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.location.AddressGeocoder
import com.aether.application.core.location.DeviceLocationProvider
import com.aether.application.core.network.ServerConfigStorage
import com.aether.application.core.network.ServerConfigStorageImpl
import com.aether.application.core.security.AndroidEncryptor
import com.aether.application.core.security.Encryptor
import com.aether.application.feature.auth.data.local.SessionStorageImpl
import com.aether.application.feature.auth.data.storage.SessionManagerImpl
import com.aether.application.feature.auth.presentation.viewmodel.SplashViewModel
import com.aether.application.feature.chatbot.domain.repository.SavedConversationsRepository
import com.aether.application.feature.qa.presentation.viewmodel.ServerConfigViewModel
import com.aether.application.feature.auth.domain.usecase.ChangePasswordUseCase
import com.aether.application.feature.auth.domain.usecase.LoginUseCase
import com.aether.application.feature.auth.domain.usecase.RequestPasswordRecoveryUseCase
import com.aether.application.feature.auth.domain.usecase.ResendRecoveryCodeUseCase
import com.aether.application.feature.auth.domain.usecase.VerifyCodeUseCase
import com.aether.application.feature.auth.presentation.viewmodel.ChangePasswordViewModel
import com.aether.application.feature.auth.presentation.viewmodel.LoginViewModel
import com.aether.application.feature.auth.presentation.viewmodel.PasswordRecoveryViewModel
import com.aether.application.feature.auth.presentation.viewmodel.VerificationViewModel
import com.aether.application.feature.calculator.domain.usecase.CalculateEmissionsUseCase
import com.aether.application.feature.calculator.domain.usecase.ValidateInputUseCase
import com.aether.application.feature.calculator.presentation.viewmodel.CalculatorScreenViewModel
import com.aether.application.feature.chatbot.presentation.viewmodel.ChatbotViewModel
import com.aether.application.feature.home.presentation.viewmodel.HomeViewModel
import com.aether.application.feature.notifications.presentation.viewmodel.NotificationsViewModel
import com.aether.application.feature.proximity.domain.WorkplaceProximityChecker
import com.aether.application.feature.proximity.presentation.viewmodel.ProximityViewModel
import com.aether.application.feature.reports.presentation.viewmodel.manager.ReportAnalysisViewModel
import com.aether.application.feature.reports.presentation.viewmodel.shared.ReportReviewViewModel
import com.aether.application.feature.reports.domain.usecase.GetPendingReportsUseCase
import com.aether.application.feature.reports.domain.usecase.GetReportsUseCase
import com.aether.application.feature.reports.domain.usecase.GetReviewedReportsUseCase
import com.aether.application.feature.reports.presentation.viewmodel.ReportHistoryViewModel
import com.aether.application.feature.profile.presentation.viewmodel.ProblemViewModel
import com.aether.application.feature.reports.presentation.viewmodel.employee.NewReportViewModel
import com.aether.application.feature.settings.presentation.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object AppModule {

    val module: Module = module {

        single<Encryptor> {
            AndroidEncryptor()
        }

        single<SessionStorage> {
            SessionStorageImpl(
                dataStore = get(),
                encryptor = get(),
                profileLocalDataSource = get()
            )
        }

        single<SessionManager> {
            SessionManagerImpl(
                sessionStorage = get(),
                onLogout = { get<SavedConversationsRepository>().clear() }
            )
        }

        single<ServerConfigStorage> {
            ServerConfigStorageImpl(dataStore = get())
        }

        viewModel {
            ServerConfigViewModel(serverConfigStorage = get())
        }

        viewModel {
            SplashViewModel(
                sessionManager = get(),
                context = get()
            )
        }

        single<LoginUseCase> {
            LoginUseCase(authRepository = get())
        }

        viewModel {
            LoginViewModel(loginUseCase = get())
        }
        single<RequestPasswordRecoveryUseCase> {
            RequestPasswordRecoveryUseCase(authRepository = get())
        }
        single<ResendRecoveryCodeUseCase> {
            ResendRecoveryCodeUseCase(authRepository = get())
        }
        single<VerifyCodeUseCase> {
            VerifyCodeUseCase(authRepository = get())
        }
        viewModel { (email: String) ->
            PasswordRecoveryViewModel(
                email = email,
                requestPasswordRecoveryUseCase = get()
            )
        }
        viewModel { (email: String) ->
            VerificationViewModel(
                email = email,
                verifyCodeUseCase = get(),
                resendRecoveryCodeUseCase = get()
            )
        }
        single<ChangePasswordUseCase> {
            ChangePasswordUseCase(authRepository = get())
        }
        viewModel { (email: String, key: String) ->
            ChangePasswordViewModel(
                email = email,
                key = key,
                changePasswordUseCase = get()
            )
        }

        viewModel {
            HomeViewModel(
                homeRepository = get(),
                profileRepository = get(),
                sessionManager = get()
            )
        }

        viewModel {
            NotificationsViewModel(notificationsRepository = get())
        }

        viewModel { (reportId: Long) ->
            ReportReviewViewModel(
                reportId = reportId,
                reportsRepository = get(),
                sessionManager = get()
            )
        }

        viewModel { (reportId: Long) ->
            ReportAnalysisViewModel(
                reportId = reportId,
                reportsRepository = get(),
                sessionManager = get()
            )
        }

        single<GetReportsUseCase> {
            GetReportsUseCase(reportsRepository = get())
        }
        single<GetPendingReportsUseCase> {
            GetPendingReportsUseCase(reportsRepository = get())
        }
        single<GetReviewedReportsUseCase> {
            GetReviewedReportsUseCase(reportsRepository = get())
        }
        viewModel {
            ReportHistoryViewModel(
                getReports = get(),
                getReviewedReports = get(),
                sessionManager = get()
            )
        }

        viewModel {
            ProblemViewModel(problemRepository = get())
        }

        viewModel {
            NewReportViewModel(newReportRepository = get())
        }

        viewModel {
            ChatbotViewModel(
                chatbotRepository = get(),
                savedConversationsRepository = get(),
                sessionManager = get()
            )
        }

        single<CalculateEmissionsUseCase> {
            CalculateEmissionsUseCase(calculatorRepository = get())
        }
        single<ValidateInputUseCase> {
            ValidateInputUseCase()
        }
        viewModel {
            CalculatorScreenViewModel(
                calculatorRepository = get(),
                calculateEmissions = get(),
                validateInput = get()
            )
        }

        viewModel {
            SettingsViewModel(
                settingsRepository = get(),
                homeRepository = get(),
                profileRepository = get(),
                sessionManager = get()
            )
        }

        single {
            DeviceLocationProvider(context = androidContext())
        }

        single {
            AddressGeocoder(context = androidContext())
        }

        single {
            WorkplaceProximityChecker(
                profileLocalDataSource = get(),
                locationProvider = get(),
                geocoder = get()
            )
        }

        viewModel {
            ProximityViewModel(proximityChecker = get())
        }
    }
}
