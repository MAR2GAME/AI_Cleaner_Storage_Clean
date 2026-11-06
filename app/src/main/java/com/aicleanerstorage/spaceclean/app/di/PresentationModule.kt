package com.aicleanerstorage.spaceclean.app.di

import com.aicleanerstorage.spaceclean.feature.toolbox.data.deviceinfo.ProcCpuUsageReader


import com.aicleanerstorage.spaceclean.app.AppViewModel
import com.aicleanerstorage.spaceclean.core.contracts.coroutines.AppDispatchers
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanViewModel
import com.aicleanerstorage.spaceclean.feature.applock.view.applock.presentation.AppLockViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.audios.presentation.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.documents.presentation.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.largefiles.presentation.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.presentation.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.photos.presentation.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.presentation.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.videos.presentation.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.home.view.home.presentation.HomeViewModel
import com.aicleanerstorage.spaceclean.feature.onboarding.view.scan.presentation.OnboardingScanViewModel
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.feature.startup.view.splash.presentation.SplashViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.presentation.BatteryInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanDevicesViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkspeed.presentation.NetworkSpeedViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkusage.presentation.NetworkUsageViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.presentation.WhatsAppCleanerViewModel
import com.aicleanerstorage.spaceclean.app.permission.NotificationPermissionViewModel
import com.aicleanerstorage.spaceclean.app.permission.PermissionInterceptor
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequestPort
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.app.analytics.AnalyticsEffect
import com.aicleanerstorage.spaceclean.app.analytics.AnalyticsIntentTelemetry
import com.aicleanerstorage.spaceclean.app.analytics.IntentAnalyticsMapper
import com.aicleanerstorage.spaceclean.app.analytics.IntentAnalyticsReporter
import com.aicleanerstorage.spaceclean.app.analytics.IntentPipelineEvent
import com.aicleanerstorage.spaceclean.core.contracts.ads.AdAreaKeys
import com.aicleanerstorage.spaceclean.app.commercial.InterstitialEffect
import com.aicleanerstorage.spaceclean.app.analytics.AppAnalyticsReporter
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent.AppUsageExecutorFactory
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent.AppUsageOperation
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.intent.AppUsageResult
import com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation.ManagePermissionsViewModel
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation.SettingsViewModel
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.view.notificationcleaner.presentation.NotificationCleanerViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        single { AppDispatchers() }
        viewModel { AppViewModel() }
        single { PermissionInterceptor(androidApplication(), get()) }
        single<PermissionGateway> { get<PermissionInterceptor>() }
        single<AnalyticsReporter> { AppAnalyticsReporter() }
        single {
            val telemetry = AnalyticsIntentTelemetry<AppUsageOperation, AppUsageResult>(
                reporter = IntentAnalyticsReporter { event ->
                    com.aicleanerstorage.spaceclean.app.platform.analytics.AnalyticsTracker.track(
                        event.name,
                        mapOf(
                            "request_id" to event.requestId,
                            "source" to event.source,
                            "reason" to event.reason,
                        ),
                    )
                },
                eventMapper = IntentAnalyticsMapper { name, request, reason ->
                    IntentPipelineEvent(name, request.id, request.source, reason)
                },
            )
            AppUsageExecutorFactory(
                repository = get(),
                permissions = get(),
                ui = get<UiRequestPort>(),
                telemetry = telemetry,
                effects = listOf(
                    AnalyticsEffect<AppUsageOperation, AppUsageResult>(
                        reporter = get<AnalyticsReporter>(),
                        event = { _, _ ->
                            AnalyticsEvent.FeatureOperation(
                                FeatureOperationEvent.OperationFinished(
                                    feature = FeatureKey.APP_USAGE,
                                    action = OperationAction.TEST,
                                    success = true,
                                ),
                            )
                        },
                    ),
                    InterstitialEffect<AppUsageOperation, AppUsageResult> { _, result ->
                        AdAreaKeys.Interstitial.MAIN_APP_USAGE.takeIf { result.entries.isNotEmpty() }
                    },
                ),
            )
        }
        viewModel { HomeViewModel(get(), get(), get()) }
        viewModel {
            NotificationPermissionViewModel(
                application = androidApplication(),
                permissionPreferences = get(),
            )
        }
        viewModel { AppUsageViewModel(get(), get<AppDispatchers>().io, get()) }
        viewModel { DeviceInfoViewModel(get(), get(), get(), get(), get<AppDispatchers>().io, ProcCpuUsageReader(get<AppDispatchers>().io)) }
        viewModel { BatteryInfoViewModel(get(), get(), get(), get(), get(), get<AppDispatchers>().io, ProcCpuUsageReader(get<AppDispatchers>().io)) }
        viewModel { NetworkScanViewModel(get(), get(), get<AppDispatchers>().io) }
        viewModel { NetworkScanDevicesViewModel(get(), get<AppDispatchers>().io) }
        viewModel { NetworkSpeedViewModel(get(), get(), get<AppDispatchers>().io) }
        viewModel { NetworkUsageViewModel(get(), get<AppDispatchers>().io) }
        viewModel { WhatsAppCleanerViewModel(androidApplication()::getString, get(), get<AppDispatchers>().io) }
        viewModel {
            NotificationCleanerViewModel(
                message = androidApplication()::getString,
                repository = get(),
                ioDispatcher = get<AppDispatchers>().io,
            )
        }
        viewModel { OnboardingScanViewModel(get(), get()) }
        viewModel {
            JunkCleanViewModel(
                cleanRepository = get(),
                sharedState = get(),
                permissionGateway = get(),
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
        viewModel { VirusScanViewModel( get(), get(), get()) }
        viewModel { ManagePermissionsViewModel(get()) }
        viewModel { SettingsViewModel(get()) }
        viewModel {
            AppLockViewModel(
                repository = get(),
                monitoringController = get(),
                ioDispatcher = Dispatchers.IO,
            )
        }
    }
