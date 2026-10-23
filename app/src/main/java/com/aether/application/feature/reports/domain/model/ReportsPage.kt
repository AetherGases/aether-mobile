package com.aether.application.feature.reports.domain.model

import com.aether.application.feature.home.domain.model.Inventory

/** One page of reports; [totalCount] is the size of the whole list on the server. */
data class ReportsPage(
    val reports: List<Inventory>,
    val totalCount: Long,
)
