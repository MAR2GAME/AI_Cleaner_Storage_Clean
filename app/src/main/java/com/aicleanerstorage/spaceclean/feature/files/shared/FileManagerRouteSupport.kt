package com.aicleanerstorage.spaceclean.feature.files.shared

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import com.aicleanerstorage.spaceclean.common.permission.PermissionType

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.DeleteConfirmDialog
import com.aicleanerstorage.spaceclean.common.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.common.permission.ProtectedAction
import com.aicleanerstorage.spaceclean.common.permission.AppPermissionCoordinator
import com.aicleanerstorage.spaceclean.common.permission.ui.rememberPermissionGranted
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.feature.files.shared.BaseFileManagerViewModel
import com.aicleanerstorage.spaceclean.feature.files.shared.FileUri
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerPageBrush
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.requestMediaStoreDeleteOrDeleteDirectly


import com.aicleanerstorage.spaceclean.feature.files.shared.ui.*

@Composable
internal fun FileManagerOperationEventsEffect(
    viewModel: BaseFileManagerViewModel,
    featureFlow: FeatureFlow,
) {
    LaunchedEffect(viewModel, featureFlow) {
        viewModel.operationEvents.collect { event ->
            if (event.isFileManagerCompletionSuccess()) {
                featureFlow.handleOperation(event) {
                    viewModel.showPendingResult()
                }
            } else {
                featureFlow.handleOperation(event)
            }
        }
    }
}

private val FileManagerFeatureKeys =
    setOf(
        FeatureKey.PHOTOS,
        FeatureKey.SIMILAR_PHOTOS,
        FeatureKey.PHOTO_PRIVACY,
        FeatureKey.SCREENSHOTS,
        FeatureKey.VIDEOS,
        FeatureKey.AUDIOS,
        FeatureKey.LARGE_FILES,
        FeatureKey.DUPLICATE_FILES,
        FeatureKey.DOCUMENTS,
    )

private fun FeatureOperationEvent.isFileManagerCompletionSuccess(): Boolean =
    this is FeatureOperationEvent.OperationFinished &&
        feature in FileManagerFeatureKeys &&
        success

@Composable
internal fun rememberFileManagerPermissionState(
): FileManagerPermissionState {
    val observedPermissionGranted = rememberPermissionGranted(PermissionType.StorageFiles)
    var permissionGranted by remember {
        mutableStateOf(observedPermissionGranted)
    }
    var isLeavingPage by remember { mutableStateOf(false) }

    LaunchedEffect(observedPermissionGranted) {
        permissionGranted = observedPermissionGranted
    }

    return FileManagerPermissionState(
        granted = permissionGranted,
        leavingPage = isLeavingPage,
        markLeaving = { isLeavingPage = true },
        onPermissionChanged = { permissionGranted = it },
    )
}

internal class FileManagerPermissionState(
    val granted: Boolean,
    val leavingPage: Boolean,
    val markLeaving: () -> Unit,
    val onPermissionChanged: (Boolean) -> Unit,
)

internal fun FileManagerPermissionState.leaveHome(router: AppNavigator) {
    markLeaving()
    router.home()
}

internal fun FileManagerPermissionState.leaveBackForPermissionRejected(
    router: AppNavigator,
    feature: FeatureKey,
    featureFlow: FeatureFlow,
) {
    markLeaving()
    featureFlow.exit(feature, FeatureExitReason.PermissionRejected) {
        router.back()
    }
}

internal fun FileManagerPermissionState.leaveBack(
    router: AppNavigator,
    feature: FeatureKey,
    featureFlow: FeatureFlow,
) {
    markLeaving()
    featureFlow.exit(feature, FeatureExitReason.Return) {
        router.back()
    }
}

internal fun FileManagerPermissionState.leaveHome(
    router: AppNavigator,
    feature: FeatureKey,
    featureFlow: FeatureFlow,
) {
    markLeaving()
    featureFlow.exit(feature, FeatureExitReason.Return) {
        router.home()
    }
}


@Composable
internal fun FileManagerErrorToastEffect(
    errorMessage: String?,
    onConsumed: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(errorMessage) {
        val message = errorMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        onConsumed()
    }
}

@Composable
internal fun FileManagerStartEffect(
    feature: FeatureKey,
    permissionState: FileManagerPermissionState,
    permissionCoordinator: AppPermissionCoordinator,
    featureFlow: FeatureFlow,
    onStartIfNeeded: () -> Unit,
    onPermissionRejected: () -> Unit,
) {
    val latestStartIfNeeded by rememberUpdatedState(onStartIfNeeded)
    val latestPermissionRejected by rememberUpdatedState(onPermissionRejected)
    LaunchedEffect(permissionState.granted, permissionState.leavingPage) {
        if (permissionState.leavingPage) return@LaunchedEffect
        if (permissionState.granted) {
            latestStartIfNeeded()
            return@LaunchedEffect
        }
        permissionCoordinator.ensure(
            action = ProtectedAction.FileManagerLoadFiles,
            onGranted = {
                permissionState.onPermissionChanged(true)
                latestStartIfNeeded()
            },
            onDenied = {
                featureFlow.exit(feature, FeatureExitReason.PermissionRejected) {
                    latestPermissionRejected()
                }
            },
        )
    }
}

@Composable
internal fun FileManagerStopOperationDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    onQuit: () -> Unit,
    onResume: () -> Unit,
) {
    if (permissionGranted && visible) {
        StopScanDialog(
            onQuit = onQuit,
            onResume = onResume,
        )
    }
}

@Composable
internal fun FileManagerDeleteConfirmDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    selectedUris: List<FileUri>,
    permissionCoordinator: AppPermissionCoordinator,
    onCancel: () -> Unit,
    onBeforeDeleteRequest: () -> Unit = {},
    onDeleteReady: () -> Unit,
    onRejected: () -> Unit,
) {
    val context = LocalContext.current
    val (launchDeleteRequest, deleteDirectly) = rememberMediaStoreDeleteLauncher(
        onConfirmed = onDeleteReady,
        onRejected = onRejected,
    )
    if (permissionGranted && visible) {
        DeleteConfirmDialog(
            onCancel = onCancel,
            onDelete = {
                permissionCoordinator.ensure(ProtectedAction.FileManagerDeleteFiles) {
                    onBeforeDeleteRequest()
                    requestMediaStoreDeleteOrDeleteDirectly(
                        context = context,
                        uris = selectedUris,
                        launchRequest = launchDeleteRequest,
                        deleteDirectly = deleteDirectly,
                    )
                }
            },
        )
    }
}

@Composable
internal fun FileManagerNoResultsDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    onBack: () -> Unit,
) {
    if (permissionGranted && visible) {
        NoResultsDialog(onBack = onBack)
    }
}

@Composable
internal fun rememberMediaStoreDeleteLauncher(
    onConfirmed: () -> Unit,
    onRejected: () -> Unit,
): Pair<(IntentSenderRequest) -> Unit, () -> Unit> {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onConfirmed()
        } else {
            onRejected()
        }
    }
    return ({ request: IntentSenderRequest -> launcher.launch(request) }) to onConfirmed
}
