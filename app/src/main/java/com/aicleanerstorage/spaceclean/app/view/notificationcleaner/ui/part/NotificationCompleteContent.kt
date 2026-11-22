package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.CommonResultCheckIcon
import com.aicleanerstorage.spaceclean.app.view.common.tool.CommonResultContent
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteAnimation
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanXDeleteCompleteStage
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import kotlinx.coroutines.delay

@Composable
internal fun NotificationCompleteContent(
    cleanedCount: Int,
    showResult: Boolean,
    onCelebrationFinished: () -> Unit,
    onNavigateTool: (AppDestination) -> Unit,
) {
    LaunchedEffect(showResult) {
        if (showResult) return@LaunchedEffect
        delay(800L)
        onCelebrationFinished()
    }

    if (!showResult) {
        CleanXDeleteCompleteAnimation(
            stage = CleanXDeleteCompleteStage.Complete,
            modifier = Modifier.fillMaxSize(),
            cleaningText = stringResource(R.string.cleaning_notifications),
            completeText = stringResource(R.string.notification_clean_result_cleared),
            drawBackground = false,
        )
    } else {
        CommonResultContent(
            onNavigateTool = onNavigateTool,
            modifier = Modifier.fillMaxSize(),
            excludedToolDestinations = setOf(AppDestination.NotificationCleaner),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CommonResultCheckIcon(size = 45.dp)
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text =
                        stringResource(
                            R.string.notification_clean_result_count,
                            cleanedCount,
                            pluralStringResource(R.plurals.notifications_count, cleanedCount),
                        ),
                    color = Navy,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.notification_clean_result_cleared),
                    color = NavyMuted,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
