package com.aicleanerstorage.spaceclean.common.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.ViewGroup
import com.pdffox.adv.AdvertiseSdk
import com.pdffox.adv.NativeAdContent
import com.pdffox.adv.notification.NotificationManager as AdvertiseNotificationManager
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

object AdvertiseSdkAdapter {
    suspend fun initialize(context: Application, isTest: Boolean) {
        AdvertiseSdk.init(
            context = context,
            isTest = isTest,
            sdkConfig = AdvertiseConfigFactory.create(context),
        )
    }

    fun initConsent(activity: Activity, onResult: (Boolean) -> Unit): Boolean =
        AdvertiseSdk.initConsent(activity, onResult)

    fun showSplashConsent(activity: Activity, onFinished: () -> Unit) =
        AdvertiseSdk.showSplashConsent(activity, onFinished)

    fun showOpenAd(activity: Activity, areaKey: String, onClosed: () -> Unit, onLoaded: () -> Unit) {
        AdvertiseSdk.showOpenAd(
            activity = activity,
            areaKey = areaKey,
            onCloseListener = AdvertiseSdk.OpenAdCloseListener(onClosed),
            onLoadedListener = AdvertiseSdk.OpenAdLoadedListener(onLoaded),
            onPaidListener = AdvertiseSdk.OpenAdPaidListener { _ -> },
        )
    }

    fun showInterstitial(activity: Activity, areaKey: String, onClosed: () -> Unit) {
        AdvertiseSdk.showInterstitialAd(activity, areaKey, onClosed)
    }

    fun showRewarded(
        activity: Activity,
        areaKey: String,
        onClosed: () -> Unit,
        onReward: (String, Int) -> Unit,
    ) {
        AdvertiseSdk.showRewardedAd(
            activity = activity,
            areaKey = areaKey,
            onCloseListener = AdvertiseSdk.RewardedAdCloseListener(onClosed),
            onLoadedListener = AdvertiseSdk.RewardedAdLoadedListener { },
            onPaidListener = AdvertiseSdk.RewardedAdPaidListener { _ -> },
            onRewardListener = AdvertiseSdk.RewardedAdRewardListener(onReward),
        )
    }

    fun getBanner(context: Context, areaKey: String): android.view.View? =
        AdvertiseSdk.getBannerAd(context, areaKey)

    fun destroyBanner(view: ViewGroup?) {
        AdvertiseSdk.destroyBannerAd(view)
    }

    fun getNative(context: Context, areaKey: String, onLoaded: () -> Unit): NativeAdContent? =
        AdvertiseSdk.getNativeAd(context, areaKey, onLoaded, AdvertiseSdk.NativeAdClickListener { })

    fun preloadStartup(context: Context) = preload(context, AdvertiseSdk.LOAD_TIME_OPEN_APP)

    fun preloadStartupInterstitial(context: Context) = preloadInterstitial(context, AdvertiseSdk.LOAD_TIME_OPEN_APP)

    fun preloadMainPage(context: Context) = preload(context, AdvertiseSdk.LOAD_TIME_ENTER_FEATURE)

    fun preloadAfterInterstitial(context: Context) = preload(context, AdvertiseSdk.LOAD_TIME_PLAY_FINISH)

    fun preloadAfterPlayFinish(context: Context) = preloadAfterInterstitial(context)

    fun preloadNative(context: Context, onLoaded: () -> Unit) {
        if (AdvertiseSdk.canPreloadNative(AdvertiseSdk.LOAD_TIME_OPEN_APP)) {
            AdvertiseSdk.preloadNative(context.applicationContext, AdvertiseSdk.LOAD_TIME_OPEN_APP, onLoaded)
        }
    }

    fun preloadRewarded(context: Context) {
        if (AdvertiseSdk.canPreloadRewarded(AdvertiseSdk.LOAD_TIME_ENTER_FEATURE)) {
            AdvertiseSdk.preloadRewarded(context.applicationContext, AdvertiseSdk.LOAD_TIME_ENTER_FEATURE)
        }
    }

    fun isPrivacyOptionsRequired(): Boolean = AdvertiseSdk.isPrivacyOptionsRequired

    fun showPrivacyOptions(activity: Activity) = AdvertiseSdk.showPrivacyOptions(activity)

    fun ensurePersistentNotificationServiceRunning(context: Context) {
        AdvertiseSdk.ensurePersistentNotificationServiceRunning(context)
    }

    fun updateNotificationContent(content: String) {
        AdvertiseNotificationManager.updateNotificationContent(content)
    }

    fun updateNotificationConfig(config: String) {
        AdvertiseNotificationManager.updateNotificationConfig(config)
    }

    suspend fun awaitRemoteConfigInitialization(timeoutMillis: Long = 6_500L): Boolean =
        withTimeoutOrNull(timeoutMillis) {
            suspendCancellableCoroutine { continuation ->
                AdvertiseSdk.getRemoteConfigInitStatus { _, _, _ ->
                    if (continuation.isActive) continuation.resume(true)
                }
            }
        } ?: false

    fun hasNotificationConfig(): Boolean =
        AdvertiseNotificationManager.notificationConfig != null

    fun hasNotificationContent(): Boolean =
        !AdvertiseNotificationManager.notificationContents.isNullOrEmpty()

    fun isAppOpenEnabled(): Boolean = AdvertiseSdk.isAppOpenAdEnabled

    fun setAppOpenEnabled(enabled: Boolean) { AdvertiseSdk.isAppOpenAdEnabled = enabled }

    fun suppressNextAppOpen(suppress: Boolean) { AdvertiseSdk.suppressNextAppOpenAd = suppress }

    private fun preloadInterstitial(context: Context, loadTimeKey: String) {
        if (AdvertiseSdk.canPreloadInterstitial(loadTimeKey)) {
            AdvertiseSdk.preloadInterstitial(context.applicationContext, loadTimeKey)
        }
    }

    private fun preload(context: Context, loadTimeKey: String) {
        if (AdvertiseSdk.canPreloadOpen(loadTimeKey)) {
            AdvertiseSdk.preloadOpen(context.applicationContext, loadTimeKey)
        }
        preloadInterstitial(context, loadTimeKey)
    }
}
