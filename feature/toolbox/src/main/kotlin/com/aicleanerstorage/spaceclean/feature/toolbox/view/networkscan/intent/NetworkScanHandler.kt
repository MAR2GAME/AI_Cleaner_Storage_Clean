package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentHandler
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkScanResult

class NetworkScanHandler(
    private val repository: NetworkRepository,
) : IntentHandler<NetworkScanOperation, NetworkScanResult> {
    override suspend fun handle(
        request: IntentRequest<NetworkScanOperation>,
    ): NetworkScanResult = repository.scanWifi()
}
