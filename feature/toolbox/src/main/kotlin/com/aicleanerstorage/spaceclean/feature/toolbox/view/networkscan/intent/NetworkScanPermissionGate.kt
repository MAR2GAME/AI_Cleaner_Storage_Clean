package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionPromptMode
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction

class NetworkScanPermissionGate(
    private val permissions: PermissionGateway,
) : IntentGate<NetworkScanOperation> {
    override suspend fun check(
        request: IntentRequest<NetworkScanOperation>,
        ui: UiRequestPort,
    ): GateResult = ActionPermissionGate(permissions)
        .checkAction(ProtectedAction.NetworkScanStart, PermissionPromptMode.Direct)
}
