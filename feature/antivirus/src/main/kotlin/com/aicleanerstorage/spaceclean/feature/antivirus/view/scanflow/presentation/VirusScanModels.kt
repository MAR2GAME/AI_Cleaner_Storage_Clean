package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation

import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanMode
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusThreat

data class VirusScanUiState(
    val home: VirusHomeUiState = VirusHomeUiState(),
    val mode: VirusScanMode? = null,
    val isScanning: Boolean = false,
    val scanCompleted: Boolean = false,
    val hasAdbRisk: Boolean = false,
    val isPathMode: Boolean = false,
    val currentLabel: String = "",
    val currentIcon: String? = null,
    val threats: List<VirusThreat> = emptyList(),
    val appThreatCount: Int = 0,
    val fileThreatCount: Int = 0,
    val progressFraction: Float = 0f,
    val errorMessage: String? = null
) {
    val effectiveThreatCount: Int
        get() = threats.size + if (hasAdbRisk) 1 else 0
}
