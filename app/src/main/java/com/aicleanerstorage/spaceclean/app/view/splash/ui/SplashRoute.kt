package com.aicleanerstorage.spaceclean.app.view.splash.ui

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
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashAction
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashEvent
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashViewModel
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.util.ExternalLinkUtil
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SplashRoute(
    navigator: AppNavigator,
    splashPaused: Boolean,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var externalLinkActive by remember { mutableStateOf(false) }

    LaunchedEffect(splashPaused) {
        viewModel.dispatch(SplashAction.PermissionPauseChanged(splashPaused))
    }
    CollectEffects(viewModel) { effect ->
        when (effect) {
            is SplashEvent.Navigate -> navigator.replace(effect.destination)
            SplashEvent.OpenTerms -> {
                externalLinkActive = true
                ExternalLinkUtil.open(context, BuildConfig.TERMS_OF_SERVICE_URL)
            }
            SplashEvent.OpenPrivacy -> {
                externalLinkActive = true
                ExternalLinkUtil.open(context, BuildConfig.PRIVACY_POLICY_URL)
            }
        }
    }
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME && externalLinkActive) {
                    externalLinkActive = false
                    viewModel.dispatch(SplashAction.ExternalLinkStateChanged(active = false))
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    SplashScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}
