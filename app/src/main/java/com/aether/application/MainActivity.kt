package com.aether.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aether.application.feature.auth.presentation.screen.LoginScreen
import com.aether.application.feature.auth.presentation.viewmodel.LoginViewModel
import com.aether.core.ui.theme.AetherTheme
import org.koin.compose.viewmodel.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AetherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = koinViewModel<LoginViewModel>()
                    val uiState by viewModel.uiState.collectAsState()

                    LoginScreen(
                        onLoginClick = { email, password, rememberMe ->
                            viewModel.onLoginClick(email, password, rememberMe)
                        },
                        onForgotPasswordClick = {},
                        isLoading = uiState.isLoading,
                        errorMessage = uiState.errorMessage,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AetherTheme {
        LoginScreen(
            onLoginClick = { _, _, _ -> },
            onForgotPasswordClick = {}
        )
    }
}