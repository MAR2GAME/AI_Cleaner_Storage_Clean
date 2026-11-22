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
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.leaveBack
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerFlowDialogParams
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
 * 杩斿洖褰撳墠浠嶇己灏戠殑闅愮椤垫潈闄愩€? *
 * @param context 鐢ㄤ簬鏌ヨ绯荤粺鏉冮檺銆? * @return 绗竴涓湭鎺堜簣鐨勬潈闄愶紱鍏ㄩ儴宸叉巿浜堣繑鍥?null銆? */
private fun nextMissingPermission(context: Context): AppPermission? =
    PRIVACY_PERMISSIONS.firstOrNull { permission -> !permission.isGranted(context) }

/**
 * 闅愮鍥鹃〉闈€? *
 * 鏂囦欢绠＄悊娴佺▼鐨勯€氱敤闂ㄧ鍙姹傚瓨鍌ㄦ潈闄愶紱鏈〉棰濆闇€瑕佺収鐗囦綅缃潈闄愶紝
 * 鍥犳杩涘叆鏃舵寜 [PRIVACY_PERMISSIONS] 椤哄簭琛ラ綈锛屼袱涓兘鎺堜簣鍚庢墠鍏佽鍚姩鎵弿銆? *
 * @param navigator 搴旂敤瀵艰埅鍣ㄣ€? * @param viewModel 椤甸潰 ViewModel銆? * @param permissionState 鏂囦欢绠＄悊娴佺▼鍏变韩鐨勬潈闄愮姸鎬併€? */
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
        FileManagerFlowDialogParams(
        flowState = flowState,
        onDismissStopDialog = { viewModel.dispatch(PhotoPrivacyAction.StopDismissed) },
        permissionState = permissionState,
        onExitConfirmed = { permissionState.leaveBack(navigator) },
        phase = displayState.phase,
        deleteDialogVisible = false,
        onCancelActiveOperation = { viewModel.dispatch(PhotoPrivacyAction.OperationCancellationRequested) },
        onCancelDelete = { viewModel.dispatch(PhotoPrivacyAction.LocationRemovalDismissed) },
        onDeleteReady = { viewModel.dispatch(PhotoPrivacyAction.LocationRemovalConfirmed) },
    
        ),
    )
}



