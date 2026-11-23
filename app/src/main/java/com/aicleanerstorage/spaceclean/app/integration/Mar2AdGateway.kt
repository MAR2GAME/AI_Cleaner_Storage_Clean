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

/** 统一调用 impl 模块的 AdActivity，并在广告页结束后继续业务动作。 */
object Mar2AdGateway {
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
        val started = runCatching {
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

    fun startForegroundNotificationIfAllowed() {
        runCatching { Core.startFGS() }
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
    observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> paused = true
            Lifecycle.Event.ON_RESUME -> if (paused) continueOnce()
            Lifecycle.Event.ON_DESTROY -> continueOnce()
            else -> Unit
        }
    }
    activity.lifecycle.addObserver(observer)
    return ::continueOnce
}
