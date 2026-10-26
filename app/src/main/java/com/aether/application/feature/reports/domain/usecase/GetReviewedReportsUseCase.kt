package com.aether.application.feature.reports.domain.usecase

import com.aether.application.feature.reports.domain.model.ReportsPage
import com.aether.application.feature.reports.domain.repository.ReportsRepository

/** Reports already reviewed by the current user. */
class GetReviewedReportsUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(skip: Int, take: Int): ReportsPage =
        reportsRepository.getReviewedReports(skip, take)
}
