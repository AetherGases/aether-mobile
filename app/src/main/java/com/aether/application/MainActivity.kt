package com.aether.application

import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import com.aether.application.core.navigation.AppNavigation
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aether.application.core.notification.domain.EXTRA_REPORT_ID
import com.aether.application.feature.settings.domain.model.ThemePreference
import com.aether.application.feature.settings.domain.model.UserSettings
import com.aether.application.feature.settings.domain.repository.SettingsRepository
import com.aether.core.ui.theme.AetherTheme
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()
    private var pendingReportId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) pendingReportId = reportIdFrom(intent)
        setContent {
            val settings by settingsRepository.settings
                .collectAsStateWithLifecycle(initialValue = UserSettings())
            val darkTheme = when (settings.theme) {
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
            }

            AetherTheme(darkTheme = darkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // O teclado é tratado por tela (imePadding); o root só reserva as barras do sistema.
                    contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.ime)
                ) { innerPadding ->
                    AppNavigation(
                        pendingReportId = pendingReportId,
                        onPendingReportConsumed = { pendingReportId = null },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingReportId = reportIdFrom(intent)
    }

    /** A tap on a push (FCM `data` extra, or our own notification) carries the report id as a string. */
    private fun reportIdFrom(intent: Intent?): Long? =
        intent?.getStringExtra(EXTRA_REPORT_ID)?.toLongOrNull()
}
