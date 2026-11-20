package com.aicleanerstorage.spaceclean.app.view.applock.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.model.AppLockAppModel
import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.R

enum class AppLockPage {
    SelectApps,
    Pin,
    Manage,
    Search,
    Settings
}

enum class AppLockPinStep(
    @StringRes val titleRes: Int,
    @StringRes val hintRes: Int
) {
    Create(R.string.create_your_pin, R.string.create_your_pin),
    CreateConfirm(R.string.create_your_pin, R.string.re_enter_pin_to_proceed),
    Verify(R.string.app_lock, R.string.enter_pin_to_use),
    ChangeVerify(R.string.change_pin, R.string.enter_current_pin_to_verify),
    ChangeNew(R.string.change_pin, R.string.enter_your_new_pin),
    ChangeConfirm(R.string.change_pin, R.string.re_enter_pin_to_proceed)
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
    val overlayPermissionRequired: Boolean = false
) {
    val lockedCount: Int get() = apps.count { it.isLocked }
    val hasSelectedApps: Boolean get() = lockedCount > 0
    val allAppsLocked: Boolean get() = apps.isNotEmpty() && apps.all { it.isLocked }
}

sealed interface AppLockAction {
    data class NavigationRequested(val destination: AppDestination) : AppLockAction
    data object BackClicked : AppLockAction
    data object Resumed : AppLockAction
    data object ToastShown : AppLockAction
    data object OverlayPermissionRequestHandled : AppLockAction
    data object SettingsClicked : AppLockAction
    data object SearchClicked : AppLockAction
    data object AllAppsSelectionToggled : AppLockAction
    data object PinCreationRequested : AppLockAction
    data object PinBackspaceClicked : AppLockAction
    data object ChangePinClicked : AppLockAction
    data class PackageToggled(val packageName: String) : AppLockAction
    data class PinDigitClicked(val digit: Char) : AppLockAction
    data class SearchQueryChanged(val query: String) : AppLockAction
    data class MonitoringChanged(val enabled: Boolean) : AppLockAction
    data class AutoLockChanged(val enabled: Boolean) : AppLockAction
    data class VibrationChanged(val enabled: Boolean) : AppLockAction

    data class SearchInputChanged(val query: String) : AppLockAction
    data object ManagePageRequested : AppLockAction
    data object PinPageExitRequested : AppLockAction
    data class MonitoringSettingRequested(val enabled: Boolean) : AppLockAction
    data class AutoLockSettingRequested(val enabled: Boolean) : AppLockAction
    data class VibrationSettingRequested(val enabled: Boolean) : AppLockAction
}

sealed interface AppLockEvent {
    data class Navigate(val destination: AppDestination) : AppLockEvent

    data object Exit : AppLockEvent
}
