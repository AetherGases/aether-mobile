package com.aether.application.core.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable data object SplashRoute
@Serializable data object ServerConfigRoute

@Serializable data object AuthGraph
@Serializable data object LoginRoute
@Serializable data class PasswordRecoveryRoute(val email: String)
@Serializable data class ValidateRecoveryCodeRoute(val email: String)
@Serializable data class ChangePasswordRoute(val email: String, val key: String)

@Serializable data object AppGraph
@Serializable data object HomeRoute
@Serializable data class ReportReviewRoute(val reportId: Long)
@Serializable data class ReportAnalysisRoute(val reportId: Long)
@Serializable data object CreateReportRoute
@Serializable data object ConsolidationApproachRoute
@Serializable data object ReportHistoryRoute
@Serializable data object NotificationsRoute
@Serializable data object SettingsRoute
@Serializable data object TermsOfUseRoute
@Serializable data object PrivacyPolicyRoute
/** [screen] is where the user was when they decided to report, sent as `screen` (see [screenNameOf]). */
@Serializable data class ReportProblemRoute(val screen: String)
@Serializable data object CalculatorRoute
@Serializable data object ChatbotRoute

/** Name of a screen as the support team reads it in a problem report; at most 50 characters. */
private val SCREEN_NAMES: List<Pair<KClass<*>, String>> = listOf(
    HomeRoute::class to "Início",
    ReportReviewRoute::class to "Revisão de relatório",
    ReportAnalysisRoute::class to "Análise de relatório",
    CreateReportRoute::class to "Novo relatório",
    ConsolidationApproachRoute::class to "Abordagem de consolidação",
    ReportHistoryRoute::class to "Histórico de relatórios",
    NotificationsRoute::class to "Notificações",
    SettingsRoute::class to "Configurações",
    TermsOfUseRoute::class to "Termos de uso",
    PrivacyPolicyRoute::class to "Política de privacidade",
    CalculatorRoute::class to "Calculadora",
    ChatbotRoute::class to "Aeko",
)

/** The name of the screen [destination] shows, or null when it isn't one of the named ones. */
fun screenNameOf(destination: NavDestination?): String? =
    destination?.let { current -> SCREEN_NAMES.firstOrNull { (route, _) -> current.hasRoute(route) }?.second }
