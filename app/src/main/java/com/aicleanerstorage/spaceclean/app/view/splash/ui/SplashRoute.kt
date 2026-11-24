package com.aicleanerstorage.spaceclean.app.view.splash.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdGateway
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdPlacementRegistry
import com.aicleanerstorage.spaceclean.app.integration.Mar2Analytics
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashAction
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashEvent
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashViewModel
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.util.ExternalLinkUtil
import org.koin.androidx.compose.koinViewModel
import com.mar2sdk.core.Core
import com.mar2sdk.core.ad.status.AdFormat
import com.mar2sdk.core.ad.policy.ScreenAdTrigger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun SplashRoute(
    navigator: AppNavigator,
    splashPaused: Boolean,
    notificationPermissionReleased: Boolean = true,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var externalLinkActive by remember { mutableStateOf(false) }
    var startupStarted by remember { mutableStateOf(false) }
    var pendingDestination by remember { mutableStateOf<AppDestination?>(null) }
    val startupStartedAt = remember { SystemClock.elapsedRealtime() }
    var splashReported by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    SplashPauseEffect(splashPaused, viewModel)
    CollectEffects(viewModel) { effect ->
        when (effect) {
            is SplashEvent.Navigate -> pendingDestination = effect.destination
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
    SplashStartupEffect(
        released = notificationPermissionReleased,
        destination = pendingDestination,
        started = startupStarted,
        elapsedSinceStart = SystemClock.elapsedRealtime() - startupStartedAt,
        onStarted = { startupStarted = true },
        start = { destination -> startSplashFlow(context.findActivity(), destination, navigator) },
        scope = scope,
    )
    SplashExternalLinkEffect(externalLinkActive, viewModel) { externalLinkActive = false }

    SplashScreen(
        state = state,
        onAction = { action ->
            if (action == SplashAction.VisualFinished && !splashReported) {
                splashReported = true
                Mar2Analytics.trackSplashDisplayed(SystemClock.elapsedRealtime() - startupStartedAt)
            }
            viewModel.dispatch(action)
        },
    )
}

@Composable
private fun SplashPauseEffect(splashPaused: Boolean, viewModel: SplashViewModel) {
    LaunchedEffect(splashPaused) { viewModel.dispatch(SplashAction.PermissionPauseChanged(splashPaused)) }
}

@Composable
private fun SplashExternalLinkEffect(
    active: Boolean,
    viewModel: SplashViewModel,
    clear: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, active) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && active) {
                clear()
                viewModel.dispatch(SplashAction.ExternalLinkStateChanged(active = false))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
private fun SplashStartupEffect(
    released: Boolean,
    destination: AppDestination?,
    started: Boolean,
    elapsedSinceStart: Long,
    onStarted: () -> Unit,
    start: (AppDestination) -> Unit,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    LaunchedEffect(released, destination) {
        val target = destination ?: return@LaunchedEffect
        if (!released || started) return@LaunchedEffect
        onStarted()
        scope.launch {
            delay((3_000L - elapsedSinceStart).coerceAtLeast(0L))
            start(target)
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun startSplashFlow(
    activity: Activity?,
    destination: AppDestination,
    navigator: AppNavigator,
) {
    if (activity == null) {
        navigator.replace(destination)
        return
    }
    val flow = runCatching { Core.beginConsentFlow("startup") }.getOrNull()
    if (flow == null) {
        showSplashAd(activity, destination, navigator)
        return
    }
    startConsent(activity, flow) {
        showSplashAd(activity, destination, navigator)
    }
}

private fun startConsent(
    activity: Activity,
    flow: com.mar2sdk.core.ad.ConsentFlow,
    onContinue: () -> Unit,
) {
    var finished = false
    fun finish(cancelled: Boolean) {
        if (finished) return
        finished = true
        runCatching { Core.endConsentFlow(flow, cancelled) }
        onContinue()
    }
    fun showForm() {
        runCatching {
            Core.showSplashConsent(activity, flow) { finish(cancelled = false) }
        }.onFailure { finish(cancelled = true) }
    }
    val cached = runCatching {
        Core.initConsent(activity, flow) { success ->
            if (success) showForm() else finish(cancelled = true)
        }
    }.getOrElse {
        finish(cancelled = true)
        return
    }
    if (cached) showForm()
}

private fun showSplashAd(
    activity: Activity,
    destination: AppDestination,
    navigator: AppNavigator,
) {
    Mar2AdGateway.show(
        activity = activity,
        areaKey = Mar2AdPlacementRegistry.OPEN_PAGE,
        format = AdFormat.OPEN,
        trigger = ScreenAdTrigger.ENTER,
        fromRoute = "splash",
        toRoute = destination.javaClass.simpleName,
        onContinue = { navigator.replace(destination) },
    )
}
