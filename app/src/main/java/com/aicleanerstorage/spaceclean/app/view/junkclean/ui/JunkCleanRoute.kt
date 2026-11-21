package com.aicleanerstorage.spaceclean.app.view.junkclean.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.cleanerkit.CleanerKit
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanAction
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanPhase
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkExitTarget
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkOperation
import com.aicleanerstorage.spaceclean.app.view.junkclean.ui.effect.JunkCleanEffects
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionHost
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequestEffect
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun JunkCleanRoute(
    viewModel: JunkCleanViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateHomeAfterComplete: () -> Unit = onNavigateHome,
    resultTools: @Composable (modifier: Modifier, enabled: Boolean) -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val authorization = koinInject<CleanerKit>().mediaDeletion
    var exitStarted by remember(viewModel) { mutableStateOf(false) }

    var pendingPermission by remember { mutableStateOf<AppPermission?>(null) }
    var pendingOperation by remember { mutableStateOf<JunkOperation?>(null) }

    LaunchedEffect(uiState.phase) {
        val checking = uiState.phase as? JunkCleanPhase.CheckingPermission ?: return@LaunchedEffect
        pendingOperation = checking.request
        pendingPermission = AppPermission.StorageFiles
    }

    PermissionRequestEffect(pendingPermission) { result ->
        val operation = pendingOperation
        pendingPermission = null
        pendingOperation = null
        if (operation != null) {
            viewModel.dispatch(
                JunkCleanAction.PermissionResultReceived(
                    request = operation,
                    granted = result == PermissionResult.Granted,
                ),
            )
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

    JunkCleanEffects(
        viewModel = viewModel,
        onExit = { effect ->
            exitOnce {
                when (effect.target) {
                    JunkExitTarget.Back -> onNavigateBack()
                    JunkExitTarget.HomeAfterComplete -> onNavigateHomeAfterComplete()
                }
            }
        },
        onDeleteAuthorization = { effect ->
            if (authorizationJob?.isActive != true) {
                authorizationJob =
                    routeScope.launch {
                        val deletedIds = mutableSetOf<String>()
                        try {
                            for (batch in effect.ids.chunked(authorization.maxBatchSize)) {
                                val request = authorization.prepare(batch) ?: continue
                                val approved = MediaDeletionHost.authorize(request.sender)
                                val statuses = authorization.complete(request.requestId, approved)
                                deletedIds += statuses.filterValues { it == DeleteStatus.Deleted }.keys
                                if (!approved) break
                            }
                        } finally {
                            authorization.discard(effect.ids)
                        }
                        ensureActive()
                        if (exitStarted) return@launch
                        viewModel.dispatch(JunkCleanAction.DeleteAuthorizationResultReceived(deletedIds))
                    }
            }
        },
    )

    JunkCleanScreen(
        state = uiState.copy(interactionsEnabled = !exitStarted),
        resultTools = resultTools,
        onAction = { intent ->
            if (!exitStarted) viewModel.dispatch(intent)
        },
    )
}
