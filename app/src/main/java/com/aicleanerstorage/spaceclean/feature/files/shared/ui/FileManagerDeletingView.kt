package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteAnimation
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteStage

@Composable
internal fun FileManagerDeletingView(
    deletingText: String,
    completeText: String,
    stage: CleanXDeleteCompleteStage = CleanXDeleteCompleteStage.Deleting,
) {
    CleanXDeleteCompleteAnimation(
        stage = stage,
        cleaningText = deletingText,
        completeText = completeText,
        drawBackground = false,
    )
}
