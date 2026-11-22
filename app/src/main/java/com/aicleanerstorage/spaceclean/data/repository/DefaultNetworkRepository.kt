package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.data.local.network.AndroidNetworkInfoReader
import com.aicleanerstorage.spaceclean.data.local.network.NetworkSpeedEndpoints
import com.aicleanerstorage.spaceclean.data.local.network.NetworkSpeedProbe
import com.aicleanerstorage.spaceclean.data.local.network.NetworkToolboxDataSource

import android.content.Context
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.SpeedTestPhase
import com.aicleanerstorage.spaceclean.domain.port.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/** 网络状态、扫描、测速与流量统计的唯一实现，直接实现 domain 端口。 */
internal class DefaultNetworkRepository(
    context: Context,
    private val endpoints: NetworkSpeedEndpoints,
) : NetworkRepository {
    private val appContext = context.applicationContext

    private val infoReader = AndroidNetworkInfoReader(appContext)
    private val mutableLastScan = MutableStateFlow<NetworkScanResultModel?>(null)

    override val lastScan: StateFlow<NetworkScanResultModel?> = mutableLastScan.asStateFlow()

    override fun readNetworkInfo(): NetworkInfoModel = infoReader.read()

    override fun isNetworkAvailable(): Boolean = NetworkToolboxDataSource.isNetworkAvailable(appContext)

    override fun isWifiConnected(): Boolean = NetworkToolboxDataSource.isWifiConnected(appContext)

    override fun hasNetworkUsageAccess(): Boolean = NetworkToolboxDataSource.hasUsageAccess(appContext)

    override suspend fun readNetworkUsage(): NetworkUsageInfoModel =
        NetworkToolboxDataSource.readUsage(appContext)

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

    override suspend fun scanWifi(): NetworkScanResultModel =
        NetworkToolboxDataSource.scanWifi(appContext).also { mutableLastScan.value = it }

    override fun clearLastScan() {
        mutableLastScan.value = null
    }
}
