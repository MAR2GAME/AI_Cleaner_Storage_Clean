package com.aicleanerstorage.spaceclean.data.local.network

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkDeviceModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageAppModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.SpeedTestPhase
import com.aicleanerstorage.spaceclean.domain.model.network.TrafficSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.util.Calendar

internal object NetworkToolboxDataSource {
    fun hasUsageAccess(context: Context): Boolean = AppPermission.UsageAccess.isGranted(context)

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivity.getNetworkCapabilities(connectivity.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun isWifiConnected(context: Context): Boolean = activeNetworkHasTransport(context, NetworkCapabilities.TRANSPORT_WIFI)

    suspend fun readUsage(context: Context): NetworkUsageResultModel =
        withContext(Dispatchers.IO) {
            val hasAccess = hasUsageAccess(context)
            val cal =
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
            val start = cal.timeInMillis
            val end = System.currentTimeMillis()

            if (hasAccess) {
                runCatching {
                    val stats = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
                    val wifi = stats.readSummary(NetworkCapabilities.TRANSPORT_WIFI, start, end)
                    val cellular = stats.readSummary(NetworkCapabilities.TRANSPORT_CELLULAR, start, end)
                    val wifiApps = stats.readPerAppUsage(context, ConnectivityManager.TYPE_WIFI, start, end)
                    val cellularApps = stats.readPerAppUsage(context, ConnectivityManager.TYPE_MOBILE, start, end)
                    NetworkUsageResultModel(
                        usage =
                            NetworkUsageInfoModel(
                                wifiRxBytes = wifi.first,
                                wifiTxBytes = wifi.second,
                                cellularRxBytes = cellular.first,
                                cellularTxBytes = cellular.second,
                                wifiApps = wifiApps,
                                cellularApps = cellularApps,
                            ),
                        isToday = true,
                        needsUsageAccess = false,
                    )
                }.getOrElse { trafficStatsUsage(needsUsageAccess = true) }
            } else {
                trafficStatsUsage(needsUsageAccess = true)
            }
        }

    suspend fun scanWifi(context: Context): NetworkScanResultModel =
        withContext(Dispatchers.IO) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val dhcp = wifiManager?.dhcpInfo
            val wifiInfo = wifiManager?.connectionInfo
            val currentIp = dhcp?.ipAddress?.takeIf { it != 0 }?.let { Formatter.formatIpAddress(it) }
            val gateway = dhcp?.gateway?.takeIf { it != 0 }?.let { Formatter.formatIpAddress(it) }
            val ssid = wifiInfo?.ssid?.removeSurrounding("\"")?.takeIf { it.isNotBlank() && it != "<unknown ssid>" }

            val devices =
                if (currentIp != null) {
                    val prefix = currentIp.substringBeforeLast('.')
                    if (prefix.isNotBlank()) {
                        scanSubnet(prefix, currentIp, gateway)
                    } else {
                        readArpDevices()
                    }
                } else {
                    readArpDevices()
                }

            NetworkScanResultModel(
                ssid,
                currentIp,
                devices.distinctBy { it.ip }.sortedBy { ipSortKey(it.ip) },
                wifiManager?.isWifiEnabled == true && currentIp != null,
            )
        }

    private fun activeNetworkHasTransport(
        context: Context,
        transport: Int,
    ): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivity.getNetworkCapabilities(connectivity.activeNetwork) ?: return false
        return capabilities.hasTransport(transport)
    }

    private fun trafficStatsUsage(needsUsageAccess: Boolean): NetworkUsageResultModel {
        val rx = TrafficStats.getTotalRxBytes().takeIf { it != TrafficStats.UNSUPPORTED.toLong() } ?: 0L
        val tx = TrafficStats.getTotalTxBytes().takeIf { it != TrafficStats.UNSUPPORTED.toLong() } ?: 0L
        val fallbackApps =
            if (rx + tx > 0L) {
                listOf(
                    NetworkUsageAppModel(
                        source = TrafficSource.SystemAndUnknown,
                        rxBytes = rx,
                        txBytes = tx,
                    ),
                )
            } else {
                emptyList()
            }
        return NetworkUsageResultModel(
            usage =
                NetworkUsageInfoModel(
                    wifiRxBytes = rx,
                    wifiTxBytes = tx,
                    cellularRxBytes = 0L,
                    cellularTxBytes = 0L,
                ),
            fallbackApps = fallbackApps,
            isToday = false,
            needsUsageAccess = needsUsageAccess,
        )
    }

    private fun NetworkStatsManager.readSummary(
        transport: Int,
        start: Long,
        end: Long,
    ): Pair<Long, Long> {
        val netType =
            if (transport == NetworkCapabilities.TRANSPORT_CELLULAR) {
                ConnectivityManager.TYPE_MOBILE
            } else {
                ConnectivityManager.TYPE_WIFI
            }
        val bucket = querySummaryForDevice(netType, null, start, end)
        return bucket.rxBytes to bucket.txBytes
    }

    private fun NetworkStatsManager.readPerAppUsage(
        context: Context,
        networkType: Int,
        start: Long,
        end: Long,
    ): List<NetworkUsageAppModel> {
        val pm = context.packageManager
        val packageInfosByUid =
            pm
                .getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { it.uid > 0 && (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                .groupBy { it.uid }
                .mapValues { (_, apps) ->
                    apps.map { app ->
                        TrafficSource.App(
                            packageName = app.packageName,
                            appName = app.loadLabel(pm).toString(),
                        )
                    }
                }

        return packageInfosByUid
            .mapNotNull { (uid, apps) ->
                runCatching {
                    val summary = queryDetailsForUid(networkType, null, start, end, uid)
                    var rx = 0L
                    var tx = 0L
                    val item = NetworkStats.Bucket()
                    summary.use {
                        while (it.hasNextBucket()) {
                            it.getNextBucket(item)
                            rx += item.rxBytes
                            tx += item.txBytes
                        }
                    }
                    if (rx + tx <= 0L) null else NetworkUsageAppModel(apps.first(), rx, tx)
                }.getOrNull()
            }.sortedByDescending { it.totalBytes }
            .take(8)
    }

    fun networkCapabilitiesSpeed(
        context: Context,
        latency: Long?,
    ): NetworkSpeedModel {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = connectivity.getNetworkCapabilities(connectivity.activeNetwork)
        return NetworkSpeedModel(
            downloadMbps = caps?.linkDownstreamBandwidthKbps?.takeIf { it > 0 }?.div(1000.0),
            uploadMbps = caps?.linkUpstreamBandwidthKbps?.takeIf { it > 0 }?.div(1000.0),
            latencyMs = latency,
            phase = SpeedTestPhase.Done,
            measured = false,
        )
    }

    private suspend fun scanSubnet(
        prefix: String,
        currentIp: String,
        gateway: String?,
    ): List<NetworkDeviceModel> =
        coroutineScope {
            val seed = listOfNotNull(currentIp, gateway)
            val targets = (1..254).map { "$prefix.$it" }
            targets.chunked(32).flatMap { chunk ->
                chunk
                    .map { ip ->
                        async(Dispatchers.IO) {
                            try {
                                currentCoroutineContext().ensureActive()
                                val addr = InetAddress.getByName(ip)
                                if (addr.isReachable(200) || ip in seed) {
                                    NetworkDeviceModel(ip, addr.canonicalHostName?.takeIf { it != ip }, readMacFromArp(ip))
                                } else {
                                    null
                                }
                            } catch (_: Exception) {
                                null
                            }
                        }
                    }.awaitAll()
                    .filterNotNull()
            }
        }

    private fun readArpDevices(): List<NetworkDeviceModel> =
        runCatching {
            BufferedReader(InputStreamReader(java.io.FileInputStream("/proc/net/arp"))).useLines { lines ->
                lines
                    .drop(1)
                    .mapNotNull { line ->
                        val parts = line.trim().split(Regex("\\s+"))
                        val ip = parts.getOrNull(0) ?: return@mapNotNull null
                        val mac = parts.getOrNull(3)?.takeIf { it != "00:00:00:00:00:00" }
                        NetworkDeviceModel(ip, null, mac)
                    }.toList()
            }
        }.getOrDefault(emptyList())

    private fun readMacFromArp(ip: String): String? = readArpDevices().firstOrNull { it.ip == ip }?.macAddress

    private fun ipSortKey(ip: String): Long = ip.split('.').fold(0L) { acc, p -> acc * 256 + (p.toLongOrNull() ?: 0L) }
}
