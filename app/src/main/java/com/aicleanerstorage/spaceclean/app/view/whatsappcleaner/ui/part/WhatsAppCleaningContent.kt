package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteAnimation
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteStage

@Composable
internal fun WhatsAppCleaningContent(stage: CleanXDeleteCompleteStage) {
    CleanXDeleteCompleteAnimation(
        stage = stage,
        cleaningText = stringResource(R.string.cleaning_whatsapp_files),
        drawBackground = false,
    )
}
