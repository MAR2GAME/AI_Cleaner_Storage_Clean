package com.aicleanerstorage.spaceclean.feature.files.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentGate
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.ActionPermissionGate
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.permission.ProtectedAction

class FileManagerPermissionGate(
    private val permissions: PermissionGateway,
) : IntentGate<FileManagerOperation> {
    override suspend fun check(
        request: IntentRequest<FileManagerOperation>,
        ui: UiRequestPort,
    ): GateResult = checkOperation(request.intent)

    suspend fun checkOperation(operation: FileManagerOperation): GateResult =
        ActionPermissionGate(permissions).checkAction(
        when (operation) {
            is FileManagerOperation.LoadFiles -> ProtectedAction.FileManagerLoadFiles
            is FileManagerOperation.DeleteFiles -> ProtectedAction.FileManagerDeleteFiles
        },
    )
}
