package com.aicleanerstorage.spaceclean.app.view.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdGateway
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdPlacementRegistry
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
    var exiting by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel) { viewModel.dispatch(OnboardingAction.Entered) }
    CollectEffects(viewModel) { effect ->
        when (effect) {
            OnboardingEvent.NotifyScanFinished -> Unit
            is OnboardingEvent.FinishOnboarding -> {
                if (!exiting) {
                    exiting = true
                    val activity = context.findActivity()
                    val continueNavigation = { navigator.replace(effect.destination) }
                    if (activity == null) {
                        continueNavigation()
                    } else {
                        Mar2AdGateway.showInterstitial(
                            activity = activity,
                            areaKey = Mar2AdPlacementRegistry.NEW_GUIDE_SCAN_FINISH,
                            fromRoute = "onboarding",
                            toRoute = effect.destination.javaClass.simpleName,
                            onContinue = continueNavigation,
                        )
                    }
                }
            }
        }
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
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
