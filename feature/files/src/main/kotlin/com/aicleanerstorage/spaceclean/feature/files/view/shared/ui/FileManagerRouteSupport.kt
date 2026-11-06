package com.aicleanerstorage.spaceclean.feature.files.view.shared.ui


import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerEffect

import com.aicleanerstorage.spaceclean.core.contracts.files.FileUri

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
import com.aicleanerstorage.spaceclean.core.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult
import com.aicleanerstorage.spaceclean.core.presentation.intent.UiRequestBroker
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import kotlinx.coroutines.flow.Flow
import org.koin.compose.koinInject

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
            if (permissionState.leavingPage) return@collect
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

class FileManagerPermissionState(initialGranted: Boolean, val scope: CoroutineScope) {
    var granted by mutableStateOf(initialGranted)
        private set
    var leavingPage by mutableStateOf(false)
        private set
    var operationJob: Job? = null
    var uiRequestJob: Job? = null
    fun activate() { leavingPage = false }
    fun onPermissionChanged(value: Boolean) { granted = value }
    fun markLeaving() {
        leavingPage = true
        operationJob?.cancel()
        uiRequestJob?.cancel()
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
    permissionState: FileManagerPermissionState,
    onStartIfNeeded: () -> Unit,
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
    ownerState: FileManagerPermissionState,
    onCancel: () -> Unit,
    onDeleteReady: () -> Unit,
    onRejected: () -> Unit,
) {
    if (permissionGranted && visible) {
        val broker: UiRequestBroker = koinInject()
        val scope = rememberCoroutineScope()
        var requestJob by remember { mutableStateOf<Job?>(null) }
        val ready by rememberUpdatedState(onDeleteReady)
        val rejected by rememberUpdatedState(onRejected)
        DeleteConfirmDialog(
            onCancel = onCancel,
            onDelete = {
                if (ownerState.leavingPage || requestJob?.isActive == true) return@DeleteConfirmDialog
                requestJob = scope.launch {
                    val mediaUris = selectedUris.map { android.net.Uri.parse(it.value) }.filter {
                        it.scheme == "content" && it.authority == "media" &&
                            runCatching { android.content.ContentUris.parseId(it); true }.getOrDefault(false)
                    }
                    if (android.os.Build.VERSION.SDK_INT < 30 || mediaUris.isEmpty()) {
                        ready()
                        return@launch
                    }
                    val result = broker.request(
                        UiRequest.MediaStoreDelete(
                            requestId = UiRequestBroker.newRequestId(),
                            uris = mediaUris.map { it.toString() },
                        ),
                    )
                    ensureActive()
                    if (!ownerState.leavingPage) {
                        if (result == UiResult.Confirmed) ready() else rejected()
                    }
                }
                ownerState.uiRequestJob = requestJob
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
