package com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageInfo
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.contracts.coroutines.runSuspendCatching
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent.AppUsageExecutorFactory
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent.AppUsageOperation
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent.AppUsageResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class AppUsageViewModel(
    private val repository: AppUsageRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val executorFactory: AppUsageExecutorFactory? = null,
) : MviViewModel<AppUsageUiState, AppUsageIntent, AppUsageEffect>(AppUsageUiState(
                hasAccess = repository.hasAppUsageAccess(),
            )) {
    private val stateUpdateLock = Any()
    override fun onIntent(intent: AppUsageIntent) {
        when (intent) {
            is AppUsageIntent.SelectRange -> selectRange(intent.range)
            is AppUsageIntent.SelectTab -> selectTab(intent.tab)
            else -> {
        if (intent is AppUsageStateChanged) {
            updateState { AppUsageReducer.reduce(this, intent) }
        } else {
            handleIntent(intent)
        }
    
            }
        }
    }

    private fun publishState(value: AppUsageUiState) {
        synchronized(stateUpdateLock) { dispatch(AppUsageStateChanged(value)) }
    }

    private fun changeState(transform: (AppUsageUiState) -> AppUsageUiState) {
        synchronized(stateUpdateLock) { dispatch(AppUsageStateChanged(transform(currentState))) }
    }


    private var scanDelayFinished = false
    private var scanJob: Job? = null
    private var loadJob: Job? = null
    private var permissionRequestAttempted = false
    private val executor = executorFactory?.create(viewModelScope) { currentState.selectedRange }

    init {
        refreshUsage(startScan = true)
    }

    private fun handleIntent(action: AppUsageIntent) {
        when (action) {
            AppUsageIntent.Resumed -> {
                repository.resetAppUsagePermissionCache()
                refreshUsage(startScan = true)
            }
            AppUsageIntent.PermissionRequestConsumed -> {
                permissionRequestAttempted = true
                changeState { it.copy(permissionRequestPending = false) }
            }
            AppUsageIntent.PermissionGranted -> {
                repository.resetAppUsagePermissionCache()
                refreshUsage(startScan = true)
            }
            AppUsageIntent.PermissionRejected -> exit(AppUsageExitReason.PermissionRejected)
            AppUsageIntent.Back -> {
                val state = currentState
                when {
                    state.hasAccess && state.isScanning -> showStopScanDialog()
                    !state.hasAccess -> exit(AppUsageExitReason.PermissionRejected)
                    else -> exit(AppUsageExitReason.Return)
                }
            }
            AppUsageIntent.QuitScan -> {
                changeState { it.copy(showStopDialog = false) }
                exit(AppUsageExitReason.Return)
            }
            AppUsageIntent.ResumeScan -> startScanDelay()
            is AppUsageIntent.RangeSelected -> selectRange(action.range)
            is AppUsageIntent.TabSelected -> selectTab(action.tab)
            is AppUsageIntent.StopApp -> openAppInfo(action.packageName)
            is AppUsageStateChanged -> Unit
            else -> Unit
        }
    }

    private fun showStopScanDialog() {
        if (!currentState.isScanning) return
        scanJob?.cancel()
        scanDelayFinished = false
        changeState { it.copy(showStopDialog = true) }
    }

    private fun selectRange(range: AppUsageDateRange) {
        if (currentState.selectedRange == range) return
        changeState { it.copy(selectedRange = range) }
        refreshUsage(startScan = false)
    }

    private fun selectTab(tab: AppUsageMetricTab) {
        if (currentState.selectedTab == tab) return
        changeState { current ->
            current.copy(
                selectedTab = tab,
                visibleItems =
                    buildVisibleItems(
                        usages = current.usages,
                        runningPackages = current.runningPackages,
                        selectedTab = tab,
                    ),
            )
        }
    }

    private fun openAppInfo(packageName: String) {
        emitEffect(AppUsageEffect.OpenAppInfo(packageName))
    }

    private fun refreshUsage(startScan: Boolean) {
        if (executor != null) {
            refreshUsageThroughPipeline(startScan)
            return
        }
        val hasAccess = repository.hasAppUsageAccess()
        changeState { state ->
            state.copy(
                hasAccess = hasAccess,
                usages = if (hasAccess) state.usages else emptyList(),
                runningPackages = if (hasAccess) state.runningPackages else emptySet(),
                visibleItems = if (hasAccess) state.visibleItems else emptyList(),
                totalUsageMs = if (hasAccess) state.totalUsageMs else 0L,
                isLoading = hasAccess,
                errorMessage = null,
                isScanning = hasAccess && (startScan || state.isScanning),
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
            val selectedRange = currentState.selectedRange
            val (startMillis, endMillis) = selectedRange.timeBounds()
            runSuspendCatching {
                val usages = repository.appUsageBetween(startMillis, endMillis)
                val runningPackages = repository.runningPackages(usages.map { it.packageName }.toSet())
                usages to runningPackages
            }.onSuccess { (usages, runningPackages) ->
                changeState { current ->
                    current.copy(
                        hasAccess = true,
                        usages = usages,
                        runningPackages = runningPackages,
                        visibleItems =
                            buildVisibleItems(
                                usages = usages,
                                runningPackages = runningPackages,
                                selectedTab = current.selectedTab,
                            ),
                        totalUsageMs = usages.sumOf { it.totalForegroundMs },
                        isLoading = false,
                        errorMessage = null,
                    )
                }
                finishScanIfReady()
            }.onFailure { error ->
                changeState {
                    it.copy(
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
        if (loadJob?.isActive == true) return
        repository.resetAppUsagePermissionCache()
        val hasAccess = repository.hasAppUsageAccess()
        changeState { state ->
            state.copy(
                hasAccess = hasAccess,
                usages = if (hasAccess) state.usages else emptyList(),
                runningPackages = if (hasAccess) state.runningPackages else emptySet(),
                visibleItems = if (hasAccess) state.visibleItems else emptyList(),
                totalUsageMs = if (hasAccess) state.totalUsageMs else 0L,
                isLoading = true,
                errorMessage = null,
                isScanning = startScan,
                showStopDialog = false,
                permissionRequestPending = false,
            )
        }
        if (startScan) startScanDelay()
        loadJob = launchTask {
            try {
                when (val result = pipeline.execute(AppUsageOperation.LoadUsageStats, "app_usage")) {
                    is ExecutionResult.Accepted -> publishPipelineResult(result.value)
                    is ExecutionResult.Rejected -> {
                        changeState { it.copy(isLoading = false, isScanning = false, errorMessage = result.reason) }
                        exit(AppUsageExitReason.PermissionRejected)
                    }
                    is ExecutionResult.Busy -> changeState { it.copy(isLoading = false, isScanning = false) }
                    is ExecutionResult.Cancelled -> changeState { it.copy(isLoading = false, isScanning = false) }
                    is ExecutionResult.Failed -> changeState {
                        it.copy(isLoading = false, isScanning = false, errorMessage = result.error.message)
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                changeState { it.copy(isLoading = false, isScanning = false, errorMessage = error.message) }
            } finally {
                loadJob = null
                finishScanIfReady()
            }
        }
    }

    private fun publishPipelineResult(result: AppUsageResult) {
        changeState { current ->
            current.copy(
                hasAccess = true,
                usages = result.entries,
                runningPackages = result.runningPackages,
                visibleItems = buildVisibleItems(result.entries, result.runningPackages, current.selectedTab),
                totalUsageMs = result.entries.sumOf { it.totalForegroundMs },
                isLoading = false,
                errorMessage = null,
            )
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

    private fun exit(reason: AppUsageExitReason) {
        loadJob?.cancel()
        emitEffect(AppUsageEffect.Exit(reason))
    }

    private companion object {
        private const val TOOLBOX_SCAN_DURATION_MILLIS = 1_500L
        private const val VISIBLE_ITEM_LIMIT = 8

        fun buildVisibleItems(
            usages: List<AppUsageInfo>,
            runningPackages: Set<String>,
            selectedTab: AppUsageMetricTab,
        ): List<AppUsageDisplayItem> {
            if (usages.isEmpty()) return emptyList()

            val sorted =
                when (selectedTab) {
                    AppUsageMetricTab.Duration ->
                        usages.sortedWith(
                            compareByDescending<AppUsageInfo> { it.totalForegroundMs }
                                .thenByDescending { it.launchCount }
                                .thenBy { it.appName.lowercase(Locale.US) },
                        )

                    AppUsageMetricTab.LaunchCount ->
                        usages.sortedWith(
                            compareByDescending<AppUsageInfo> { it.launchCount }
                                .thenByDescending { it.totalForegroundMs }
                                .thenBy { it.appName.lowercase(Locale.US) },
                        )
                }

            val totalValue =
                when (selectedTab) {
                    AppUsageMetricTab.Duration -> usages.sumOf { it.totalForegroundMs }
                    AppUsageMetricTab.LaunchCount -> usages.sumOf { it.launchCount }.toLong()
                }

            return sorted
                .take(VISIBLE_ITEM_LIMIT)
                .mapIndexed { index, usage ->
                    val value =
                        when (selectedTab) {
                            AppUsageMetricTab.Duration -> usage.totalForegroundMs
                            AppUsageMetricTab.LaunchCount -> usage.launchCount.toLong()
                        }
                    AppUsageDisplayItem(
                        appName = usage.appName,
                        packageName = usage.packageName,
                        totalForegroundMs = usage.totalForegroundMs,
                        launchCount = usage.launchCount,
                        progress =
                            if (totalValue > 0L) {
                                (value.toFloat() / totalValue).coerceIn(0f, 1f)
                            } else {
                                0f
                            },
                        iconText = usage.appName.take(1).ifBlank { "A" },
                        colorIndex = index,
                        isRunning = usage.packageName in runningPackages,
                    )
                }
        }
    }
}

