package com.aicleanerstorage.spaceclean.app.view.home.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeSummaryUiState
import com.aicleanerstorage.spaceclean.app.view.home.interaction.ToolboxTargets
import com.aicleanerstorage.spaceclean.base.designsystem.component.cleanXPressable
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.CleanXSingleActionDialog
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination

@Suppress("UNUSED_PARAMETER")
@Composable
internal fun ToolBoxTabContent(
    summaryState: HomeSummaryUiState,
    onFeatureClick: (AppDestination) -> Unit = {},
) {
    val context = LocalContext.current
    var showWhatsAppMissingDialog by remember { mutableStateOf(false) }
    val tools = remember { ToolboxTargets }

    fun openTool(feature: AppDestination) {
        if (feature == AppDestination.WhatsAppCleaner && !context.hasWhatsAppInstalled()) {
            showWhatsAppMissingDialog = true
        } else {
            onFeatureClick(feature)
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            ToolboxShortcutPanel(onFeatureClick = ::openTool)
            Spacer(modifier = Modifier.height(20.dp))
            HomeFeatureGridPanel(
                title = stringResource(R.string.home_toolbox_title),
                features = tools,
                iconSize = { it.toolboxIconSize() },
                onFeatureClick = ::openTool,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showWhatsAppMissingDialog) {
        CleanXSingleActionDialog(
            title = stringResource(R.string.whatsapp_not_found_title),
            message = stringResource(R.string.whatsapp_not_found_message),
            actionText = stringResource(R.string.close),
            onAction = { showWhatsAppMissingDialog = false },
            onDismissRequest = { showWhatsAppMissingDialog = false },
        )
    }
}

@Composable
private fun ToolboxShortcutPanel(onFeatureClick: (AppDestination) -> Unit) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(136.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            ToolboxShortcut(
                iconRes = R.drawable.ic_device_phone,
                title = stringResource(R.string.home_device_information),
                onClick = { onFeatureClick(AppDestination.DeviceInfo) },
            )
            Spacer(
                modifier =
                    Modifier
                        .width(0.5.dp)
                        .height(68.dp)
                        .background(Color(0xFFE5E5E5)),
            )
            ToolboxShortcut(
                iconRes = R.drawable.ic_battery,
                title = stringResource(R.string.home_battery_usage),
                onClick = { onFeatureClick(AppDestination.BatteryInfo) },
            )
        }
    }
}

@Composable
private fun ToolboxShortcut(
    @androidx.annotation.DrawableRes iconRes: Int,
    title: String,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .width(96.dp)
                .height(96.dp)
                .cleanXPressable(
                    pressedAlpha = 0.9f,
                    pressedScale = 0.985f,
                    onClick = onClick,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = title,
            modifier = Modifier.size(44.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            modifier =
                Modifier
                    .width(96.dp)
                    .height(48.dp),
            color = Color(0xFF121212),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            softWrap = true,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun AppDestination.toolboxIconSize(): DpSize =
    when (this) {
        AppDestination.AppUsage -> DpSize(39.dp, 41.dp)
        AppDestination.NotificationCleaner -> DpSize(39.dp, 44.dp)
        AppDestination.WhatsAppCleaner -> DpSize(44.dp, 44.dp)
        AppDestination.NetworkUsage -> DpSize(41.dp, 41.dp)
        AppDestination.NetworkScan -> DpSize(45.dp, 41.dp)
        AppDestination.NetworkSpeed -> DpSize(40.dp, 23.dp)
        else -> DpSize(40.dp, 40.dp)
    }

private fun android.content.Context.hasWhatsAppInstalled(): Boolean =
    WhatsAppPackageNames.any { packageName ->
        packageManager.getLaunchIntentForPackage(packageName) != null
    }

private val WhatsAppPackageNames = listOf("com.whatsapp", "com.whatsapp.w4b")
