package com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation

import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.applock.AppLockApp
import androidx.annotation.StringRes
import com.aicleanerstorage.spaceclean.core.ui.R

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
    val apps: List<AppLockApp> = emptyList(),
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

sealed interface AppLockIntent {
    data class Navigate(val destination: AppDestination) : AppLockIntent
    data object Back : AppLockIntent
    data object RefreshAfterResume : AppLockIntent
    data object ConsumeToast : AppLockIntent
    data object ConsumeOverlayPermissionRequest : AppLockIntent
    data object OpenSettings : AppLockIntent
    data object OpenSearch : AppLockIntent
    data object ToggleAllApps : AppLockIntent
    data object BeginCreatePin : AppLockIntent
    data object RemovePinDigit : AppLockIntent
    data object StartChangePin : AppLockIntent
    data class TogglePackage(val packageName: String) : AppLockIntent
    data class AddPinDigit(val digit: Char) : AppLockIntent
    data class SearchQueryChanged(val query: String) : AppLockIntent
    data class MonitoringChanged(val enabled: Boolean) : AppLockIntent
    data class AutoLockChanged(val enabled: Boolean) : AppLockIntent
    data class VibrationChanged(val enabled: Boolean) : AppLockIntent

    data class UpdateSearchQuery(val query: String) : AppLockIntent
    data object ReturnToManage : AppLockIntent
    data object LeavePinPage : AppLockIntent
    data class SetMonitoringEnabled(val enabled: Boolean) : AppLockIntent
    data class SetAutoLockEnabled(val enabled: Boolean) : AppLockIntent
    data class SetVibrationEnabled(val enabled: Boolean) : AppLockIntent
}

sealed interface AppLockEffect {
    data class Navigate(val destination: AppDestination) : AppLockEffect

    data object Exit : AppLockEffect
}

internal data class AppLockStateChanged(val value: AppLockUiState) : AppLockIntent
