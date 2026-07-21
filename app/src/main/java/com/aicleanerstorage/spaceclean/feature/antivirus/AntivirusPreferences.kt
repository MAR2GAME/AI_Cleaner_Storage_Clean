package com.aicleanerstorage.spaceclean.feature.antivirus

interface AntivirusPreferences {
    fun isScanNoticeAccepted(): Boolean

    fun setScanNoticeAccepted()

    fun hasInstalledAppsAccessFailedBefore(): Boolean

    fun setInstalledAppsAccessFailed(failed: Boolean)
}
