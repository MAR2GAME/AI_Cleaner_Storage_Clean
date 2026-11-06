package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentTelemetry
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentExecutor
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkUsageInfo
import kotlinx.coroutines.CoroutineScope

class NetworkUsageExecutorFactory(
    private val repository: NetworkRepository,
    private val permissions: PermissionGateway,
    private val ui: UiRequestPort,
    private val telemetry: IntentTelemetry<NetworkUsageOperation, NetworkUsageInfo>? = null,
    private val effects: List<IntentEffect<NetworkUsageOperation, NetworkUsageInfo>> = emptyList(),
) {
    fun create(scope: CoroutineScope): IntentExecutor<NetworkUsageOperation, NetworkUsageInfo> =
        IntentExecutor(
            ui = ui,
            gates = listOf(NetworkUsagePermissionGate(permissions)),
            handler = NetworkUsageHandler(repository),
            effects = effects,
            telemetry = telemetry,
            effectScope = scope,
        )
}
