package com.aicleanerstorage.spaceclean.app.di

import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.background.AppRuntimeService
import com.aicleanerstorage.spaceclean.app.background.BatterySamplingSync
import com.aicleanerstorage.spaceclean.app.permission.ActivityMediaDeletionRequester
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionRequester
import com.aicleanerstorage.spaceclean.data.local.battery.BatteryHistorySampler
import com.aicleanerstorage.spaceclean.data.local.files.DirectoryFileDataSource
import com.aicleanerstorage.spaceclean.data.local.files.FileDeletionDataSource
import com.aicleanerstorage.spaceclean.data.local.network.NetworkSpeedEndpoints
import com.aicleanerstorage.spaceclean.data.local.preferences.PreferencesDataSource
import com.aicleanerstorage.spaceclean.data.local.security.TrustlookVirusScanner
import com.aicleanerstorage.spaceclean.data.local.storage.MediaDeletionAuthorization
import com.aicleanerstorage.spaceclean.data.local.whatsapp.WhatsAppFileDataSource
import com.aicleanerstorage.spaceclean.data.repository.DefaultAppLockRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultAppSettingsRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultAppUsageRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultBatteryHistoryRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultDeviceInfoRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultJunkCleanRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultManagedFileRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultNetworkRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultNotificationCleanerRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultSimilarPhotoRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultVirusSecurityRepository
import com.aicleanerstorage.spaceclean.data.repository.DefaultWhatsAppCleanerRepository
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import com.aicleanerstorage.spaceclean.domain.port.AppUsageRepository
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRecorder
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.port.BatterySamplingControl
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.domain.port.JunkCleanRepository
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
import com.aicleanerstorage.spaceclean.domain.port.NetworkRepository
import com.aicleanerstorage.spaceclean.domain.port.NotificationCleanerRepository
import com.aicleanerstorage.spaceclean.domain.port.SimilarPhotoRepository
import com.aicleanerstorage.spaceclean.domain.port.VirusScanner
import com.aicleanerstorage.spaceclean.domain.port.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.domain.port.WhatsAppCleanerRepository
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule: Module =
    module {
        single { PreferencesDataSource(androidContext()) }
        single(named("applock")) { PreferencesDataSource(androidContext(), "phone_cleaner_sp") }
        single(named("notification")) { PreferencesDataSource(androidContext(), "notification_blocker") }
        single { MediaDeletionAuthorization() }
        single { ActivityMediaDeletionRequester() }
        single<MediaDeletionRequester> { get<ActivityMediaDeletionRequester>() }
        single {
            val context = androidContext()
            DefaultManagedFileRepository(
                context = context,
                deletion = FileDeletionDataSource(context, get<MediaDeletionRequester>(), get()),
            )
        }
        single<ManagedFileRepository> { get<DefaultManagedFileRepository>() }
        single {
            DefaultJunkCleanRepository(androidContext(), get<MediaDeletionRequester>(), get())
        }
        single<JunkCleanRepository> { get<DefaultJunkCleanRepository>() }
        single<BatterySamplingControl> {
            val context = androidContext()
            val settingsRepository = get<AppSettingsRepository>()
            val appLockRepository = get<AppLockRepository>()
            BatterySamplingSync(
                settingsRepository = settingsRepository,
                syncRuntime = { AppRuntimeService.reconcile(context, appLockRepository, settingsRepository) },
            )
        }
        single { DefaultDeviceInfoRepository(androidContext(), Dispatchers.IO) }
        single<DeviceInfoRepository> { get<DefaultDeviceInfoRepository>() }
        single<VirusScanner> { TrustlookVirusScanner(androidContext(), BuildConfig.TRUSTLOOK_API_KEY) }
        single<VirusSecurityRepository> { DefaultVirusSecurityRepository(androidContext(), get()) }
        single<AppLockRepository> { DefaultAppLockRepository(androidContext(), get(named("applock"))) }
        single<AppUsageRepository> { DefaultAppUsageRepository(androidContext()) }
        single { DefaultNetworkRepository(androidContext(), NetworkSpeedEndpoints()) }
        single<NetworkRepository> { get<DefaultNetworkRepository>() }
        single { DirectoryFileDataSource(androidContext()) }
        single { WhatsAppFileDataSource(get()) }
        single<WhatsAppCleanerRepository> {
            DefaultWhatsAppCleanerRepository(
                source = get(),
                files = get<ManagedFileRepository>(),
            )
        }
        single<SimilarPhotoRepository> { DefaultSimilarPhotoRepository(get()) }
        single<AppSettingsRepository> { DefaultAppSettingsRepository(get()) }
        single<NotificationCleanerRepository> {
            val context = androidContext()
            DefaultNotificationCleanerRepository(
                context = context,
                retainContent = false,
                preferences = get(named("notification")),
            )
        }
        single<BatteryHistoryRepository> { DefaultBatteryHistoryRepository(androidContext()) }
        single<BatteryHistoryRecorder> { BatteryHistorySampler(get(), get(), Dispatchers.IO) }
    }
