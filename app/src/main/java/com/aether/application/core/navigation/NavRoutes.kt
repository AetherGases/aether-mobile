package com.aether.application.core.navigation

import kotlinx.serialization.Serializable

@Serializable data object SplashRoute

@Serializable data object AuthGraph
@Serializable data object LoginRoute
@Serializable data class PasswordRecoveryRoute(val email: String)
@Serializable data class ValidateRecoveryCodeRoute(val email: String)
@Serializable data class ChangePasswordRoute(val email: String, val key: String)

@Serializable data object EmployeeGraph
@Serializable data object EmployeeHomeRoute

@Serializable data object ManagerGraph
@Serializable data object ManagerHomeRoute
