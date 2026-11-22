package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequestEffect
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult

/**
 * 文件管理流程的权限状态宿主。
 *
 * 状态挂在流程根 entry 的 ViewModel 上，列表页与详情页共用同一个实例；
 * [onCleared] 时标记离开，避免返回过程中的迟到事件再次导航。
 *
 * @param requiredPermissions 本流程需要的权限集合。
 * @param initialGranted 创建时根据系统状态算出的初始值。
 */
internal class FileManagerOwnerState(
    requiredPermissions: Set<AppPermission>,
    initialGranted: Boolean,
) : ViewModel() {
    val permissionState = FileManagerPermissionState(requiredPermissions, initialGranted)

    override fun onCleared() {
        permissionState.markLeaving()
        super.onCleared()
    }
}

/**
 * 取得（或创建）文件管理流程的权限状态。
 *
 * @param ownerEntry 流程根 destination 的 back stack entry。
 * @param requiredPermissions 本流程需要的权限集合。
 * @return 挂在 [ownerEntry] 上、列表页与详情页共享的权限状态。
 */
@Composable
internal fun rememberFileManagerOwnerState(
    ownerEntry: NavBackStackEntry,
    requiredPermissions: Set<AppPermission>,
): FileManagerPermissionState {
    val context = LocalContext.current
    val granted = requiredPermissions.all { it.isGranted(context) }
    val factory =
        remember(granted, requiredPermissions) {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    FileManagerOwnerState(requiredPermissions, granted) as T
            }
        }
    val holder: FileManagerOwnerState = viewModel(viewModelStoreOwner = ownerEntry, factory = factory)
    return holder.permissionState
}

/**
 * 文件管理流程宿主：列表页与详情页各自挂载一份，但只有处于 RESUMED 的当前页会收集
 * [MviViewModel.event]，因此流程级 effect 在任意时刻只有一个消费者。
 *
 * 进入页面和从系统设置返回时都会重新查询 [requiredPermissions]；权限齐备后
 * [FileManagerFlowEffects] 才会触发首次加载，避免“先扫描、后报权限失败”。
 *
 * @param rootEntry 流程根 destination 的 entry，权限状态挂在它上面。
 * @param currentEntry 当前页面的 entry，用于判断是否 RESUMED。
 * @param navigator 应用导航器，由 effect 执行跳转。
 * @param viewModel 本流程共享的 MVI ViewModel。
 * @param error 从页面状态中取错误文案；为 null 时不提示。
 * @param requiredPermissions 本流程需要的权限集合，默认只要求存储权限。
 * @param content 页面内容；参数是共享的权限状态。
 */
@Composable
internal fun <S : Any, A : Any> FileManagerFlowHost(
    rootEntry: NavBackStackEntry,
    currentEntry: NavBackStackEntry,
    navigator: AppNavigator,
    viewModel: MviViewModel<S, A, FileManagerEvent>,
    initialLoad: A,
    dismissError: A,
    error: (S) -> UiText?,
    requiredPermissions: Set<AppPermission> = setOf(AppPermission.StorageFiles),
    requestPermissions: Boolean = true,
    content: @Composable (FileManagerPermissionState) -> Unit,
) {
    val context = LocalContext.current
    val permissionState = rememberFileManagerOwnerState(rootEntry, requiredPermissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleState by currentEntry.lifecycle.currentStateAsState()
    var pendingPermission by remember(requestPermissions, requiredPermissions) {
        mutableStateOf(
            if (requestPermissions) {
                requiredPermissions.firstOrNull { !it.isGranted(context) }
            } else {
                null
            },
        )
    }

    PermissionRequestEffect(pendingPermission) { result ->
        pendingPermission = null
        permissionState.refresh(context)
        when (result) {
            PermissionResult.Granted ->
                pendingPermission = requiredPermissions.firstOrNull { !it.isGranted(context) }
            PermissionResult.Denied,
            PermissionResult.PermanentlyDenied,
            PermissionResult.RequiresSettings,
            -> navigator.back()
        }
    }

    DisposableEffect(lifecycleOwner, permissionState) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    permissionState.refresh(context)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (lifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
        FileManagerFlowEffects(
            effects = viewModel.event,
            navigator = navigator,
            permissionState = permissionState,
            error = error(uiState),
            onClearError = { viewModel.dispatch(dismissError) },
            onStartIfNeeded = { viewModel.dispatch(initialLoad) },
        )
    }
    content(permissionState)
}

