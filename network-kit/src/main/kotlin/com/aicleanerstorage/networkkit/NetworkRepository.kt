package com.aicleanerstorage.networkkit

import com.aicleanerstorage.networkkit.model.NetworkInfo
import com.aicleanerstorage.networkkit.model.NetworkScanResult
import com.aicleanerstorage.networkkit.model.NetworkSpeed
import com.aicleanerstorage.networkkit.model.NetworkUsageInfo

interface NetworkRepository {
    fun readNetworkInfo(): NetworkInfo

    fun isNetworkAvailable(): Boolean

    fun isWifiConnected(): Boolean

    fun hasNetworkUsageAccess(): Boolean

    suspend fun readNetworkUsage(): NetworkUsageInfo

    suspend fun runSpeedTest(onProgress: (NetworkSpeed) -> Unit): NetworkSpeed

    suspend fun scanWifi(): NetworkScanResult
}

