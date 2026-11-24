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
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdPlacementRegistry
import com.aicleanerstorage.spaceclean.app.integration.Mar2CompletionAdEffect
import com.aicleanerstorage.spaceclean.app.view.common.permission.junkCleanCopy
import com.aicleanerstorage.spaceclean.app.view.common.permission.rememberPermissionPrompt
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanAction
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanPhase
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.JunkCleanViewModel
import com.aicleanerstorage.spaceclean.app.view.junkclean.ui.effect.JunkCleanEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

@Composable
fun JunkCleanRoute(
    navigator: AppNavigator,
    viewModel: JunkCleanViewModel,
    resultTools: @Composable (modifier: Modifier, enabled: Boolean) -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    Mar2CompletionAdEffect(
        show = uiState.phase == JunkCleanPhase.CompleteAnimation,
        areaKey = Mar2AdPlacementRegistry.MAIN_JUNK_CLEAN_FINISH,
        destination = AppDestination.JunkClean,
    )
    var exitStarted by remember(viewModel) { mutableStateOf(false) }

    val permissionPrompt = rememberPermissionPrompt(copy = { permission -> permission.junkCleanCopy() })

    LaunchedEffect(uiState.phase) {
        val checking = uiState.phase as? JunkCleanPhase.CheckingPermission ?: return@LaunchedEffect
        permissionPrompt.request(AppPermission.StorageFiles) { result ->
            viewModel.dispatch(
                JunkCleanAction.PermissionResultReceived(
                    request = checking.request,
                    granted = result == PermissionResult.Granted,
                ),
            )
        }
    }

    val routeScope = rememberCoroutineScope()

    fun exitOnce(onContinue: () -> Unit) {
        if (exitStarted) return
        exitStarted = true
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
        onExit = { exitOnce { navigator.back() } },
    )

    JunkCleanScreen(
        state = uiState.copy(interactionsEnabled = !exitStarted),
        resultTools = resultTools,
        onAction = { intent ->
            if (!exitStarted) viewModel.dispatch(intent)
        },
    )
}
