package com.aicleanerstorage.spaceclean.app.di

import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.app.background.AppBackgroundRuntimeService
import com.aicleanerstorage.spaceclean.app.platform.permission.PermissionPreferences
import com.aicleanerstorage.spaceclean.base.storage.PreferencesUtil
import com.aicleanerstorage.spaceclean.base.toolkit.antivirus.TrustlookVirusScanner
import com.aicleanerstorage.spaceclean.base.coroutines.AppDispatchers
import com.aicleanerstorage.spaceclean.data.repository.AppLockRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.AppUsageRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.BatteryHistoryRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.CleanRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.DeviceInfoRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.FileRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.NetworkRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.NotificationRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.SettingsRepositoryImpl
import com.aicleanerstorage.spaceclean.data.repository.VirusSecurityRepositoryImpl
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.port.AppUsageRepository
import com.aicleanerstorage.spaceclean.domain.port.BatteryHistoryRepository
import com.aicleanerstorage.spaceclean.domain.port.CleanRepository
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.domain.port.FileRepository
import com.aicleanerstorage.spaceclean.domain.port.NetworkRepository
import com.aicleanerstorage.spaceclean.domain.port.NotificationRepository
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import com.aicleanerstorage.spaceclean.domain.port.VirusScanner
import com.aicleanerstorage.spaceclean.domain.port.VirusSecurityRepository
import com.aicleanerstorage.spaceclean.domain.usecase.SyncBackgroundBatterySamplingUseCase
import com.aicleanerstorage.spaceclean.domain.usecase.SampleBatteryHistoryUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module =
    module {
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
        single<DeviceInfoRepository> { DeviceInfoRepositoryImpl(androidContext(), get<AppDispatchers>().io) }
        single<VirusScanner> { TrustlookVirusScanner(androidContext(), com.aicleanerstorage.spaceclean.BuildConfig.TRUSTLOOK_API_KEY) }
        single<VirusSecurityRepository> { VirusSecurityRepositoryImpl(androidContext(), get()) }
        single<AppLockRepository> { AppLockRepositoryImpl(androidContext()) }
        single<AppUsageRepository> { AppUsageRepositoryImpl(androidContext()) }
        single<NetworkRepository> { NetworkRepositoryImpl(androidContext()) }
        single<FileRepository> { FileRepositoryImpl(androidContext()) }
        single<CleanRepository> { CleanRepositoryImpl(androidContext()) }
        single<SettingsRepository> { SettingsRepositoryImpl(get()) }
        single<NotificationRepository> { NotificationRepositoryImpl(androidContext()) }
        single<BatteryHistoryRepository> { BatteryHistoryRepositoryImpl(androidContext()) }
        single {
            SampleBatteryHistoryUseCase(
                deviceInfoRepository = get(),
                historyRepository = get(),
                ioDispatcher = get<AppDispatchers>().io,
            )
        }
    }
