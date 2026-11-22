package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.network.AndroidNetworkInfoReader
import com.aicleanerstorage.spaceclean.data.local.network.NetworkSpeedEndpoints
import com.aicleanerstorage.spaceclean.data.local.network.NetworkSpeedProbe
import com.aicleanerstorage.spaceclean.data.local.network.NetworkToolboxDataSource
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.SpeedTestPhase
import com.aicleanerstorage.spaceclean.domain.port.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 缃戠粶鐘舵€併€佹壂鎻忋€佹祴閫熶笌娴侀噺缁熻鐨勫敮涓€瀹炵幇锛岀洿鎺ュ疄鐜?domain 绔彛銆?*/
internal class DefaultNetworkRepository(
    context: Context,
    private val endpoints: NetworkSpeedEndpoints,
) : NetworkRepository {
    private val appContext = context.applicationContext

    private val infoReader = AndroidNetworkInfoReader(appContext)

    override fun readNetworkInfo(): NetworkInfoModel = infoReader.read()

    override fun isNetworkAvailable(): Boolean = NetworkToolboxDataSource.isNetworkAvailable(appContext)

    override fun isWifiConnected(): Boolean = NetworkToolboxDataSource.isWifiConnected(appContext)

    override fun hasNetworkUsageAccess(): Boolean = NetworkToolboxDataSource.hasUsageAccess(appContext)

    override suspend fun readNetworkUsage(): NetworkUsageResultModel = NetworkToolboxDataSource.readUsage(appContext)

    override suspend fun runSpeedTest(onProgress: (NetworkSpeedModel) -> Unit): NetworkSpeedModel =
        withContext(Dispatchers.IO) {
            onProgress(NetworkSpeedModel(phase = SpeedTestPhase.Latency))
            val latency = NetworkSpeedProbe.measureLatency(endpoints.latency)
            onProgress(NetworkSpeedModel(latencyMs = latency, phase = SpeedTestPhase.Download))

            val download = NetworkSpeedProbe.measureDownloadMbps(endpoints.download)
            onProgress(
                NetworkSpeedModel(
                    downloadMbps = download,
                    latencyMs = latency,
                    phase = SpeedTestPhase.Upload,
                ),
            )

            val upload = NetworkSpeedProbe.measureUploadMbps(endpoints.upload)
            val result =
                NetworkSpeedProbe.measuredSpeed(download, upload, latency)
                    ?: NetworkToolboxDataSource.networkCapabilitiesSpeed(appContext, latency)
            val model = result
            onProgress(model)
            model
        }

    override suspend fun scanWifi(): NetworkScanResultModel = NetworkToolboxDataSource.scanWifi(appContext)
}
