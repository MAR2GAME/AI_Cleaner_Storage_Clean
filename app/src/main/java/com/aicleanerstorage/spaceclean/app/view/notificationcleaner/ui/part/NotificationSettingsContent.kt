package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction.NotificationCleanerUiState
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXSelectionCircle
import com.aicleanerstorage.spaceclean.base.designsystem.component.PackageAppIcon
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.model.BlockableNotificationAppModel

@Composable
internal fun NotificationSettingsContent(
    listModifier: Modifier,
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
            listModifier.fillMaxWidth(),
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

internal val NavyMuted = Color(0xFF8190A5)

private val NotificationSettingsCardRadius = 12.dp

private val NotificationSettingsRowHeight = 84.dp

private val NotificationSelectionSize = 20.dp

@Composable
private fun NotificationSettingsSwitch(checked: Boolean) {
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
    app: BlockableNotificationAppModel,
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
                modifier = Modifier.size(44.dp),
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
