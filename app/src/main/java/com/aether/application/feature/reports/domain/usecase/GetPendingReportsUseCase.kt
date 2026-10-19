package com.aether.application.feature.reports.domain.usecase

import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.reports.domain.repository.ReportsRepository

/** Reports waiting for the current user's review. */
class GetPendingReportsUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(take: Int): List<Inventory> =
        reportsRepository.getPendingReports(take)
}
