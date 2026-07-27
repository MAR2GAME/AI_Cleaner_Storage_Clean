package com.aicleanerstorage.spaceclean.feature.applock.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXTopAppBar
import com.aicleanerstorage.spaceclean.common.ui.components.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.feature.applock.AppLockUiState

@Composable
internal fun AppLockPinView(uiState: AppLockUiState, onDigit: (Char) -> Unit, onDelete: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val errorOffset = remember { Animatable(0f) }
    LaunchedEffect(uiState.pinErrorRes) {
        if (uiState.pinErrorRes != null) {
            errorOffset.snapTo(0f)
            listOf(20f, -20f, 20f, -20f, 0f).forEach { errorOffset.animateTo(it, tween(80)) }
        } else errorOffset.snapTo(0f)
    }

    Box(Modifier.fillMaxSize().background(Color(0xFFF4F4F4)).stableNavigationBarsPadding()) {
        CleanXTopAppBar(
            title = stringResource(R.string.app_lock),
            onBack = onBack,
        )
        Text(stringResource(uiState.pinStep.hintRes), color = Color(0xFF282927), fontSize = 18.sp, modifier = Modifier.fillMaxWidth().padding(top = 170.dp), textAlign = TextAlign.Center)
        PinDots(uiState.pinInput.length, Modifier.align(Alignment.TopCenter).padding(top = 233.dp))
        if (uiState.pinErrorRes != null) {
            Text(stringResource(uiState.pinErrorRes), color = PinErrorColor, fontSize = 14.sp, modifier = Modifier.fillMaxWidth().padding(top = 260.dp).graphicsLayer { translationX = errorOffset.value }, textAlign = TextAlign.Center)
        }
        NumberPad(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 289.dp),
            onDigit = { digit ->
                if (uiState.vibrationEnabled && uiState.pinInput.length != 4) performPinVibration(context)
                onDigit(digit)
            },
            onDelete = onDelete,
        )
    }
}

@Composable
private fun PinDots(length: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        repeat(4) { index ->
            Box(Modifier.size(24.dp).background(if (index < length) Color(0xFF282927) else Color.Transparent, CircleShape))
        }
    }
}

@Composable
private fun NumberPad(modifier: Modifier, onDigit: (Char) -> Unit, onDelete: () -> Unit) {
    val rows = listOf(listOf('1', '2', '3'), listOf('4', '5', '6'), listOf('7', '8', '9'))
    Column(modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        rows.forEach { row -> Row(horizontalArrangement = Arrangement.spacedBy(44.dp)) { row.forEach { digit -> NumberButton(digit.toString()) { onDigit(digit) } } } }
        Row(horizontalArrangement = Arrangement.spacedBy(44.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.size(66.dp))
            NumberButton("0") { onDigit('0') }
            NumberButton(null, onDelete)
        }
    }
}

@Composable
private fun NumberButton(label: String?, onClick: () -> Unit) {
    Box(Modifier.size(66.dp).background(Color.White.copy(alpha = 0.65f), CircleShape).clickable(remember { MutableInteractionSource() }, indication = null, onClick = onClick), contentAlignment = Alignment.Center) {
        if (label != null) Text(label, color = Color(0xFF282927), fontSize = 32.sp, fontWeight = FontWeight.Medium)
        else Image(painterResource(R.drawable.ic_pin_delete_figma), contentDescription = stringResource(R.string.delete), modifier = Modifier.size(36.dp))
    }
}

private fun performPinVibration(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator else @Suppress("DEPRECATION") (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
    runCatching { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createOneShot(30L, VibrationEffect.DEFAULT_AMPLITUDE)) else @Suppress("DEPRECATION") vibrator.vibrate(30L) }
}
