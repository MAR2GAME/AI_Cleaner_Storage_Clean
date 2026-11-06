package com.aicleanerstorage.spaceclean.feature.junkclean.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentTelemetry
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentExecutor
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.CleanRepository
import kotlinx.coroutines.CoroutineScope

class JunkCleanExecutorFactory(
    private val repository: CleanRepository,
    private val permissions: PermissionGateway,
    private val ui: UiRequestPort,
    private val telemetry: IntentTelemetry<JunkCleanOperation, JunkCleanResult>? = null,
    private val effects: List<IntentEffect<JunkCleanOperation, JunkCleanResult>> = emptyList(),
) {
    fun create(scope: CoroutineScope): IntentExecutor<JunkCleanOperation, JunkCleanResult> =
        IntentExecutor(
            ui = ui,
            gates = listOf(JunkCleanPermissionGate(permissions)),
            handler = JunkCleanHandler(repository),
            effects = effects,
            telemetry = telemetry,
            effectScope = scope,
        )
}
