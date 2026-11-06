package com.aicleanerstorage.spaceclean.app.navigation.owner

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType
import com.aicleanerstorage.spaceclean.feature.files.intent.FileManagerMediaType
import org.koin.compose.koinInject
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.*
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerEffect
import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.photos.presentation.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.similarphotos.presentation.SimilarPhotosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.screenshots.presentation.ScreenshotsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.videos.presentation.VideosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.audios.presentation.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.largefiles.presentation.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.documents.presentation.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.photoprivacy.presentation.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.view.duplicates.presentation.DuplicateFilesManagerViewModel

internal class FileManagerOwnerState(permissions: PermissionGateway) : ViewModel() {
    val permissionState = FileManagerPermissionState(
        permissions.isGranted(PermissionType.StorageFiles), viewModelScope,
    )
    override fun onCleared() {
        permissionState.markLeaving()
        super.onCleared()
    }
}

@Composable
internal fun rememberFileManagerOwnerState(
    ownerEntry: NavBackStackEntry,
    permissions: PermissionGateway = koinInject(),
): FileManagerPermissionState {
    val factory = remember(permissions) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = FileManagerOwnerState(permissions) as T
        }
    }
    val holder: FileManagerOwnerState = viewModel(viewModelStoreOwner = ownerEntry, factory = factory)
    return holder.permissionState
}

@Composable
internal fun FileManagerRouteOwner(
    owner: FeatureOwner.FileManager,
    entry: NavBackStackEntry,
    navigator: NavHostAppNavigator,
    featureFlow: FeatureFlow,
    permissions: PermissionGateway = koinInject(),
) {
    val permissionState = rememberFileManagerOwnerState(owner.ownerEntry, permissions)
    DisposableEffect(permissionState) {
        permissionState.activate()
        onDispose { permissionState.markLeaving() }
    }
    val granted = com.aicleanerstorage.spaceclean.core.ui.permission.rememberPermissionGranted(
        PermissionType.StorageFiles, permissions,
    )
    LaunchedEffect(granted) { permissionState.onPermissionChanged(granted) }
    val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
    val binding = when (owner.mediaType) {
        FileManagerMediaType.Photos -> bind(
            koinViewModel<PhotosManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), FeatureKey.PHOTOS,
        ) { it.errorMessage }
        FileManagerMediaType.SimilarPhotos -> bind(
            koinViewModel<SimilarPhotosManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), FeatureKey.SIMILAR_PHOTOS,
        ) { it.errorMessage }
        FileManagerMediaType.Screenshots -> bind(
            koinViewModel<ScreenshotsManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), FeatureKey.SCREENSHOTS,
        ) { it.errorMessage }
        FileManagerMediaType.Videos -> bind(
            koinViewModel<VideosManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), FeatureKey.VIDEOS,
        ) { it.errorMessage }
        FileManagerMediaType.Audios -> bind(
            koinViewModel<AudiosManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), FeatureKey.AUDIOS,
        ) { it.errorMessage }
        FileManagerMediaType.LargeFiles -> bind(
            koinViewModel<LargeFilesManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), FeatureKey.LARGE_FILES,
        ) { it.errorMessage }
        FileManagerMediaType.Documents -> bind(
            koinViewModel<DocumentsManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), FeatureKey.DOCUMENTS,
        ) { it.errorMessage }
        FileManagerMediaType.PhotoPrivacy -> bind(
            koinViewModel<PhotoPrivacyManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), FeatureKey.PHOTO_PRIVACY,
        ) { it.errorMessage }
        FileManagerMediaType.DuplicateFiles -> bind(
            koinViewModel<DuplicateFilesManagerViewModel>(viewModelStoreOwner = owner.ownerEntry), FeatureKey.DUPLICATE_FILES,
        ) { it.errorMessage }
    }
    val vm = binding.viewModel
    DisposableEffect(vm, permissionState) {
        onDispose {
            permissionState.markLeaving()
            vm.dispatch(FileManagerIntent.CancelActiveOperation)
        }
    }
    FileManagerFlowEffects(
        effects = vm.effects,
        navigator = pageNavigator,
        onCompletionAdFinished = { vm.dispatch(FileManagerIntent.CompletionAdFinished) },
        featureFlow = featureFlow,
        feature = binding.feature,
        permissionState = permissionState,
        errorMessage = binding.errorMessage,
        onClearError = { vm.dispatch(FileManagerIntent.ClearError) },
        onStartIfNeeded = { vm.dispatch(FileManagerIntent.StartIfNeeded) },
    )
}

private data class FileManagerBinding(
    val viewModel: MviViewModel<*, FileManagerIntent, FileManagerEffect>,
    val feature: FeatureKey,
    val errorMessage: String?,
)

@Composable
private fun <S : Any> bind(
    viewModel: MviViewModel<S, FileManagerIntent, FileManagerEffect>,
    feature: FeatureKey,
    errorMessage: (S) -> String?,
): FileManagerBinding {
    val state by viewModel.state.collectAsStateWithLifecycle()
    return FileManagerBinding(viewModel, feature, errorMessage(state))
}
