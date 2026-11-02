package com.aether.application.feature.reports.domain.usecase

import com.aether.application.feature.reports.domain.model.ReportsPage
import com.aether.application.feature.reports.domain.repository.ReportsRepository

/** Reports waiting for the current user's review. */
class GetPendingReportsUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(skip: Int, take: Int): ReportsPage =
        reportsRepository.getPendingReports(skip, take)
}
