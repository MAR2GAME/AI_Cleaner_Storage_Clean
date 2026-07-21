package com.aicleanerstorage.spaceclean.feature.toolbox.shared.network

import android.content.Context
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkToolboxDataSource
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkScanResult
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkSpeedProgress
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkSpeedResult
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkUsageInfo
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkRepository

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
