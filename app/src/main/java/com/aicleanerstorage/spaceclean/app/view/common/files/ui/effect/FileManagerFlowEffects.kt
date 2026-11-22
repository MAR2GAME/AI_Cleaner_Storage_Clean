package com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowEffectsParams

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
fun FileManagerFlowEffects(params: FileManagerFlowEffectsParams) {
    FileManagerOperationEventsEffect(params.effects, params.permissionState, params.navigator)
    FileManagerErrorToastEffect(params.error, params.onClearError)
    FileManagerStartEffect(params.permissionState, params.onStartIfNeeded)
}
