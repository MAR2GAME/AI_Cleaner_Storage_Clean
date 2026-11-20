package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerAction
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerPage
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerUiState
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part.ErrorCard
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part.Navy
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part.NotificationBlockingTurnedOffDialog
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part.NotificationCleaningContent
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part.NotificationCompleteContent
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part.NotificationOnboardingContent
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part.NotificationScanningContent
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part.NotificationSettingsContent
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part.NotificationStatusContent
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXBottomActionBar
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget

@Composable
fun NotificationCleanerScreen(
    state: NotificationCleanerUiState,
    onAction: (NotificationCleanerAction) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBlockingTurnedOffDialog by remember { mutableStateOf(false) }

    BackHandler { onAction(NotificationCleanerAction.BackClicked) }

    CleanXScaffoldPage(
        title = stringResource(R.string.notification_cleaner),
        modifier = modifier,
        onBack = { onAction(NotificationCleanerAction.BackClicked) },
        actions = {
            if (state.page == NotificationCleanerPage.Status) {
                IconButton(
                    onClick = { onAction(NotificationCleanerAction.SettingsClicked) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings),
                        tint = Navy,
                    )
                }
            }
        },
        bottomBar = {
            if (
                state.isInitialized &&
                state.errorMessage == null &&
                state.page == NotificationCleanerPage.Onboarding
            ) {
                CleanXBottomActionBar(
                    text = stringResource(R.string.notification_one_tap_tidy_up),
                    onClick = { onAction(NotificationCleanerAction.BlockingEnableRequested) },
                    backgroundColor = Color.Transparent,
                )
            } else if (
                state.isInitialized &&
                state.errorMessage == null &&
                state.page == NotificationCleanerPage.Status &&
                state.blockedCount > 0
            ) {
                CleanXBottomActionBar(
                    text = stringResource(R.string.notification_one_tap_tidy_up),
                    onClick = { onAction(NotificationCleanerAction.TidyUpClicked) },
                    backgroundColor = Color.Transparent,
                )
            }
        },
        contentPadding =
            if (
                state.page == NotificationCleanerPage.Scanning ||
                state.page == NotificationCleanerPage.Cleaning ||
                state.page == NotificationCleanerPage.Complete
            ) {
                PaddingValues(0.dp)
            } else {
                PaddingValues(horizontal = 16.dp)
            },
        scrollEnabled =
                state.page != NotificationCleanerPage.Onboarding &&
                state.page != NotificationCleanerPage.Scanning &&
                state.page != NotificationCleanerPage.Settings &&
                state.page != NotificationCleanerPage.Cleaning &&
                state.page != NotificationCleanerPage.Complete,
    ) {
        when {
            !state.isInitialized -> Spacer(modifier = Modifier.height(220.dp))
            state.errorMessage != null -> ErrorCard(
                message = state.errorMessage.orEmpty(),
                onRetry = { onAction(NotificationCleanerAction.RefreshRequested) },
            )
            else -> {
                when (state.page) {
                    NotificationCleanerPage.Onboarding -> NotificationOnboardingContent()
                    NotificationCleanerPage.Scanning -> NotificationScanningContent()
                    NotificationCleanerPage.Status -> NotificationStatusContent(uiState = state)
                    NotificationCleanerPage.Settings -> NotificationSettingsContent(
                        listModifier = Modifier.weight(1f),
                        uiState = state,
                        onEnabledChange = { checked ->
                            if (checked) {
                                onAction(NotificationCleanerAction.BlockingEnableRequested)
                            } else {
                                showBlockingTurnedOffDialog = true
                            }
                        },
                        onTogglePackage = { onAction(NotificationCleanerAction.PackageToggled(it)) },
                    )
                    NotificationCleanerPage.Cleaning -> NotificationCleaningContent(
                        paused = state.showStopDialog,
                        complete = state.cleaningAnimationComplete,
                        onFinished = { onAction(NotificationCleanerAction.CleaningAnimationFinished) },
                    )
                    NotificationCleanerPage.Complete -> NotificationCompleteContent(
                        cleanedCount = state.cleanedCount,
                        showResult = state.completionResultVisible,
                        onCelebrationFinished = { onAction(NotificationCleanerAction.CompletionAnimationFinished) },
                        onNavigateTool = onNavigate,
                    )
                }
            }
        }
        if (
            state.page != NotificationCleanerPage.Onboarding &&
            state.page != NotificationCleanerPage.Scanning &&
            state.page != NotificationCleanerPage.Settings &&
            state.page != NotificationCleanerPage.Cleaning &&
            state.page != NotificationCleanerPage.Complete
        ) {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showBlockingTurnedOffDialog) {
        NotificationBlockingTurnedOffDialog(
            onCancel = { showBlockingTurnedOffDialog = false },
            onConfirm = {
                showBlockingTurnedOffDialog = false
                onAction(NotificationCleanerAction.BlockingDisableRequested)
            },
        )
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(NotificationCleanerAction.StopConfirmed) },
            onResume = { onAction(NotificationCleanerAction.StopDismissed) },
        )
    }
}
