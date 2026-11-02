package com.aether.application.feature.reports.domain.usecase

import com.aether.application.feature.reports.domain.model.ReportsPage
import com.aether.application.feature.reports.domain.repository.ReportsRepository

/** Reports the backend lists in the current user's history, whatever their role. */
class GetHistoryUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(skip: Int, take: Int): ReportsPage =
        reportsRepository.getHistory(skip, take)
}
