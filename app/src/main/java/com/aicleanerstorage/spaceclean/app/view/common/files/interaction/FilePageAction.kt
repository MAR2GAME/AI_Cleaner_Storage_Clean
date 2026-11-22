package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/**
 * 六个扁平文件页共享的动作。
 *
 * 只包含照片/截图/视频/音频/文档/大文件共有的语义；重复文件、位置移除、
 * 相似照片各自使用独立的 sealed action。
 */
sealed interface FilePageAction {
    data class NavigationRequested(
        val destination: AppDestination,
        val reset: Boolean = true,
    ) : FilePageAction

    data object PermissionRejected : FilePageAction

    data object DetailBackClicked : FilePageAction

    data object BackClicked : FilePageAction

    data object DeleteDismissed : FilePageAction

    data object DeleteClicked : FilePageAction

    data object ContinueManagingClicked : FilePageAction

    data object VisibleItemsToggled : FilePageAction

    data class TabChangeRequested(
        val index: Int,
    ) : FilePageAction

    data class SelectionToggled(
        val id: Int,
    ) : FilePageAction

    data class VisibleSelectionToggled(
        val ids: Set<Int>,
    ) : FilePageAction

    data class DetailClicked(
        val index: Int,
    ) : FilePageAction

    data object StopDismissed : FilePageAction

    data object OperationCancellationRequested : FilePageAction

    data object InitialLoadRequested : FilePageAction

    data object RefreshRequested : FilePageAction

    data object ErrorDismissed : FilePageAction

    data object DeleteConfirmed : FilePageAction
}
