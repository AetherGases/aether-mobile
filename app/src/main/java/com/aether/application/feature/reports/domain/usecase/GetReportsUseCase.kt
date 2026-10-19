package com.aether.application.feature.reports.domain.usecase

import com.aether.application.feature.home.domain.model.Inventory
import com.aether.application.feature.reports.domain.repository.ReportsRepository

/** Reports created by the current user. */
class GetReportsUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(take: Int): List<Inventory> =
        reportsRepository.getMyReports(take)
}
