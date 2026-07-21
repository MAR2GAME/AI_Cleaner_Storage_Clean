package com.aicleanerstorage.spaceclean.app.runtime.permission

import android.content.Intent
import com.aicleanerstorage.spaceclean.common.permission.PermissionStatus
import com.aicleanerstorage.spaceclean.common.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.common.permission.PermissionType

internal data class PermissionSession(
    val target: PermissionTarget,
    val missingPermission: PermissionType?,
    val onGranted: () -> Unit,
    val onDenied: () -> Unit,
    val showDialog: Boolean = true,
    val settingsLaunchPending: Boolean = false,
    val settingsLaunchObservedPause: Boolean = false,
)

internal sealed interface PermissionLaunch {
    val target: PermissionTarget

    data class Runtime(
        override val target: PermissionTarget,
        val permissions: Array<String>,
    ) : PermissionLaunch

    data class Settings(
        override val target: PermissionTarget,
        val intents: List<Intent>,
    ) : PermissionLaunch
}

internal sealed interface PermissionRecheckDecision {
    data object Granted : PermissionRecheckDecision
    data class Continue(val missingPermission: PermissionType) : PermissionRecheckDecision
    data object Denied : PermissionRecheckDecision
}

internal fun resolvePermissionRecheck(
    previousMissingPermission: PermissionType?,
    status: PermissionStatus,
): PermissionRecheckDecision {
    if (status.granted) return PermissionRecheckDecision.Granted
    val nextMissingPermission = status.missing.firstOrNull()
    return if (
        previousMissingPermission != null &&
        nextMissingPermission != null &&
        previousMissingPermission != nextMissingPermission
    ) {
        PermissionRecheckDecision.Continue(nextMissingPermission)
    } else {
        PermissionRecheckDecision.Denied
    }
}
