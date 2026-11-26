package com.aicleanerstorage.spaceclean.app.view.applock.interaction

import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.domain.model.applock.AppLockAppModel

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

/** App Lock 需要的两个特殊权限；用于 Manage 页权限行与监控前置条件。 */
val AppLockPermissions: List<AppPermission> =
    listOf(AppPermission.UsageAccess, AppPermission.Overlay)

/**
 * 动作执行前必须补齐的权限。
 *
 * 业务语义与旧版一致：选择应用不需要权限；进入 Manage 后的切换、全选、创建 PIN
 * 需要使用情况访问；开启监控需要两项权限都具备。权限门控发生在 Route，ViewModel
 * 只消费已经放行的动作。
 *
 * @param page 当前页面，用于区分 SelectApps 与受保护区域。
 * @return 需要依次申请的权限；空表示可直接执行。
 */
internal fun AppLockAction.requiredPermissions(page: AppLockPage): List<AppPermission> =
    when (this) {
        is AppLockAction.PackageToggled ->
            if (page == AppLockPage.SelectApps) emptyList() else listOf(AppPermission.UsageAccess)
        AppLockAction.AllAppsSelectionToggled,
        AppLockAction.PinCreationRequested,
        -> listOf(AppPermission.UsageAccess)
        is AppLockAction.MonitoringChanged ->
            if (enabled) listOf(AppPermission.UsageAccess, AppPermission.Overlay) else emptyList()
        else -> emptyList()
    }

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
    /** 权限状态由 Route 注入，只用于 Manage 页渲染权限行；动作门控在 Route。 */
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

    /** 进入 Manage 页后需要 Route 发起的权限申请；由 Route 消费一次。 */
    data class RequestPermission(
        val permission: AppPermission,
    ) : AppLockEvent
}
