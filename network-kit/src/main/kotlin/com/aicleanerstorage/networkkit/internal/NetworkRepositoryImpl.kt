package com.aicleanerstorage.networkkit.internal

import com.aicleanerstorage.networkkit.model.measuredNetworkSpeed

import com.aicleanerstorage.networkkit.model.SpeedTestPhase

import android.content.Context
import com.aicleanerstorage.networkkit.NetworkRepository
import com.aicleanerstorage.networkkit.NetworkSpeedEndpoints
import com.aicleanerstorage.networkkit.model.NetworkScanResult
import com.aicleanerstorage.networkkit.model.NetworkSpeed
import com.aicleanerstorage.networkkit.model.NetworkUsageInfo
import com.aicleanerstorage.networkkit.permission.hasUsageAccess as hasSystemUsageAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class NetworkRepositoryImpl(
    context: Context,
    private val endpoints: NetworkSpeedEndpoints,
) : NetworkRepository {
    private val appContext = context.applicationContext

    private val infoReader = AndroidNetworkInfoReader(appContext)

    override fun readNetworkInfo() = infoReader.read()

    override fun isNetworkAvailable(): Boolean = NetworkToolboxDataSource.isNetworkAvailable(appContext)

    override fun isWifiConnected(): Boolean = NetworkToolboxDataSource.isWifiConnected(appContext)

    override fun hasNetworkUsageAccess(): Boolean = hasSystemUsageAccess(appContext)

    override suspend fun readNetworkUsage(): NetworkUsageInfo = NetworkToolboxDataSource.readUsage(appContext)

    override suspend fun runSpeedTest(
        onProgress: (NetworkSpeed) -> Unit,
    ): NetworkSpeed =
        withContext(Dispatchers.IO) {
            onProgress(NetworkSpeed(phase = SpeedTestPhase.Latency))
            val latency = NetworkSpeedProbe.measureLatency(endpoints.latency)
            onProgress(NetworkSpeed(latencyMs = latency, phase = SpeedTestPhase.Download))

            val download = NetworkSpeedProbe.measureDownloadMbps(endpoints.download)
            onProgress(
                NetworkSpeed(
                    downloadMbps = download,
                    latencyMs = latency,
                    phase = SpeedTestPhase.Upload,
                ),
            )

            val upload = NetworkSpeedProbe.measureUploadMbps(endpoints.upload)
            val result =
                measuredNetworkSpeed(download, upload, latency)
                    ?: NetworkToolboxDataSource.networkCapabilitiesSpeed(appContext, latency)

            onProgress(result)
            result
        }

    override suspend fun scanWifi(): NetworkScanResult = NetworkToolboxDataSource.scanWifi(appContext)
}

