package com.aicleanerstorage.spaceclean.feature.antivirus

interface VirusSecurityRepository {
    fun hasInstalledAppsAccess(): Boolean

    fun hasAdbRisk(): Boolean

    fun trustlookApiKeyState(): TrustlookApiKeyState
}

data class TrustlookApiKeyState(
    val isConfigured: Boolean,
    val logState: String,
)
