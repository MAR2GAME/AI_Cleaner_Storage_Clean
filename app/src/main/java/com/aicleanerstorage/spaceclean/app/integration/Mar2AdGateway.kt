package com.aicleanerstorage.spaceclean.app.integration

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mar2sdk.core.Core
import com.mar2sdk.core.ad.AdConfig
import com.mar2sdk.core.ad.policy.ScreenAdContext
import com.mar2sdk.core.ad.policy.ScreenAdTrigger
import com.mar2sdk.core.ad.status.AdFormat
import com.mar2sdk.impl.AppOpenAdManager
import com.mar2sdk.impl.AreaKeys
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mar2sdk.core.ad.AdLoader as CoreAdLoader

/** 统一调用 impl 模块的 AdActivity，并在广告页结束后继续业务动作。 */
object Mar2AdGateway {
    private val preloadScope = CoroutineScope(Dispatchers.Main.immediate)

    /**
     * Fills the SDK ad pools without blocking the caller. The SDK also preloads on foreground
     * transitions; this call covers Activity recreation where no foreground event is emitted.
     */
    fun preloadAds() {
        if (!AdConfig.isOpen) return
        preloadScope.launch {
            try {
                CoreAdLoader.fillAd()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // Preload failures must not affect the user flow.
            }
        }
    }

    fun show(
        activity: Activity,
        areaKey: String,
        format: AdFormat,
        trigger: ScreenAdTrigger,
        fromRoute: String,
        toRoute: String,
        onContinue: () -> Unit,
    ) {
        if (activity.isFinishing || activity.isDestroyed) {
            onContinue()
            return
        }
        val componentActivity = activity as? ComponentActivity
        if (componentActivity == null) {
            onContinue()
            return
        }
        val continueOnce = observeAdLifecycle(componentActivity, onContinue)
        val started =
            runCatching {
                com.mar2sdk.impl.AdActivity.showAd(
                    activity,
                    ScreenAdContext(
                        areaKey = areaKey,
                        adFormat = format,
                        adPlatform = AdConfig.defaultPlatform,
                        trigger = trigger,
                        fromRoute = fromRoute,
                        toRoute = toRoute,
                    ),
                )
            }.getOrDefault(false)
        if (!started) continueOnce()
    }

    fun showInterstitial(
        activity: Activity,
        areaKey: String,
        fromRoute: String,
        toRoute: String,
        onContinue: () -> Unit,
    ) = show(
        activity = activity,
        areaKey = areaKey,
        format = AdFormat.INTER,
        trigger = ScreenAdTrigger.ENTER,
        fromRoute = fromRoute,
        toRoute = toRoute,
        onContinue = onContinue,
    )

    fun showForegroundAd(
        activity: Activity,
        currentRoute: String,
        onContinue: () -> Unit,
    ) {
        val format = AdConfig.adUnits[AreaKeys.KEY_APP_FOREGROUND_OPEN]?.format
        if (format != AdFormat.OPEN && format != AdFormat.OPEN_INTER) {
            onContinue()
            return
        }
        show(
            activity = activity,
            areaKey = AreaKeys.KEY_APP_FOREGROUND_OPEN,
            format = format,
            trigger = ScreenAdTrigger.RETURN,
            fromRoute = currentRoute,
            toRoute = currentRoute,
            onContinue = onContinue,
        )
    }

    fun startForegroundNotificationIfAllowed() {
        runCatching { Core.startFGS() }
    }

    fun suppressNextForegroundAd() {
        runCatching { AppOpenAdManager.suppressNextForegroundAd() }
    }

    fun completeOriginalOperation() {
        runCatching { AppOpenAdManager.completeOriginalOperation() }
    }
}

private fun observeAdLifecycle(
    activity: ComponentActivity,
    onContinue: () -> Unit,
): () -> Unit {
    var paused = false
    var continued = false
    lateinit var observer: LifecycleEventObserver

    fun continueOnce() {
        if (continued) return
        continued = true
        activity.lifecycle.removeObserver(observer)
        onContinue()
    }
    observer =
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> paused = true
                Lifecycle.Event.ON_RESUME -> if (paused) continueOnce()
                Lifecycle.Event.ON_DESTROY -> {
                    continued = true
                    activity.lifecycle.removeObserver(observer)
                }
                else -> Unit
            }
        }
    activity.lifecycle.addObserver(observer)
    return ::continueOnce
}
