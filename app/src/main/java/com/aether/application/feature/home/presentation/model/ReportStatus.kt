package com.aether.application.feature.home.presentation.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.aether.application.R
import com.aether.application.feature.home.domain.model.InventoryStatus
import com.aether.core.ui.theme.lightRed

enum class ReportStatus(
    @StringRes val labelRes: Int,
    val color: Color,
    val backgroundColor: Color,
    val iconRes: Int
){
    PENDENTE(
        labelRes = R.string.reportstatus_in_review,
        color = Color(0xFF7848C5),
        backgroundColor = Color(0xFFECE7FE),
        iconRes = R.drawable.ic_pendent
    ),
    APROVADO(
        labelRes = R.string.reportstatus_approved,
        color = Color(0xFF498371),
        backgroundColor = Color(0xFFD6F6E8),
        iconRes = R.drawable.ic_approved
    ),
    RECUSADO(
        labelRes = R.string.reportstatus_refused,
        color = lightRed,
        backgroundColor = Color(0xFFF9C5CC),
        iconRes = R.drawable.ic_rejected
    )
}

fun InventoryStatus.toReportStatus(): ReportStatus =
    when (this) {
        InventoryStatus.Input.UNDER_REVIEW -> ReportStatus.PENDENTE
        InventoryStatus.Input.APPROVED -> ReportStatus.APROVADO
        InventoryStatus.Input.REJECTED -> ReportStatus.RECUSADO
        InventoryStatus.Input.PROCESSING -> ReportStatus.PENDENTE
        InventoryStatus.Input.PROCESSING_FAILED -> ReportStatus.PENDENTE
        InventoryStatus.Output.PROCESSING_FAILED -> ReportStatus.PENDENTE
        InventoryStatus.Output.REQUESTED -> ReportStatus.PENDENTE
        InventoryStatus.Output.ISSUED -> ReportStatus.PENDENTE
}