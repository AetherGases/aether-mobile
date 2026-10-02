package com.aether.application.feature.home.domain.repository

import com.aether.application.feature.home.domain.model.Inventory

interface HomeRepository {

    suspend fun getMyInventories(
        take: Int
    ): List<Inventory>

    suspend fun getPendingInventories(
        take: Int
    ): List<Inventory>

    fun getLastReviewedInventory(): Inventory
    fun getMonthlyUnitEmission(): Double
    fun getMonthlyUnitChange(): Double
    fun getLastSubmittedInventory(): Inventory
    fun countAllSubmittedInvetories(): Int
    fun getTotalEmissions(): Double
    fun getTotalReduction(): Double
    fun countReviewedInventories(): Int
}