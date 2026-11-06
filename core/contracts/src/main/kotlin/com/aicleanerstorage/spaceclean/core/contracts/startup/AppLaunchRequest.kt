package com.aicleanerstorage.spaceclean.core.contracts.startup

sealed interface AppLaunchRequest {
    data object Normal : AppLaunchRequest
    data class NotificationTarget(
        val route: String,
        val source: NotificationLaunchSource = NotificationLaunchSource.InitialIntent,
    ) : AppLaunchRequest
}

enum class NotificationLaunchSource {
    InitialIntent,
    NewIntent,
}

