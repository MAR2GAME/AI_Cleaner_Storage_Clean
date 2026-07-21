package com.aicleanerstorage.spaceclean.app.runtime.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.app.runtime.external.ExternalActivityLauncher
import com.aicleanerstorage.spaceclean.common.permission.AppRuntimePermissionDenialStore
import com.aicleanerstorage.spaceclean.common.permission.PermissionEngine
import com.aicleanerstorage.spaceclean.common.permission.PermissionPreferences
import com.aicleanerstorage.spaceclean.common.permission.ui.LocalPermissionCoordinator
import com.aicleanerstorage.spaceclean.common.ui.permission.StorageCleanPermissionUi
import org.koin.compose.koinInject

@Composable
internal fun AppPermissionHost(
    externalActivityLauncher: ExternalActivityLauncher,
    onPermissionFlowActiveChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val permissionPreferences = koinInject<PermissionPreferences>()
    val engine =
        remember(permissionPreferences) {
            PermissionEngine(denialStore = AppRuntimePermissionDenialStore(permissionPreferences))
        }
    val coordinator =
        remember(context, engine) {
            PermissionCoordinator(context, engine)
        }
    val latestOnPermissionFlowActiveChange =
        rememberUpdatedState(onPermissionFlowActiveChange)

    LaunchedEffect(coordinator.session != null) {
        latestOnPermissionFlowActiveChange.value(coordinator.session != null)
    }

    CompositionLocalProvider(LocalPermissionCoordinator provides coordinator) {
        content()
        PermissionPromptHost(
            state = coordinator,
            externalActivityLauncher = externalActivityLauncher,
            permissionPrompt = StorageCleanPermissionUi::PermissionPrompt,
        )
    }
}
