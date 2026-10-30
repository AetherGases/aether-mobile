package com.aether.application.core.database.model

import com.aether.application.feature.auth.domain.model.Permission
import kotlinx.datetime.LocalDate

data class LocalProfile(
    val id: Long,
    val name: String,
    val email: String,
    val cpf: String,
    val phone: String?,
    val avatarUrl: String?,
    val createdAt: LocalDate?,
    val permissions: List<Permission>,
)

data class LocalEnterprise(
    val id: Long,
    val name: String,
    val tradeName: String?,
    val cnpj: String,
)

enum class AddressKind { ENTERPRISE, UNIT }

data class WorkplaceAddress(
    val kind: AddressKind,
    val zipCode: String?,
    val state: String,
    val city: String,
    val neighborhood: String,
    val street: String,
    val number: Int,
    val complement: String?,
    /** Filled once the address has been geocoded. */
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    val fullAddress: String
        get() = buildString {
            append("$street, $number - $neighborhood, $city - $state")
            if (!zipCode.isNullOrBlank()) append(", $zipCode")
            append(", Brasil")
        }
}
