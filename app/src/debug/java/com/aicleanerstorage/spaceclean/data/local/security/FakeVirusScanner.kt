package com.aicleanerstorage.spaceclean.data.local.security

import android.content.Context
import com.aicleanerstorage.spaceclean.domain.error.VirusScanError
import com.aicleanerstorage.spaceclean.domain.model.security.VirusRiskLevel
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanItemModel
import com.aicleanerstorage.spaceclean.domain.model.security.VirusScanMode
import com.aicleanerstorage.spaceclean.domain.port.VirusScanner
import org.json.JSONObject

/**
 * 仅 debug 构建使用的假扫描器：按 `testres/fake-virus-fixtures` 的顺序回放扫描事件。
 *
 * 不联网、不读设备，只用来在测试机上稳定复现威胁列表和结果页。
 * release 构建不包含本类；只有显式传 `-PfakeVirusScan=true` 才会被 Koin 装配。
 */
internal class FakeVirusScanner(
    private val context: Context,
) : VirusScanner {
    @Volatile
    private var cancelled = false
    private var worker: Thread? = null

    override fun startScan(
        mode: VirusScanMode,
        listener: VirusScanner.Listener,
    ) {
        cancelled = false
        val items = readItems()
        val thread =
            Thread {
                try {
                    listener.onScanStarted()
                    for (item in items) {
                        if (cancelled) return@Thread
                        if (item.packageName == null && mode != VirusScanMode.Deep) continue
                        listener.onScanProgress(item)
                        Thread.sleep(STEP_MILLIS)
                    }
                    if (!cancelled) listener.onScanFinished()
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (error: Exception) {
                    if (!cancelled) {
                        listener.onScanError(VirusScanError.Engine(code = UNKNOWN_ERROR_CODE, message = error.message))
                    }
                }
            }
        thread.isDaemon = true
        worker = thread
        thread.start()
    }

    override fun cancelScan() {
        cancelled = true
        worker?.interrupt()
        worker = null
    }

    private fun readItems(): List<VirusScanItemModel> =
        runCatching {
            val root = JSONObject(context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() })
            val items = mutableListOf<VirusScanItemModel>()
            root.optJSONArray("apps")?.let { array ->
                for (index in 0 until array.length()) {
                    items += array.getJSONObject(index).toItem(isFile = false)
                }
            }
            root.optJSONArray("files")?.let { array ->
                for (index in 0 until array.length()) {
                    items += array.getJSONObject(index).toItem(isFile = true)
                }
            }
            items
        }.getOrDefault(emptyList())

    private fun JSONObject.toItem(isFile: Boolean): VirusScanItemModel {
        val isThreat = optString("risk").equals("threat", ignoreCase = true)
        return VirusScanItemModel(
            appName = valueOrNull("appName"),
            packageName = if (isFile) null else valueOrNull("packageName"),
            apkPath = valueOrNull("apkPath"),
            threatName = valueOrNull("threatName"),
            description = valueOrNull("description"),
            riskLevel = if (isThreat) VirusRiskLevel.Threat else VirusRiskLevel.Safe,
        )
    }

    private fun JSONObject.valueOrNull(name: String): String? =
        if (isNull(name)) null else optString(name).takeIf { it.isNotBlank() }

    private companion object {
        const val ASSET_NAME = "fake_virus_scan.json"
        const val STEP_MILLIS = 120L
        const val UNKNOWN_ERROR_CODE = -1
    }
}
