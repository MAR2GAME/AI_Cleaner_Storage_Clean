package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import com.aicleanerstorage.spaceclean.domain.security.model.VirusScanMode
import com.aicleanerstorage.spaceclean.domain.security.model.VirusThreatModel

data class VirusScanUiState(
    val home: VirusHomeUiState = VirusHomeUiState(),
    val mode: VirusScanMode? = null,
    val isScanning: Boolean = false,
    val scanCompleted: Boolean = false,
    val hasAdbRisk: Boolean = false,
    val isPathMode: Boolean = false,
    val currentLabel: String = "",
    val currentIcon: String? = null,
    val threats: List<VirusThreatModel> = emptyList(),
    val appThreatCount: Int = 0,
    val fileThreatCount: Int = 0,
    val progressFraction: Float = 0f,
) {
    val effectiveThreatCount: Int
        get() = threats.size + if (hasAdbRisk) 1 else 0
}
