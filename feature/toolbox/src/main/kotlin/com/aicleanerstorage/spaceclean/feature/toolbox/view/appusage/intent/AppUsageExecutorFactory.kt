package com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent

import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentEffect
import com.aicleanerstorage.spaceclean.core.contracts.intent.IntentTelemetry
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.presentation.intent.IntentExecutor
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageDateRange
import kotlinx.coroutines.CoroutineScope

class AppUsageExecutorFactory(
    private val repository: AppUsageRepository,
    private val permissions: PermissionGateway,
    private val ui: UiRequestPort,
    private val telemetry: IntentTelemetry<AppUsageOperation, AppUsageResult>? = null,
    private val effects: List<IntentEffect<AppUsageOperation, AppUsageResult>> = emptyList(),
) {
    fun create(
        scope: CoroutineScope,
        rangeProvider: () -> AppUsageDateRange,
    ): IntentExecutor<AppUsageOperation, AppUsageResult> = IntentExecutor(
        ui = ui,
        gates = listOf(AppUsagePermissionGate(permissions)),
        handler = AppUsageHandler(repository, rangeProvider),
        effects = effects,
        telemetry = telemetry,
        effectScope = scope,
    )
}

