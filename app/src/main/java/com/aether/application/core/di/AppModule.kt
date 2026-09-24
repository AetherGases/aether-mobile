package com.aether.application.core.di

import com.aether.application.core.auth.data.SessionStorage
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.security.AndroidEncryptor
import com.aether.application.core.security.Encryptor
import com.aether.application.feature.auth.data.local.SessionStorageImpl
import com.aether.application.feature.auth.data.storage.SessionManagerImpl
import com.aether.application.feature.auth.domain.usecase.LoginUseCase
import com.aether.application.feature.auth.domain.usecase.RequestPasswordRecoveryUseCase
import com.aether.application.feature.auth.domain.usecase.VerifyCodeUseCase
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
        single<LoginUseCase> {
            LoginUseCase(authRepository = get())
        }
        viewModel {
            LoginViewModel(loginUseCase = get())
        }
        single<RequestPasswordRecoveryUseCase> {
            RequestPasswordRecoveryUseCase(authRepository = get())
        }
        single<VerifyCodeUseCase> {
            VerifyCodeUseCase(authRepository = get())
        }
        viewModel {
            PasswordRecoveryViewModel(
                requestPasswordRecoveryUseCase = get()
            )
        }
        viewModel { (email: String) ->
            VerificationViewModel(
                email = email,
                verifyCodeUseCase = get(),
                requestPasswordRecoveryUseCase = get()
            )
        }
    }
}
