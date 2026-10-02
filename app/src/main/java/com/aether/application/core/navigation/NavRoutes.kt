package com.aether.application.core.navigation

import kotlinx.serialization.Serializable

@Serializable data object SplashRoute
@Serializable data object ServerConfigRoute

@Serializable data object AuthGraph
@Serializable data object LoginRoute
@Serializable data class PasswordRecoveryRoute(val email: String)
@Serializable data class ValidateRecoveryCodeRoute(val email: String)
@Serializable data class ChangePasswordRoute(val email: String, val key: String)

@Serializable data object AppGraph
@Serializable data object HomeRoute
@Serializable data class ReportAnalysisRoute(val reportId: Long)
@Serializable data object CreateReportRoute
@Serializable data object ReportHistoryRoute
@Serializable data object NotificationsRoute
@Serializable data object SettingsRoute
@Serializable data object CalculatorRoute
@Serializable data object ChatbotRoute
