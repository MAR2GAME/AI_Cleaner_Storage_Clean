package com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentRequest
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentHandler
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageInfo
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageDateRange

data class AppUsageResult(
    val entries: List<AppUsageInfo>,
    val runningPackages: Set<String> = emptySet(),
)

class AppUsageHandler(
    private val repository: AppUsageRepository,
    private val rangeProvider: () -> AppUsageDateRange,
) : IntentHandler<AppUsageOperation, AppUsageResult> {
    override suspend fun handle(
        request: IntentRequest<AppUsageOperation>,
    ): AppUsageResult {
        val (startMillis, endMillis) = rangeProvider().timeBounds()
        val entries = repository.appUsageBetween(startMillis, endMillis)
        return AppUsageResult(
            entries = entries,
            runningPackages = repository.runningPackages(entries.map { it.packageName }.toSet()),
        )
    }
}

