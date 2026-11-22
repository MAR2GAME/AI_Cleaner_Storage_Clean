package com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerPermissionState
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import kotlinx.coroutines.flow.Flow

/**
 * 文件管理流程的副作用集合：导航、错误提示、权限齐备后的首次加载。
 *
 * @param effects ViewModel 下发的事件流。
 * @param navigator 应用导航器。
 * @param permissionState 流程权限状态；首次加载以 [FileManagerPermissionState.granted] 为前提。
 * @param error 当前错误文案；为 null 时不提示。
 * @param onClearError 错误提示消费后的回调。
 * @param onStartIfNeeded 权限齐备后触发首次加载的回调。
 */
@Composable
fun FileManagerFlowEffects(
    effects: Flow<FileManagerEvent>,
    navigator: AppNavigator,
    permissionState: FileManagerPermissionState,
    error: UiText?,
    onClearError: () -> Unit,
    onStartIfNeeded: () -> Unit,
) {
    FileManagerOperationEventsEffect(effects, permissionState, navigator)
    FileManagerErrorToastEffect(error, onClearError)
    FileManagerStartEffect(permissionState, onStartIfNeeded)
}
