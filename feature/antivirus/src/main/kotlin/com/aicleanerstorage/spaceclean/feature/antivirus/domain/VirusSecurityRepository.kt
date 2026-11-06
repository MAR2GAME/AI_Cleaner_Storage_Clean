package com.aicleanerstorage.spaceclean.feature.antivirus.domain

import com.aicleanerstorage.spaceclean.feature.antivirus.domain.TrustlookApiKeyState
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanItem
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusThreat

interface VirusSecurityRepository {
    fun appLabel(packageName: String): String
    fun threatFor(item: VirusScanItem, isFile: Boolean): VirusThreat
    fun reportStartError(error: Throwable)
    fun reportScanError(code: Int, message: String?)

    fun hasInstalledAppsAccess(): Boolean

    fun hasAdbRisk(): Boolean

    fun trustlookApiKeyState(): TrustlookApiKeyState
}

data class TrustlookApiKeyState(
    val isConfigured: Boolean,
    val logState: String,
)
