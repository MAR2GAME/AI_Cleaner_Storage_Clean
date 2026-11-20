package com.aicleanerstorage.spaceclean.base.toolkit.antivirus

import android.content.Context
import com.aicleanerstorage.spaceclean.data.security.ScanEngineConfigurationException
import com.aicleanerstorage.spaceclean.data.security.model.VirusScanErrorKind
import com.aicleanerstorage.spaceclean.data.security.model.VirusScanItemModel
import com.aicleanerstorage.spaceclean.data.security.model.VirusScanMode
import com.aicleanerstorage.spaceclean.data.security.VirusScanner
import com.trustlook.sdk.cloudscan.CloudScanClient
import com.trustlook.sdk.cloudscan.CloudScanListener
import com.trustlook.sdk.data.AppInfo
import com.trustlook.sdk.data.Error as TrustlookError
import com.trustlook.sdk.data.Region

class TrustlookVirusScanner(
    context: Context,
    private val apiKey: String,
) : VirusScanner {
    private val appContext = context.applicationContext
    private var cloudScanClient: CloudScanClient? = null

    override fun startScan(
        mode: VirusScanMode,
        listener: VirusScanner.Listener,
    ) {
        if (apiKey.isBlank()) throw ScanEngineConfigurationException()
        cloudScanClient = CloudScanClient.Builder(appContext)
            .setRegion(Region.INTL)
            .setConnectionTimeout(30_000)
            .setSocketTimeout(30_000)
            .build()

        val cloudListener = object : CloudScanListener() {
            override fun onScanStarted() {
                listener.onScanStarted()
            }

            override fun onScanProgress(progress: Int, total: Int, appInfo: AppInfo?) {
                listener.onScanProgress(appInfo?.toVirusScanItem())
            }

            override fun onScanError(code: Int, message: String?) {
                android.util.Log.w("VirusScan", "Scan error $code: ${message.orEmpty()}")
                listener.onScanError(code, message, code.toErrorKind())
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

        when (mode) {
            VirusScanMode.Quick -> cloudScanClient?.startQuickScan(cloudListener)
            VirusScanMode.Deep -> cloudScanClient?.startComprehensiveScan(cloudListener)
        }
    }

    override fun cancelScan() {
        runCatching { cloudScanClient?.cancelScan() }
        cloudScanClient = null
    }

    private fun AppInfo.toVirusScanItem(): VirusScanItemModel =
        VirusScanItemModel(
            packageName = packageName,
            apkPath = apkPath,
            appName = appName,
            virusName = virusName,
            summary = summary?.filterNotNull(),
            score = score,
        )

    private fun Int.toErrorKind(): VirusScanErrorKind =
        if (this in NETWORK_ERROR_CODES) {
            VirusScanErrorKind.Network
        } else {
            VirusScanErrorKind.Unknown
        }

    private companion object {
        private val NETWORK_ERROR_CODES =
            setOf(
                TrustlookError.NO_NETWORK,
                TrustlookError.SOCKET_TIMEOUT_EXCEPTION,
                TrustlookError.UNSTABLE_NETWORK,
            )
    }
}
