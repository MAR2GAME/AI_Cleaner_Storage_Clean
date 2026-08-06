package com.aicleanerstorage.spaceclean.app.navigation.register

import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.app.OPEN_AD_ACTIVE
import com.aicleanerstorage.spaceclean.app.OPEN_AD_STATE_CHANGED
import com.aicleanerstorage.spaceclean.app.OpenAdHostContract
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchCoordinator
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
    externalActivityTracker: ExternalActivityTracker,
    splashPermissionPaused: Boolean,
    analyticsReporter: AnalyticsReporter,
) {
    composable(AppDestination.Splash.route) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val viewModel: SplashViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val pendingRequest by launchCoordinator.pendingRequest.collectAsStateWithLifecycle()
        var externalLinkActive by remember { mutableStateOf(false) }
        val splashStartedAt = remember { SystemClock.elapsedRealtime() }
        var splashReported by remember { mutableStateOf(false) }
        val openAdStateReceiver =
            remember(viewModel) {
                object : ResultReceiver(Handler(Looper.getMainLooper())) {
                    override fun onReceiveResult(resultCode: Int, resultData: android.os.Bundle?) {
                        if (resultCode != OPEN_AD_STATE_CHANGED) return
                        viewModel.onAction(
                            SplashAction.OpenAdStateChanged(
                                resultData?.getBoolean(OPEN_AD_ACTIVE) == true,
                            ),
                        )
                    }
                }
            }
        val openAdHostLauncher =
            rememberLauncherForActivityResult(OpenAdHostContract()) {
                viewModel.onAction(SplashAction.OpenAdStateChanged(false))
                viewModel.onAction(SplashAction.OpenAdFinished)
            }

        fun dispatch(effect: SplashEffect) {
            when (effect) {
                SplashEffect.RunColdStartAd -> Unit
                is SplashEffect.OpenNotificationTarget -> navigator.openNotificationTarget(effect.route)
                is SplashEffect.Navigate -> navigator.replace(effect.destination)
            }
        }

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
                    SplashEffect.RunColdStartAd -> {
                        withFrameNanos { }
                        runCatching {
                            openAdHostLauncher.launch(openAdStateReceiver)
                        }.onFailure {
                            viewModel.onAction(SplashAction.OpenAdStateChanged(false))
                            viewModel.onAction(SplashAction.OpenAdFinished)
                        }
                    }
                    is SplashEffect.OpenNotificationTarget,
                    is SplashEffect.Navigate,
                    -> dispatch(effect)
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
            onAction = { action ->
                if (action == SplashAction.VisualFinished && !splashReported) {
                    splashReported = true
                    analyticsReporter.report(
                        AnalyticsEvent.SplashDisplayed(SystemClock.elapsedRealtime() - splashStartedAt),
                    )
                }
                viewModel.onAction(action)
            },
            onOpenLegalDocument = { document ->
                externalLinkActive = true
                viewModel.onAction(SplashAction.ExternalLinkStateChanged(active = true))
                externalActivityTracker.markLaunch()
                val opened =
                    when (document) {
                        SplashLegalDocument.Terms -> context.openUrl(AppConfig.TERMS_OF_SERVICE_URL)
                        SplashLegalDocument.Privacy -> context.openUrl(AppConfig.PRIVACY_POLICY_URL)
                    }
                if (!opened) {
                    externalActivityTracker.cancelLaunch()
                    externalLinkActive = false
                    viewModel.onAction(SplashAction.ExternalLinkStateChanged(active = false))
                }
            },
        )
    }
}
