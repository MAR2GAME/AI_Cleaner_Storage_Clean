package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteStage

@Composable
fun FileOperationPhaseContent(
    params: FileOperationPhaseParams,
    browsingContent: @Composable () -> Unit,
) {
    when (params.phase) {
        FileOperationPhase.Scanning -> FileManagerScanningView(text = params.scanningText)
        FileOperationPhase.Browsing,
        FileOperationPhase.ConfirmDelete,
        -> browsingContent()
        FileOperationPhase.Deleting,
        FileOperationPhase.CompleteAnimation,
        ->
            FileManagerDeletingView(
                deletingText = params.deletingText ?: stringResource(R.string.file_deleting_files),
                completeText = params.completeText ?: stringResource(R.string.file_cleanup_completed),
                stage =
                    if (params.phase == FileOperationPhase.CompleteAnimation) {
                        CleanXDeleteCompleteStage.Complete
                    } else {
                        CleanXDeleteCompleteStage.Deleting
                    },
            )
        FileOperationPhase.Result ->
            FileManagerResultView(
                amount = params.resultAmount,
                unit = params.resultUnit,
                caption = params.resultCaption,
                onNavigateTool = params.onNavigateTool,
                onContinue = params.onContinue,
            )
        FileOperationPhase.NoResults -> params.noResultsContent()
    }
}
