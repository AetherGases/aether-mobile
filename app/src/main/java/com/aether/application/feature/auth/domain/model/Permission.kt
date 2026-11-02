package com.aether.application.feature.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Permission(
    val id: Long,
    val name: String,
    val description: String? = null
)

enum class AppPermission(val value: String) {
    INVENTORY_VIEW("inventory"),
    INVENTORY_ANALYSIS("inventory:analysis"),
    INVENTORY_CREATING("inventory:register"),
    CALCULATOR("calculator:gases"),
    CHATBOT("chatbot"),
}