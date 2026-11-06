package com.aicleanerstorage.spaceclean.app.runtime

import androidx.compose.runtime.*
import androidx.lifecycle.withResumed
import androidx.navigation.NavBackStackEntry
import com.aicleanerstorage.spaceclean.app.navigation.NavigationEvent
import com.aicleanerstorage.spaceclean.app.navigation.NavigationKind
import com.aicleanerstorage.spaceclean.core.contracts.ads.AdPlacementRegistry
import com.aicleanerstorage.spaceclean.core.contracts.ads.AdScene
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.presentation.intent.UiRequestBroker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import org.koin.compose.koinInject

@Composable
internal fun NavigationEffectHost(
    events: Flow<NavigationEvent>,
    entry: NavBackStackEntry?,
    broker: UiRequestBroker = koinInject(),
) {
    val currentEntry by rememberUpdatedState(entry)
    val policy = remember { RouteEntryAdPolicy() }
    LaunchedEffect(events, broker) {
        events.collectLatest { event ->
            if (event !is NavigationEvent.Accepted) return@collectLatest
            val request = event.request
            if (request.kind != NavigationKind.Navigate && request.kind != NavigationKind.ResetTo) return@collectLatest
            // Wait for Compose to observe the committed entry.
            val target = snapshotFlow { currentEntry }.first { it?.id != request.sourceEntryId } ?: return@collectLatest
            if (target.destination.route?.substringBefore('?') != request.destination.route) return@collectLatest
            val sourceRoute = request.sourceRoute.takeUnless {
                request.kind == NavigationKind.ResetTo && it in AppDestination.startupRoutes
            }
            val decision = policy.entryAdDecision(sourceRoute, request.destination.route) ?: return@collectLatest
            val placement = AdPlacementRegistry.interstitialArea(AdScene.EnterFeature(decision.feature, decision.route))
                ?: return@collectLatest
            coroutineScope {
                val requestJob = launch {
                    target.lifecycle.withResumed { }
                    ensureActive()
                    if (currentEntry?.id == target.id) {
                        try {
                            broker.request(UiRequest.ShowInterstitial(
                                requestId = UiRequestBroker.newRequestId(),
                                placement = placement,
                            ))
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (_: Exception) {
                            // A failed effect cannot invalidate a committed navigation.
                        }
                    }
                }
                val cancellation = launch {
                    snapshotFlow { currentEntry?.id }.first { it != target.id }
                    requestJob.cancel()
                }
                try {
                    requestJob.join()
                } finally {
                    cancellation.cancel()
                }
            }
        }
    }
}
