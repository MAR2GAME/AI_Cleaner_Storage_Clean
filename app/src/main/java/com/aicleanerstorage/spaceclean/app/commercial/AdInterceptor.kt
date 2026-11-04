package com.aicleanerstorage.spaceclean.app.commercial

import com.aicleanerstorage.spaceclean.app.commercial.policy.FeatureEventAdPolicy
import com.aicleanerstorage.spaceclean.common.ads.AdDriver
import com.aicleanerstorage.spaceclean.common.ads.AdNeed
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.presentation.InterceptResult
import com.aicleanerstorage.spaceclean.core.presentation.IntentInterceptor
import com.aicleanerstorage.spaceclean.core.presentation.IntentPolicy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withTimeoutOrNull

class AdInterceptor(
    private val driver: AdDriver,
    private val timeoutMillis: Long = 30_000L,
    private val policy: FeatureEventAdPolicy = FeatureEventAdPolicy(),
) : IntentInterceptor {
    override suspend fun intercept(policy: IntentPolicy): InterceptResult {
        if (policy != AdNeed.JunkCleanCompletion) return InterceptResult.NotHandled
        currentCoroutineContext().ensureActive()
        try {
            val request = this.policy.request(
                FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true),
            )
            if (request != null) withTimeoutOrNull(timeoutMillis) { driver.show(request) }
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            currentCoroutineContext().ensureActive()
        }
        currentCoroutineContext().ensureActive()
        return InterceptResult.Continue
    }
}
