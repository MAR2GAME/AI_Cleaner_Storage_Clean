package com.aicleanerstorage.spaceclean.feature.notificationcleaner.ui

import android.text.format.DateFormat
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CommonResultCheckIcon
import com.aicleanerstorage.spaceclean.common.ui.components.CommonResultContent
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.BlockableNotificationApp
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.BlockedNotification
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerAction
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXBottomActionBar
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXSelectionCircle
import com.aicleanerstorage.spaceclean.common.ui.components.PackageAppIcon
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteAnimation
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanXDeleteCompleteStage
import com.aicleanerstorage.spaceclean.common.ui.components.buttons.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.ui.NotificationBlockingTurnedOffDialog
import com.aicleanerstorage.spaceclean.common.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.ui.ToolboxScanningContent
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerPage
import com.aicleanerstorage.spaceclean.feature.notificationcleaner.NotificationCleanerUiState
import kotlinx.coroutines.delay
import java.util.Date

private val CardBg = Color.White
private val Navy = Color(0xFF2D3748)
private val NavyMuted = Color(0xFF8190A5)
private val Divider15 = Color(0xFFE2EAF3)
private val CardRadius = 10.dp
private val NotificationSettingsCardRadius = 12.dp
private val NotificationStatusCardRadius = 12.dp
private val NotificationSettingsText = Color(0xFF282927)
private val NotificationGreen = Color(0xFF5DD595)
private val NotificationSettingsRowHeight = 84.dp
private val NotificationStatusRowHeight = 84.dp
private val NotificationBellBadgeSize = 66.dp
private val NotificationBellIconSize = 40.dp
private val NotificationSelectionSize = 20.dp
private val NotificationRowHeight = 84.dp
private const val NotificationGuideSlideDelayMillis = 2_000L

private val NotificationGuideSlides =
    listOf(
        R.drawable.notification_bar_guide_1,
        R.drawable.notification_bar_guide_2,
        R.drawable.notification_bar_guide_3,
        R.drawable.notification_bar_guide_4,
    )

@Composable
internal fun NotificationCleanerScreen(
    state: NotificationCleanerUiState,
    onAction: (NotificationCleanerAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBlockingTurnedOffDialog by remember { mutableStateOf(false) }

    BackHandler { onAction(NotificationCleanerAction.Back) }

    CleanXScaffoldPage(
        title = stringResource(R.string.notification_cleaner),
        modifier = modifier,
        onBack = { onAction(NotificationCleanerAction.Back) },
        actions = {
            if (state.page == NotificationCleanerPage.Status) {
                IconButton(
                    onClick = { onAction(NotificationCleanerAction.ShowSettings) },
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
                    onClick = { onAction(NotificationCleanerAction.EnableBlocking) },
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
                    onClick = { onAction(NotificationCleanerAction.TidyUp) },
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
                onRetry = { onAction(NotificationCleanerAction.Refresh) },
            )
            else -> {
                when (state.page) {
                    NotificationCleanerPage.Onboarding -> OnboardingContent()
                    NotificationCleanerPage.Scanning -> ScanningContent()
                    NotificationCleanerPage.Status -> StatusContent(uiState = state)
                    NotificationCleanerPage.Settings -> SettingsContent(
                        uiState = state,
                        onEnabledChange = { checked ->
                            if (checked) {
                                onAction(NotificationCleanerAction.EnableBlocking)
                            } else {
                                showBlockingTurnedOffDialog = true
                            }
                        },
                        onTogglePackage = { onAction(NotificationCleanerAction.TogglePackage(it)) },
                    )
                    NotificationCleanerPage.Cleaning -> CleaningContent(
                        paused = state.showStopDialog,
                        complete = state.cleaningAnimationComplete,
                        onFinished = { onAction(NotificationCleanerAction.FinishCleaning) },
                    )
                    NotificationCleanerPage.Complete -> CompleteContent(
                        cleanedCount = state.cleanedCount,
                        showResult = state.completionResultVisible,
                        onCelebrationFinished = { onAction(NotificationCleanerAction.FinishCompleteAnimation) },
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
                onAction(NotificationCleanerAction.DisableBlocking)
            },
        )
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(NotificationCleanerAction.QuitStoppedOperation) },
            onResume = { onAction(NotificationCleanerAction.ResumeStoppedOperation) },
        )
    }
}

@Composable
private fun OnboardingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NotificationCleanerGuideCarousel(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(400.dp),
        )
        Spacer(modifier = Modifier.height(70.dp))
        Text(
            text = stringResource(R.string.notification_tidy_one_tap),
            color = Navy,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun NotificationCleanerGuideCarousel(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { NotificationGuideSlides.size })

    LaunchedEffect(pagerState) {
        while (true) {
            val nextPage = (pagerState.currentPage + 1) % NotificationGuideSlides.size
            pagerState.animateScrollToPage(nextPage)
            delay(NotificationGuideSlideDelayMillis)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.clipToBounds(),
        verticalAlignment = Alignment.CenterVertically,
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(NotificationGuideSlides[page]),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun ScanningContent() {
    ToolboxScanningContent(
        centerIconRes = R.drawable.ic_scan_notification_clean,
        captionText = stringResource(R.string.scan_loading_fallback),
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun StatusContent(uiState: NotificationCleanerUiState) {
    val showEmptyState = uiState.enabled && uiState.blockedCount == 0
    val blockedGroups =
        remember(uiState.blockedNotifications, uiState.apps) {
            uiState.blockedNotifications
                .groupBy { it.packageName }
                .map { (packageName, items) ->
                    val sortedItems = items.sortedByDescending { it.postTimeMillis }
                    BlockedNotificationGroup(
                        app =
                            uiState.apps.firstOrNull { it.packageName == packageName }
                                ?: BlockableNotificationApp(sortedItems.firstOrNull()?.appName ?: packageName, packageName),
                        items = sortedItems,
                    )
                }
                .sortedByDescending { it.items.firstOrNull()?.postTimeMillis ?: 0L }
        }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(NotificationStatusCardRadius),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BellBadge()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(
                    if (showEmptyState) {
                        R.string.notification_status_empty_title
                    } else if (uiState.enabled) {
                        R.string.notification_status_enabled
                    } else {
                        R.string.notification_status_disabled
                    },
                ),
                color = NotificationSettingsText,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (showEmptyState) {
                Text(
                    text = stringResource(R.string.notification_status_empty_message),
                    color = NotificationSettingsText,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                )
            } else {
                BlockedCountText(count = uiState.blockedCount)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.notification_showing_limit),
                color = NotificationSettingsText.copy(alpha = 0.65f),
                fontSize = 16.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
            )
        }
    }

    if (blockedGroups.isNotEmpty()) {
        Spacer(modifier = Modifier.height(28.dp))
        blockedGroups.forEachIndexed { groupIndex, group ->
            Text(
                text = group.app.packageName,
                color = NotificationSettingsText,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            group.items.forEachIndexed { itemIndex, entry ->
                BlockedNotificationRow(
                    entry = entry,
                    app = group.app,
                )
                if (itemIndex != group.items.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            if (groupIndex != blockedGroups.lastIndex) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun BlockedCountText(count: Int) {
    val countLabel = pluralStringResource(R.plurals.notifications_blocked_count, count, count)
    val countText = count.toString()
    val countStart = countLabel.indexOf(countText)
    val styledLabel =
        buildAnnotatedString {
            append(countLabel)
            if (countStart >= 0) {
                addStyle(
                    style = SpanStyle(color = NotificationGreen),
                    start = countStart,
                    end = countStart + countText.length,
                )
            }
        }
    Text(
        text = styledLabel,
        color = NotificationSettingsText,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ColumnScope.SettingsContent(
    uiState: NotificationCleanerUiState,
    onEnabledChange: (Boolean) -> Unit,
    onTogglePackage: (String) -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(99.dp)
                .toggleable(
                    value = uiState.enabled,
                    role = Role.Switch,
                    onValueChange = onEnabledChange,
                ),
        color = CardBg,
        shape = RoundedCornerShape(NotificationSettingsCardRadius),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.notification_blocked_notifications),
                    color = NotificationSettingsText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 21.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.notification_block_settings_desc),
                    color = NotificationSettingsText.copy(alpha = 0.65f),
                    fontSize = 16.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                )
            }
            NotificationSettingsSwitch(
                checked = uiState.enabled,
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .weight(1f),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (uiState.isAppsLoading && uiState.apps.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.result_loading),
                    color = NavyMuted,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                )
            }
        }
        items(
            items = uiState.apps,
            key = { it.packageName },
        ) { app ->
            NotificationAppRow(
                app = app,
                enabled = uiState.enabled,
                selected = app.packageName in uiState.selectedPackages,
                onToggleSelection = { onTogglePackage(app.packageName) },
            )
        }
    }
}

@Composable
private fun NotificationSettingsSwitch(
    checked: Boolean,
) {
    Box(
        modifier =
            Modifier
                .size(48.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Box(
            modifier =
                Modifier
                    .width(34.dp)
                    .height(18.dp)
                    .clip(CircleShape)
                    .background(if (checked) NotificationGreen else Color(0xFFECF0F4))
                    .padding(1.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (checked) Color.White else Color(0xFF999999)),
            )
        }
    }
}

@Composable
private fun NotificationAppRow(
    app: BlockableNotificationApp,
    enabled: Boolean,
    selected: Boolean,
    onToggleSelection: () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(NotificationSettingsRowHeight)
                .clickable(enabled = enabled, onClick = onToggleSelection),
        color = CardBg,
        shape = RoundedCornerShape(NotificationSettingsCardRadius),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PackageAppIcon(
                packageName = app.packageName,
                fallbackText = app.appName.take(1).ifBlank { "A" },
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = app.appName,
                color = if (enabled) Navy else NavyMuted,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            SelectionBubble(selected = selected, enabled = enabled)
        }
    }
}

@Composable
private fun BlockedNotificationRow(
    entry: BlockedNotification,
    app: BlockableNotificationApp,
) {
    val context = LocalContext.current
    val timeText =
        remember(context, entry.postTimeMillis) {
            DateFormat.getTimeFormat(context).format(Date(entry.postTimeMillis))
        }
    val messageText = remember(entry.title, entry.text) {
        entry.text.ifBlank { entry.title }.ifBlank { "" }
    }

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(NotificationRowHeight),
        color = CardBg,
        shape = RoundedCornerShape(NotificationStatusCardRadius),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PackageAppIcon(
                packageName = app.packageName,
                fallbackText = app.appName.take(1).ifBlank { "A" },
                modifier = Modifier.size(44.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = messageText.ifBlank { stringResource(R.string.notification_message_fallback) },
                    color = NotificationSettingsText,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = app.appName,
                    color = NotificationSettingsText.copy(alpha = 0.65f),
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = timeText,
                color = NotificationSettingsText.copy(alpha = 0.65f),
                fontSize = 14.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.widthIn(min = 42.dp),
            )
        }
    }
}

@Composable
private fun CleaningContent(
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

@Composable
private fun CompleteContent(
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
                    text = stringResource(
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

@Composable
private fun BellBadge() {
    Box(
        modifier =
            Modifier
                .size(NotificationBellBadgeSize)
                .clip(CircleShape)
                .background(Color(0xFFEFFFFA)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_notification_status_bell),
            contentDescription = null,
            modifier = Modifier.size(NotificationBellIconSize),
        )
    }
}

@Composable
private fun SelectionBubble(
    selected: Boolean,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        CleanXSelectionCircle(
            selected = selected,
            enabled = enabled,
            size = NotificationSelectionSize,
        )
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(CardRadius),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.error),
                color = Navy,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                color = NavyMuted,
                fontSize = 15.sp,
            )
            CleanXPrimaryButton(
                text = stringResource(R.string.retry),
                onClick = onRetry,
            )
        }
    }
}

private data class BlockedNotificationGroup(
    val app: BlockableNotificationApp,
    val items: List<BlockedNotification>,
)
