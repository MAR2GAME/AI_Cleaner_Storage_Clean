package com.aicleanerstorage.spaceclean.feature.junkclean.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate

class JunkCleanPermissionGate(
    private val permissions: PermissionGateway,
) : IntentGate<JunkCleanOperation> {
    override suspend fun check(
        request: IntentRequest<JunkCleanOperation>,
        ui: UiRequestPort,
    ): GateResult {
        return checkOperation(request.intent)
    }

    suspend fun checkOperation(intent: JunkCleanOperation): GateResult {
        val action = when (intent) {
            JunkCleanOperation.Scan -> ProtectedAction.JunkStartScan
            is JunkCleanOperation.Clean -> ProtectedAction.JunkCleanSelected
        }
        return ActionPermissionGate(permissions).checkAction(action)
    }
}
