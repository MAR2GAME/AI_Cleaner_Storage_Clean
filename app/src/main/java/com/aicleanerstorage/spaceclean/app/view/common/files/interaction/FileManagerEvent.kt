package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/** 文件页下发的导航/退出命令。 */
sealed interface FileManagerEvent {
    data class Exit(
        val home: Boolean,
    ) : FileManagerEvent

    data class Navigate(
        val destination: AppDestination,
        val reset: Boolean,
    ) : FileManagerEvent

    data object NavigateBack : FileManagerEvent
}
