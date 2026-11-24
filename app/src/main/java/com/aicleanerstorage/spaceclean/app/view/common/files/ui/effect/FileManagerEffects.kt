package com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.app.integration.Mar2AdPlacementRegistry
import com.aicleanerstorage.spaceclean.app.integration.Mar2CompletionAdEffect
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveBack
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveHome
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.mvi.resolve
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import kotlinx.coroutines.flow.Flow

@Composable
fun FileManagerOperationEventsEffect(
    effects: Flow<FileManagerEvent>,
    permissionState: FileManagerPermissionState,
    navigator: AppNavigator,
) {
    val currentNavigator by rememberUpdatedState(navigator)
    LaunchedEffect(effects) {
        effects.collect { effect ->
            if (permissionState.leavingPage) return@collect
            when (effect) {
                is FileManagerEvent.Exit ->
                    if (effect.home) {
                        permissionState.leaveHome(currentNavigator)
                    } else {
                        permissionState.leaveBack(currentNavigator)
                    }
                is FileManagerEvent.Navigate -> {
                    if (effect.reset) {
                        permissionState.markLeaving()
                        currentNavigator.resetToRoot(effect.destination)
                    } else {
                        currentNavigator.navigate(effect.destination)
                    }
                }
                FileManagerEvent.NavigateBack -> currentNavigator.back()
            }
        }
    }
}

@Composable
fun FileManagerErrorToastEffect(
    error: UiText?,
    onConsumed: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(error) {
        val message = error?.resolve(context) ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        onConsumed()
    }
}

/**
 * 权限齐备后触发首次加载。
 *
 * 权限未授予或页面正在离开时什么都不做；权限状态从 false 变成 true 后
 * 会自动补触发一次，避免“先扫描、后报权限失败”。
 *
 * @param permissionState 流程权限状态。
 * @param onStartIfNeeded 触发加载的回调。
 */
@Composable
fun FileManagerStartEffect(
    permissionState: FileManagerPermissionState,
    onStartIfNeeded: () -> Unit,
) {
    val latestStartIfNeeded by rememberUpdatedState(onStartIfNeeded)
    LaunchedEffect(permissionState.granted, permissionState.leavingPage) {
        if (!permissionState.granted || permissionState.leavingPage) return@LaunchedEffect
        latestStartIfNeeded()
    }
}

@Composable
fun FileManagerCompletionAdEffect(
    phase: FileOperationPhase,
    destination: AppDestination,
) {
    Mar2CompletionAdEffect(
        show = phase == FileOperationPhase.CompleteAnimation,
        areaKey = Mar2AdPlacementRegistry.MAIN_FILE_MANAGE_FINISH,
        destination = destination,
    )
}
