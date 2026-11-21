package com.aicleanerstorage.networkkit

import com.aicleanerstorage.networkkit.model.NetworkInfo
import com.aicleanerstorage.networkkit.model.NetworkScanResult
import com.aicleanerstorage.networkkit.model.NetworkSpeed
import com.aicleanerstorage.networkkit.model.NetworkUsageInfo

import kotlinx.coroutines.flow.StateFlow

interface NetworkRepository {
    fun readNetworkInfo(): NetworkInfo

    fun isNetworkAvailable(): Boolean

    fun isWifiConnected(): Boolean

    fun hasNetworkUsageAccess(): Boolean

    suspend fun readNetworkUsage(): NetworkUsageInfo

    suspend fun runSpeedTest(onProgress: (NetworkSpeed) -> Unit): NetworkSpeed

    /** Last successful wifi scan; the single source shared by the scan and devices pages. */
    val lastScan: StateFlow<NetworkScanResult?>

    suspend fun scanWifi(): NetworkScanResult

    fun clearLastScan()
}

