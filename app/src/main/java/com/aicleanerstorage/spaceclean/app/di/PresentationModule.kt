package com.aicleanerstorage.spaceclean.app.di

import com.aicleanerstorage.spaceclean.common.coroutines.AppDispatchers
import com.aicleanerstorage.spaceclean.feature.antivirus.VirusScanViewModel
import com.aicleanerstorage.spaceclean.feature.applock.AppLockViewModel
import com.aicleanerstorage.spaceclean.feature.files.audios.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.documents.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.largefiles.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.photoprivacy.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.photos.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.screenshots.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.similarphotos.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.videos.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.home.HomeViewModel
import com.aicleanerstorage.spaceclean.feature.onboarding.OnboardingScanViewModel
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.feature.startup.SplashViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanDevicesViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkscan.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkspeed.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.whatsappcleaner.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.app.permission.NotificationPermissionViewModel
import com.aicleanerstorage.spaceclean.feature.settings.ManagePermissionsViewModel
import com.aicleanerstorage.spaceclean.feature.settings.SettingsViewModel
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        single { AppDispatchers() }
        viewModel { HomeViewModel(get(), get(), get()) }
        viewModel {
            NotificationPermissionViewModel(
                application = androidApplication(),
                permissionPreferences = get(),
            )
        }
        viewModel { AppUsageViewModel(get(), get<AppDispatchers>().io) }
        viewModel { DeviceInfoViewModel(get(), get(), get(), get(), get<AppDispatchers>().io) }
        viewModel { BatteryInfoViewModel(get(), get(), get(), get(), get<AppDispatchers>().io) }
        viewModel { NetworkScanViewModel(get(), get(), get<AppDispatchers>().io) }
        viewModel { NetworkScanDevicesViewModel(get(), get<AppDispatchers>().io) }
        viewModel { NetworkSpeedViewModel(get(), get(), get<AppDispatchers>().io) }
        viewModel { NetworkUsageViewModel(get(), get<AppDispatchers>().io) }
        viewModel { WhatsAppCleanerViewModel(androidApplication(), get(), get<AppDispatchers>().io) }
        viewModel {
            NotificationCleanerViewModel(
                application = androidApplication(),
                repository = get(),
                ioDispatcher = get<AppDispatchers>().io,
                settingsGateway = get(),
            )
        }
        viewModel { OnboardingScanViewModel(get(), get()) }
        viewModel { JunkCleanViewModel(get(), get(), Dispatchers.IO) }
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
        viewModel { VirusScanViewModel(androidApplication(), get(), get(), get()) }
        viewModel { ManagePermissionsViewModel() }
        viewModel { SettingsViewModel(get()) }
        viewModel {
            AppLockViewModel(
                application = androidApplication(),
                repository = get(),
                monitoringController = get(),
                ioDispatcher = Dispatchers.IO,
            )
        }
    }
