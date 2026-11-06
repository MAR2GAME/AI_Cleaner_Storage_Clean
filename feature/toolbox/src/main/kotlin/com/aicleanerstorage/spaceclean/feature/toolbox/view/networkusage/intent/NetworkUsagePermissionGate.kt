package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction

class NetworkUsagePermissionGate(
    private val permissions: PermissionGateway,
) : IntentGate<NetworkUsageOperation> {
    override suspend fun check(
        request: IntentRequest<NetworkUsageOperation>,
        ui: UiRequestPort,
    ): GateResult = ActionPermissionGate(permissions)
        .checkAction(ProtectedAction.NetworkUsageLoadStats)
}
