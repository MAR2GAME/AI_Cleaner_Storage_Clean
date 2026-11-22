package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanMode
import com.aicleanerstorage.spaceclean.domain.model.security.VirusThreatModel

internal sealed interface VirusScanUpdate {
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
