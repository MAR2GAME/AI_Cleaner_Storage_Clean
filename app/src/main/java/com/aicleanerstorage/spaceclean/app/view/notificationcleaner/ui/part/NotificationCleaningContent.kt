package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteAnimation
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteStage
import kotlinx.coroutines.delay

@Composable
internal fun NotificationCleaningContent(
    paused: Boolean,
    complete: Boolean,
    onFinished: () -> Unit,
) {
    LaunchedEffect(paused, complete) {
        if (paused || complete) return@LaunchedEffect
        delay(1800L)
        onFinished()
    }

    CleanXDeleteCompleteAnimation(
        stage =
            if (complete) {
                CleanXDeleteCompleteStage.Complete
            } else {
                CleanXDeleteCompleteStage.Deleting
            },
        cleaningText = stringResource(R.string.cleaning_notifications),
        drawBackground = false,
    )
}
