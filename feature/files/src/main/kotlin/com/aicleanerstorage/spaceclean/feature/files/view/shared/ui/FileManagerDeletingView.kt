package com.aicleanerstorage.spaceclean.feature.files.view.shared.ui

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.core.ui.components.animations.CleanXDeleteCompleteAnimation
import com.aicleanerstorage.spaceclean.core.ui.components.animations.CleanXDeleteCompleteStage

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
