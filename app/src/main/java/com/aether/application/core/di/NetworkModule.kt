package com.aether.application.core.di

import com.aether.application.core.network.ApiClient
import com.aether.application.core.network.AuthInterceptor
import com.aether.application.feature.auth.data.remote.AuthApi
import com.aether.application.feature.auth.data.repository.AuthRepositoryImpl
import com.aether.application.feature.auth.domain.repository.AuthRepository
import com.aether.application.feature.auth.domain.usecase.LoginUseCase
import com.aether.application.feature.auth.presentation.viewmodel.LoginViewModel
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

object NetworkModule {

    val module: Module = module {

        single<AuthInterceptor> {
            AuthInterceptor(sessionManager = get())
        }

        single<OkHttpClient> {
            ApiClient.getOkHttpClient(authInterceptor = get())
        }

        single<Retrofit> {
            ApiClient.getRetrofit(okHttpClient = get())
        }

        single<AuthApi> {
            val retrofit: Retrofit = get()
            retrofit.create(AuthApi::class.java)
        }

        single<AuthRepository> {
            AuthRepositoryImpl(
                api = get(),
                sessionManager = get()
            )
        }

        single<LoginUseCase> {
            LoginUseCase(authRepository = get())
        }

        viewModel {
            LoginViewModel(loginUseCase = get())
        }
    }
}