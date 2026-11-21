package com.aicleanerstorage.spaceclean.app.view.appusage.interaction

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.appusage.model.AppUsageInfoModel
import java.util.Calendar

enum class AppUsageDateRange(
    @StringRes val labelRes: Int,
) {
    Today(R.string.today),
    Yesterday(R.string.yesterday),
    Last7Days(R.string.last_7_days),
    Last30Days(R.string.last_30_days),
    ;

    fun timeBounds(nowMillis: Long = System.currentTimeMillis()): Pair<Long, Long> {
        val startToday =
            Calendar
                .getInstance()
                .apply {
                    timeInMillis = nowMillis
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
        return when (this) {
            Today -> startToday to nowMillis
            Yesterday -> startToday - DAY_MILLIS to startToday
            Last7Days -> startToday - 6 * DAY_MILLIS to nowMillis
            Last30Days -> startToday - 29 * DAY_MILLIS to nowMillis
        }
    }

    private companion object {
        private const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}

enum class AppUsageMetricTab(
    @StringRes val titleRes: Int,
) {
    Duration(R.string.hours_spent),
    LaunchCount(R.string.times_opened),
}

data class AppUsageDisplayItem(
    val appName: String,
    val packageName: String,
    val totalForegroundMs: Long,
    val launchCount: Int,
    val progress: Float,
    val iconText: String,
    val colorIndex: Int,
    val isRunning: Boolean,
)

data class AppUsageUiState(
    val selectedRange: AppUsageDateRange = AppUsageDateRange.Today,
    val selectedTab: AppUsageMetricTab = AppUsageMetricTab.Duration,
    val hasAccess: Boolean = false,
    val usages: List<AppUsageInfoModel> = emptyList(),
    val runningPackages: Set<String> = emptySet(),
    val visibleItems: List<AppUsageDisplayItem> = emptyList(),
    val totalUsageMs: Long = 0L,
    val isLoading: Boolean = true,
    val error: UiText? = null,
    val isScanning: Boolean = false,
    val showStopDialog: Boolean = false,
    val permissionRequestPending: Boolean = false,
)

sealed interface AppUsageEvent {
    data class OpenAppInfo(
        val packageName: String,
    ) : AppUsageEvent

    data class Exit(
        val reason: AppUsageExitReason,
    ) : AppUsageEvent
}

enum class AppUsageExitReason {
    Return,
    PermissionRejected,
}

sealed interface AppUsageAction {
    data object Resumed : AppUsageAction

    data object PermissionRequestConsumed : AppUsageAction

    data object PermissionGranted : AppUsageAction

    data object PermissionRejected : AppUsageAction

    data object BackClicked : AppUsageAction

    data object QuitScanConfirmed : AppUsageAction

    data object ResumeScanClicked : AppUsageAction

    data class RangeSelected(
        val range: AppUsageDateRange,
    ) : AppUsageAction

    data class TabSelected(
        val tab: AppUsageMetricTab,
    ) : AppUsageAction

    data class StopAppClicked(
        val packageName: String,
    ) : AppUsageAction
}
