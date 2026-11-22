package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

/** 文件操作阶段内容的展示参数与回调。 */
data class FileOperationPhaseParams(
    val phase: FileOperationPhase,
    val scanningText: String,
    val deletingText: String? = null,
    val completeText: String? = null,
    val resultAmount: String,
    val resultUnit: String,
    val resultCaption: String,
    val onNavigateTool: (AppDestination) -> Unit,
    val onContinue: () -> Unit,
    val noResultsContent: @Composable () -> Unit = { FileManagerNoResultsContent() },
)
