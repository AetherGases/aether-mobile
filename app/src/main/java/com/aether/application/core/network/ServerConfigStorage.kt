package com.aether.application.core.network

import kotlinx.coroutines.flow.Flow

interface ServerConfigStorage {

    suspend fun getSavedDomains(): List<String>

    fun observeSelectedDomain(): Flow<String?>

    suspend fun saveDomain(domain: String)
}
