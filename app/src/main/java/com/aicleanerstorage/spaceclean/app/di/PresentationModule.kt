package com.aicleanerstorage.spaceclean.app.di

import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.spaceclean.app.background.AppRuntimeService
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanViewModel
import com.aicleanerstorage.spaceclean.app.view.applock.interaction.AppLockViewModel
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageViewModel
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListIconKind
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileListManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.MediaManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeViewModel
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.app.view.networkusage.interaction.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerViewModel
import com.aicleanerstorage.spaceclean.app.view.onboarding.interaction.OnboardingScanViewModel
import com.aicleanerstorage.spaceclean.app.view.permissions.interaction.ManagePermissionsViewModel
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.screenshots.interaction.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.settings.interaction.SettingsViewModel
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashViewModel
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.applock.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.settings.port.SettingsRepository
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val presentationModule =
    module {
        viewModel { HomeViewModel(get(), get(), get()) }
        viewModel { AppUsageViewModel(get(), Dispatchers.IO) }
        viewModel { DeviceInfoViewModel(get(), get(), get(), get(), Dispatchers.IO) }
        viewModel { BatteryInfoViewModel(get(), get(), get(), get(), get(), Dispatchers.IO) }
        viewModel { NetworkScanViewModel(get(), Dispatchers.IO) }
        viewModel { NetworkSpeedViewModel(get(), Dispatchers.IO) }
        viewModel { NetworkUsageViewModel(get(), Dispatchers.IO) }
        viewModel { WhatsAppCleanerViewModel(get(), Dispatchers.IO) }
        viewModel {
            NotificationCleanerViewModel(
                repository = get(),
                ioDispatcher = Dispatchers.IO,
            )
        }
        viewModel { OnboardingScanViewModel(get(), get()) }
        viewModel {
            JunkCleanViewModel(
                cleanRepository = get(),
                ioDispatcher = Dispatchers.IO,
            )
        }
        viewModel { SplashViewModel(get()) }
        viewModel { PhotosManagerViewModel(get(), Dispatchers.IO) }
        viewModel { ScreenshotsManagerViewModel(get(), Dispatchers.IO) }
        viewModel(named<AppDestination.VideosManager>()) {
            MediaManagerViewModel(
                repository = get(),
                loadFiles = FileRepository::loadVideos,
                defaultTabs = listOf("All", "DCIM", "Download"),
                detailDestination = { AppDestination.VideosDetail(it) },
                ioDispatcher = Dispatchers.IO,
            )
        }
        viewModel(named<AppDestination.AudiosManager>()) {
            MediaManagerViewModel(
                repository = get(),
                loadFiles = FileRepository::loadAudios,
                defaultTabs = listOf("All", "Music"),
                detailDestination = { AppDestination.AudiosDetail(it) },
                ioDispatcher = Dispatchers.IO,
            )
        }
        viewModel { SimilarPhotosManagerViewModel(get(), Dispatchers.IO) }
        viewModel { PhotoPrivacyManagerViewModel(get(), Dispatchers.IO) }
        viewModel(named<AppDestination.LargeFilesManager>()) {
            FileListManagerViewModel(
                repository = get(),
                loadFiles = FileRepository::loadLargeFiles,
                iconKind = FileListIconKind.LargeVideo,
                detailDestination = { AppDestination.LargeFilesDetail(it) },
                ioDispatcher = Dispatchers.IO,
            )
        }
        viewModel(named<AppDestination.DocumentsManager>()) {
            FileListManagerViewModel(
                repository = get(),
                loadFiles = FileRepository::loadDocuments,
                iconKind = FileListIconKind.Document,
                detailDestination = { AppDestination.DocumentsDetail(it) },
                ioDispatcher = Dispatchers.IO,
            )
        }
        viewModel { DuplicateFilesManagerViewModel(get(), get(), Dispatchers.IO) }
        viewModel { VirusScanViewModel(get(), get()) }
        viewModel { ManagePermissionsViewModel() }
        viewModel { SettingsViewModel(get()) }
        viewModel {
            val repository = get<AppLockRepository>()
            val settingsRepository = get<SettingsRepository>()
            val context = androidContext()
            AppLockViewModel(
                repository = repository,
                syncMonitoringService = { AppRuntimeService.reconcile(context, repository, settingsRepository) },
                ioDispatcher = Dispatchers.IO,
            )
        }
    }
