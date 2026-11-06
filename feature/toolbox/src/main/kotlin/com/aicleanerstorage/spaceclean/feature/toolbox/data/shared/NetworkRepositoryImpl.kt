package com.aicleanerstorage.spaceclean.feature.toolbox.data.shared

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkUsageInfo
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkSpeedResult
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkSpeedProgress
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkScanResult
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository

import android.content.Context
import com.aicleanerstorage.spaceclean.feature.toolbox.data.shared.NetworkToolboxDataSource

class NetworkRepositoryImpl(
    context: Context,
) : NetworkRepository {
    private val appContext = context.applicationContext

    override fun isNetworkAvailable(): Boolean = NetworkToolboxDataSource.isNetworkAvailable(appContext)

    override fun isWifiConnected(): Boolean = NetworkToolboxDataSource.isWifiConnected(appContext)

    override fun isMobileConnected(): Boolean = NetworkToolboxDataSource.isMobileConnected(appContext)

    override fun hasNetworkUsageAccess(): Boolean = NetworkToolboxDataSource.hasUsageAccess(appContext)

    override suspend fun readNetworkUsage(): NetworkUsageInfo = NetworkToolboxDataSource.readUsage(appContext)

    override suspend fun runSpeedTest(): NetworkSpeedResult = NetworkToolboxDataSource.runSpeedTest(appContext)

    override suspend fun runSpeedTestWithProgress(onProgress: (NetworkSpeedProgress) -> Unit): NetworkSpeedResult =
        NetworkToolboxDataSource.runSpeedTestWithProgress(appContext, onProgress)

    override suspend fun scanWifi(): NetworkScanResult = NetworkToolboxDataSource.scanWifi(appContext)
}

