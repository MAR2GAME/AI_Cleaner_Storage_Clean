package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentHandler
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkUsageInfo

class NetworkUsageHandler(
    private val repository: NetworkRepository,
) : IntentHandler<NetworkUsageOperation, NetworkUsageInfo> {
    override suspend fun handle(
        request: IntentRequest<NetworkUsageOperation>,
    ): NetworkUsageInfo = repository.readNetworkUsage()
}
