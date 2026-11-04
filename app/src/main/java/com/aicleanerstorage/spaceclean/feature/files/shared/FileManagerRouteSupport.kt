package com.aicleanerstorage.spaceclean.feature.files.shared

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import com.aicleanerstorage.spaceclean.feature.files.shared.*

import com.aicleanerstorage.spaceclean.common.permission.PermissionType

import kotlinx.coroutines.*
import androidx.compose.runtime.rememberCoroutineScope
import com.aicleanerstorage.spaceclean.common.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult
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
import com.aicleanerstorage.spaceclean.common.permission.PermissionGateway
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
    permissionState: FileManagerPermissionState,
) {
    LaunchedEffect(viewModel, featureFlow) {
        viewModel.operationEvents.collect { event ->
            if (!permissionState.leavingPage) {
                if (event.isFileManagerCompletionSuccess()) {
                    if (permissionState.operationJob?.isActive != true) {
                        permissionState.operationJob = permissionState.scope.launch {
                            featureFlow.handleOperation(event)
                            ensureActive()
                            if (!permissionState.leavingPage) viewModel.showPendingResult()
                        }
                    }
                } else featureFlow.recordOperation(event)
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
internal fun rememberFileManagerPermissionState(): FileManagerPermissionState {
    val granted = rememberPermissionGranted(PermissionType.StorageFiles)
    val scope = rememberCoroutineScope()
    val state = remember(scope) { FileManagerPermissionState(granted, scope) }
    LaunchedEffect(granted) { state.onPermissionChanged(granted) }
    return state
}

internal class FileManagerPermissionState(initialGranted: Boolean, val scope: CoroutineScope) {
    var granted by mutableStateOf(initialGranted)
        private set
    var leavingPage by mutableStateOf(false)
        private set
    var operationJob: Job? = null
    var permissionJob: Job? = null
    fun onPermissionChanged(value: Boolean) { granted = value }
    fun markLeaving() {
        leavingPage = true
        operationJob?.cancel()
        permissionJob?.cancel()
    }
}

internal fun FileManagerPermissionState.leaveHome(router: AppNavigator) {
    markLeaving()
    router.home()
}

internal fun FileManagerPermissionState.leaveBackForPermissionRejected(
    router: AppNavigator, feature: FeatureKey, featureFlow: FeatureFlow,
) = exit(feature, FeatureExitReason.PermissionRejected, featureFlow) { router.back() }

internal fun FileManagerPermissionState.leaveBack(
    router: AppNavigator, feature: FeatureKey, featureFlow: FeatureFlow,
) = exit(feature, FeatureExitReason.Return, featureFlow) { router.back() }

internal fun FileManagerPermissionState.leaveHome(
    router: AppNavigator, feature: FeatureKey, featureFlow: FeatureFlow,
) = exit(feature, FeatureExitReason.Return, featureFlow) { router.home() }

private fun FileManagerPermissionState.exit(
    feature: FeatureKey, reason: FeatureExitReason, featureFlow: FeatureFlow, navigate: () -> Unit,
) {
    if (leavingPage) return
    markLeaving()
    scope.launch { featureFlow.exit(feature, reason); ensureActive(); navigate() }
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
    permissionGateway: PermissionGateway,
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
        if (permissionState.permissionJob?.isActive != true) {
            permissionState.permissionJob = permissionState.scope.launch {
                when (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.FileManagerLoadFiles))) {
                    PermissionResult.Granted -> {
                        if (!permissionState.leavingPage) {
                            permissionState.onPermissionChanged(true)
                            latestStartIfNeeded()
                        }
                    }
                    PermissionResult.Denied -> permissionState.exit(feature, FeatureExitReason.PermissionRejected, featureFlow) {
                        latestPermissionRejected()
                    }
                    PermissionResult.Busy -> Unit
                }
            }
        }
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
    permissionGateway: PermissionGateway,
    onCancel: () -> Unit,
    onBeforeDeleteRequest: () -> Unit = {},
    onDeleteReady: () -> Unit,
    onRejected: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var permissionJob by remember { mutableStateOf<Job?>(null) }
    val context = LocalContext.current
    val (launchDeleteRequest, deleteDirectly) = rememberMediaStoreDeleteLauncher(
        onConfirmed = onDeleteReady,
        onRejected = onRejected,
    )
    if (permissionGranted && visible) {
        DeleteConfirmDialog(
            onCancel = onCancel,
            onDelete = {
                if (permissionJob?.isActive != true) permissionJob = scope.launch {
                    if (permissionGateway.ensure(PermissionTarget.Action(ProtectedAction.FileManagerDeleteFiles)) == PermissionResult.Granted) {
                        ensureActive()
                        onBeforeDeleteRequest()
                        requestMediaStoreDeleteOrDeleteDirectly(
                            context = context,
                            uris = selectedUris,
                            launchRequest = launchDeleteRequest,
                            deleteDirectly = deleteDirectly,
                        )
                    }
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
