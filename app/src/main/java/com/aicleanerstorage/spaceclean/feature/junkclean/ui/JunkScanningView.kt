package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.animations.CleanSpiralAnimation
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanScanState
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanUiState
import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkCategory

private val JunkScanText = Color(0xFF282927)
private val JunkScanNumber = Color(0xFF2D3748)
private val JunkScanUnit = Color(0xA62D3748)

@Composable
internal fun JunkScanningView(uiState: JunkCleanUiState) {
    val categoryLabel = uiState.currentCategory?.let { stringResource(it.titleRes) }
    val scanningText =
        categoryLabel?.let { stringResource(R.string.scan_scanning_category, it) }
            ?: stringResource(R.string.scan_loading_fallback)
    val errorText =
        uiState.errorMessage ?: uiState.errorMessageRes?.let { stringResource(it) }
            ?: stringResource(R.string.scan_failed)
    val caption =
        if (uiState.scanState == JunkCleanScanState.Error) {
            errorText
        } else {
            scanningText
        }
    val foundSize = normalizeScanSize(uiState.formattedFoundSize)

    Box(modifier = Modifier.fillMaxSize()) {
        CleanSpiralAnimation(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 163.dp),
            containerSize = 268.dp,
            centerSize = 200.dp,
            spiralResId = R.drawable.junk_scan_spiral_figma,
            animationDurationMillis = 2000,
        ) {
            ScanSizeLabel(formattedSize = foundSize)
        }
        Text(
            text = caption,
            color = JunkScanText,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 458.dp),
        )
    }
}

@Composable
private fun ScanSizeLabel(formattedSize: String) {
    Column(
        modifier = Modifier.size(112.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = sizeMainValue(formattedSize),
            color = JunkScanNumber,
            fontSize = 40.sp,
            fontWeight = FontWeight.W600,
            textAlign = TextAlign.Center,
        )
        Text(
            text = sizeUnitValue(formattedSize).ifBlank { "kB" },
            color = JunkScanUnit,
            fontSize = 22.sp,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Center,
        )
    }
}

private fun sizeMainValue(formattedSize: String): String =
    formattedSize.trim().takeWhile { it.isDigit() || it == '.' }.ifBlank { "0" }

private fun sizeUnitValue(formattedSize: String): String =
    formattedSize.trim().dropWhile { it.isDigit() || it == '.' }.trim()

private fun normalizeScanSize(formattedSize: String): String {
    val trimmed = formattedSize.trim()
    return if (trimmed.isBlank() || trimmed == "0 B") "0 kB" else trimmed
}

private val JunkCategory.titleRes: Int
    @StringRes
    get() =
        when (this) {
            JunkCategory.CACHE -> R.string.category_cache
            JunkCategory.TEMP_FILE -> R.string.category_temp
            JunkCategory.RESIDUAL -> R.string.category_residual
            JunkCategory.APK -> R.string.category_apk
            JunkCategory.DUPLICATE -> R.string.category_duplicate
            JunkCategory.LARGE_FILE -> R.string.category_large
        }
