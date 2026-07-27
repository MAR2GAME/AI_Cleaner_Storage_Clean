package com.aicleanerstorage.spaceclean.feature.home.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import android.view.WindowManager
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.ui.components.buttons.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.common.ui.theme.LocalAppThemeTokens

private val HomeExitDialogTitleColor = Color(0xFF282927)
private val HomeExitDialogBorderColor = Color(0xFF999999)
private val HomeExitDialogButtonStart = Color(0xFFD0FD91)
private val HomeExitDialogButtonEnd = Color(0xFF5DD595)
private val HomeExitDialogCardStart = Color(0xFFDFFFDC)
private val HomeExitDialogShape = RoundedCornerShape(12.dp)
private val HomeExitDialogActionShape = RoundedCornerShape(26.dp)

@Composable
internal fun HomeExitPromptDialog(
    spec: ToolNotificationSpec,
    onExit: () -> Unit,
    onViewNow: () -> Unit,
) {
    if (spec.route == AppDestination.NotificationCleaner.route) {
        NotificationCleanerExitPromptDialog(
            onEnableNow = onViewNow,
            onClose = onExit,
        )
    } else {
        ToolExitPromptDialog(
            spec = spec,
            onExit = onExit,
            onViewNow = onViewNow,
        )
    }
}

@Composable
private fun ToolExitPromptDialog(
    spec: ToolNotificationSpec,
    onExit: () -> Unit,
    onViewNow: () -> Unit,
) {
    HomeExitPromptScaffold(
        primaryText = stringResource(spec.actionRes),
        secondaryText = stringResource(R.string.exit),
        onPrimaryAction = onViewNow,
        onSecondaryAction = onExit,
    ) {
        Icon(
            painter = painterResource(spec.iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(44.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(toolExitPromptMessageRes(spec.route)),
            color = HomeExitDialogTitleColor,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.72.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun NotificationCleanerExitPromptDialog(
    onEnableNow: () -> Unit,
    onClose: () -> Unit,
) {
    val themeTokens = LocalAppThemeTokens.current
    Dialog(
        onDismissRequest = {},
        properties =
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            ),
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        LaunchedEffect(dialogWindow) {
            dialogWindow?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
            )
            Surface(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp)
                        .widthIn(max = 343.dp)
                        .fillMaxWidth(),
                color = themeTokens.colors.cardBackground,
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.exit_prompt_notification_title),
                        color = HomeExitDialogTitleColor,
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NotificationPromptTip(text = stringResource(R.string.exit_prompt_notification_tip_block))
                    Spacer(modifier = Modifier.height(14.dp))
                    NotificationPromptTip(text = stringResource(R.string.exit_prompt_notification_tip_matters))
                    Spacer(modifier = Modifier.height(14.dp))
                    NotificationPromptTip(text = stringResource(R.string.exit_prompt_notification_tip_focus))
                    Spacer(modifier = Modifier.height(20.dp))
                    CleanXPrimaryButton(
                        text = stringResource(R.string.enable_now),
                        onClick = onEnableNow,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color(0xFF999999)),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF999999),
                            ),
                    ) {
                        Text(
                            text = stringResource(R.string.close),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeExitPromptScaffold(
    primaryText: String,
    secondaryText: String,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties =
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            ),
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        LaunchedEffect(dialogWindow) {
            dialogWindow?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        val interactionSource = remember { MutableInteractionSource() }
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {},
                        ),
            )
            Column(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp)
                        .widthIn(max = 343.dp)
                        .fillMaxWidth()
                        .clip(HomeExitDialogShape)
                        .background(
                            Brush.verticalGradient(
                                colorStops =
                                    arrayOf(
                                        0f to HomeExitDialogCardStart,
                                        0.26381f to Color.White,
                                        1f to Color.White,
                                    ),
                            ),
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        )
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content()
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = onSecondaryAction,
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        shape = HomeExitDialogActionShape,
                        border = BorderStroke(1.56.dp, HomeExitDialogBorderColor),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = HomeExitDialogBorderColor,
                            ),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = secondaryText,
                            color = HomeExitDialogBorderColor,
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                    Button(
                        onClick = onPrimaryAction,
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(HomeExitDialogButtonStart, HomeExitDialogButtonEnd),
                                    ),
                                    HomeExitDialogActionShape,
                                ),
                        shape = HomeExitDialogActionShape,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = HomeExitDialogTitleColor,
                            ),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = primaryText,
                            color = HomeExitDialogTitleColor,
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationPromptTip(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_ok),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(11.dp))
        Text(
            text = text,
            color = LocalAppThemeTokens.current.colors.textSecondary,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@StringRes
private fun toolExitPromptMessageRes(route: String): Int =
    when (route) {
        AppDestination.DeviceInfo.route -> R.string.exit_prompt_device_info
        AppDestination.JunkClean.route -> R.string.exit_prompt_junk_removal
        AppDestination.BatteryInfo.route -> R.string.exit_prompt_battery_info
        AppDestination.NetworkScan.route -> R.string.exit_prompt_network_scan
        AppDestination.NetworkUsage.route -> R.string.exit_prompt_network_usage
        else -> R.string.exit_prompt_device_info
    }
