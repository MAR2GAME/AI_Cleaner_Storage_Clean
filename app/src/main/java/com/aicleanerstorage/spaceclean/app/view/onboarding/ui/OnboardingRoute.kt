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
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingEvent
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingAction
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingScanViewModel

@Composable
fun OnboardingRoute(
    navigator: AppNavigator,
    viewModel: OnboardingScanViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var exiting by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel) {
        viewModel.dispatch(OnboardingAction.Entered)
        viewModel.event.collect { effect ->
            when (effect) {
                OnboardingEvent.NotifyScanFinished -> Unit
                OnboardingEvent.FinishOnboarding -> {
                    if (!exiting) {
                        exiting = true
                        navigator.replace(AppDestination.Main())
                    }
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
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
