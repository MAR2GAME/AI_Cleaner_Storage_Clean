package com.aicleanerstorage.spaceclean.data.local.security

import android.content.Context
import com.aicleanerstorage.spaceclean.domain.error.VirusScanError
import com.aicleanerstorage.spaceclean.domain.model.security.VirusRiskLevel
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanItemModel
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanMode
import com.aicleanerstorage.spaceclean.domain.port.VirusScanner
import com.trustlook.sdk.cloudscan.CloudScanClient
import com.trustlook.sdk.cloudscan.CloudScanListener
import com.trustlook.sdk.data.AppInfo
import com.trustlook.sdk.data.Region

/**
 * 用 Trustlook CloudScan SDK 实现病毒扫描。
 *
 * 只负责 SDK 适配与事件转换；扫描流程、页面状态和失败文案留在 ViewModel / UI。
 * SDK 的错误码、启动异常和配置缺失都在这里收口成 [VirusScanError]。
 *
 * @param context 用于构建 SDK 客户端。
 * @param apiKey Trustlook API key；为空时通过 listener 返回
 *   [VirusScanError.NotConfigured]，不抛异常。
 */
internal class TrustlookVirusScanner(
    context: Context,
    private val apiKey: String,
) : VirusScanner {
    private val appContext = context.applicationContext
    private var cloudScanClient: CloudScanClient? = null

    override fun startScan(
        mode: VirusScanMode,
        listener: VirusScanner.Listener,
    ) {
        if (apiKey.isBlank()) {
            listener.onScanError(VirusScanError.NotConfigured)
            return
        }
        val client =
            runCatching {
                CloudScanClient
                    .Builder(appContext)
                    .setRegion(Region.INTL)
                    .setConnectionTimeout(30_000)
                    .setSocketTimeout(30_000)
                    .build()
            }.getOrElse { error ->
                listener.onScanError(VirusScanError.Engine(code = UNKNOWN_ERROR_CODE, message = error.message))
                return
            }
        cloudScanClient = client

        val cloudListener =
            object : CloudScanListener() {
                override fun onScanStarted() {
                    listener.onScanStarted()
                }

                override fun onScanProgress(
                    progress: Int,
                    total: Int,
                    appInfo: AppInfo?,
                ) {
                    listener.onScanProgress(appInfo?.toVirusScanItem())
                }

                override fun onScanError(
                    code: Int,
                    message: String?,
                ) {
                    android.util.Log.w("VirusScan", "Scan error $code: ${message.orEmpty()}")
                    listener.onScanError(VirusScanError.Engine(code, message))
                }

                override fun onScanCanceled() {
                    listener.onScanCanceled()
                }

                override fun onScanInterrupt() {
                    listener.onScanInterrupt()
                }

                override fun onScanFinished(appList: List<AppInfo?>?) {
                    listener.onScanFinished()
                }
            }

        runCatching {
            when (mode) {
                VirusScanMode.Quick -> client.startQuickScan(cloudListener)
                VirusScanMode.Deep -> client.startComprehensiveScan(cloudListener)
            }
        }.onFailure { error ->
            listener.onScanError(VirusScanError.Engine(code = UNKNOWN_ERROR_CODE, message = error.message))
        }
    }

    override fun cancelScan() {
        runCatching { cloudScanClient?.cancelScan() }
        cloudScanClient = null
    }

    private fun AppInfo.toVirusScanItem(): VirusScanItemModel {
        val summaryLines = summary?.filterNotNull()
        return VirusScanItemModel(
            appName = appName,
            packageName = packageName,
            apkPath = apkPath,
            threatName = virusName,
            description = summaryLines?.getOrNull(1)?.takeIf { it.isNotBlank() } ?: virusName,
            riskLevel = if (score >= THREAT_SCORE) VirusRiskLevel.Threat else VirusRiskLevel.Safe,
        )
    }

    private companion object {
        const val UNKNOWN_ERROR_CODE = -1
        const val THREAT_SCORE = 6
    }
}
