package com.aicleanerstorage.spaceclean.app.view.networkusage.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.domain.port.NetworkRepository
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageAppModel
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.util.CoroutineUtil.runSuspendCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NetworkUsageViewModel(
    private val repository: NetworkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<NetworkUsageUiState, NetworkUsageAction, NetworkUsageEvent>(
        NetworkUsageUiState(
            hasAccess = repository.hasNetworkUsageAccess(),
        ),
    ) {
    private var scanDelayFinished = false
    private var scanJob: Job? = null
    private var permissionRequestAttempted = false

    init {
        refreshUsage(startScan = true)
    }

    override fun onAction(action: NetworkUsageAction) {
        when (action) {
            NetworkUsageAction.Resumed -> refreshUsage(startScan = true)
            NetworkUsageAction.PermissionRequestConsumed -> {
                permissionRequestAttempted = true
                setState { it.copy(permissionRequestPending = false) }
            }
            NetworkUsageAction.PermissionGranted -> refreshUsage(startScan = true)
            NetworkUsageAction.PermissionRejected -> exit(NetworkUsageExitReason.PermissionRejected)
            NetworkUsageAction.BackClicked -> {
                val state = currentState
                when {
                    state.hasAccess && state.isScanning -> showStopScanDialog()
                    !state.hasAccess -> exit(NetworkUsageExitReason.PermissionRejected)
                    else -> exit(NetworkUsageExitReason.Return)
                }
            }
            NetworkUsageAction.QuitScanConfirmed -> {
                setState { it.copy(showStopDialog = false) }
                exit(NetworkUsageExitReason.Return)
            }
            NetworkUsageAction.ResumeScanClicked -> startScanDelay()
            is NetworkUsageAction.TabSelected -> selectTab(action.index)
            else -> Unit
        }
    }

    private fun showStopScanDialog() {
        if (!currentState.isScanning) return
        scanJob?.cancel()
        scanDelayFinished = false
        setState { it.copy(showStopDialog = true) }
    }

    private fun selectTab(index: Int) {
        val tab = NetworkUsageTab.entries.getOrNull(index) ?: return
        setState { it.copy(selectedTab = tab) }
    }

    private fun refreshUsage(startScan: Boolean) {
        val hasAccess = repository.hasNetworkUsageAccess()
        setState {
            it.copy(
                hasAccess = hasAccess,
                usage = if (hasAccess) it.usage else null,
                isLoading = hasAccess,
                error = null,
                isScanning = hasAccess && (startScan || it.isScanning),
                showStopDialog = false,
                permissionRequestPending = !hasAccess && !permissionRequestAttempted,
            )
        }
        if (!hasAccess) {
            scanJob?.cancel()
            return
        }
        if (startScan) startScanDelay()
        loadUsage()
    }

    private fun loadUsage() {
        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching {
                repository.readNetworkUsage()
            }.onSuccess { usage ->
                setState {
                    it.copy(
                        hasAccess = !usage.needsUsageAccess,
                        usage = if (usage.needsUsageAccess) null else usage,
                        isLoading = false,
                        error = null,
                    )
                }
                finishScanIfReady()
            }.onFailure { error ->
                setState {
                    it.copy(
                        hasAccess = repository.hasNetworkUsageAccess(),
                        isLoading = false,
                        error = UiText.Res(R.string.load_failed),
                    )
                }
                finishScanIfReady()
            }
        }
    }

    private fun startScanDelay() {
        scanJob?.cancel()
        scanDelayFinished = false
        setState { it.copy(isScanning = true, showStopDialog = false) }
        scanJob =
            viewModelScope.launch {
                delay(TOOLBOX_SCAN_DURATION_MILLIS)
                scanDelayFinished = true
                finishScanIfReady()
            }
    }

    private fun finishScanIfReady() {
        if (scanDelayFinished && !currentState.isLoading) {
            setState { it.copy(isScanning = false, showStopDialog = false) }
        }
    }

    private fun exit(reason: NetworkUsageExitReason) {
        sendEvent(NetworkUsageEvent.Exit(reason))
    }

    private companion object {
        private const val TOOLBOX_SCAN_DURATION_MILLIS = 1_500L
    }
}

fun buildNetworkUsageDisplayItems(
    apps: List<NetworkUsageAppModel>,
    selectedTotalBytes: Long,
): List<NetworkUsageDisplayItem> {
    if (selectedTotalBytes <= 0L) return emptyList()

    val items =
        apps
            .filter { it.totalBytes > 0L }
            .map { NetworkUsageDisplayItem.fromApp(it) }
            .sortedByDescending { it.totalBytes }

    val normalizedItems = mutableListOf<NetworkUsageDisplayItem>()
    var remainingBytes = selectedTotalBytes
    for (item in items) {
        if (remainingBytes <= 0L) break
        val displayBytes = item.totalBytes.coerceAtMost(remainingBytes)
        normalizedItems +=
            item.copy(
                rxBytes = displayBytes,
                txBytes = 0L,
                totalBytes = displayBytes,
            )
        remainingBytes -= displayBytes
    }

    if (remainingBytes > 0L) {
        normalizedItems += NetworkUsageDisplayItem.systemTraffic(remainingBytes)
    }

    val aggregateBytes =
        normalizedItems
            .filter { it.isAggregate }
            .sumOf { it.totalBytes }
    val nonAggregateItems = normalizedItems.filterNot { it.isAggregate }

    return (
        nonAggregateItems +
            listOfNotNull(
                aggregateBytes
                    .takeIf { it > 0L }
                    ?.let { NetworkUsageDisplayItem.systemTraffic(it) },
            )
    ).sortedByDescending { it.totalBytes }
}
