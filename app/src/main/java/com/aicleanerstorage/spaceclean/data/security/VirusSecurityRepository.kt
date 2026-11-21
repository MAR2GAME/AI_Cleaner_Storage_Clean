package com.aicleanerstorage.spaceclean.data.security

import com.aicleanerstorage.spaceclean.data.security.model.VirusScanItemModel
import com.aicleanerstorage.spaceclean.data.security.model.VirusThreatModel

interface VirusSecurityRepository {
    fun isScanNoticeAccepted(): Boolean

    fun setScanNoticeAccepted()

    fun hasInstalledAppsAccessFailedBefore(): Boolean

    fun setInstalledAppsAccessFailed(failed: Boolean)

    fun appLabel(packageName: String): String

    fun threatFor(
        item: VirusScanItemModel,
        isFile: Boolean,
    ): VirusThreatModel

    fun hasInstalledAppsAccess(): Boolean

    fun hasAdbRisk(): Boolean
}
