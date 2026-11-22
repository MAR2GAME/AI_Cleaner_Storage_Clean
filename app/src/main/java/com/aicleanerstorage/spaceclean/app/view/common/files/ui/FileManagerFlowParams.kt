package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import kotlinx.coroutines.flow.Flow

/** 文件管理流程副作用的参数集合。 */
data class FileManagerFlowParams(
    val effects: Flow<FileManagerEvent>,
    val navigator: AppNavigator,
    val permissionState: FileManagerPermissionState,
    val error: UiText?,
    val onClearError: () -> Unit,
    val onStartIfNeeded: () -> Unit,
)
