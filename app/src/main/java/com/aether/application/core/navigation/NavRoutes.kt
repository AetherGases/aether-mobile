package com.aether.application.core.navigation

import kotlinx.serialization.Serializable

@Serializable data object SplashRoute

@Serializable data object AuthGraph
@Serializable data object LoginRoute
@Serializable data object PasswordRecoveryRoute
@Serializable data object ValidateRecoveryCodeRoute
@Serializable data object ChangePasswordRoute

@Serializable data object EmployeeGraph
@Serializable data object EmployeeHomeRoute

@Serializable data object ManagerGraph
@Serializable data object ManagerHomeRoute