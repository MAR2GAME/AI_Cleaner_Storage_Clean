package com.aicleanerstorage.spaceclean.feature.files.view.shared.ui


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


import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType

import kotlinx.coroutines.*
import androidx.compose.runtime.rememberCoroutineScope
import android.widget.Toast
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
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult
import com.aicleanerstorage.spaceclean.core.presentation.intent.UiRequestBroker
import com.aicleanerstorage.spaceclean.core.ui.permission.rememberPermissionGranted
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject


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
    val permissionGateway: com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionGateway = koinInject()
    val granted = rememberPermissionGranted(PermissionType.StorageFiles, permissionGateway)
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
    featureFlow: FeatureFlow,
    onStartIfNeeded: () -> Unit,
    onPermissionRejected: () -> Unit,
) {
    val latestStartIfNeeded by rememberUpdatedState(onStartIfNeeded)
    LaunchedEffect(permissionState.leavingPage) {
        if (permissionState.leavingPage) return@LaunchedEffect
        latestStartIfNeeded()
    }
}

@Composable
fun FileManagerStopOperationDialog(
    visible: Boolean,
    permissionGranted: Boolean,
    onQuit: () -> Unit,
    onResume: () -> Unit,
) {
    val broker: UiRequestBroker = koinInject()
    val scope = rememberCoroutineScope()
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
    onCancel: () -> Unit,
    onBeforeDeleteRequest: () -> Unit = {},
    onDeleteReady: () -> Unit,
    onRejected: () -> Unit,
) {
    if (permissionGranted && visible) {
        DeleteConfirmDialog(
            onCancel = onCancel,
            onDelete = {
                onBeforeDeleteRequest()
                scope.launch {
                    val result = broker.request(
                        UiRequest.MediaStoreDelete(
                            requestId = UiRequestBroker.newRequestId(),
                            uris = selectedUris.map { it.value },
                        ),
                    )
                    if (result == UiResult.Confirmed) onDeleteReady() else onRejected()
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
