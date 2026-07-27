package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.ui.components.ToolFeatureBanners
import com.aicleanerstorage.spaceclean.feature.junkclean.JunkCleanResultUiState

private val ResultText = Color(0xFF282927)
private val ResultSubtext = Color(0xFF999999)
private val ResultAction = Color(0xFF5DD595)

@Composable
internal fun JunkCleanResultView(
    uiState: JunkCleanResultUiState,
    onNavigateTool: (AppDestination) -> Unit,
) {
    val formatted = uiState.formattedFreedSpace
    val showResult = uiState.hasVisibleResult && formatted.isNotBlank()
    val numberPart = if (showResult) formatted.substringBeforeLast(" ", formatted) else "0"
    val unitPart = if (showResult) formatted.substringAfterLast(" ", "MB") else "MB"

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 175.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFD0FD91), ResultAction)),
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = numberPart,
                    color = ResultText,
                    fontSize = 33.85.sp,
                    lineHeight = 39.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unitPart,
                    color = ResultText,
                    fontSize = 18.46.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Text(
            text =
                if (uiState.failedCount > 0) {
                    stringResource(R.string.clean_result_junk_removed_with_failed, uiState.failedCount)
                } else {
                    stringResource(R.string.clean_result_junk_removed)
                },
            color = ResultSubtext,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 231.dp),
        )

        ToolFeatureBanners(
            onNavigateTool = onNavigateTool,
            excludedDestinations = setOf(AppDestination.JunkClean),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 284.dp),
        )
    }
}
