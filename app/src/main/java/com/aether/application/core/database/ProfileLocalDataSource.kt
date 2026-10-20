package com.aether.application.core.database

import com.aether.application.core.database.model.AddressKind
import com.aether.application.core.database.model.LocalProfile
import com.aether.application.core.database.model.WorkplaceAddress

interface ProfileLocalDataSource {

    /** Replaces the stored profile and its permissions. */
    suspend fun saveProfile(profile: LocalProfile)

    suspend fun getProfile(): LocalProfile?

    suspend fun updateAvatar(avatarUrl: String)

    /** Replaces every stored address. */
    suspend fun saveAddresses(addresses: List<WorkplaceAddress>)

    suspend fun getAddress(kind: AddressKind): WorkplaceAddress?

    suspend fun saveCoordinates(kind: AddressKind, latitude: Double, longitude: Double)

    suspend fun clear()
}
