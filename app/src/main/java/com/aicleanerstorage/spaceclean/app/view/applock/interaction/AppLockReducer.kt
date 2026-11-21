package com.aicleanerstorage.spaceclean.app.view.applock.interaction

import com.aicleanerstorage.spaceclean.R

internal object AppLockReducer {
    fun firstPinEntered(
        state: AppLockUiState,
        pin: String,
    ): AppLockUiState =
        state.copy(
            pinStep = AppLockPinStep.CreateConfirm,
            pinInput = "",
            firstPin = pin,
            pinErrorRes = null,
        )

    fun pinCreated(state: AppLockUiState): AppLockUiState =
        state.copy(
            page = AppLockPage.Manage,
            pinStep = AppLockPinStep.Create,
            pinInput = "",
            firstPin = "",
            pinErrorRes = null,
            monitoringEnabled = true,
            isPinSet = true,
        )

    fun pinVerified(state: AppLockUiState): AppLockUiState =
        state.copy(
            page = AppLockPage.Manage,
            pinInput = "",
            pinErrorRes = null,
            isPinSet = true,
        )

    fun oldPinVerified(state: AppLockUiState): AppLockUiState =
        state.copy(
            pinStep = AppLockPinStep.ChangeNew,
            pinInput = "",
            firstPin = "",
            pinErrorRes = null,
        )

    fun newPinEntered(
        state: AppLockUiState,
        pin: String,
    ): AppLockUiState =
        state.copy(
            pinStep = AppLockPinStep.ChangeConfirm,
            pinInput = "",
            firstPin = pin,
            pinErrorRes = null,
        )

    fun pinChanged(state: AppLockUiState): AppLockUiState =
        state.copy(
            page = state.pinReturnPage,
            pinInput = "",
            firstPin = "",
            pinErrorRes = null,
            isPinSet = true,
            toastRes = R.string.pin_updated_successfully,
        )

    fun pinRejected(state: AppLockUiState): AppLockUiState =
        state.copy(
            pinInput = "",
            pinErrorRes = R.string.pin_no_match,
        )
}
