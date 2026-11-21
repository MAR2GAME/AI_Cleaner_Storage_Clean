package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission

internal class FileManagerOwnerState(
    initialGranted: Boolean,
) : ViewModel() {
    val permissionState = FileManagerPermissionState(initialGranted)

    override fun onCleared() {
        permissionState.markLeaving()
        super.onCleared()
    }
}

/** 文件管理流程的权限状态挂在流程根 entry 上，列表页与详情页共享同一份。 */
@Composable
internal fun rememberFileManagerOwnerState(ownerEntry: NavBackStackEntry): FileManagerPermissionState {
    val context = LocalContext.current
    val granted = AppPermission.StorageFiles.isGranted(context)
    val factory =
        remember(granted) {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = FileManagerOwnerState(granted) as T
            }
        }
    val holder: FileManagerOwnerState = viewModel(viewModelStoreOwner = ownerEntry, factory = factory)
    return holder.permissionState
}

/**
 * 文件管理流程宿主：列表页与详情页各自挂载一份，但只有处于 RESUMED 的当前页会收集 [MviViewModel.event]，
 * 因此流程级 effect 在任意时刻只有一个消费者（共享同一个 [rootEntry] 上的 ViewModel 与权限状态）。
 */
@Composable
internal fun <S : Any> FileManagerFlowHost(
    rootEntry: NavBackStackEntry,
    currentEntry: NavBackStackEntry,
    navigator: AppNavigator,
    viewModel: MviViewModel<S, FileManagerAction, FileManagerEvent>,
    error: (S) -> UiText?,
    content: @Composable (FileManagerPermissionState) -> Unit,
) {
    val permissionState = rememberFileManagerOwnerState(rootEntry)
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleState by currentEntry.lifecycle.currentStateAsState()
    if (lifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
        FileManagerFlowEffects(
            effects = viewModel.event,
            navigator = navigator,
            permissionState = permissionState,
            error = error(uiState),
            onClearError = { viewModel.dispatch(FileManagerAction.ErrorDismissed) },
            onStartIfNeeded = { viewModel.dispatch(FileManagerAction.InitialLoadRequested) },
        )
    }
    content(permissionState)
}
