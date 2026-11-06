package com.aicleanerstorage.spaceclean.core.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey


const val DETAIL_INITIAL_INDEX_ARG = "initialIndex"

/**
 * The single source of truth for navigation destinations.
 *
 * Legacy route, screen and notification APIs are kept as adapters around this
 * registry while callers migrate to [AppDestination].
 */
@Serializable
sealed class AppDestination {
    abstract val route: String
    open val featureKey: FeatureKey? get() = null
    open val featureEntry: Boolean get() = false
    open val notificationAliases: Set<String> get() = emptySet()
    open val notificationTarget: Boolean get() = false

    @Serializable
    @SerialName("splash")
    data object Splash : AppDestination() {
        override val route get() = "splash"
    }

    @Serializable
    @SerialName("onboarding_scan")
    data object OnboardingScan : AppDestination() {
        override val route get() = "onboarding_scan"
    }

    @Serializable
    @SerialName("home")
    data class Main(val initialTab: MainTab = MainTab.Home) : AppDestination() {
        override val route get() = when (initialTab) {
            MainTab.Home -> "home"
            MainTab.Files -> HOME_FILE_MANAGER_ROUTE
            MainTab.Toolbox -> HOME_TOOLBOX_ROUTE
        }
        override val notificationAliases get() = when (initialTab) {
            MainTab.Home -> setOf("home", "main", "/main")
            MainTab.Files -> setOf("file_manager", HOME_FILE_MANAGER_ROUTE)
            MainTab.Toolbox -> setOf("toolbox", HOME_TOOLBOX_ROUTE)
        }
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("settings")
    data object Settings : AppDestination() {
        override val route get() = "settings"
    }

    @Serializable
    @SerialName("manage_permissions")
    data object ManagePermissions : AppDestination() {
        override val route get() = "manage_permissions"
    }

    @Serializable
    @SerialName("scan")
    data object JunkClean : AppDestination() {
        override val route get() = "scan"
        override val featureKey get() = FeatureKey.JUNK_CLEAN
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("junk_files", "clean", "scan", "/scan", "/junkClean")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("anti_virus")
    data object AntiVirus : AppDestination() {
        override val route get() = "anti_virus"
        override val featureKey get() = FeatureKey.ANTI_VIRUS
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("virus_anti", "virusAnti", "/virusAnti")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("virus_quick_scan")
    data object VirusQuickScan : AppDestination() {
        override val route get() = "virus_quick_scan"
        override val featureKey get() = FeatureKey.ANTI_VIRUS
    }

    @Serializable
    @SerialName("virus_deep_scan")
    data object VirusDeepScan : AppDestination() {
        override val route get() = "virus_deep_scan"
        override val featureKey get() = FeatureKey.ANTI_VIRUS
    }

    @Serializable
    @SerialName("virus_result")
    data object VirusResult : AppDestination() {
        override val route get() = "virus_result"
        override val featureKey get() = FeatureKey.ANTI_VIRUS
    }

    @Serializable
    @SerialName("no_virus_result")
    data object NoVirusResult : AppDestination() {
        override val route get() = "no_virus_result"
        override val featureKey get() = FeatureKey.ANTI_VIRUS
    }

    @Serializable
    @SerialName("app_lock")
    data object AppLock : AppDestination() {
        override val route get() = "app_lock"
        override val featureKey get() = FeatureKey.APP_LOCK
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("app_lock", "appLock", "/appLock")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("device_info")
    data object DeviceInfo : AppDestination() {
        override val route get() = "device_info"
        override val featureKey get() = FeatureKey.DEVICE_INFO
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("device_info", "deviceInfo", "/deviceInfo")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("battery_info")
    data object BatteryInfo : AppDestination() {
        override val route get() = "battery_info"
        override val featureKey get() = FeatureKey.BATTERY_INFO
        override val featureEntry get() = true
        override val notificationAliases get() = setOf(
                "battery_info",
                "batteryInfo",
                "/batteryInfo",
                "checkBatteryInfo",
                "/checkBatteryInfo",
            )
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("app_usage")
    data object AppUsage : AppDestination() {
        override val route get() = "app_usage"
        override val featureKey get() = FeatureKey.APP_USAGE
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("app_usage", "appUsage", "/appUsage")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("network_usage")
    data object NetworkUsage : AppDestination() {
        override val route get() = "network_usage"
        override val featureKey get() = FeatureKey.NETWORK_USAGE
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("network_usage", "networkUsage", "/networkUsage")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("network_scan")
    data object NetworkScan : AppDestination() {
        override val route get() = "network_scan"
        override val featureKey get() = FeatureKey.NETWORK_SCAN
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("network_scan", "networkScan", "/networkScan")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("network_scan_devices")
    data object NetworkScanDevices : AppDestination() {
        override val route get() = "network_scan_devices"
        override val featureKey get() = FeatureKey.NETWORK_SCAN
    }

    @Serializable
    @SerialName("network_speed")
    data object NetworkSpeed : AppDestination() {
        override val route get() = "network_speed"
        override val featureKey get() = FeatureKey.NETWORK_SPEED
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("network_speed", "networkSpeed", "/networkSpeed")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("whatsapp_cleaner")
    data object WhatsAppCleaner : AppDestination() {
        override val route get() = "whatsapp_cleaner"
        override val featureKey get() = FeatureKey.WHATSAPP_CLEANER
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("whatsapp", "/whatsapp", "whatsapp_cleaner")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("notification_cleaner")
    data object NotificationCleaner : AppDestination() {
        override val route get() = "notification_cleaner"
        override val featureKey get() = FeatureKey.NOTIFICATION_CLEANER
        override val featureEntry get() = true
        override val notificationAliases get() = setOf(
                "notification_bar",
                "notification_clean",
                "notificationClean",
                "/notificationClean",
            )
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("photos_manager")
    data object PhotosManager : AppDestination() {
        override val route get() = "photos_manager"
        override val featureKey get() = FeatureKey.PHOTOS
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("photos", "managePhotos", "/managePhotos")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("similar_photos_manager")
    data object SimilarPhotosManager : AppDestination() {
        override val route get() = "similar_photos_manager"
        override val featureKey get() = FeatureKey.SIMILAR_PHOTOS
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("similar_photos", "manageSimilarPhotos", "/manageSimilarPhotos")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("photo_privacy_manager")
    data object PhotoPrivacyManager : AppDestination() {
        override val route get() = "photo_privacy_manager"
        override val featureKey get() = FeatureKey.PHOTO_PRIVACY
        override val featureEntry get() = true
    }

    @Serializable
    @SerialName("screenshots_manager")
    data object ScreenshotsManager : AppDestination() {
        override val route get() = "screenshots_manager"
        override val featureKey get() = FeatureKey.SCREENSHOTS
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("screenshots", "screenshot_manager", "/screenshotManager")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("videos_manager")
    data object VideosManager : AppDestination() {
        override val route get() = "videos_manager"
        override val featureKey get() = FeatureKey.VIDEOS
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("videos", "manageVideos", "/manageVideos")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("audios_manager")
    data object AudiosManager : AppDestination() {
        override val route get() = "audios_manager"
        override val featureKey get() = FeatureKey.AUDIOS
        override val featureEntry get() = true
    }

    @Serializable
    @SerialName("large_files_manager")
    data object LargeFilesManager : AppDestination() {
        override val route get() = "large_files_manager"
        override val featureKey get() = FeatureKey.LARGE_FILES
        override val featureEntry get() = true
        override val notificationAliases get() = setOf("large_files", "largeFileManager", "/largeFileManager")
        override val notificationTarget get() = true
    }

    @Serializable
    @SerialName("duplicate_files_manager")
    data object DuplicateFilesManager : AppDestination() {
        override val route get() = "duplicate_files_manager"
        override val featureKey get() = FeatureKey.DUPLICATE_FILES
        override val featureEntry get() = true
    }

    @Serializable
    @SerialName("documents_manager")
    data object DocumentsManager : AppDestination() {
        override val route get() = "documents_manager"
        override val featureKey get() = FeatureKey.DOCUMENTS
        override val featureEntry get() = true
    }

    @Serializable
    @SerialName("photos_detail")
    data class PhotosDetail(val initialIndex: Int = 0) : AppDestination() {
        override val route get() = "photos_detail"
        override val featureKey get() = FeatureKey.PHOTOS
    }

    @Serializable
    @SerialName("similar_photos_detail")
    data class SimilarPhotosDetail(val initialIndex: Int = 0) : AppDestination() {
        override val route get() = "similar_photos_detail"
        override val featureKey get() = FeatureKey.SIMILAR_PHOTOS
    }

    @Serializable
    @SerialName("screenshots_detail")
    data class ScreenshotsDetail(val initialIndex: Int = 0) : AppDestination() {
        override val route get() = "screenshots_detail"
        override val featureKey get() = FeatureKey.SCREENSHOTS
    }

    @Serializable
    @SerialName("videos_detail")
    data class VideosDetail(val initialIndex: Int = 0) : AppDestination() {
        override val route get() = "videos_detail"
        override val featureKey get() = FeatureKey.VIDEOS
    }

    @Serializable
    @SerialName("audios_detail")
    data class AudiosDetail(val initialIndex: Int = 0) : AppDestination() {
        override val route get() = "audios_detail"
        override val featureKey get() = FeatureKey.AUDIOS
    }

    @Serializable
    @SerialName("large_files_detail")
    data class LargeFilesDetail(val initialIndex: Int = 0) : AppDestination() {
        override val route get() = "large_files_detail"
        override val featureKey get() = FeatureKey.LARGE_FILES
    }

    @Serializable
    @SerialName("documents_detail")
    data class DocumentsDetail(val initialIndex: Int = 0) : AppDestination() {
        override val route get() = "documents_detail"
        override val featureKey get() = FeatureKey.DOCUMENTS
    }

    companion object {
        const val HOME_FILE_MANAGER_ROUTE = "home_file_manager"
        const val HOME_TOOLBOX_ROUTE = "home_toolbox"

        /** All registered destinations, in stable navigation/catalog order. */
        val all: List<AppDestination> by lazy {
            listOf(
                Splash,
                OnboardingScan,
                Main(),
                Main(MainTab.Files),
                Main(MainTab.Toolbox),
                Settings,
                ManagePermissions,
                JunkClean,
                AntiVirus,
                VirusQuickScan,
                VirusDeepScan,
                VirusResult,
                NoVirusResult,
                AppLock,
                DeviceInfo,
                BatteryInfo,
                AppUsage,
                NetworkUsage,
                NetworkScan,
                NetworkScanDevices,
                NetworkSpeed,
                WhatsAppCleaner,
                NotificationCleaner,
                PhotosManager,
                SimilarPhotosManager,
                PhotoPrivacyManager,
                ScreenshotsManager,
                VideosManager,
                AudiosManager,
                LargeFilesManager,
                DuplicateFilesManager,
                DocumentsManager,
                PhotosDetail(),
                SimilarPhotosDetail(),
                ScreenshotsDetail(),
                VideosDetail(),
                AudiosDetail(),
                LargeFilesDetail(),
                DocumentsDetail(),
            )
        }

        val featureEntries: List<AppDestination> by lazy { all.filter(AppDestination::featureEntry) }

        private val byRoute: Map<String, AppDestination> by lazy { all.associateBy(AppDestination::route) }
        private val byFeature: Map<FeatureKey, AppDestination> by lazy {
            featureEntries.associateBy { requireNotNull(it.featureKey) }
        }

        val startupRoutes: Set<String> by lazy { setOf(Splash.route, OnboardingScan.route) }
        val homeRoutes: Set<String> by lazy { setOf("home", HOME_FILE_MANAGER_ROUTE, HOME_TOOLBOX_ROUTE) }
        val rootRoutes: Set<String> by lazy { startupRoutes + homeRoutes }

        val notificationTargets: List<AppDestination> by lazy { all.filter(AppDestination::notificationTarget) }
        val notificationTargetRoutes: Set<String> by lazy {
            notificationTargets.mapTo(linkedSetOf(), AppDestination::route)
        }

        private val notificationAliasToRoute: Map<String, String> by lazy {
            notificationTargets
                .flatMap { destination ->
                    destination.notificationAliases.map { alias ->
                        alias.normalizedKey() to destination.route
                    }
                }.toMap()
        }

        private val notificationRouteByKey: Map<String, String> by lazy {
            notificationTargetRoutes.associateBy { route -> route.normalizedKey() }
        }

        fun forRoute(route: String?): AppDestination? {
            val key = route?.routeKey() ?: return null
            val destination = byRoute[key] ?: detailDestinations.firstOrNull { key.startsWith("${it.route}/") }
                ?: return null
            val index = route.substringAfter('?', "").split('&')
                .firstOrNull { it.substringBefore('=') == DETAIL_INITIAL_INDEX_ARG }
                ?.substringAfter('=')?.toIntOrNull()
                ?: key.substringAfter("${destination.route}/", "").toIntOrNull()
                ?: 0
            return when (destination) {
                is PhotosDetail -> destination.copy(initialIndex = index.coerceAtLeast(0))
                is SimilarPhotosDetail -> destination.copy(initialIndex = index.coerceAtLeast(0))
                is ScreenshotsDetail -> destination.copy(initialIndex = index.coerceAtLeast(0))
                is VideosDetail -> destination.copy(initialIndex = index.coerceAtLeast(0))
                is AudiosDetail -> destination.copy(initialIndex = index.coerceAtLeast(0))
                is LargeFilesDetail -> destination.copy(initialIndex = index.coerceAtLeast(0))
                is DocumentsDetail -> destination.copy(initialIndex = index.coerceAtLeast(0))
                else -> destination
            }
        }

        fun forFeature(featureKey: FeatureKey): AppDestination? = byFeature[featureKey]

        fun featureForRoute(route: String?): FeatureKey? = forRoute(route)?.featureKey

        fun normalizeNotificationRoute(rawRoute: String?): String? {
            val key = rawRoute?.normalizedKey()?.takeIf(String::isNotEmpty) ?: return null
            return notificationAliasToRoute[key] ?: notificationRouteByKey[key]
        }

        fun notificationAliases(): List<NotificationAlias> =
            notificationTargets.flatMap { destination ->
                destination.notificationAliases.map { rawRoute ->
                    NotificationAlias(rawRoute = rawRoute, route = destination.route)
                }
            }

        private fun String.routeKey(): String =
            trim()
                .substringBefore("#")
                .substringBefore("?")
                .trim()

        private fun String.normalizedKey(): String =
            routeKey()
                .trim('/')
                .lowercase()

        private val detailDestinations: Set<AppDestination> by lazy {
            setOf(
                PhotosDetail(),
                SimilarPhotosDetail(),
                ScreenshotsDetail(),
                VideosDetail(),
                AudiosDetail(),
                LargeFilesDetail(),
                DocumentsDetail(),
            )
        }
    }
}

data class NotificationAlias(
    val rawRoute: String,
    val route: String,
)
