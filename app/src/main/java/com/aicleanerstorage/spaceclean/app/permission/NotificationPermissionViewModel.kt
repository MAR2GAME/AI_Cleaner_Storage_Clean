package com.aicleanerstorage.spaceclean.app.permission

import android.app.Application
import android.os.Build
import androidx.lifecycle.ViewModel
import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.app.platform.permission.PermissionPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class NotificationPermissionViewModel(
    private val application: Application,
    private val permissionPreferences: PermissionPreferences,
    private val nowMillis: () -> Long = System::currentTimeMillis,
) : ViewModel() {
    private val runtimePermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    private val _uiState = MutableStateFlow(
        initialNotificationPermissionState(
            runtimePermissionRequired = runtimePermissionRequired,
            facts = readFacts(shouldShowRationale = false),
        )
    )
    val uiState: StateFlow<NotificationPermissionUiState> = _uiState.asStateFlow()

    private val effectsChannel = Channel<NotificationPermissionCommand.Host>(Channel.UNLIMITED)
    internal val effects = effectsChannel.receiveAsFlow()

    fun onSurfaceChanged(
        surface: NotificationPermissionSurface,
        shouldShowRationale: Boolean,
    ) {
        dispatch(
            NotificationPermissionIntent.SurfaceChanged(
                surface = surface,
                facts = readFacts(shouldShowRationale),
            ),
        )
    }

    fun onAppResumed(shouldShowRationale: Boolean) {
        dispatch(NotificationPermissionIntent.AppResumed(readFacts(shouldShowRationale)))
    }

    fun onSystemPermissionResult(
        granted: Boolean,
        shouldShowRationale: Boolean,
    ) {
        dispatch(
            NotificationPermissionIntent.SystemPermissionResult(
                readFacts(
                    shouldShowRationale = shouldShowRationale,
                    grantedOverride = granted,
                ),
            ),
        )
    }

    fun onSystemRequestUnavailable() { dispatch(NotificationPermissionIntent.SystemRequestUnavailable) }

    fun onCustomPromptConfirmed() {
        dispatch(NotificationPermissionIntent.CustomPromptConfirmed)
    }

    fun onCustomPromptDismissed() {
        dispatch(NotificationPermissionIntent.CustomPromptDismissed)
    }

    fun onSettingsFinished(shouldShowRationale: Boolean) {
        dispatch(NotificationPermissionIntent.SettingsFinished(readFacts(shouldShowRationale)))
    }

    fun onCustomPromptShown() { dispatch(NotificationPermissionIntent.CustomPromptShown) }

    override fun onCleared() { effectsChannel.cancel(); super.onCleared() }

    private fun dispatch(action: NotificationPermissionIntent) {
        if (!runtimePermissionRequired) return
        val transition = reduceNotificationPermissionState(_uiState.value, action, nowMillis())
        _uiState.value = transition.state
        transition.commands.forEach(::handleCommand)
    }

    private fun handleCommand(command: NotificationPermissionCommand) {
        when (command) {
            is NotificationPermissionCommand.SaveLastCustomPromptAt ->
                permissionPreferences.saveLastNotificationPermissionCustomPromptAt(command.timestampMillis)
            is NotificationPermissionCommand.Host -> effectsChannel.trySend(command)
        }
    }

    private fun readFacts(
        shouldShowRationale: Boolean,
        grantedOverride: Boolean? = null,
    ): NotificationPermissionFacts =
        NotificationPermissionFacts(
            hasPermission = grantedOverride ?: AppConfig.hasPostNotificationsPermission(application),
            hasRequestedBefore = permissionPreferences.hasRequestedNotificationRuntimePermissionBefore(),
            shouldShowRationale = shouldShowRationale,
            lastCustomPromptAt = permissionPreferences.readLastNotificationPermissionCustomPromptAt(),
        )
}
