package com.aether.application

import android.app.Application
import com.aether.application.core.di.AppModule
import com.aether.application.core.di.NetworkModule
import com.aether.application.core.di.dataStoreModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AetherApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AetherApplication)
            modules(AppModule.module, NetworkModule.module, dataStoreModule)
        }
    }
}
