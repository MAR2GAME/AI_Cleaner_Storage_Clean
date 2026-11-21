package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import com.aicleanerstorage.spaceclean.domain.security.model.VirusScanMode
import com.aicleanerstorage.spaceclean.domain.security.model.VirusThreatModel

internal sealed interface VirusScanUpdate : VirusScanAction {
    data object ScanNoticeShown : VirusScanUpdate

    data class AccessCheckStarted(
        val mode: VirusScanMode,
    ) : VirusScanUpdate

    data class AccessCheckFinished(
        val showPermissionDialog: Boolean,
    ) : VirusScanUpdate

    data object ScanReset : VirusScanUpdate

    data class ScanStarted(
        val mode: VirusScanMode,
    ) : VirusScanUpdate

    data class AdbRiskChecked(
        val hasRisk: Boolean,
    ) : VirusScanUpdate

    data class ThreatFound(
        val threat: VirusThreatModel,
    ) : VirusScanUpdate

    data class ProgressUpdated(
        val fraction: Float,
    ) : VirusScanUpdate

    data class AppDisplayed(
        val label: String,
        val icon: String,
    ) : VirusScanUpdate

    data object PathDisplayStarted : VirusScanUpdate

    data class PathDisplayed(
        val path: String,
    ) : VirusScanUpdate

    data object ScanFinished : VirusScanUpdate
}
