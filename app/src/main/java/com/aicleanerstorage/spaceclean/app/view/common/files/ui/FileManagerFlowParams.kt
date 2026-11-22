package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import kotlinx.coroutines.flow.Flow

/** 鏂囦欢绠＄悊娴佺▼鍓綔鐢ㄧ殑鍙傛暟闆嗗悎銆?*/
data class FileManagerFlowParams(
    val effects: Flow<FileManagerEvent>,
    val navigator: AppNavigator,
    val permissionState: FileManagerPermissionState,
    val error: UiText?,
    val onClearError: () -> Unit,
    val onStartIfNeeded: () -> Unit,
)


