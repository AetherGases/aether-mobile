package com.aether.application.feature.auth.data.remote.dto

import com.aether.application.core.database.model.AddressKind
import com.aether.application.core.database.model.LocalEnterprise
import com.aether.application.core.database.model.LocalProfile
import com.aether.application.core.database.model.WorkplaceAddress
import com.aether.application.feature.auth.domain.model.Permission
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val id: Long,
    val cpf: String,
    val email: String,
    val name: String,
    val phone: String?,
    val image: ImageDto? = null,
    val permissions: List<Permission>,
    val enterprise: EnterpriseDto? = null,
    val unit: UnitDto? = null,
    @Serializable
    val createdAt: LocalDate? = null,
) {
    val avatarUrl: String? get() = image?.path

    fun toLocalProfile() = LocalProfile(
        id = id,
        name = name,
        email = email,
        cpf = cpf,
        phone = phone,
        avatarUrl = avatarUrl,
        createdAt = createdAt,
        permissions = permissions
    )

    fun toLocalEnterprise() = enterprise?.let {
        LocalEnterprise(id = it.id, name = it.name, tradeName = it.tradeName, cnpj = it.cnpj)
    }

    fun toWorkplaceAddresses(): List<WorkplaceAddress> = listOfNotNull(
        enterprise?.address?.toWorkplaceAddress(AddressKind.ENTERPRISE),
        unit?.address?.toWorkplaceAddress(AddressKind.UNIT)
    )
}

@Serializable
data class ImageDto(
    val id: Long,
    val name: String,
    val path: String,
)

@Serializable
data class EnterpriseDto(
    val id: Long,
    val name: String,
    val tradeName: String? = null,
    val cnpj: String,
    val address: AddressDto? = null,
)

@Serializable
data class UnitDto(
    val id: Long,
    val cnae: String? = null,
    val cnpj: String? = null,
    val isActive: Boolean? = null,
    val address: AddressDto? = null,
)

@Serializable
data class AddressDto(
    val id: Long,
    val zipCode: String? = null,
    val state: String,
    val city: String,
    val neighborhood: String,
    val street: String,
    val number: Int,
    val complement: String? = null,
) {
    fun toWorkplaceAddress(kind: AddressKind) = WorkplaceAddress(
        kind = kind,
        zipCode = zipCode,
        state = state,
        city = city,
        neighborhood = neighborhood,
        street = street,
        number = number,
        complement = complement
    )
}
