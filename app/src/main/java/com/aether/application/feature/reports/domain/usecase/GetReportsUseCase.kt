package com.aether.application.feature.reports.domain.usecase

import com.aether.application.feature.reports.domain.model.ReportsPage
import com.aether.application.feature.reports.domain.repository.ReportsRepository

/** Reports created by the current user. */
class GetReportsUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(skip: Int, take: Int): ReportsPage =
        reportsRepository.getMyReports(skip, take)
}
