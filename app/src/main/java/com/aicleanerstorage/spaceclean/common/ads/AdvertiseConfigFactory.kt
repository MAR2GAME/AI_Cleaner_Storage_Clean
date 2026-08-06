package com.aicleanerstorage.spaceclean.common.ads

import android.content.Context
import android.util.Log
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.pdffox.adv.AdvertiseSdkConfig
import com.pdffox.adv.AdvertiseSdkConfigs
import com.pdffox.adv.DEFAULT_DEBUG_NATIVE_IDS_JSON
import com.pdffox.adv.NotificationActionConfig
import com.pdffox.adv.NotificationFeatureConfig
import com.pdffox.adv.NotificationRouteMapping
import com.pdffox.adv.PushSceneKeyConfig

object AdvertiseConfigFactory {
    private const val TAG = "StorageCleanAdConfig"

    fun create(context: Context): AdvertiseSdkConfig {
        val singularEnabled = BuildConfig.ADV_SINGULAR_API_KEY.isNotBlank() && BuildConfig.ADV_SINGULAR_SECRET.isNotBlank()
        val facebookEnabled = BuildConfig.ADV_FACEBOOK_APP_ID.isNotBlank() && BuildConfig.ADV_FACEBOOK_CLIENT_TOKEN.isNotBlank()
        val tiktokEnabled = BuildConfig.ADV_TIKTOK_ACCESS_TOKEN.isNotBlank() &&
            BuildConfig.ADV_TIKTOK_TT_APP_ID.isNotBlank() && BuildConfig.ADV_TIKTOK_APP_ID.isNotBlank()
        val thinkingEnabled = BuildConfig.ADV_THINKING_APP_KEY.isNotBlank() && BuildConfig.ADV_THINKING_SERVER_URL.isNotBlank()
        val remoteConfigEnabled = BuildConfig.ADV_REMOTE_CONFIG_ENCRYPTION_KEY.isNotBlank() &&
            BuildConfig.ADV_REMOTE_CONFIG_ENCRYPTION_KEY_ID.isNotBlank()
        val serverEnabled = BuildConfig.ADV_SERVER_RELEASE_HOST.isNotBlank()
        val integrityEnabled = BuildConfig.ADV_PLAY_INTEGRITY_CLOUD_PROJECT_NUMBER > 0L

        logCapability("ThinkingData", thinkingEnabled)
        logCapability("RemoteConfig", remoteConfigEnabled)
        logCapability("Singular", singularEnabled)
        logCapability("Facebook", facebookEnabled)
        logCapability("TikTok", tiktokEnabled)

        return AdvertiseSdkConfigs.create(context, BuildConfig.DEBUG) {
            legal(
                privacyUrl = BuildConfig.ADV_PRIVACY_URL,
                termsUrl = BuildConfig.ADV_TERMS_URL,
            )
            defaultTopic(BuildConfig.ADV_DEFAULT_TOPIC)
            adDebugOverride(BuildConfig.ADV_AD_DEBUG_OVERRIDE_MODE)
            resources(
                adPolicyRawResId = R.raw.ad_policy,
                adLoadConfigRawResId = R.raw.adload_config,
                nativeAdPolicyRawResId = R.raw.native_ad_policy,
                nativeAdIdsRawResId = R.raw.native_ad_ids,
                cloudCidrsRawResId = com.pdffox.adv.R.raw.cloud,
                googleCidrsRawResId = com.pdffox.adv.R.raw.google,
                pushConfigRawResId = R.raw.push,
            )
            server(
                enabled = serverEnabled,
                releaseHost = BuildConfig.ADV_SERVER_RELEASE_HOST,
                testHost = BuildConfig.ADV_SERVER_TEST_HOST,
                parseTokenKey = BuildConfig.ADV_PLAY_INTEGRITY_PARSE_TOKEN_KEY,
            )
            firebase(
                analyticsEnabled = true,
                messagingEnabled = true,
                subscribeDefaultTopic = true,
            )
            remoteConfig(
                enabled = remoteConfigEnabled,
                encryptionKeyBase64 = BuildConfig.ADV_REMOTE_CONFIG_ENCRYPTION_KEY,
                encryptionKeyId = BuildConfig.ADV_REMOTE_CONFIG_ENCRYPTION_KEY_ID,
            )
            thinking(
                enabled = thinkingEnabled,
                appKey = BuildConfig.ADV_THINKING_APP_KEY,
                serverUrl = BuildConfig.ADV_THINKING_SERVER_URL,
            )
            singular(
                enabled = singularEnabled,
                apiKey = BuildConfig.ADV_SINGULAR_API_KEY,
                secret = BuildConfig.ADV_SINGULAR_SECRET,
            )
            adMob(
                enabled = BuildConfig.ADV_ADMOB_APP_ID.isNotBlank(),
                appId = BuildConfig.ADV_ADMOB_APP_ID,
                bannerId = BuildConfig.ADV_ADMOB_BANNER_ID,
                interstitialId = BuildConfig.ADV_ADMOB_INTERSTITIAL_ID,
                nativeId = BuildConfig.ADV_ADMOB_NATIVE_ID,
                openId = BuildConfig.ADV_ADMOB_OPEN_ID,
                rewardedId = BuildConfig.ADV_ADMOB_REWARDED_ID,
                nativeIdsJson = BuildConfig.ADV_ADMOB_NATIVE_IDS_JSON,
                debugNativeIdsJson = DEFAULT_DEBUG_NATIVE_IDS_JSON,
            )
            facebook(
                enabled = facebookEnabled,
                appId = BuildConfig.ADV_FACEBOOK_APP_ID,
                clientToken = BuildConfig.ADV_FACEBOOK_CLIENT_TOKEN,
            )
            tiktok(
                enabled = tiktokEnabled,
                accessToken = BuildConfig.ADV_TIKTOK_ACCESS_TOKEN,
                ttAppId = BuildConfig.ADV_TIKTOK_TT_APP_ID,
                appId = BuildConfig.ADV_TIKTOK_APP_ID,
            )
            safe(
                enabled = BuildConfig.ADV_SAFE_EXPECTED_SIGNATURES.isNotBlank(),
                expectedSignatures = BuildConfig.ADV_SAFE_EXPECTED_SIGNATURES,
                expectedPackageName = BuildConfig.APPLICATION_ID,
                rejectDebuggableBuilds = true,
                rejectDebuggerAttached = true,
                killProcessOnFailure = true,
                enforceInDebugBuilds = false,
            )
            push(
                enabled = true,
                persistentServiceEnabled = true,
                firebaseMessagingServiceEnabled = true,
                serviceStarterJobEnabled = true,
                bootReceiverEnabled = true,
                notificationDeletedReceiverEnabled = true,
                fileProviderEnabled = true,
                deletionObserverEnabled = true,
                sceneKeys =
                    PushSceneKeyConfig(
                        imageDeleted = "/managePhotos",
                        videoDeleted = "/manageVideos",
                        fileDeleted = "/largeFileManager",
                    ),
            )
            notifications(notificationConfig(context))
            playIntegrity(
                enabled = integrityEnabled,
                cloudProjectNumber = BuildConfig.ADV_PLAY_INTEGRITY_CLOUD_PROJECT_NUMBER,
                runInDebugBuilds = false,
            )
        }
    }

    private fun notificationConfig(context: Context): NotificationFeatureConfig =
        NotificationFeatureConfig(
            enabled = true,
            smallIconResId = R.drawable.app_logo,
            persistentContentText = context.getString(R.string.app_name),
            persistentActions =
                listOf(
                    NotificationActionConfig(
                        "junk_files",
                        context.getString(R.string.junk_removal),
                        R.drawable.ic_persistent_clean,
                    ),
                    NotificationActionConfig(
                        "file_manager",
                        context.getString(R.string.home_tab_file_manager),
                        R.drawable.ic_persistent_file,
                    ),
                    NotificationActionConfig(
                        "toolbox",
                        context.getString(R.string.home_tab_toolbox),
                        R.drawable.ic_persistent_tools,
                    ),
                    NotificationActionConfig(
                        "battery_info",
                        context.getString(R.string.nav_battery_info),
                        R.drawable.ic_persistent_battery,
                    ),
                ),
            routeMappings = notificationRouteMappings(),
        )

    private fun notificationRouteMappings(): List<NotificationRouteMapping> =
        AppDestination.notificationAliases().map { alias ->
            NotificationRouteMapping(
                rawRoute = alias.rawRoute,
                route = alias.route,
                temporaryIconResId = iconForRoute(alias.route),
                persistentIconResId = iconForRoute(alias.route),
            )
        }

    private fun iconForRoute(route: String): Int =
        when (route) {
            AppDestination.Home.route -> R.mipmap.ic_launcher_round
            AppDestination.HomeFileManager.route -> R.drawable.ic_persistent_file
            AppDestination.HomeToolbox.route -> R.drawable.ic_persistent_tools
            AppDestination.JunkClean.route -> R.drawable.ic_persistent_clean
            AppDestination.AntiVirus.route -> R.drawable.virus_shield
            AppDestination.AppLock.route -> R.drawable.app_lock_permission_figma
            AppDestination.DeviceInfo.route -> R.drawable.ic_device_phone
            AppDestination.BatteryInfo.route -> R.drawable.ic_battery
            AppDestination.AppUsage.route -> R.drawable.ic_app_usage
            AppDestination.NetworkUsage.route -> R.drawable.ic_network_usage
            AppDestination.NetworkScan.route -> R.drawable.ic_network_scan
            AppDestination.NetworkSpeed.route -> R.drawable.ic_network_speed
            AppDestination.WhatsAppCleaner.route -> R.drawable.ic_whats_app_cleaner
            AppDestination.NotificationCleaner.route -> R.drawable.ic_notification_cleaner
            AppDestination.PhotosManager.route -> R.drawable.ic_photos
            AppDestination.SimilarPhotosManager.route -> R.drawable.ic_similar_photos
            AppDestination.ScreenshotsManager.route -> R.drawable.ic_screenshots
            AppDestination.VideosManager.route -> R.drawable.ic_videos
            AppDestination.LargeFilesManager.route -> R.drawable.ic_large_files
            else -> R.mipmap.ic_launcher_round
        }

    private fun logCapability(name: String, enabled: Boolean) {
        if (!enabled) Log.i(TAG, "$name is disabled until Storage Clean credentials are configured")
    }
}
