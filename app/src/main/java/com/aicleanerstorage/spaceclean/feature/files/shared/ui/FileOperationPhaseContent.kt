package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteStage

@Composable
internal fun FileOperationPhaseContent(
    phase: FileOperationPhase,
    scanningText: String,
    deletingText: String? = null,
    completeText: String? = null,
    resultAmount: String,
    resultUnit: String,
    resultCaption: String,
    onNavigateTool: (AppDestination) -> Unit,
    onContinue: () -> Unit,
    noResultsContent: @Composable () -> Unit = { FileManagerNoResultsContent() },
    browsingContent: @Composable () -> Unit,
) {
    when (phase) {
        FileOperationPhase.Scanning -> FileManagerScanningView(text = scanningText)
        FileOperationPhase.Browsing,
        FileOperationPhase.ConfirmDelete -> browsingContent()
        FileOperationPhase.Deleting,
        FileOperationPhase.CompleteAnimation -> FileManagerDeletingView(
            deletingText = deletingText ?: stringResource(R.string.file_deleting_files),
            completeText = completeText ?: stringResource(R.string.file_cleanup_completed),
            stage =
                if (phase == FileOperationPhase.CompleteAnimation) {
                    CleanXDeleteCompleteStage.Complete
                } else {
                    CleanXDeleteCompleteStage.Deleting
                },
        )
        FileOperationPhase.Result -> FileManagerResultView(
            amount = resultAmount,
            unit = resultUnit,
            caption = resultCaption,
            onNavigateTool = onNavigateTool,
            onContinue = onContinue,
        )
        FileOperationPhase.NoResults -> noResultsContent()
    }
}
