package com.aicleanerstorage.spaceclean.feature.files.view.shared.ui

import com.aicleanerstorage.spaceclean.core.contracts.intent.GateResult
import com.aicleanerstorage.spaceclean.feature.files.intent.FileManagerOperation
import com.aicleanerstorage.spaceclean.feature.files.intent.FileManagerPermissionGate

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerEffect

import com.aicleanerstorage.spaceclean.core.contracts.files.FileUri

import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerOperationEventsEffect
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.rememberFileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveHome
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveBackForPermissionRejected
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.leaveBack
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerErrorToastEffect
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerStartEffect
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerStopOperationDialog
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerDeleteConfirmDialog
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.FileManagerNoResultsDialog
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.rememberMediaStoreDeleteLauncher


import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType

import kotlinx.coroutines.*
import androidx.compose.runtime.rememberCoroutineScope
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionResult
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.DeleteConfirmDialog
import com.aicleanerstorage.spaceclean.core.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.core.ui.permission.rememberPermissionGranted
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import kotlinx.coroutines.flow.Flow
import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.requestMediaStoreDeleteOrDeleteDirectly


import com.aicleanerstorage.spaceclean.feature.files.view.shared.ui.NoResultsDialog


@Composable
fun FileManagerOperationEventsEffect(
    effects: Flow<FileManagerEffect>,
    onCompletionAdFinished: () -> Unit,
    featureFlow: FeatureFlow,
    permissionState: FileManagerPermissionState,
    navigator: AppNavigator,
    feature: FeatureKey,
) {
    val currentNavigator by rememberUpdatedState(navigator)
    LaunchedEffect(effects, featureFlow) {
        effects.collect { effect ->
            when (effect) {
                is FileManagerEffect.Exit -> {
                    if (effect.home) permissionState.leaveHome(currentNavigator, feature, featureFlow)
                    else if (effect.reason == FeatureExitReason.PermissionRejected)
                        permissionState.leaveBackForPermissionRejected(currentNavigator, feature, featureFlow)
                    else permissionState.leaveBack(currentNavigator, feature, featureFlow)
                    return@collect
                }
                is FileManagerEffect.Navigate -> {
                    if (!permissionState.leavingPage) {
                        if (effect.reset) { permissionState.markLeaving(); currentNavigator.resetTo(effect.destination) }
                        else currentNavigator.navigate(effect.destination)
                    }
                    return@collect
                }
                FileManagerEffect.Back -> { currentNavigator.back(); return@collect }
                is FileManagerEffect.Operation -> Unit
            }
            val event = (effect as FileManagerEffect.Operation).event
            if (!permissionState.leavingPage) {
                if (event.isFileManagerCompletionSuccess()) {
                    if (permissionState.operationJob?.isActive != true) {
                        permissionState.operationJob = permissionState.scope.launch {
                            featureFlow.handleOperation(event)
                            ensureActive()
                            if (!permissionState.leavingPage) onCompletionAdFinished()
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

val LocalFileManagerPermissionState = androidx.compose.runtime.staticCompositionLocalOf<FileManagerPermissionState?> { null }

@Composable
fun rememberFileManagerPermissionState(): FileManagerPermissionState {
    LocalFileManagerPermissionState.current?.let { return it }
    val granted = rememberPermissionGranted(PermissionType.StorageFiles)
    val scope = rememberCoroutineScope()
    val state = remember(scope) { FileManagerPermissionState(granted, scope) }
    LaunchedEffect(granted) { state.onPermissionChanged(granted) }
    return state
}

class FileManagerPermissionState(initialGranted: Boolean, val scope: CoroutineScope) {
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

fun FileManagerPermissionState.leaveHome(router: AppNavigator) {
    markLeaving()
    router.backToMain()
}

fun FileManagerPermissionState.leaveBackForPermissionRejected(
    router: AppNavigator, feature: FeatureKey, featureFlow: FeatureFlow,
) = exit(feature, FeatureExitReason.PermissionRejected, featureFlow) { router.back() }

fun FileManagerPermissionState.leaveBack(
    router: AppNavigator, feature: FeatureKey, featureFlow: FeatureFlow,
) = exit(feature, FeatureExitReason.Return, featureFlow) { router.back() }

fun FileManagerPermissionState.leaveHome(
    router: AppNavigator, feature: FeatureKey, featureFlow: FeatureFlow,
) = exit(feature, FeatureExitReason.Return, featureFlow) { router.backToMain() }

private fun FileManagerPermissionState.exit(
    feature: FeatureKey, reason: FeatureExitReason, featureFlow: FeatureFlow, navigate: () -> Unit,
) {
    if (leavingPage) return
    markLeaving()
    scope.launch { featureFlow.exit(feature, reason); ensureActive(); navigate() }
}

@Composable
fun FileManagerErrorToastEffect(
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
fun FileManagerStartEffect(
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
                when (FileManagerPermissionGate(permissionGateway).checkOperation(FileManagerOperation.LoadFiles(feature))) {
                    GateResult.Allow -> {
                        if (!permissionState.leavingPage) {
                            permissionState.onPermissionChanged(true)
                            latestStartIfNeeded()
                        }
                    }
                    is GateResult.Deny -> permissionState.exit(feature, FeatureExitReason.PermissionRejected, featureFlow) {
                        latestPermissionRejected()
                    }
                    GateResult.Busy -> Unit
                }
            }
        }
    }
}

@Composable
fun FileManagerStopOperationDialog(
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
fun FileManagerDeleteConfirmDialog(
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
                    if (FileManagerPermissionGate(permissionGateway).checkOperation(FileManagerOperation.DeleteFiles(emptyList())) == GateResult.Allow) {
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
fun FileManagerNoResultsDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    onBack: () -> Unit,
) {
    if (permissionGranted && visible) {
        NoResultsDialog(onBack = onBack)
    }
}

@Composable
fun rememberMediaStoreDeleteLauncher(
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
