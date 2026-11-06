package com.aicleanerstorage.spaceclean.app.startup

import com.aicleanerstorage.spaceclean.core.contracts.startup.AppLaunchRequest
import com.aicleanerstorage.spaceclean.core.contracts.startup.NotificationLaunchSource

import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class AppLaunchCoordinator(
    private val targetRouteResolver: (Intent?) -> String? = { null },
) {
    private val _pendingRequest = MutableStateFlow<AppLaunchRequest?>(null)
    val pendingRequest: StateFlow<AppLaunchRequest?> = _pendingRequest.asStateFlow()

    fun onCreate(intent: Intent?) {
        _pendingRequest.value = targetRouteResolver(intent)
            ?.let { route ->
                AppLaunchRequest.NotificationTarget(
                    route = route,
                    source = NotificationLaunchSource.InitialIntent,
                )
            }
            ?: AppLaunchRequest.Normal
    }

    fun onNewIntent(intent: Intent?) {
        targetRouteResolver(intent)?.let { route ->
            _pendingRequest.value =
                AppLaunchRequest.NotificationTarget(
                    route = route,
                    source = NotificationLaunchSource.NewIntent,
                )
        }
    }

    fun consumeRequestIfCurrent(request: AppLaunchRequest): Boolean {
        if (_pendingRequest.value != request) return false
        _pendingRequest.value = null
        return true
    }
}
