package com.aicleanerstorage.spaceclean.app.view.networkusage.interaction

import com.aicleanerstorage.networkkit.model.NetworkUsageApp
import com.aicleanerstorage.networkkit.model.NetworkUsageInfo
import com.aicleanerstorage.networkkit.model.TrafficSource
import com.aicleanerstorage.spaceclean.base.mvi.UiText

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
        fun fromApp(app: NetworkUsageApp): NetworkUsageDisplayItem {
            val source = app.source as? TrafficSource.App ?: return systemTraffic(app.totalBytes)
            return NetworkUsageDisplayItem(
                appName = source.appName,
                packageName = source.packageName,
                rxBytes = app.rxBytes,
                txBytes = app.txBytes,
                totalBytes = app.totalBytes,
            )
        }

        fun systemTraffic(totalBytes: Long): NetworkUsageDisplayItem =
            NetworkUsageDisplayItem(
                appName = "",
                packageName = "",
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
    val error: UiText? = null,
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
        usage
            ?.let {
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

    val selectedTotalBytes: Long get() = totalBytes(selectedTab)

    val displayItems: List<NetworkUsageDisplayItem>
        get() = displayItems(selectedTab)
}

enum class NetworkUsageExitReason {
    Return,
    PermissionRejected,
}

sealed interface NetworkUsageEvent {
    data class Exit(
        val reason: NetworkUsageExitReason,
    ) : NetworkUsageEvent
}

sealed interface NetworkUsageAction {
    data object Resumed : NetworkUsageAction

    data object PermissionRequestConsumed : NetworkUsageAction

    data object PermissionGranted : NetworkUsageAction

    data object PermissionRejected : NetworkUsageAction

    data object BackClicked : NetworkUsageAction

    data object QuitScanConfirmed : NetworkUsageAction

    data object ResumeScanClicked : NetworkUsageAction

    data class TabSelected(
        val index: Int,
    ) : NetworkUsageAction
}
