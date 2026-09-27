package com.aether.application.core.di

import com.aether.application.R
import com.aether.application.core.auth.data.SessionStorage
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.security.AndroidEncryptor
import com.aether.application.core.security.Encryptor
import com.aether.application.feature.auth.data.local.SessionStorageImpl
import com.aether.application.feature.auth.data.storage.SessionManagerImpl
import com.aether.application.feature.auth.presentation.viewmodel.SplashViewModel
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

        viewModel {
            SplashViewModel(
                sessionManager = get(),
                context = get(),
                videoRes = R.raw.splash
            )
        }
    }
}