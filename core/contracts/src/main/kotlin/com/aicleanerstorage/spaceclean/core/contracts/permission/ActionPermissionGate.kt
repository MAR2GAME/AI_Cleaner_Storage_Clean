package com.aicleanerstorage.spaceclean.core.contracts.permission

import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import kotlinx.coroutines.CancellationException

class ActionPermissionGate(
    private val permissions: PermissionGateway,
) : IntentGate<ProtectedAction> {
    override suspend fun check(
        request: IntentRequest<ProtectedAction>,
        ui: UiRequestPort,
    ): GateResult = checkAction(request.intent)

    suspend fun checkAction(
        action: ProtectedAction,
        mode: PermissionPromptMode = PermissionPromptMode.Explained,
    ): GateResult = try {
        when (permissions.ensure(PermissionTarget.Action(action), mode)) {
            PermissionResult.Granted -> GateResult.Allow
            PermissionResult.Busy -> GateResult.Busy
            PermissionResult.Denied -> GateResult.Deny("permission_denied:${action.key}")
        }
    } catch (_: CancellationException) {
        GateResult.Cancelled
    }
}
