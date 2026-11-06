package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation

import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.contracts.coroutines.runSuspendCatching
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkUsageApp
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.intent.NetworkUsageExecutorFactory
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.intent.NetworkUsageOperation
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult

class NetworkUsageViewModel(
    private val repository: NetworkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val executorFactory: NetworkUsageExecutorFactory? = null,
) : MviViewModel<NetworkUsageUiState, NetworkUsageIntent, NetworkUsageEffect>(NetworkUsageUiState(
                hasAccess = repository.hasNetworkUsageAccess(),
            )) {
    private val stateUpdateLock = Any()
    override fun onIntent(intent: NetworkUsageIntent) {
        when (intent) {
            is NetworkUsageIntent.SelectTab -> selectTab(intent.index)
            else -> {
        if (intent is NetworkUsageStateChanged) {
            updateState { NetworkUsageReducer.reduce(this, intent) }
        } else {
            handleIntent(intent)
        }
    
            }
        }
    }

    private fun publishState(value: NetworkUsageUiState) {
        synchronized(stateUpdateLock) { dispatch(NetworkUsageStateChanged(value)) }
    }

    private fun changeState(transform: (NetworkUsageUiState) -> NetworkUsageUiState) {
        synchronized(stateUpdateLock) { dispatch(NetworkUsageStateChanged(transform(currentState))) }
    }


    private var scanDelayFinished = false
    private var scanJob: Job? = null
    private var permissionRequestAttempted = false
    private val executor = executorFactory?.create(viewModelScope)

    init {
        refreshUsage(startScan = true)
    }

    private fun handleIntent(action: NetworkUsageIntent) {
        when (action) {
            NetworkUsageIntent.Resumed -> refreshUsage(startScan = true)
            NetworkUsageIntent.Load -> refreshUsage(startScan = true)
            NetworkUsageIntent.PermissionRequestConsumed -> {
                permissionRequestAttempted = true
                changeState { it.copy(permissionRequestPending = false) }
            }
            NetworkUsageIntent.PermissionGranted -> refreshUsage(startScan = true)
            NetworkUsageIntent.PermissionRejected -> exit(NetworkUsageExitReason.PermissionRejected)
            NetworkUsageIntent.Back -> {
                val state = currentState
                when {
                    state.hasAccess && state.isScanning -> showStopScanDialog()
                    !state.hasAccess -> exit(NetworkUsageExitReason.PermissionRejected)
                    else -> exit(NetworkUsageExitReason.Return)
                }
            }
            NetworkUsageIntent.QuitScan -> {
                changeState { it.copy(showStopDialog = false) }
                exit(NetworkUsageExitReason.Return)
            }
            NetworkUsageIntent.ResumeScan -> startScanDelay()
            is NetworkUsageIntent.TabSelected -> selectTab(action.index)
            is NetworkUsageStateChanged -> Unit
            else -> Unit
        }
    }

    private fun showStopScanDialog() {
        if (!currentState.isScanning) return
        scanJob?.cancel()
        scanDelayFinished = false
        changeState { it.copy(showStopDialog = true) }
    }

    private fun selectTab(index: Int) {
        val tab = NetworkUsageTab.entries.getOrNull(index) ?: return
        changeState { it.copy(selectedTab = tab) }
    }

    private fun refreshUsage(startScan: Boolean) {
        if (executor != null) {
            refreshUsageThroughPipeline(startScan)
            return
        }
        val hasAccess = repository.hasNetworkUsageAccess()
        changeState {
            it.copy(
                hasAccess = hasAccess,
                usage = if (hasAccess) it.usage else null,
                isLoading = hasAccess,
                errorMessage = null,
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

        viewModelScope.launch(ioDispatcher) {
            runSuspendCatching {
                repository.readNetworkUsage()
            }.onSuccess { usage ->
                changeState {
                    it.copy(
                        hasAccess = !usage.needsUsageAccess,
                        usage = if (usage.needsUsageAccess) null else usage,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
                finishScanIfReady()
            }.onFailure { error ->
                changeState {
                    it.copy(
                        hasAccess = repository.hasNetworkUsageAccess(),
                        isLoading = false,
                        errorMessage = error.message,
                    )
                }
                finishScanIfReady()
            }
        }
    }

    private fun refreshUsageThroughPipeline(startScan: Boolean) {
        val pipeline = executor ?: return
        if (currentState.isLoading && currentState.isScanning) return
        changeState {
            it.copy(
                hasAccess = repository.hasNetworkUsageAccess(),
                isLoading = true,
                errorMessage = null,
                isScanning = startScan,
                showStopDialog = false,
                permissionRequestPending = false,
            )
        }
        if (startScan) startScanDelay()
        launchTask {
            when (val result = pipeline.execute(NetworkUsageOperation.LoadStats, "network_usage")) {
                is ExecutionResult.Accepted -> changeState {
                    it.copy(
                        hasAccess = true,
                        usage = result.value,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
                is ExecutionResult.Rejected -> changeState {
                    it.copy(isLoading = false, isScanning = false, errorMessage = result.reason)
                }
                is ExecutionResult.Busy -> changeState { it.copy(isLoading = false, isScanning = false) }
                is ExecutionResult.Cancelled -> changeState { it.copy(isLoading = false, isScanning = false) }
                is ExecutionResult.Failed -> changeState {
                    it.copy(isLoading = false, isScanning = false, errorMessage = result.error.message)
                }
            }
            finishScanIfReady()
        }
    }

    private fun startScanDelay() {
        scanJob?.cancel()
        scanDelayFinished = false
        changeState { it.copy(isScanning = true, showStopDialog = false) }
        scanJob =
            viewModelScope.launch {
                delay(TOOLBOX_SCAN_DURATION_MILLIS)
                scanDelayFinished = true
                finishScanIfReady()
            }
    }

    private fun finishScanIfReady() {
        if (scanDelayFinished && !currentState.isLoading) {
            changeState { it.copy(isScanning = false, showStopDialog = false) }
        }
    }

    private fun exit(reason: NetworkUsageExitReason) {
        emitEffect(NetworkUsageEffect.Exit(reason))
    }

    private companion object {
        private const val TOOLBOX_SCAN_DURATION_MILLIS = 1_500L
    }
}

fun buildNetworkUsageDisplayItems(
    apps: List<NetworkUsageApp>,
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

