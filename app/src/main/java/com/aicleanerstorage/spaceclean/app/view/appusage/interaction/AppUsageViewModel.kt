package com.aicleanerstorage.spaceclean.app.view.appusage.interaction

import com.aicleanerstorage.spaceclean.domain.model.AppUsageInfoModel
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.base.util.CoroutineUtil.runSuspendCatching
import com.aicleanerstorage.spaceclean.domain.port.AppUsageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class AppUsageViewModel(
    private val repository: AppUsageRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<AppUsageUiState, AppUsageAction, AppUsageEvent>(AppUsageUiState(
                hasAccess = repository.hasAppUsageAccess(),
            )) {
    override fun onAction(intent: AppUsageAction) {
        when (intent) {
            is AppUsageAction.RangeChangeRequested -> selectRange(intent.range)
            is AppUsageAction.TabChangeRequested -> selectTab(intent.tab)
            else -> handleIntent(intent)
        }
    }

    private var scanDelayFinished = false
    private var scanJob: Job? = null
    private var loadJob: Job? = null
    private var permissionRequestAttempted = false

    init {
        refreshUsage(startScan = true)
    }

    private fun handleIntent(action: AppUsageAction) {
        when (action) {
            AppUsageAction.Resumed, AppUsageAction.PermissionGranted -> refreshUsage(startScan = true, refreshPermission = true)
            AppUsageAction.PermissionRequestConsumed -> {
                permissionRequestAttempted = true
                changeState { it.copy(permissionRequestPending = false) }
            }
            AppUsageAction.PermissionRejected -> exit(AppUsageExitReason.PermissionRejected)
            AppUsageAction.BackClicked -> {
                val state = currentState
                when {
                    state.hasAccess && state.isScanning -> showStopScanDialog()
                    !state.hasAccess -> exit(AppUsageExitReason.PermissionRejected)
                    else -> exit(AppUsageExitReason.Return)
                }
            }
            AppUsageAction.QuitScanConfirmed -> {
                changeState { it.copy(showStopDialog = false) }
                exit(AppUsageExitReason.Return)
            }
            AppUsageAction.ResumeScanClicked -> startScanDelay()
            is AppUsageAction.RangeSelected -> selectRange(action.range)
            is AppUsageAction.TabSelected -> selectTab(action.tab)
            is AppUsageAction.StopAppClicked -> openAppInfo(action.packageName)
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
        sendEvent(AppUsageEvent.OpenAppInfo(packageName))
    }

    private fun refreshUsage(startScan: Boolean, refreshPermission: Boolean = false) {
        val hasAccess = repository.hasAppUsageAccess(refresh = refreshPermission)
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
        sendEvent(AppUsageEvent.Exit(reason))
    }

    private companion object {
        private const val TOOLBOX_SCAN_DURATION_MILLIS = 1_500L
        private const val VISIBLE_ITEM_LIMIT = 8

        fun buildVisibleItems(
            usages: List<AppUsageInfoModel>,
            runningPackages: Set<String>,
            selectedTab: AppUsageMetricTab,
        ): List<AppUsageDisplayItem> {
            if (usages.isEmpty()) return emptyList()

            val sorted =
                when (selectedTab) {
                    AppUsageMetricTab.Duration ->
                        usages.sortedWith(
                            compareByDescending<AppUsageInfoModel> { it.totalForegroundMs }
                                .thenByDescending { it.launchCount }
                                .thenBy { it.appName.lowercase(Locale.US) },
                        )

                    AppUsageMetricTab.LaunchCount ->
                        usages.sortedWith(
                            compareByDescending<AppUsageInfoModel> { it.launchCount }
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
