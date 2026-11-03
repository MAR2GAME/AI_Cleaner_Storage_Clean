package com.aicleanerstorage.spaceclean.app.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.common.ui.permission.StorageCleanPermissionUi
import org.koin.compose.koinInject

@Composable
internal fun AppPermissionHost(
    externalActivityTracker: ExternalActivityTracker,
    onPermissionFlowActiveChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    val coordinator: PermissionCoordinator = koinInject()
    val latestOnPermissionFlowActiveChange =
        rememberUpdatedState(onPermissionFlowActiveChange)

    LaunchedEffect(coordinator.session != null) {
        latestOnPermissionFlowActiveChange.value(coordinator.session != null)
    }

    CompositionLocalProvider(LocalPermissionCoordinator provides coordinator) {
        content()
        PermissionPromptHost(
            state = coordinator,
            externalActivityTracker = externalActivityTracker,
            permissionPrompt = StorageCleanPermissionUi::PermissionPrompt,
        )
    }
}
