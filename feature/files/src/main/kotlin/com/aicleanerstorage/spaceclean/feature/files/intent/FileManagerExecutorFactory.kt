package com.aicleanerstorage.spaceclean.feature.files.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentTelemetry
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.files.FileRepository
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import com.aicleanerstorage.spaceclean.core.contracts.files.FileDeleteResult

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

    fun pipeline(scope: CoroutineScope): FileManagerPipeline = FileManagerPipeline(create(scope))
}

class FileManagerPipeline(
    private val executor: IntentExecutor<FileManagerOperation, FileManagerResult>,
) {
    suspend fun load(mediaType: FileManagerMediaType): FileManagerResult.Loaded =
        when (val result = executor.execute(FileManagerOperation.Load(mediaType), "file_manager")) {
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Accepted -> result.value as FileManagerResult.Loaded
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Failed -> throw result.error
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Rejected -> error(result.reason)
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Busy -> error("busy")
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Cancelled -> throw CancellationException()
        }

    suspend fun delete(files: List<com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem>): FileDeleteResult =
        when (val result = executor.execute(FileManagerOperation.Delete(files), "file_manager")) {
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Accepted -> (result.value as FileManagerResult.Deleted).result
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Failed -> throw result.error
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Rejected -> error(result.reason)
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Busy -> error("busy")
            is com.aicleanerstorage.spaceclean.core.contracts.intent.ExecutionResult.Cancelled -> throw CancellationException()
        }
}
