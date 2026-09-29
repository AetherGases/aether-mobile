package com.aether.application.core.di

import com.aether.application.R
import com.aether.application.core.auth.data.SessionStorage
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.network.ServerConfigStorage
import com.aether.application.core.network.ServerConfigStorageImpl
import com.aether.application.core.security.AndroidEncryptor
import com.aether.application.core.security.Encryptor
import com.aether.application.feature.auth.data.local.SessionStorageImpl
import com.aether.application.feature.auth.data.storage.SessionManagerImpl
import com.aether.application.feature.auth.presentation.viewmodel.SplashViewModel
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
                encryptor = get()
            )
        }

        single<SessionManager> {
            SessionManagerImpl(
                sessionStorage = get()
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
                context = get(),
                videoRes = R.raw.splash
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
    }
}