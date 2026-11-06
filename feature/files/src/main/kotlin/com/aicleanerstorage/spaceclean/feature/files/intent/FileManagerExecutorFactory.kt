package com.aicleanerstorage.spaceclean.feature.files.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentTelemetry
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.files.FileRepository
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentExecutor
import kotlinx.coroutines.CoroutineScope

class FileManagerExecutorFactory(
    private val repository: FileRepository,
    private val permissions: PermissionGateway,
    private val ui: UiRequestPort,
    private val telemetry: IntentTelemetry<FileManagerOperation, FileManagerResult>? = null,
    private val effects: List<IntentEffect<FileManagerOperation, FileManagerResult>> = emptyList(),
) {
    fun create(scope: CoroutineScope): IntentExecutor<FileManagerOperation, FileManagerResult> =
        IntentExecutor(
            ui = ui,
            gates = listOf(FileManagerPermissionGate(permissions)),
            handler = FileManagerHandler(repository),
            effects = effects,
            telemetry = telemetry,
            effectScope = scope,
        )
}
