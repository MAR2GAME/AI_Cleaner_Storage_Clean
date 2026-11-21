package com.aicleanerstorage.spaceclean.app.view.applock.interaction

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.data.applock.model.AppLockAppModel

enum class AppLockPage {
    SelectApps,
    Pin,
    Manage,
    Search,
    Settings,
}

enum class AppLockPinStep(
    @StringRes val titleRes: Int,
    @StringRes val hintRes: Int,
) {
    Create(R.string.create_your_pin, R.string.create_your_pin),
    CreateConfirm(R.string.create_your_pin, R.string.re_enter_pin_to_proceed),
    Verify(R.string.app_lock, R.string.enter_pin_to_use),
    ChangeVerify(R.string.change_pin, R.string.enter_current_pin_to_verify),
    ChangeNew(R.string.change_pin, R.string.enter_your_new_pin),
    ChangeConfirm(R.string.change_pin, R.string.re_enter_pin_to_proceed),
}

/** App Lock 需要的两个特殊权限；用于“是否允许开启监控”的判断。 */
val AppLockPermissions: List<AppPermission> =
    listOf(AppPermission.UsageAccess, AppPermission.Overlay)

data class AppLockUiState(
    val page: AppLockPage = AppLockPage.SelectApps,
    val apps: List<AppLockAppModel> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val pinStep: AppLockPinStep = AppLockPinStep.Create,
    val pinInput: String = "",
    val firstPin: String = "",
    val pinReturnPage: AppLockPage = AppLockPage.Manage,
    @StringRes val pinErrorRes: Int? = null,
    @StringRes val toastRes: Int? = null,
    val monitoringEnabled: Boolean = true,
    val autoLockEnabled: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val isPinSet: Boolean = false,
    /** 权限状态由 Route 注入；VM 不查权限。 */
    val statuses: Map<AppPermission, Boolean> = emptyMap(),
) {
    val lockedCount: Int get() = apps.count { it.isLocked }
    val hasSelectedApps: Boolean get() = lockedCount > 0
    val allAppsLocked: Boolean get() = apps.isNotEmpty() && apps.all { it.isLocked }
    val missingPermissions: List<AppPermission>
        get() = AppLockPermissions.filter { statuses[it] != true }
}

sealed interface AppLockAction {
    data class NavigationRequested(
        val destination: AppDestination,
    ) : AppLockAction

    data object BackClicked : AppLockAction

    data object Resumed : AppLockAction

    data object ToastShown : AppLockAction

    data object SettingsClicked : AppLockAction

    data object SearchClicked : AppLockAction

    data object AllAppsSelectionToggled : AppLockAction

    data object PinCreationRequested : AppLockAction

    data object PinBackspaceClicked : AppLockAction

    data object ChangePinClicked : AppLockAction

    data class PackageToggled(
        val packageName: String,
    ) : AppLockAction

    data class PinDigitClicked(
        val digit: Char,
    ) : AppLockAction

    data class SearchQueryChanged(
        val query: String,
    ) : AppLockAction

    data class MonitoringChanged(
        val enabled: Boolean,
    ) : AppLockAction

    data class AutoLockChanged(
        val enabled: Boolean,
    ) : AppLockAction

    data class VibrationChanged(
        val enabled: Boolean,
    ) : AppLockAction

    data class PermissionClicked(
        val permission: AppPermission,
    ) : AppLockAction

    data class StatusesChanged(
        val statuses: Map<AppPermission, Boolean>,
    ) : AppLockAction
}

sealed interface AppLockEvent {
    data class Navigate(
        val destination: AppDestination,
    ) : AppLockEvent

    data object Exit : AppLockEvent

    data class OpenPermissionSettings(
        val permission: AppPermission,
    ) : AppLockEvent
}
