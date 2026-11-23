package com.aicleanerstorage.spaceclean.common.ui.components.popups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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

private val PermissionText = Color(0xFF282927)
private val PermissionGreen = Color(0xFF5DD595)
private val PermissionLime = Color(0xFFD0FD91)

@Composable
fun InlinePermissionOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
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
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.60f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            ) {
                content()
            }
        }
    }
}

@Composable
fun PermissionRequiredDialog(
    copy: PermissionCopy,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-10).dp)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colorStops =
                            arrayOf(
                                0f to Color(0xFFDFFFDC),
                                0.26381f to Color.White,
                                1f to Color.White,
                            ),
                    ),
                ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "\u2728 " + stringResource(copy.titleRes),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    color = PermissionText,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(copy.descriptionRes),
                    modifier = Modifier.fillMaxWidth(),
                    color = PermissionText,
                    fontSize = 18.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.54.sp,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    PermissionBullet(
                        iconRes = R.drawable.ic_permission_junk,
                        text = stringResource(copy.hint1Res),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PermissionBullet(
                        iconRes = R.drawable.ic_permission_privacy,
                        text = stringResource(copy.hint2Res),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                PermissionButton(
                    text = stringResource(copy.cancelRes),
                    onClick = onCancel,
                    filled = false,
                )
                Spacer(modifier = Modifier.width(5.dp))
                PermissionButton(
                    text = stringResource(copy.allowRes),
                    onClick = onSubmit,
                    filled = true,
                )
            }
        }
    }
}

@Composable
private fun PermissionBullet(
    iconRes: Int,
    text: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = PermissionText,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun PermissionButton(
    text: String,
    onClick: () -> Unit,
    filled: Boolean,
) {
    val shape = RoundedCornerShape(26.dp)
    val baseModifier =
        Modifier
            .width(153.dp)
            .height(44.dp)
            .clip(shape)

    Box(
        modifier =
            if (filled) {
                baseModifier
                    .background(Brush.horizontalGradient(listOf(PermissionLime, PermissionGreen)))
                    .clickable(onClick = onClick)
            } else {
                baseModifier
                    .border(1.5.dp, Color(0xFF999999), shape)
                    .clickable(onClick = onClick)
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (filled) PermissionText else Color(0xFF999999),
            fontSize = if (filled) 18.sp else 16.sp,
            lineHeight = if (filled) 21.sp else 19.sp,
            fontWeight = if (filled) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
    }
}
