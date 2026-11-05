package com.aether.application.feature.home.presentation.model

import androidx.annotation.StringRes
import com.aether.application.R
import com.aether.application.feature.home.domain.model.InventoryStatus

enum class ReportStatus(
    @StringRes val labelRes: Int,
    val iconRes: Int
){
    PENDENTE(
        labelRes = R.string.reportstatus_in_review,
        iconRes = R.drawable.ic_pendent
    ),
    APROVADO(
        labelRes = R.string.reportstatus_approved,
        iconRes = R.drawable.ic_approved
    ),
    RECUSADO(
        labelRes = R.string.reportstatus_refused,
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