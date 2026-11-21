package com.aicleanerstorage.spaceclean.app.di

import android.content.ComponentName
import com.aicleanerstorage.cleanerkit.CleanerConfig
import com.aicleanerstorage.cleanerkit.CleanerKit
import com.aicleanerstorage.cleanerkit.clean.CleanRepository
import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.devicekit.DeviceInfoRepository
import com.aicleanerstorage.devicekit.DeviceKit
import com.aicleanerstorage.networkkit.NetworkKit
import com.aicleanerstorage.networkkit.NetworkRepository
import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.app.background.AppBackgroundRuntimeService
import com.aicleanerstorage.spaceclean.app.platform.permission.PermissionPreferences
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionHost
import com.aicleanerstorage.spaceclean.base.storage.PreferencesUtil
import com.aicleanerstorage.spaceclean.base.toolkit.antivirus.TrustlookVirusScanner
import com.aicleanerstorage.spaceclean.data.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.data.applock.internal.AppLockRepositoryImpl
import com.aicleanerstorage.spaceclean.data.appusage.AppUsageRepository
import com.aicleanerstorage.spaceclean.data.appusage.internal.AppUsageRepositoryImpl
import com.aicleanerstorage.spaceclean.data.battery.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.data.battery.BatteryHistorySampler
import com.aicleanerstorage.spaceclean.data.battery.internal.BatteryHistoryRepositoryImpl
import com.aicleanerstorage.spaceclean.data.notificationcleaner.NotificationCleanerRepository
import com.aicleanerstorage.spaceclean.data.notificationcleaner.internal.NotificationCleanerRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.SettingsRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.notificationcleaner.StorageCleanNotificationListener
import com.aicleanerstorage.spaceclean.data.security.VirusScanner
import com.aicleanerstorage.spaceclean.data.security.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.data.security.internal.VirusSecurityRepositoryImpl
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import com.aicleanerstorage.spaceclean.domain.usecase.SyncBackgroundBatterySamplingUseCase
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module =
    module {
        single {
            CleanerKit.create(
                context = androidContext(),
                config = CleanerConfig(authorizeMediaDeletion = MediaDeletionHost::authorize),
            )
        }
        single { PreferencesUtil(androidContext()) }
        single { PermissionPreferences(get()) }
        single {
            val context = androidContext()
            SyncBackgroundBatterySamplingUseCase(
                settingsRepository = get(),
                hasNotificationPermission = { AppConfig.hasPostNotificationsPermission(context) },
                enableServiceSampling = { AppBackgroundRuntimeService.enableBatterySampling(context) },
                disableServiceSampling = { AppBackgroundRuntimeService.disableBatterySampling(context) },
            )
        }
        single<DeviceInfoRepository> { DeviceKit.create(androidContext()) }
        single<VirusScanner> { TrustlookVirusScanner(androidContext(), com.aicleanerstorage.spaceclean.BuildConfig.TRUSTLOOK_API_KEY) }
        single<VirusSecurityRepository> { VirusSecurityRepositoryImpl(androidContext()) }
        single<AppLockRepository> { AppLockRepositoryImpl(androidContext()) }
        single<AppUsageRepository> { AppUsageRepositoryImpl(androidContext()) }
        single<NetworkRepository> { NetworkKit.create(androidContext()) }
        single<FileRepository> { get<CleanerKit>().files }
        single<CleanRepository> { get<CleanerKit>().clean }
        single<SettingsRepository> { SettingsRepositoryImpl(get()) }
        single<NotificationCleanerRepository> {
            val context = androidContext()
            NotificationCleanerRepositoryImpl(
                context = context,
                listener = ComponentName(context, StorageCleanNotificationListener::class.java),
                retainContent = false,
            )
        }
        single<BatteryHistoryRepository> { BatteryHistoryRepositoryImpl(androidContext()) }
        single<BatteryHistorySampler> { BatteryHistorySampler(get(), get(), Dispatchers.IO) }
    }
