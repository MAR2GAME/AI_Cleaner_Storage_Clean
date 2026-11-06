package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentTelemetry
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentExecutor
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkScanResult
import kotlinx.coroutines.CoroutineScope

class NetworkScanExecutorFactory(
    private val repository: NetworkRepository,
    private val permissions: PermissionGateway,
    private val ui: UiRequestPort,
    private val telemetry: IntentTelemetry<NetworkScanOperation, NetworkScanResult>? = null,
    private val effects: List<IntentEffect<NetworkScanOperation, NetworkScanResult>> = emptyList(),
) {
    fun create(scope: CoroutineScope): IntentExecutor<NetworkScanOperation, NetworkScanResult> =
        IntentExecutor(
            ui = ui,
            gates = listOf(NetworkScanPermissionGate(permissions)),
            handler = NetworkScanHandler(repository),
            effects = effects,
            telemetry = telemetry,
            effectScope = scope,
        )
}
