package com.aicleanerstorage.spaceclean.common.ui.permission

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXText
import com.aicleanerstorage.spaceclean.common.ui.components.CircularAppLogo
import com.aicleanerstorage.spaceclean.common.ui.components.popups.InlinePermissionOverlay

private val PermissionGreen = Color(0xFF5DD595)
private val PermissionLime = Color(0xFFD0FD91)

@Composable
internal fun AppLockUsageAccessPermissionDialog(onManagePermission: () -> Unit, onDismissToHome: () -> Unit) {
    InlinePermissionOverlay(onDismiss = onDismissToHome) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).widthIn(max = 360.dp)
                .clip(RoundedCornerShape(12.dp)).background(permissionPanelBrush()).padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.app_lock_usage_permission_title), color = CleanXText, fontSize = 24.sp, lineHeight = 28.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.72.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.app_lock_usage_permission_desc), color = CleanXText, fontSize = 18.sp, lineHeight = 21.sp, letterSpacing = 0.54.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            AppPermissionCard()
            Spacer(Modifier.height(20.dp))
            PermissionGradientButton(stringResource(R.string.submit), onManagePermission, 44.dp)
        }
    }
}

@Composable
internal fun AppLockOverlayPermissionDialog(onAllowNow: () -> Unit, onCancel: () -> Unit) {
    InlinePermissionOverlay(onDismiss = onCancel) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).widthIn(max = 360.dp)
                .clip(RoundedCornerShape(12.dp)).background(permissionPanelBrush()).padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PermissionHeroImage()
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.app_lock_overlay_permission_message), color = CleanXText, fontSize = 24.sp, lineHeight = 28.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.72.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            PermissionGradientButton(stringResource(R.string.manage_permission), onAllowNow, 46.dp)
            Spacer(Modifier.height(12.dp))
            PermissionCancelButton(onCancel)
        }
    }
}

private fun permissionPanelBrush() = Brush.verticalGradient(*arrayOf(0f to Color(0xFFDFFFDC), 0.26381f to Color.White, 1f to Color.White))

@Composable
private fun PermissionHeroImage() {
    Image(painterResource(R.drawable.app_lock_permission_figma), null, contentScale = ContentScale.Crop, modifier = Modifier.size(215.dp, 128.dp))
}

@Composable
private fun PermissionGradientButton(text: String, onClick: () -> Unit, height: Dp) {
    Box(Modifier.fillMaxWidth().height(height).clip(CircleShape).background(Brush.horizontalGradient(listOf(PermissionLime, PermissionGreen))).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text, color = CleanXText, fontSize = 18.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun PermissionCancelButton(onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(46.dp).border(1.56.dp, Color(0xFF999999), CircleShape).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.cancel), color = Color(0xFF999999), fontSize = 20.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AppPermissionCard() {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF8FFF8)).padding(horizontal = 16.dp, vertical = 18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularAppLogo(contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.app_name), color = PermissionGreen, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.app_lock_usage_permission_grant), color = CleanXText, fontSize = 16.sp)
    }
}
