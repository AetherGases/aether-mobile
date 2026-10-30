package com.aether.application.core.di

import com.aether.application.BuildConfig
import com.aether.application.core.network.ApiClient
import com.aether.application.core.network.HttpCallLoggingInterceptor
import com.aether.application.core.network.ApiConfig
import com.aether.application.core.network.AuthInterceptor
import com.aether.application.core.network.BaseUrlInterceptor
import com.aether.application.core.network.ServerConfigCache
import com.aether.application.core.network.TokenAuthenticator
import com.aether.application.core.network.cloudinary.CLOUDINARY_BASE_URL
import com.aether.application.core.network.cloudinary.CloudinaryApi
import com.aether.application.core.network.cloudinary.CloudinaryUploader
import com.aether.application.core.network.cloudinary.MOCK_CLOUD_NAME
import com.aether.application.feature.auth.data.remote.AuthApi
import com.aether.application.feature.auth.data.remote.ProfileApi
import com.aether.application.feature.auth.data.repository.AuthRepositoryImpl
import com.aether.application.feature.auth.domain.repository.AuthRepository
import com.aether.application.feature.auth.domain.usecase.LoginUseCase
import com.aether.application.feature.auth.presentation.viewmodel.LoginViewModel
import com.aether.application.feature.chatbot.data.remote.ChatbotApi
import com.aether.application.feature.chatbot.data.repository.ChatbotRepositoryImpl
import com.aether.application.feature.chatbot.data.repository.SavedConversationsRepositoryImpl
import com.aether.application.feature.chatbot.domain.repository.ChatbotRepository
import com.aether.application.feature.chatbot.domain.repository.SavedConversationsRepository
import com.aether.application.feature.home.data.remote.InventoryApi
import com.aether.application.feature.reports.data.remote.HubApi
import com.aether.application.feature.reports.data.remote.ReportsApi
import com.aether.application.feature.reports.data.repository.NewReportRepositoryImpl
import com.aether.application.feature.reports.domain.repository.NewReportRepository
import com.aether.application.feature.home.data.repository.HomeRepositoryImpl
import com.aether.application.feature.home.domain.repository.HomeRepository
import com.aether.application.feature.profile.data.local.AvatarImageProcessor
import com.aether.application.feature.profile.data.repository.ProfileRepositoryImpl
import com.aether.application.feature.profile.domain.repository.ProfileRepository
import com.aether.application.feature.reports.data.repository.ReportsRepositoryImpl
import com.aether.application.feature.reports.domain.repository.ReportsRepository
import com.aether.application.feature.settings.data.repository.SettingsRepositoryImpl
import com.aether.application.feature.settings.domain.repository.SettingsRepository
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

private const val REFRESH = "refresh"

object NetworkModule {

    val module: Module = module {

        single<AuthInterceptor> {
            AuthInterceptor(sessionManager = get())
        }

        single {
            ServerConfigCache(serverConfigStorage = get())
        }

        single<BaseUrlInterceptor> {
            BaseUrlInterceptor(serverConfigCache = get())
        }

        single<TokenAuthenticator> {
            TokenAuthenticator(sessionManager = get()) { get(named(REFRESH)) }
        }

        // The refresh runs on its own client: see ApiClient.getRefreshOkHttpClient
        single<AuthApi>(named(REFRESH)) {
            val client = ApiClient.getRefreshOkHttpClient(
                baseUrlInterceptor = if (BuildConfig.DEBUG) get() else null
            )
            ApiClient.getRetrofit(okHttpClient = client).create(AuthApi::class.java)
        }

        single<OkHttpClient> {
            ApiClient.getOkHttpClient(
                authInterceptor = get(),
                baseUrlInterceptor = if (BuildConfig.DEBUG) get() else null,
                tokenAuthenticator = get()
            )
        }

        single<Retrofit> {
            ApiClient.getRetrofit(okHttpClient = get())
        }

        single<AuthApi> {
            val retrofit: Retrofit = get()
            retrofit.create(AuthApi::class.java)
        }

        single<ProfileApi> {
            val retrofit: Retrofit = get()
            retrofit.create(ProfileApi::class.java)
        }

        single<AuthRepository> {
            AuthRepositoryImpl(
                api = get(),
                profileApi = get(),
                sessionManager = get(),
                profileLocalDataSource = get()
            )
        }

        single<InventoryApi> {
            val retrofit: Retrofit = get()
            retrofit.create(InventoryApi::class.java)
        }

        single<SettingsRepository> {
            SettingsRepositoryImpl(dataStore = get())
        }

        single<ReportsApi> {
            val retrofit: Retrofit = get()
            retrofit.create(ReportsApi::class.java)
        }

        single<CloudinaryApi> {
            val retrofit: Retrofit = get()
            retrofit.create(CloudinaryApi::class.java)
        }

        single<HubApi> {
            val retrofit: Retrofit = get()
            retrofit.create(HubApi::class.java)
        }

        single {
            val serverConfigCache: ServerConfigCache = get()
            CloudinaryUploader(
                contentResolver = androidContext().contentResolver,
                // A plain client: the app's own one would send our access token to Cloudinary
                client = OkHttpClient.Builder()
                    .addInterceptor(HttpCallLoggingInterceptor())
                    .build(),
                uploadBaseUrl = { signature ->
                    // A Mockoon signature can only be accepted by Mockoon itself
                    if (BuildConfig.DEBUG && signature.cloudName == MOCK_CLOUD_NAME) {
                        serverConfigCache.selectedDomain.value ?: ApiConfig.BASE_URL
                    } else {
                        CLOUDINARY_BASE_URL
                    }
                }
            )
        }

        single<NewReportRepository> {
            NewReportRepositoryImpl(
                reportsApi = get(),
                cloudinaryApi = get(),
                uploader = get()
            )
        }

        single<HomeRepository> {
            HomeRepositoryImpl(inventoryApi = get())
        }

        single {
            AvatarImageProcessor(context = androidContext())
        }

        single<ProfileRepository> {
            ProfileRepositoryImpl(
                profileApi = get(),
                cloudinaryApi = get(),
                cloudinaryUploader = get(),
                avatarImageProcessor = get(),
                profileLocalDataSource = get(),
                sessionManager = get()
            )
        }

        single<ReportsRepository> {
            ReportsRepositoryImpl(inventoryApi = get(), reportsApi = get(), hubApi = get())
        }

        single<ChatbotApi> {
            val retrofit: Retrofit = get()
            retrofit.create(ChatbotApi::class.java)
        }

        single<ChatbotRepository> {
            ChatbotRepositoryImpl(chatbotApi = get(), profileApi = get(), profileLocalDataSource = get())
        }

        single<SavedConversationsRepository> {
            SavedConversationsRepositoryImpl(dataStore = get(), sessionManager = get())
        }
    }
}
