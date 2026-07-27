package com.aicleanerstorage.spaceclean.app.di

import com.aicleanerstorage.spaceclean.feature.antivirus.TrustlookVirusScanEngine
import com.aicleanerstorage.spaceclean.feature.antivirus.VirusSecurityRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.antivirus.AntivirusPreferencesImpl
import com.aicleanerstorage.spaceclean.feature.antivirus.AntivirusPreferences
import com.aicleanerstorage.spaceclean.feature.antivirus.VirusScanEngine
import com.aicleanerstorage.spaceclean.feature.antivirus.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.feature.applock.AppLockMonitoringControllerImpl
import com.aicleanerstorage.spaceclean.feature.applock.AppLockRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.AndroidNetworkInfoReader
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.data.AppUsageRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistoryRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.junkclean.CleanRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.device.DeviceInfoRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.files.shared.data.FileRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.files.duplicates.data.DuplicateFilesPreferencesImpl
import com.aicleanerstorage.spaceclean.feature.files.duplicates.data.DuplicateFilesPreferences
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingPreferencesImpl
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingPreferences
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.settings.SettingsRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.AndroidNotificationSettingsGateway
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationRepository
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationRepositoryImpl
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationSettingsGateway
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistorySamplerImpl
import com.aicleanerstorage.spaceclean.feature.applock.AppLockMonitoringController
import com.aicleanerstorage.spaceclean.feature.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.feature.applock.AppLockMonitoringService
import com.aicleanerstorage.spaceclean.app.background.BackgroundAppLockMonitoringService
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.data.AppUsageRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistorySampler
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.data.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.CleanRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.CleanSessionStore
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.device.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.feature.files.shared.data.FileRepository
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkInfoReader
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.network.NetworkRepository
import com.aicleanerstorage.spaceclean.feature.settings.SettingsRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.scanner.SharedScanState
import com.aicleanerstorage.spaceclean.common.storage.preferences.AppPreferences
import com.aicleanerstorage.spaceclean.common.storage.preferences.CleanupHistoryStore
import com.aicleanerstorage.spaceclean.common.storage.preferences.DefaultCleanupHistoryStore
import com.aicleanerstorage.spaceclean.common.permission.PermissionPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module =
    module {
        single { AppPreferences(androidContext()) }
        single { PermissionPreferences(get()) }
        single<CleanupHistoryStore> { DefaultCleanupHistoryStore(get()) }
        single<DeviceInfoRepository> { DeviceInfoRepositoryImpl(androidContext()) }
        single<VirusScanEngine> { TrustlookVirusScanEngine(androidContext()) }
        single<VirusSecurityRepository> { VirusSecurityRepositoryImpl(androidContext()) }
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
        single<NotificationSettingsGateway> { AndroidNotificationSettingsGateway() }
        single<BatteryHistoryRepository> { BatteryHistoryRepositoryImpl(androidContext()) }
        single<BatteryHistorySampler> {
            BatteryHistorySamplerImpl(
                deviceInfoRepository = get(),
                historyRepository = get(),
            )
        }
    }
