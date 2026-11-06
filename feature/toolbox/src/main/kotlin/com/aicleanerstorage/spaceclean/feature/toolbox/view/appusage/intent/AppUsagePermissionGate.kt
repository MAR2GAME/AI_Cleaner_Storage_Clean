package com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate

class AppUsagePermissionGate(
    private val permissions: PermissionGateway,
) : IntentGate<AppUsageOperation> {
    override suspend fun check(
        request: IntentRequest<AppUsageOperation>,
        ui: UiRequestPort,
    ): GateResult = ActionPermissionGate(permissions).checkAction(ProtectedAction.AppUsageLoadStats)
}

