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
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchCoordinator
import com.aicleanerstorage.spaceclean.core.ui.platform.openUrl
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashIntent
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashEffect
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashViewModel
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.ui.SplashLegalDocument
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.ui.SplashScreen
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.registerSplashRoute(
    navigator: NavHostAppNavigator,
    launchCoordinator: AppLaunchCoordinator,
    externalActivityTracker: ExternalActivityTracker,
    splashPermissionPaused: Boolean,
    analyticsReporter: AnalyticsReporter,
) {
    composable<AppDestination.Splash> { entry ->
        val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val viewModel: SplashViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val pendingRequest by launchCoordinator.pendingRequest.collectAsStateWithLifecycle()
        var externalLinkActive by remember { mutableStateOf(false) }
        val splashStartedAt = remember { SystemClock.elapsedRealtime() }
        var splashReported by remember { mutableStateOf(false) }
        val openAdStateReceiver =
            remember(viewModel) {
                object : ResultReceiver(Handler(Looper.getMainLooper())) {
                    override fun onReceiveResult(resultCode: Int, resultData: android.os.Bundle?) {
                        if (resultCode != OPEN_AD_STATE_CHANGED) return
                        viewModel.dispatch(
                            SplashIntent.OpenAdStateChanged(
                                resultData?.getBoolean(OPEN_AD_ACTIVE) == true,
                            ),
                        )
                    }
                }
            }
        val openAdHostLauncher =
            rememberLauncherForActivityResult(OpenAdHostContract()) {
                viewModel.dispatch(SplashIntent.OpenAdStateChanged(false))
                viewModel.dispatch(SplashIntent.OpenAdFinished)
            }

        fun dispatch(effect: SplashEffect) {
            when (effect) {
                SplashEffect.RunColdStartAd -> Unit
                is SplashEffect.OpenNotificationTarget -> pageNavigator.resetTo(effect.destination)
                is SplashEffect.Navigate -> pageNavigator.replaceCurrent(effect.destination)
            }
        }

        LaunchedEffect(pendingRequest) {
            val request = pendingRequest ?: return@LaunchedEffect
            if (launchCoordinator.consumeRequestIfCurrent(request)) {
                viewModel.dispatch(SplashIntent.LaunchRequestChanged(request))
            }
        }
        LaunchedEffect(splashPermissionPaused) {
            viewModel.dispatch(SplashIntent.PermissionPauseChanged(splashPermissionPaused))
        }
        LaunchedEffect(viewModel, navigator) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    SplashEffect.RunColdStartAd -> {
                        withFrameNanos { }
                        runCatching {
                            openAdHostLauncher.launch(openAdStateReceiver)
                        }.onFailure {
                            viewModel.dispatch(SplashIntent.OpenAdStateChanged(false))
                            viewModel.dispatch(SplashIntent.OpenAdFinished)
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
                    viewModel.dispatch(SplashIntent.ExternalLinkStateChanged(active = false))
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        SplashScreen(
            state = state,
            onAction = { action ->
                if (action == SplashIntent.VisualFinished && !splashReported) {
                    splashReported = true
                    analyticsReporter.report(
                        AnalyticsEvent.SplashDisplayed(SystemClock.elapsedRealtime() - splashStartedAt),
                    )
                }
                viewModel.dispatch(action)
            },
            onOpenLegalDocument = { document ->
                externalLinkActive = true
                viewModel.dispatch(SplashIntent.ExternalLinkStateChanged(active = true))
                externalActivityTracker.markLaunch()
                val opened =
                    when (document) {
                        SplashLegalDocument.Terms -> context.openUrl(AppConfig.TERMS_OF_SERVICE_URL)
                        SplashLegalDocument.Privacy -> context.openUrl(AppConfig.PRIVACY_POLICY_URL)
                    }
                if (!opened) {
                    externalActivityTracker.cancelLaunch()
                    externalLinkActive = false
                    viewModel.dispatch(SplashIntent.ExternalLinkStateChanged(active = false))
                }
            },
        )
    }
}
