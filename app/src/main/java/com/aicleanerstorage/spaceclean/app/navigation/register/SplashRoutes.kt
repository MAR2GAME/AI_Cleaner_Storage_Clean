package com.aicleanerstorage.spaceclean.app.navigation.register

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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.runtime.external.ExternalActivityLauncher
import com.aicleanerstorage.spaceclean.app.runtime.startup.AppLaunchCoordinator
import com.aicleanerstorage.spaceclean.common.intent.openUrl
import com.aicleanerstorage.spaceclean.feature.startup.SplashAction
import com.aicleanerstorage.spaceclean.feature.startup.SplashEffect
import com.aicleanerstorage.spaceclean.feature.startup.SplashViewModel
import com.aicleanerstorage.spaceclean.feature.startup.ui.SplashLegalDocument
import com.aicleanerstorage.spaceclean.feature.startup.ui.SplashScreen
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerSplashRoute(
    navigator: AppNavigator,
    launchCoordinator: AppLaunchCoordinator,
    externalActivityLauncher: ExternalActivityLauncher,
    splashPermissionPaused: Boolean,
) {
    composable(AppDestination.Splash.route) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val viewModel: SplashViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val pendingRequest by launchCoordinator.pendingRequest.collectAsStateWithLifecycle()
        var externalLinkActive by remember { mutableStateOf(false) }

        LaunchedEffect(pendingRequest) {
            val request = pendingRequest ?: return@LaunchedEffect
            if (launchCoordinator.consumeRequestIfCurrent(request)) {
                viewModel.onAction(SplashAction.LaunchRequestChanged(request))
            }
        }
        LaunchedEffect(splashPermissionPaused) {
            viewModel.onAction(SplashAction.PermissionPauseChanged(splashPermissionPaused))
        }
        LaunchedEffect(viewModel, navigator) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is SplashEffect.OpenNotificationTarget -> navigator.openNotificationTarget(effect.route)
                    is SplashEffect.Navigate -> navigator.replace(effect.destination)
                }
            }
        }
        DisposableEffect(lifecycleOwner, viewModel) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME && externalLinkActive) {
                    externalLinkActive = false
                    viewModel.onAction(SplashAction.ExternalLinkStateChanged(active = false))
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        SplashScreen(
            state = state,
            onAction = viewModel::onAction,
            onOpenLegalDocument = { document ->
                externalLinkActive = true
                viewModel.onAction(SplashAction.ExternalLinkStateChanged(active = true))
                externalActivityLauncher.markLaunch()
                val opened =
                    when (document) {
                        SplashLegalDocument.Terms -> context.openUrl(AppConfig.TERMS_OF_SERVICE_URL)
                        SplashLegalDocument.Privacy -> context.openUrl(AppConfig.PRIVACY_POLICY_URL)
                    }
                if (!opened) {
                    externalActivityLauncher.cancelLaunch()
                    externalLinkActive = false
                    viewModel.onAction(SplashAction.ExternalLinkStateChanged(active = false))
                }
            },
        )
    }
}
