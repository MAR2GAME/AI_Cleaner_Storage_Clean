package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.data.local.AndroidNetworkInfoReader

import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.NetworkToolboxDataSource
import com.aicleanerstorage.spaceclean.data.remote.NetworkSpeedProbe
import com.aicleanerstorage.spaceclean.domain.model.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkSpeedProgressModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkSpeedResultModel
import com.aicleanerstorage.spaceclean.domain.model.NetworkUsageInfoModel
import com.aicleanerstorage.spaceclean.domain.port.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class NetworkRepositoryImpl(
    context: Context,
) : NetworkRepository {
    private val appContext = context.applicationContext

    private val infoReader = AndroidNetworkInfoReader(appContext)

    override fun readNetworkInfo() = infoReader.read()

    override fun isNetworkAvailable(): Boolean = NetworkToolboxDataSource.isNetworkAvailable(appContext)

    override fun isWifiConnected(): Boolean = NetworkToolboxDataSource.isWifiConnected(appContext)

    override fun hasNetworkUsageAccess(): Boolean = NetworkToolboxDataSource.hasUsageAccess(appContext)

    override suspend fun readNetworkUsage(): NetworkUsageInfoModel = NetworkToolboxDataSource.readUsage(appContext)

    override suspend fun runSpeedTest(
        onProgress: (NetworkSpeedProgressModel) -> Unit,
    ): NetworkSpeedResultModel =
        withContext(Dispatchers.IO) {
            onProgress(NetworkSpeedProgressModel(phase = "latency"))
            val latency = NetworkSpeedProbe.measureLatency()
            onProgress(NetworkSpeedProgressModel(latencyMs = latency, phase = "download"))

            val download = NetworkSpeedProbe.measureDownloadMbps()
            val downloadLabel = download?.let(NetworkToolboxDataSource::formatMbps)
            onProgress(
                NetworkSpeedProgressModel(
                    downloadMbps = downloadLabel,
                    latencyMs = latency,
                    phase = "upload",
                ),
            )

            val upload = NetworkSpeedProbe.measureUploadMbps()
            val uploadLabel = upload?.let(NetworkToolboxDataSource::formatMbps)
            val result =
                if (download != null || upload != null) {
                    NetworkSpeedResultModel(
                        downloadMbps = downloadLabel ?: NetworkToolboxDataSource.formatMbps(0.0),
                        uploadMbps = uploadLabel ?: NetworkToolboxDataSource.formatMbps(0.0),
                        latencyMs = latency,
                        measured = true,
                    )
                } else {
                    NetworkToolboxDataSource.networkCapabilitiesSpeed(appContext, latency)
                }

            onProgress(
                NetworkSpeedProgressModel(
                    downloadMbps = result.downloadMbps,
                    uploadMbps = result.uploadMbps,
                    latencyMs = result.latencyMs,
                    phase = "done",
                ),
            )
            result
        }

    override suspend fun scanWifi(): NetworkScanResultModel = NetworkToolboxDataSource.scanWifi(appContext)
}

