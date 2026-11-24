package com.aicleanerstorage.spaceclean.app.view.onboarding.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdGateway
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdPlacementRegistry
import com.aicleanerstorage.spaceclean.app.integration.Mar2Analytics
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingAction
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingEvent
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingScanViewModel
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator

@Composable
fun OnboardingRoute(
    navigator: AppNavigator,
    viewModel: OnboardingScanViewModel,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isAdPending by remember(viewModel) { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel) {
        Mar2Analytics.trackGuideScanningEntered()
        viewModel.dispatch(OnboardingAction.Entered)
    }
    CollectEffects(viewModel) { effect ->
        isAdPending = true
        when (effect) {
            OnboardingEvent.NotifyScanFinished -> Mar2Analytics.trackGuideScanResultEntered()
            is OnboardingEvent.FinishOnboarding ->
                if (effect.skipped) {
                    Mar2Analytics.trackGuideSkipClicked()
                } else {
                    Mar2Analytics.trackGuideContinueClicked()
                }
        }
        effect.requestOnboardingAd(
            showAd = { areaKey, target, onContinue ->
                val activity = context.findActivity()
                if (activity == null) {
                    onContinue()
                } else {
                    Mar2AdGateway.showInterstitial(activity, areaKey, "onboarding", target, onContinue)
                }
            },
            onScanAdFinished = { isAdPending = false },
            navigate = navigator::replace,
        )
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(OnboardingAction.RefreshRequested)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    OnboardingScanScreen(
        state = state,
        onAction = { if (!isAdPending) viewModel.dispatch(it) },
    )
}

internal fun OnboardingEvent.requestOnboardingAd(
    showAd: (areaKey: String, target: String, onContinue: () -> Unit) -> Unit,
    onScanAdFinished: () -> Unit,
    navigate: (AppDestination) -> Unit,
) {
    when (this) {
        OnboardingEvent.NotifyScanFinished ->
            showAd(Mar2AdPlacementRegistry.NEW_GUIDE_SCAN_FINISH, "onboarding", onScanAdFinished)
        is OnboardingEvent.FinishOnboarding ->
            showAd(Mar2AdPlacementRegistry.NEW_GUIDE_SKIP, destination.javaClass.simpleName) { navigate(destination) }
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
