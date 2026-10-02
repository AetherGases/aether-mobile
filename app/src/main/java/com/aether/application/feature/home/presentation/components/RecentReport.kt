package com.aether.application.feature.home.presentation.components

import com.aether.application.feature.home.presentation.model.ReportStatus

data class RecentReport(
    val id: Int,
    val name: String,
    val ownerName: String,
    val ownerAvatarUrl: String?,
    val createdAt: String,
    val status: ReportStatus
)