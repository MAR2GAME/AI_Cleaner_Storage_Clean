package com.aicleanerstorage.spaceclean.base.designsystem.component.popups

import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

private val PermissionTextColor = Color(0xFF282927)
private val PermissionDismissColor = Color(0xFF999999)
private val PermissionButtonStart = Color(0xFFD0FD91)
private val PermissionButtonEnd = Color(0xFF5DD595)
private val PermissionCardStart = Color(0xFFDFFFDC)
private val PermissionCardShape = RoundedCornerShape(12.dp)
private val PermissionActionShape = RoundedCornerShape(26.dp)

/**
 * 统一的权限解释弹窗。
 *
 * 与旧版保持一致：不可点外部/返回关闭，必须显式选择；[footer] 用于
 * AppLock 等页面的补充说明。
 *
 * @param copy 标题、说明、要点与按钮文案。
 * @param onConfirm 用户点击主按钮。
 * @param onDismiss 用户点击次按钮或系统取消。
 * @param modifier 卡片修饰符。
 * @param footer 卡片底部附加内容；为空时不占位。
 */
@Composable
fun PermissionDialog(
    copy: PermissionDialogCopy,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
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
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            contentAlignment = Alignment.Center,
        ) {
            PermissionCard(copy, onConfirm, onDismiss, modifier, footer)
        }
    }
}

@Composable
private fun PermissionCard(
    copy: PermissionDialogCopy,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    footer: (@Composable ColumnScope.() -> Unit)?,
) {
    Column(
        modifier =
            modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .widthIn(max = 343.dp)
                .fillMaxWidth()
                .clip(PermissionCardShape)
                .background(
                    Brush.verticalGradient(
                        colorStops =
                            arrayOf(
                                0f to PermissionCardStart,
                                0.26381f to Color.White,
                                1f to Color.White,
                            ),
                    ),
                ).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PermissionHeader(copy)
        PermissionBody(copy)
        footer?.let { content ->
            Spacer(modifier = Modifier.height(16.dp))
            content(this)
        }
        Spacer(modifier = Modifier.height(20.dp))
        PermissionActions(copy, onConfirm, onDismiss)
    }
}

@Composable
private fun PermissionHeader(copy: PermissionDialogCopy) {
    PermissionHero(copy.heroImageRes)
    Text(
        text = "\u2728 ${copy.title}",
        color = PermissionTextColor,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.72.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun PermissionBody(copy: PermissionDialogCopy) {
    copy.description?.let { description ->
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            color = PermissionTextColor,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            letterSpacing = 0.54.sp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    copy.hints.forEach { hint ->
        Spacer(modifier = Modifier.height(12.dp))
        PermissionHintRow(hint)
    }
}

@Composable
private fun PermissionHero(
    @DrawableRes imageRes: Int?,
) {
    if (imageRes == null) return
    Image(
        painter = painterResource(imageRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(width = 215.dp, height = 128.dp),
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun PermissionHintRow(hint: PermissionHint) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(hint.iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = hint.text,
            color = PermissionTextColor,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PermissionActions(
    copy: PermissionDialogCopy,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        copy.cancelText?.let { cancelText ->
            PermissionOutlinedAction(
                text = cancelText,
                onClick = onDismiss,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
        PermissionGradientAction(
            text = copy.confirmText,
            onClick = onConfirm,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@Composable
private fun PermissionOutlinedAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 44.dp),
        shape = PermissionActionShape,
        border = BorderStroke(1.56.dp, PermissionDismissColor),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = PermissionDismissColor,
            ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            color = PermissionDismissColor,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun PermissionGradientAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier =
            modifier
                .heightIn(min = 44.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(PermissionButtonStart, PermissionButtonEnd)),
                    shape = PermissionActionShape,
                ),
        shape = PermissionActionShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = PermissionTextColor,
            ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            color = PermissionTextColor,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
