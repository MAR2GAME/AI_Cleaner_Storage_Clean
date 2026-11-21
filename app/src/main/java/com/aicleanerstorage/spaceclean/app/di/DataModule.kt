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
import com.aicleanerstorage.spaceclean.app.background.AppRuntimeService
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionHost
import com.aicleanerstorage.spaceclean.base.storage.PreferencesUtil
import com.aicleanerstorage.spaceclean.base.toolkit.antivirus.TrustlookVirusScanner
import com.aicleanerstorage.spaceclean.domain.applock.port.AppLockRepository
import com.aicleanerstorage.spaceclean.data.applock.internal.AppLockRepositoryImpl
import com.aicleanerstorage.spaceclean.domain.appusage.port.AppUsageRepository
import com.aicleanerstorage.spaceclean.data.appusage.internal.AppUsageRepositoryImpl
import com.aicleanerstorage.spaceclean.domain.battery.port.BatteryHistoryRecorder
import com.aicleanerstorage.spaceclean.domain.battery.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.data.battery.BatteryHistorySampler
import com.aicleanerstorage.spaceclean.data.battery.internal.BatteryHistoryRepositoryImpl
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.port.NotificationCleanerRepository
import com.aicleanerstorage.spaceclean.data.notificationcleaner.internal.NotificationCleanerRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.SettingsRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.notificationcleaner.StorageCleanNotificationListener
import com.aicleanerstorage.spaceclean.domain.security.port.VirusScanner
import com.aicleanerstorage.spaceclean.domain.security.port.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.data.security.internal.VirusSecurityRepositoryImpl
import com.aicleanerstorage.spaceclean.data.similarphotos.internal.SimilarPhotoRepositoryImpl
import com.aicleanerstorage.spaceclean.data.whatsapp.internal.WhatsAppCleanerRepositoryImpl
import com.aicleanerstorage.spaceclean.domain.similarphotos.port.SimilarPhotoRepository
import com.aicleanerstorage.spaceclean.domain.whatsapp.port.WhatsAppCleanerRepository
import com.aicleanerstorage.spaceclean.domain.settings.port.SettingsRepository
import com.aicleanerstorage.spaceclean.domain.battery.usecase.SyncBackgroundBatterySamplingUseCase
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
        single {
            val context = androidContext()
            val settingsRepository = get<SettingsRepository>()
            val appLockRepository = get<AppLockRepository>()
            SyncBackgroundBatterySamplingUseCase(
                settingsRepository = settingsRepository,
                syncRuntime = { AppRuntimeService.reconcile(context, appLockRepository, settingsRepository) },
            )
        }
        single<DeviceInfoRepository> { DeviceKit.create(androidContext()) }
        single<VirusScanner> { TrustlookVirusScanner(androidContext(), com.aicleanerstorage.spaceclean.BuildConfig.TRUSTLOOK_API_KEY) }
        single<VirusSecurityRepository> { VirusSecurityRepositoryImpl(androidContext()) }
        single<AppLockRepository> { AppLockRepositoryImpl(androidContext()) }
        single<AppUsageRepository> { AppUsageRepositoryImpl(androidContext()) }
        single<NetworkRepository> { NetworkKit.create(androidContext()) }
        single<FileRepository> { get<CleanerKit>().files }
        single<WhatsAppCleanerRepository> { WhatsAppCleanerRepositoryImpl(get()) }
        single<SimilarPhotoRepository> { SimilarPhotoRepositoryImpl(get()) }
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
        single<BatteryHistoryRecorder> { BatteryHistorySampler(get(), get(), Dispatchers.IO) }
    }
