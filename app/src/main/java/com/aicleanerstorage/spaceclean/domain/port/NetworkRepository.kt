package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.NetworkInfoModel

import com.aicleanerstorage.spaceclean.domain.model.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkSpeedProgressModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkSpeedResultModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkUsageInfoModel

interface NetworkRepository {
    fun readNetworkInfo(): NetworkInfoModel

    fun isNetworkAvailable(): Boolean

    fun isWifiConnected(): Boolean

    fun hasNetworkUsageAccess(): Boolean

    suspend fun readNetworkUsage(): NetworkUsageInfoModel

    suspend fun runSpeedTest(onProgress: (NetworkSpeedProgressModel) -> Unit): NetworkSpeedResultModel

    suspend fun scanWifi(): NetworkScanResultModel
}

