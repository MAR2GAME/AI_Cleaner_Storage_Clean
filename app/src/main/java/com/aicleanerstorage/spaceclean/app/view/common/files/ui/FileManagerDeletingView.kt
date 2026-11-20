package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteAnimation
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteStage

@Composable
fun FileManagerDeletingView(
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
