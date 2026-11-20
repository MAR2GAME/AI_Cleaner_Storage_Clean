package com.aicleanerstorage.spaceclean.app.di


import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository

import com.aicleanerstorage.spaceclean.app.background.syncAppLockMonitoring



import com.aicleanerstorage.spaceclean.app.AppViewModel
import com.aicleanerstorage.spaceclean.base.coroutines.AppDispatchers
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanViewModel
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockViewModel
import com.aicleanerstorage.spaceclean.app.view.audios.interaction.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.documents.interaction.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.screenshots.interaction.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.videos.interaction.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeViewModel
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingScanViewModel
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashViewModel
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageViewModel
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesViewModel
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.base.permission.ContextPermissionChecker
import com.aicleanerstorage.spaceclean.base.permission.PermissionChecker
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsViewModel
import com.aicleanerstorage.spaceclean.app.view.settings.interaction.SettingsViewModel
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        single { AppDispatchers() }
        viewModel { AppViewModel() }
        single<PermissionChecker> { ContextPermissionChecker(androidContext()) }
        viewModel { HomeViewModel(get(), get(), get()) }
        viewModel { AppUsageViewModel(get(), get<AppDispatchers>().io) }
        viewModel { DeviceInfoViewModel(get(), get(), get(), get(), get<AppDispatchers>().io) }
        viewModel { BatteryInfoViewModel(get(), get(), get(), get(), get(), get<AppDispatchers>().io) }
        viewModel { NetworkScanViewModel(get(), get<AppDispatchers>().io) }
        viewModel { NetworkScanDevicesViewModel(get(), get<AppDispatchers>().io) }
        viewModel { NetworkSpeedViewModel(get(), get<AppDispatchers>().io) }
        viewModel { NetworkUsageViewModel(get(), get<AppDispatchers>().io) }
        viewModel { WhatsAppCleanerViewModel(androidApplication()::getString, get(), get<AppDispatchers>().io, get()) }
        viewModel {
            NotificationCleanerViewModel(
                message = androidApplication()::getString,
                repository = get(),
                ioDispatcher = get<AppDispatchers>().io,
                permissionChecker = get(),
            )
        }
        viewModel { OnboardingScanViewModel(get(), get()) }
        viewModel {
            JunkCleanViewModel(
                cleanRepository = get(),
                permissionChecker = get(),
                ioDispatcher = Dispatchers.IO,
            )
        }
        viewModel { SplashViewModel(get()) }
        viewModel { PhotosManagerViewModel(get(), Dispatchers.IO) }
        viewModel { ScreenshotsManagerViewModel(get(), Dispatchers.IO) }
        viewModel { VideosManagerViewModel(get(), Dispatchers.IO) }
        viewModel { AudiosManagerViewModel(get(), Dispatchers.IO) }
        viewModel { SimilarPhotosManagerViewModel(get(), Dispatchers.IO) }
        viewModel { PhotoPrivacyManagerViewModel(get(), Dispatchers.IO) }
        viewModel { LargeFilesManagerViewModel(get(), Dispatchers.IO) }
        viewModel { DocumentsManagerViewModel(get(), Dispatchers.IO) }
        viewModel { DuplicateFilesManagerViewModel(get(), get(), Dispatchers.IO) }
        viewModel { VirusScanViewModel(get(), get(), get(), get()) }
        viewModel { ManagePermissionsViewModel() }
        viewModel { SettingsViewModel(get()) }
        viewModel {
            val repository = get<AppLockRepository>()
            val context = androidContext()
            AppLockViewModel(
                repository = repository,
                syncMonitoringService = { syncAppLockMonitoring(context, repository) },
                ioDispatcher = Dispatchers.IO,
                permissionChecker = get(),
            )
        }
    }
