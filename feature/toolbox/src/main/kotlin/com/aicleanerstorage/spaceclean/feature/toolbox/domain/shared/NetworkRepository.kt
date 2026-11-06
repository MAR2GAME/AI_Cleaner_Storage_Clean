package com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkUsageInfo
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkSpeedResult
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkSpeedProgress
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkScanResult


interface NetworkRepository {
    fun isNetworkAvailable(): Boolean

    fun isWifiConnected(): Boolean

    fun isMobileConnected(): Boolean

    fun hasNetworkUsageAccess(): Boolean

    suspend fun readNetworkUsage(): NetworkUsageInfo

    suspend fun runSpeedTest(): NetworkSpeedResult

    suspend fun runSpeedTestWithProgress(onProgress: (NetworkSpeedProgress) -> Unit): NetworkSpeedResult

    suspend fun scanWifi(): NetworkScanResult
}

