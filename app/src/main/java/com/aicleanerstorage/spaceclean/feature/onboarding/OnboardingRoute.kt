package com.aicleanerstorage.spaceclean.feature.onboarding

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
    onScanFinishedAd: (() -> Unit) -> Unit,
    onCompletionAd: (skipped: Boolean, onContinue: () -> Unit) -> Unit,
    viewModel: OnboardingScanViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel) {
        analyticsReporter.report(AnalyticsEvent.GuideScanningEntered)
        viewModel.onAction(OnboardingAction.Entered)
        viewModel.effects.collect { effect ->
            when (effect) {
                OnboardingEffect.ScanFinished -> {
                    analyticsReporter.report(AnalyticsEvent.GuideScanResultEntered)
                    onScanFinishedAd {}
                }
                is OnboardingEffect.Complete -> {
                    analyticsReporter.report(
                        if (effect.skipped) AnalyticsEvent.GuideSkipClicked else AnalyticsEvent.GuideContinueClicked,
                    )
                    onCompletionAd(effect.skipped) { navigator.replace(AppDestination.Home) }
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
        onAction = viewModel::onAction,
        onNavigate = navigator::replace,
    )
}
