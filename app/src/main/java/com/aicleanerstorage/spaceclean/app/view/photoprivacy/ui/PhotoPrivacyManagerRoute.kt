package com.aicleanerstorage.spaceclean.app.view.photoprivacy.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerFlowState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerFlowDialogs
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyManagerViewModel
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequestEffect
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult

private val PRIVACY_PERMISSIONS =
    listOf(
        AppPermission.StorageFiles,
        AppPermission.MediaLocation,
    )

/**
 * 返回当前仍缺少的隐私页权限。
 *
 * @param context 用于查询系统权限。
 * @return 第一个未授予的权限；全部已授予返回 null。
 */
private fun nextMissingPermission(context: Context): AppPermission? =
    PRIVACY_PERMISSIONS.firstOrNull { permission -> !permission.isGranted(context) }

/**
 * 隐私图页面。
 *
 * 文件管理流程的通用门禁只要求存储权限；本页额外需要照片位置权限，
 * 因此进入时按 [PRIVACY_PERMISSIONS] 顺序补齐，两个都授予后才允许启动扫描。
 *
 * @param navigator 应用导航器。
 * @param viewModel 页面 ViewModel。
 * @param permissionState 文件管理流程共享的权限状态。
 */
@Composable
fun PhotoPrivacyManagerRoute(
    navigator: AppNavigator,
    viewModel: PhotoPrivacyManagerViewModel,
    permissionState: FileManagerPermissionState,
) {
    val context = LocalContext.current
    var pendingPermission by remember { mutableStateOf(nextMissingPermission(context)) }
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val flowState = FileManagerFlowState(uiState.showStopDialog, uiState.blockedPhase)
    val displayState = flowState.blockedPhase?.let { uiState.copy(phase = it) } ?: uiState

    PermissionRequestEffect(pendingPermission) { result ->
        pendingPermission = null
        permissionState.refresh(context)
        when (result) {
            PermissionResult.Granted -> pendingPermission = nextMissingPermission(context)
            PermissionResult.Denied,
            PermissionResult.PermanentlyDenied,
            PermissionResult.RequiresSettings,
            -> viewModel.dispatch(PhotoPrivacyAction.PermissionRejected)
        }
    }

    PhotoPrivacyManagerScreen(
        state = displayState.copy(permissionGranted = permissionState.granted),
        onAction = { action ->
            when (action) {
                PhotoPrivacyAction.BackClicked ->
                    viewModel.dispatch(
                        if (permissionState.granted) action else PhotoPrivacyAction.PermissionRejected,
                    )
                PhotoPrivacyAction.VisibleItemsToggled -> viewModel.dispatch(action)
                PhotoPrivacyAction.DeleteClicked -> viewModel.dispatch(PhotoPrivacyAction.LocationRemovalClicked)
                PhotoPrivacyAction.DeleteDismissed -> viewModel.dispatch(PhotoPrivacyAction.LocationRemovalDismissed)
                PhotoPrivacyAction.DeletePrepared -> viewModel.dispatch(PhotoPrivacyAction.LocationRemovalConfirmed)
                PhotoPrivacyAction.ContinueManagingClicked,
                is PhotoPrivacyAction.SelectionToggled,
                is PhotoPrivacyAction.VisibleSelectionToggled,
                -> viewModel.dispatch(action)
                is PhotoPrivacyAction.NavigationRequested -> viewModel.dispatch(action)
                else -> Unit
            }
        },
    )

    FileManagerFlowDialogs(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(PhotoPrivacyAction.StopDismissed) },
        permissionState = permissionState,
        router = navigator,
        phase = displayState.phase,
        deleteDialogVisible = false,
        onCancelActiveOperation = { viewModel.dispatch(PhotoPrivacyAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(PhotoPrivacyAction.LocationRemovalDismissed) },
        onDeleteReady = { viewModel.dispatch(PhotoPrivacyAction.LocationRemovalConfirmed) },
    )
}
