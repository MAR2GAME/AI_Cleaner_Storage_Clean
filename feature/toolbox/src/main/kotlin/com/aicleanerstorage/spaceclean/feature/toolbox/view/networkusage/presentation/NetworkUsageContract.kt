package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkUsageApp
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkUsageInfo
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.UNKNOWN_NETWORK_TRAFFIC_PACKAGE

enum class NetworkUsageTab {
    Cellular,
    Wifi,
}

data class NetworkUsageDisplayItem(
    val appName: String,
    val packageName: String,
    val rxBytes: Long,
    val txBytes: Long,
    val totalBytes: Long,
    val isAggregate: Boolean = false,
) {
    companion object {
        fun fromApp(app: NetworkUsageApp): NetworkUsageDisplayItem =
            NetworkUsageDisplayItem(
                appName = app.appName,
                packageName = app.packageName,
                rxBytes = app.rxBytes,
                txBytes = app.txBytes,
                totalBytes = app.totalBytes,
                isAggregate = app.packageName == UNKNOWN_NETWORK_TRAFFIC_PACKAGE,
            )

        fun systemTraffic(totalBytes: Long): NetworkUsageDisplayItem =
            NetworkUsageDisplayItem(
                appName = "System & unknown traffic",
                packageName = UNKNOWN_NETWORK_TRAFFIC_PACKAGE,
                rxBytes = totalBytes,
                txBytes = 0L,
                totalBytes = totalBytes,
                isAggregate = true,
            )
    }
}

data class NetworkUsageUiState(
    val selectedTab: NetworkUsageTab = NetworkUsageTab.Wifi,
    val hasAccess: Boolean = false,
    val usage: NetworkUsageInfo? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isScanning: Boolean = false,
    val showStopDialog: Boolean = false,
    val permissionRequestPending: Boolean = false,
) {
    val selectedIndex: Int get() = selectedTab.ordinal

    val cellularTotalBytes: Long get() = usage?.cellularTotalBytes ?: 0L

    val wifiTotalBytes: Long get() = usage?.wifiTotalBytes ?: 0L

    fun rxBytes(tab: NetworkUsageTab): Long =
        usage?.let {
            when (tab) {
                NetworkUsageTab.Cellular -> it.cellularRxBytes
                NetworkUsageTab.Wifi -> it.wifiRxBytes
            }
        } ?: 0L

    fun txBytes(tab: NetworkUsageTab): Long =
        usage?.let {
            when (tab) {
                NetworkUsageTab.Cellular -> it.cellularTxBytes
                NetworkUsageTab.Wifi -> it.wifiTxBytes
            }
        } ?: 0L

    fun totalBytes(tab: NetworkUsageTab): Long = rxBytes(tab) + txBytes(tab)

    fun apps(tab: NetworkUsageTab): List<NetworkUsageApp> =
        usage?.let {
            when {
                tab == NetworkUsageTab.Cellular && it.cellularApps.isNotEmpty() -> it.cellularApps
                tab == NetworkUsageTab.Wifi && it.wifiApps.isNotEmpty() -> it.wifiApps
                it.fallbackApps.isNotEmpty() -> it.fallbackApps
                else -> emptyList()
            }
        }.orEmpty()

    fun displayItems(tab: NetworkUsageTab): List<NetworkUsageDisplayItem> =
        buildNetworkUsageDisplayItems(
            apps = apps(tab),
            selectedTotalBytes = totalBytes(tab),
        )

    val selectedRxBytes: Long
        get() = rxBytes(selectedTab)

    val selectedTxBytes: Long
        get() = txBytes(selectedTab)

    val selectedTotalBytes: Long get() = totalBytes(selectedTab)

    val selectedApps: List<NetworkUsageApp>
        get() = apps(selectedTab)

    val displayItems: List<NetworkUsageDisplayItem>
        get() = displayItems(selectedTab)
}

enum class NetworkUsageExitReason {
    Return,
    PermissionRejected,
}

sealed interface NetworkUsageEffect {
    data class Exit(val reason: NetworkUsageExitReason) : NetworkUsageEffect
}

sealed interface NetworkUsageIntent {
    data object Load : NetworkUsageIntent
    data object Resumed : NetworkUsageIntent
    data object PermissionRequestConsumed : NetworkUsageIntent
    data object PermissionGranted : NetworkUsageIntent
    data object PermissionRejected : NetworkUsageIntent
    data object Back : NetworkUsageIntent
    data object QuitScan : NetworkUsageIntent
    data object ResumeScan : NetworkUsageIntent
    data class TabSelected(val index: Int) : NetworkUsageIntent

    data class SelectTab(val index: Int) : NetworkUsageIntent
}

internal data class NetworkUsageStateChanged(val value: NetworkUsageUiState) : NetworkUsageIntent

