package com.aicleanerstorage.spaceclean.feature.onboarding

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.feature.onboarding.ui.OnboardingScanScreen

@Composable
fun OnboardingRoute(
    navigator: AppNavigator,
    analyticsReporter: AnalyticsReporter,
    onScanFinishedAd: suspend () -> Unit,
    onCompletionAd: suspend (skipped: Boolean) -> Unit,
    viewModel: OnboardingScanViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val routeScope = rememberCoroutineScope()
    var scanAd by remember { mutableStateOf<Job?>(null) }
    var exiting by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel) {
        analyticsReporter.report(AnalyticsEvent.GuideScanningEntered)
        viewModel.onAction(OnboardingAction.Entered)
        viewModel.effects.collect { effect ->
            when (effect) {
                OnboardingEffect.ScanFinished -> {
                    if (!exiting && scanAd == null) {
                        analyticsReporter.report(AnalyticsEvent.GuideScanResultEntered)
                        scanAd = routeScope.launch { onScanFinishedAd() }
                    }
                }
                is OnboardingEffect.Complete -> {
                    if (!exiting) {
                        exiting = true
                        analyticsReporter.report(
                            if (effect.skipped) AnalyticsEvent.GuideSkipClicked else AnalyticsEvent.GuideContinueClicked,
                        )
                        scanAd?.cancel()
                        routeScope.launch { onCompletionAd(effect.skipped); ensureActive(); navigator.replace(AppDestination.Home) }
                    }
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onAction(OnboardingAction.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    OnboardingScanScreen(
        state = state,
        onAction = { if (!exiting) viewModel.onAction(it) },
        onNavigate = { if (!exiting) navigator.replace(it) },
    )
}
