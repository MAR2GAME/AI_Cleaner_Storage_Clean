package com.aicleanerstorage.spaceclean.app.navigation

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.app.runtime.AppRuntimeBindings
import com.aicleanerstorage.spaceclean.core.ui.permission.LocalPermissionGateway
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.*
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerFlowState
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanViewModel
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.VirusEffectHandler
import com.aicleanerstorage.spaceclean.feature.files.view.photos.presentation.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.presentation.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.videos.presentation.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.audios.presentation.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.largefiles.presentation.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.documents.presentation.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.presentation.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateFilesManagerViewModel

@Composable
internal fun FeatureFlowOwners(
    entry: NavBackStackEntry?,
    navController: NavHostController,
    navigator: NavHostAppNavigator,
    runtime: AppRuntimeBindings,
    content: @Composable () -> Unit,
) {
    val owner = if (entry == null) null else when {
        entry.destination.hierarchy.any { it.hasRoute<PhotosFlow>() } -> navController.getBackStackEntry<PhotosFlow>()
        entry.destination.hierarchy.any { it.hasRoute<SimilarPhotosFlow>() } -> navController.getBackStackEntry<SimilarPhotosFlow>()
        entry.destination.hierarchy.any { it.hasRoute<ScreenshotsFlow>() } -> navController.getBackStackEntry<ScreenshotsFlow>()
        entry.destination.hierarchy.any { it.hasRoute<VideosFlow>() } -> navController.getBackStackEntry<VideosFlow>()
        entry.destination.hierarchy.any { it.hasRoute<AudiosFlow>() } -> navController.getBackStackEntry<AudiosFlow>()
        entry.destination.hierarchy.any { it.hasRoute<LargeFilesFlow>() } -> navController.getBackStackEntry<LargeFilesFlow>()
        entry.destination.hierarchy.any { it.hasRoute<DocumentsFlow>() } -> navController.getBackStackEntry<DocumentsFlow>()
        entry.destination.hierarchy.any { it.hasRoute<AntiVirusFlow>() } -> navController.getBackStackEntry<AntiVirusFlow>()
        entry.destination.hasRoute<AppDestination.PhotoPrivacyManager>() -> entry
        entry.destination.hasRoute<AppDestination.DuplicateFilesManager>() -> entry
        else -> null
    }
    val permissionState = key(owner?.id) { rememberFileManagerPermissionState() }
    if (owner != null && entry != null) {
        key(owner.id) {
            val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
            when {
                owner.destination.hasRoute<PhotosFlow>() -> {
                    val vm = koinViewModel<PhotosManagerViewModel>(viewModelStoreOwner = owner)
                    val state by vm.state.collectAsStateWithLifecycle()
                    FileManagerFlowEffects(
                        effects = vm.effects,
                        navigator = pageNavigator,
                        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
                        featureFlow = runtime.featureFlow,
                        permissionGateway = LocalPermissionGateway.current,
                        feature = FeatureKey.PHOTOS,
                        permissionState = permissionState,
                        errorMessage = state.errorMessage,
                        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
                        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
                        onPermissionRejected = { pageNavigator.back() },
                        flowState = FileManagerFlowState(state.showStopDialog, state.blockedPhase),
                    )
                }
                owner.destination.hasRoute<SimilarPhotosFlow>() -> {
                    val vm = koinViewModel<SimilarPhotosManagerViewModel>(viewModelStoreOwner = owner)
                    val state by vm.state.collectAsStateWithLifecycle()
                    FileManagerFlowEffects(
                        effects = vm.effects,
                        navigator = pageNavigator,
                        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
                        featureFlow = runtime.featureFlow,
                        permissionGateway = LocalPermissionGateway.current,
                        feature = FeatureKey.SIMILAR_PHOTOS,
                        permissionState = permissionState,
                        errorMessage = state.errorMessage,
                        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
                        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
                        onPermissionRejected = { pageNavigator.back() },
                        flowState = FileManagerFlowState(state.showStopDialog, state.blockedPhase),
                    )
                }
                owner.destination.hasRoute<ScreenshotsFlow>() -> {
                    val vm = koinViewModel<ScreenshotsManagerViewModel>(viewModelStoreOwner = owner)
                    val state by vm.state.collectAsStateWithLifecycle()
                    FileManagerFlowEffects(
                        effects = vm.effects,
                        navigator = pageNavigator,
                        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
                        featureFlow = runtime.featureFlow,
                        permissionGateway = LocalPermissionGateway.current,
                        feature = FeatureKey.SCREENSHOTS,
                        permissionState = permissionState,
                        errorMessage = state.errorMessage,
                        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
                        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
                        onPermissionRejected = { pageNavigator.back() },
                        flowState = FileManagerFlowState(state.showStopDialog, state.blockedPhase),
                    )
                }
                owner.destination.hasRoute<VideosFlow>() -> {
                    val vm = koinViewModel<VideosManagerViewModel>(viewModelStoreOwner = owner)
                    val state by vm.state.collectAsStateWithLifecycle()
                    FileManagerFlowEffects(
                        effects = vm.effects,
                        navigator = pageNavigator,
                        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
                        featureFlow = runtime.featureFlow,
                        permissionGateway = LocalPermissionGateway.current,
                        feature = FeatureKey.VIDEOS,
                        permissionState = permissionState,
                        errorMessage = state.errorMessage,
                        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
                        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
                        onPermissionRejected = { pageNavigator.back() },
                        flowState = FileManagerFlowState(state.showStopDialog, state.blockedPhase),
                    )
                }
                owner.destination.hasRoute<AudiosFlow>() -> {
                    val vm = koinViewModel<AudiosManagerViewModel>(viewModelStoreOwner = owner)
                    val state by vm.state.collectAsStateWithLifecycle()
                    FileManagerFlowEffects(
                        effects = vm.effects,
                        navigator = pageNavigator,
                        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
                        featureFlow = runtime.featureFlow,
                        permissionGateway = LocalPermissionGateway.current,
                        feature = FeatureKey.AUDIOS,
                        permissionState = permissionState,
                        errorMessage = state.errorMessage,
                        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
                        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
                        onPermissionRejected = { pageNavigator.back() },
                        flowState = FileManagerFlowState(state.showStopDialog, state.blockedPhase),
                    )
                }
                owner.destination.hasRoute<LargeFilesFlow>() -> {
                    val vm = koinViewModel<LargeFilesManagerViewModel>(viewModelStoreOwner = owner)
                    val state by vm.state.collectAsStateWithLifecycle()
                    FileManagerFlowEffects(
                        effects = vm.effects,
                        navigator = pageNavigator,
                        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
                        featureFlow = runtime.featureFlow,
                        permissionGateway = LocalPermissionGateway.current,
                        feature = FeatureKey.LARGE_FILES,
                        permissionState = permissionState,
                        errorMessage = state.errorMessage,
                        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
                        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
                        onPermissionRejected = { pageNavigator.back() },
                        flowState = FileManagerFlowState(state.showStopDialog, state.blockedPhase),
                    )
                }
                owner.destination.hasRoute<DocumentsFlow>() -> {
                    val vm = koinViewModel<DocumentsManagerViewModel>(viewModelStoreOwner = owner)
                    val state by vm.state.collectAsStateWithLifecycle()
                    FileManagerFlowEffects(
                        effects = vm.effects,
                        navigator = pageNavigator,
                        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
                        featureFlow = runtime.featureFlow,
                        permissionGateway = LocalPermissionGateway.current,
                        feature = FeatureKey.DOCUMENTS,
                        permissionState = permissionState,
                        errorMessage = state.errorMessage,
                        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
                        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
                        onPermissionRejected = { pageNavigator.back() },
                        flowState = FileManagerFlowState(state.showStopDialog, state.blockedPhase),
                    )
                }
                owner.destination.hasRoute<AppDestination.PhotoPrivacyManager>() -> {
                    val vm = koinViewModel<PhotoPrivacyManagerViewModel>(viewModelStoreOwner = owner)
                    val state by vm.state.collectAsStateWithLifecycle()
                    FileManagerFlowEffects(
                        effects = vm.effects,
                        navigator = pageNavigator,
                        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
                        featureFlow = runtime.featureFlow,
                        permissionGateway = LocalPermissionGateway.current,
                        feature = FeatureKey.PHOTO_PRIVACY,
                        permissionState = permissionState,
                        errorMessage = state.errorMessage,
                        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
                        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
                        onPermissionRejected = { pageNavigator.back() },
                        flowState = FileManagerFlowState(state.showStopDialog, state.blockedPhase),
                    )
                }
                owner.destination.hasRoute<AppDestination.DuplicateFilesManager>() -> {
                    val vm = koinViewModel<DuplicateFilesManagerViewModel>(viewModelStoreOwner = owner)
                    val state by vm.state.collectAsStateWithLifecycle()
                    FileManagerFlowEffects(
                        effects = vm.effects,
                        navigator = pageNavigator,
                        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
                        featureFlow = runtime.featureFlow,
                        permissionGateway = LocalPermissionGateway.current,
                        feature = FeatureKey.DUPLICATE_FILES,
                        permissionState = permissionState,
                        errorMessage = state.errorMessage,
                        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
                        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
                        onPermissionRejected = { pageNavigator.back() },
                        flowState = FileManagerFlowState(state.showStopDialog, state.blockedPhase),
                    )
                }
                owner.destination.hasRoute<AntiVirusFlow>() -> {
                    VirusEffectHandler(pageNavigator,
                        koinViewModel<VirusScanViewModel>(viewModelStoreOwner = owner),
                        runtime.featureFlow, runtime.externalActivities)
                }
            }
        }
    }
    CompositionLocalProvider(LocalFileManagerPermissionState provides permissionState, content = content)
}
