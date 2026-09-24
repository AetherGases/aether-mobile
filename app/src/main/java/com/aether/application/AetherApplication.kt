package com.aether.application

import android.app.Application
import com.aether.application.core.auth.storage.SessionManager
import com.aether.application.core.di.AppModule
import com.aether.application.core.di.NetworkModule
import com.aether.application.core.di.dataStoreModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class AetherApplication : Application(), KoinComponent {

    private val sessionManager: SessionManager by inject()
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AetherApplication)
            modules(AppModule.module, NetworkModule.module, dataStoreModule)
        }

        applicationScope.launch {
            sessionManager.restoreSession()
        }
    }
}
