package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part

import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerUiState
import com.aicleanerstorage.spaceclean.base.designsystem.component.PackageAppIcon
import com.aicleanerstorage.spaceclean.domain.model.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.model.BlockedNotificationModel
import java.util.Date

@Composable
internal fun NotificationStatusContent(uiState: NotificationCleanerUiState) {
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
                                ?: BlockableNotificationAppModel(sortedItems.firstOrNull()?.appName ?: packageName, packageName),
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

internal val CardBg = Color.White

private val NotificationStatusCardRadius = 12.dp

internal val NotificationSettingsText = Color(0xFF282927)

internal val NotificationGreen = Color(0xFF5DD595)

private val NotificationBellBadgeSize = 66.dp

private val NotificationBellIconSize = 40.dp

private val NotificationRowHeight = 84.dp

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
private fun BlockedNotificationRow(
    entry: BlockedNotificationModel,
    app: BlockableNotificationAppModel,
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

private data class BlockedNotificationGroup(
    val app: BlockableNotificationAppModel,
    val items: List<BlockedNotificationModel>,
)
