package com.aicleanerstorage.spaceclean.app.integration

import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal enum class NotificationPermissionRequestSource {
    Splash,
    Home,
}

internal data class NotificationPermissionRequestState(
    val requestId: Long = 0L,
    val pendingRequestId: Long? = null,
    val pendingSource: NotificationPermissionRequestSource? = null,
    val splashRequestUsed: Boolean = false,
    val homeRequestUsed: Boolean = false,
    val splashReleased: Boolean = false,
)

/** Coordinates the two notification permission opportunities shared by Splash and Home. */
internal class NotificationPermissionRequestCoordinator {
    private val mutableState = MutableStateFlow(NotificationPermissionRequestState())
    val state: StateFlow<NotificationPermissionRequestState> = mutableState.asStateFlow()

    fun onHostCreated(restoringActivity: Boolean) {
        if (restoringActivity) return
        mutableState.value = NotificationPermissionRequestState()
    }

    fun begin(
        source: NotificationPermissionRequestSource,
        runtimePermissionRequired: Boolean,
        runtimePermissionGranted: Boolean,
    ): Long? {
        val current = state.value
        if (!runtimePermissionRequired || runtimePermissionGranted || current.pendingRequestId != null) {
            return null
        }
        val sourceUsed =
            when (source) {
                NotificationPermissionRequestSource.Splash -> current.splashRequestUsed
                NotificationPermissionRequestSource.Home -> current.homeRequestUsed
            }
        if (sourceUsed) return null
        val requestId = current.requestId + 1L
        mutableState.value =
            current.copy(
                requestId = requestId,
                pendingRequestId = requestId,
                pendingSource = source,
                splashRequestUsed = current.splashRequestUsed || source == NotificationPermissionRequestSource.Splash,
                homeRequestUsed = current.homeRequestUsed || source == NotificationPermissionRequestSource.Home,
            )
        return requestId
    }

    fun complete(
        requestId: Long?,
        source: NotificationPermissionRequestSource,
    ): Boolean {
        val current = state.value
        if (requestId == null || current.pendingRequestId != requestId) return false
        mutableState.value =
            current.copy(
                pendingRequestId = null,
                pendingSource = null,
                splashReleased = current.splashReleased || source == NotificationPermissionRequestSource.Splash,
            )
        return true
    }

    fun releaseSplash() {
        if (!state.value.splashReleased) {
            mutableState.value = state.value.copy(splashReleased = true)
        }
    }

    fun saveState(): Bundle =
        Bundle().apply {
            val current = state.value
            putLong(KEY_REQUEST_ID, current.requestId)
            current.pendingRequestId?.let { putLong(KEY_PENDING_REQUEST_ID, it) }
            current.pendingSource?.let { putString(KEY_PENDING_SOURCE, it.name) }
            putBoolean(KEY_SPLASH_USED, current.splashRequestUsed)
            putBoolean(KEY_HOME_USED, current.homeRequestUsed)
            putBoolean(KEY_SPLASH_RELEASED, current.splashReleased)
        }

    fun restoreState(savedState: Bundle?) {
        if (savedState == null) return
        mutableState.value =
            NotificationPermissionRequestState(
                requestId = savedState.getLong(KEY_REQUEST_ID),
                pendingRequestId =
                    savedState
                        .takeIf { it.containsKey(KEY_PENDING_REQUEST_ID) }
                        ?.getLong(KEY_PENDING_REQUEST_ID),
                pendingSource =
                    savedState.getString(KEY_PENDING_SOURCE)?.let { value ->
                        NotificationPermissionRequestSource.entries.firstOrNull { it.name == value }
                    },
                splashRequestUsed = savedState.getBoolean(KEY_SPLASH_USED),
                homeRequestUsed = savedState.getBoolean(KEY_HOME_USED),
                splashReleased = savedState.getBoolean(KEY_SPLASH_RELEASED),
            )
    }

    private companion object {
        const val KEY_REQUEST_ID = "request_id"
        const val KEY_PENDING_REQUEST_ID = "pending_request_id"
        const val KEY_PENDING_SOURCE = "pending_source"
        const val KEY_SPLASH_USED = "splash_request_used"
        const val KEY_HOME_USED = "home_request_used"
        const val KEY_SPLASH_RELEASED = "splash_released"
    }
}
