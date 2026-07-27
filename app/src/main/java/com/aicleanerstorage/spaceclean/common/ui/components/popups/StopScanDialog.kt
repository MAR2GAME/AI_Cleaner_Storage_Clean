package com.aicleanerstorage.spaceclean.common.ui.components.popups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aicleanerstorage.spaceclean.R

private val StopDialogTitleColor = Color(0xFF282927)
private val StopDialogBorderColor = Color(0xFF999999)
private val StopDialogButtonStart = Color(0xFFD0FD91)
private val StopDialogButtonEnd = Color(0xFF5DD595)
private val StopDialogCardStart = Color(0xFFDFFFDC)

@Composable
internal fun StopScanDialog(onQuit: () -> Unit, onResume: () -> Unit) {
    Dialog(
        onDismissRequest = onResume,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onResume,
                        ),
            )
            StopScanDialogCard(
                onQuit = onQuit,
                onResume = onResume,
                modifier = Modifier.align(Alignment.Center).offset(y = 21.dp),
            )
        }
    }
}

@Composable
private fun StopScanDialogCard(
    onQuit: () -> Unit,
    onResume: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .padding(horizontal = 16.dp)
                .widthIn(max = 343.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colorStops =
                            arrayOf(
                                0f to StopDialogCardStart,
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
                .padding(start = 16.dp, end = 16.dp, top = 104.dp, bottom = 24.dp),
    ) {
        val title = stringResource(R.string.file_stop_scan_title)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = title,
                color = StopDialogTitleColor,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.72.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth().height(44.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onQuit,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(1.56.dp, StopDialogBorderColor),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = StopDialogBorderColor,
                        ),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        text = stringResource(R.string.file_quit),
                        color = StopDialogBorderColor,
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
                Button(
                    onClick = onResume,
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(StopDialogButtonStart, StopDialogButtonEnd),
                                ),
                                RoundedCornerShape(26.dp),
                            ),
                    shape = RoundedCornerShape(26.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = StopDialogTitleColor,
                        ),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        text = stringResource(R.string.file_resume),
                        color = StopDialogTitleColor,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
