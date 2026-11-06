package com.aicleanerstorage.spaceclean.app.di



import com.aicleanerstorage.spaceclean.feature.antivirus.data.TrustlookVirusScanEngine
import com.aicleanerstorage.spaceclean.feature.antivirus.data.VirusSecurityRepositoryImpl
import com.aicleanerstorage.spaceclean.app.storage.AntivirusPreferencesImpl
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.AntivirusPreferences
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusScanEngine
import com.aicleanerstorage.spaceclean.feature.antivirus.domain.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.feature.applock.data.AppLockMonitoringControllerImpl
import com.aicleanerstorage.spaceclean.feature.applock.data.AppLockRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.toolbox.data.shared.AndroidNetworkInfoReader
import com.aicleanerstorage.spaceclean.feature.toolbox.data.appusage.AppUsageRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.toolbox.data.battery.BatteryHistoryRepositoryImpl
import com.aicleanerstorage.spaceclean.app.storage.BackgroundBatterySamplingPreferencesImpl
import com.aicleanerstorage.spaceclean.feature.junkclean.data.CleanRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.toolbox.data.shared.DeviceInfoRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.files.data.shared.FileRepositoryImpl
import com.aicleanerstorage.spaceclean.app.storage.DuplicateFilesPreferencesImpl
import com.aicleanerstorage.spaceclean.feature.files.domain.duplicates.DuplicateFilesPreferences
import com.aicleanerstorage.spaceclean.app.storage.OnboardingPreferencesImpl
import com.aicleanerstorage.spaceclean.core.contracts.onboarding.OnboardingPreferences
import com.aicleanerstorage.spaceclean.feature.toolbox.data.shared.NetworkRepositoryImpl
import com.aicleanerstorage.spaceclean.app.storage.SettingsRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.domain.NotificationRepository
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.data.NotificationRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.toolbox.data.battery.BatteryHistorySamplerImpl
import com.aicleanerstorage.spaceclean.feature.applock.domain.AppLockMonitoringController
import com.aicleanerstorage.spaceclean.core.contracts.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.feature.applock.data.AppLockMonitoringService
import com.aicleanerstorage.spaceclean.app.background.BackgroundAppLockMonitoringService
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistorySampler
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BackgroundBatterySamplingPreferences
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.CleanRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.CleanSessionStore
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.core.contracts.files.FileRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkInfoReader
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.shared.NetworkRepository
import com.aicleanerstorage.spaceclean.core.contracts.settings.SettingsRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.data.session.SharedScanState
import com.aicleanerstorage.spaceclean.app.platform.storage.preferences.AppPreferences
import com.aicleanerstorage.spaceclean.core.contracts.feature.CleanupHistoryStore
import com.aicleanerstorage.spaceclean.app.platform.storage.preferences.DefaultCleanupHistoryStore
import com.aicleanerstorage.spaceclean.app.platform.analytics.AnalyticsPreferences
import com.aicleanerstorage.spaceclean.app.platform.analytics.DefaultAnalyticsPreferences
import com.aicleanerstorage.spaceclean.app.platform.permission.PermissionPreferences
import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.app.background.AppBackgroundRuntimeService
import com.aicleanerstorage.spaceclean.feature.toolbox.domain.battery.BackgroundBatterySamplingController
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module =
    module {
        single { AppPreferences(androidContext()) }
        single { PermissionPreferences(get()) }
        single<BackgroundBatterySamplingPreferences> { BackgroundBatterySamplingPreferencesImpl(get()) }
        single {
            val context = androidContext()
            BackgroundBatterySamplingController(
                preferences = get(),
                hasNotificationPermission = { AppConfig.hasPostNotificationsPermission(context) },
                enableServiceSampling = { AppBackgroundRuntimeService.enableBatterySampling(context) },
                disableServiceSampling = { AppBackgroundRuntimeService.disableBatterySampling(context) },
            )
        }
        single<CleanupHistoryStore> { DefaultCleanupHistoryStore(get()) }
        single<AnalyticsPreferences> { DefaultAnalyticsPreferences(get()) }
        single<DeviceInfoRepository> { DeviceInfoRepositoryImpl(androidContext()) }
        single<VirusScanEngine> { TrustlookVirusScanEngine(androidContext()) }
        single<VirusSecurityRepository> { VirusSecurityRepositoryImpl(androidContext(), com.aicleanerstorage.spaceclean.BuildConfig.TRUSTLOOK_API_KEY) }
        single<AntivirusPreferences> { AntivirusPreferencesImpl(get()) }
        single<AppLockRepository> { AppLockRepositoryImpl(androidContext()) }
        single<AppLockMonitoringService> { BackgroundAppLockMonitoringService(androidContext()) }
        single<AppLockMonitoringController> { AppLockMonitoringControllerImpl(get(), get()) }
        single<AppUsageRepository> { AppUsageRepositoryImpl(androidContext()) }
        single<NetworkInfoReader> { AndroidNetworkInfoReader(androidContext()) }
        single<NetworkRepository> { NetworkRepositoryImpl(androidContext()) }
        single<FileRepository> { FileRepositoryImpl(androidContext()) }
        single<DuplicateFilesPreferences> { DuplicateFilesPreferencesImpl(get()) }
        single<OnboardingPreferences> { OnboardingPreferencesImpl(get()) }
        single<CleanSessionStore> { SharedScanState() }
        single<CleanRepository> { CleanRepositoryImpl(androidContext(), get()) }
        single<SettingsRepository> { SettingsRepositoryImpl(get()) }
        single<NotificationRepository> { NotificationRepositoryImpl(androidContext()) }
        single<BatteryHistoryRepository> { BatteryHistoryRepositoryImpl(androidContext()) }
        single<BatteryHistorySampler> {
            BatteryHistorySamplerImpl(
                deviceInfoRepository = get(),
                historyRepository = get(),
            )
        }
    }
