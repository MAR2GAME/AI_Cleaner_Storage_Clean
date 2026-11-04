package com.aicleanerstorage.spaceclean.app.commercial

import android.app.Activity
import android.content.Context
import com.aicleanerstorage.spaceclean.common.ads.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

class AdvertiseAdDriver(
    context: Context,
    private val activityProvider: () -> Activity?,
) : AdDriver {
    private val appContext = context.applicationContext
    private fun activity() = activityProvider()?.takeUnless { it.isFinishing || it.isDestroyed }
    override fun isAvailable() = activity() != null

    override suspend fun play(areaKey: String) {
        val activity = activity() ?: return
        awaitAdClose { closed -> AdvertiseSdkAdapter.showInterstitial(activity, areaKey, closed) }
    }
    override fun preload(stage: AdPreloadStage) {
        when (stage) {
            AdPreloadStage.Startup -> AdvertiseSdkAdapter.preloadStartupInterstitial(appContext)
            AdPreloadStage.MainPage -> AdvertiseSdkAdapter.preloadMainPage(appContext)
            AdPreloadStage.InterstitialClosed -> if (isAvailable()) AdvertiseSdkAdapter.preloadAfterInterstitial(appContext)
        }
    }
    override fun initializeConsent(onReady: () -> Unit): Boolean =
        activity()?.let { AdvertiseSdkAdapter.initConsent(it) { onReady() } } ?: false
    override fun showConsent(onClosed: () -> Unit) {
        activity()?.let { AdvertiseSdkAdapter.showSplashConsent(it, onClosed) } ?: onClosed()
    }
    override fun showOpen(onLoaded: () -> Unit, onClosed: () -> Unit) {
        activity()?.let { AdvertiseSdkAdapter.showOpenAd(it, AdAreaKeys.Open.OPEN_PAGE, onClosed, onLoaded) } ?: onClosed()
    }
    override fun isAppOpenEnabled() = AdvertiseSdkAdapter.isAppOpenEnabled()
    override fun setAppOpenEnabled(enabled: Boolean) = AdvertiseSdkAdapter.setAppOpenEnabled(enabled)
    override fun suppressNextAppOpen(suppress: Boolean) = AdvertiseSdkAdapter.suppressNextAppOpen(suppress)
}

internal suspend fun awaitAdClose(start: (onClosed: () -> Unit) -> Unit): Unit = suspendCancellableCoroutine { continuation ->
    val closed = AtomicBoolean(false)
    continuation.invokeOnCancellation { closed.set(true) }
    start {
        if (closed.compareAndSet(false, true) && continuation.isActive) continuation.resume(Unit)
    }
}
