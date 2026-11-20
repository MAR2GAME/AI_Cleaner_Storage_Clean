package com.aicleanerstorage.spaceclean.app.view.junkclean.ui

import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanAction
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkExitTarget

import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.ensureActive
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.rememberMediaStoreDeleteRequest
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate

@Composable
fun JunkCleanRoute(
    viewModel: JunkCleanViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateHomeAfterComplete: () -> Unit = onNavigateHome,
    resultTools: @Composable (modifier: Modifier, enabled: Boolean) -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val deleteRequest = rememberMediaStoreDeleteRequest()
    var exitStarted by remember(viewModel) { mutableStateOf(false) }
    val permissionGate = rememberPermissionGate()

    fun dispatchWithPermission(intent: JunkCleanAction) {
        val needsPermission =
            intent == JunkCleanAction.ScanRequested || intent == JunkCleanAction.CleanClicked
        if (needsPermission) {
            permissionGate.allow(AppPermission.StorageFiles) { viewModel.dispatch(intent) }
        } else {
            viewModel.dispatch(intent)
        }
    }
    val routeScope = rememberCoroutineScope()
    var authorizationJob by remember(viewModel) { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    fun exitOnce(onContinue: () -> Unit) {
        if (exitStarted) return
        exitStarted = true
        authorizationJob?.cancel()
        routeScope.launch {
            ensureActive()
            onContinue()
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.dispatch(JunkCleanAction.Entered)
    }

    LaunchedEffect(viewModel, deleteRequest) {
        collectJunkCleanEvents(
            effects = viewModel.event,
            currentPhase = { viewModel.state.value.phase },
            isExiting = { exitStarted },
            onExit = { effect ->
                exitOnce {
                    when (effect.target) {
                        JunkExitTarget.Back -> onNavigateBack()
                        JunkExitTarget.HomeAfterComplete -> onNavigateHomeAfterComplete()
                    }
                }
            },
            onDeleteAuthorization = { effect ->
                if (authorizationJob?.isActive == true) return@collectJunkCleanEvents
                authorizationJob = routeScope.launch {
                    val confirmed = deleteRequest(effect.uris)
                    ensureActive()
                    if (exitStarted) return@launch
                    viewModel.dispatch(JunkCleanAction.DeleteAuthorizationResultReceived(confirmed))
                }
            },
        )
    }

    JunkCleanScreen(
        state = uiState,
        interactionsEnabled = !exitStarted,
        resultTools = resultTools,
        onAction = { intent ->
            if (!exitStarted) dispatchWithPermission(intent)
        },
    )
}
