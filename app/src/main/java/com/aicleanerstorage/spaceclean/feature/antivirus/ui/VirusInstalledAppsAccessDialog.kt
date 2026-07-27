package com.aicleanerstorage.spaceclean.feature.antivirus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.popups.InlinePermissionOverlay

private val VirusInstalledAppsDialogShape = RoundedCornerShape(12.dp)
private val VirusInstalledAppsButtonShape = RoundedCornerShape(28.dp)
private val VirusInstalledAppsPrimary = Color(0xFF22A9E8)
private val VirusInstalledAppsTitle = Color(0xFF1D2959)
private val VirusInstalledAppsBody = Color(0xA61D2959)
private val VirusNoticeText = Color(0xFF282927)
private val VirusNoticeGreen = Color(0xFF5DD595)
private val VirusNoticeLime = Color(0xFFD0FD91)

@Composable
internal fun VirusInstalledAppsAccessDialog(
    title: String,
    message: String,
    primaryText: String,
    onPrimaryAction: () -> Unit,
    secondaryText: String,
    onSecondaryAction: () -> Unit,
    onDismissRequest: () -> Unit = onSecondaryAction,
    showHeroImage: Boolean = false,
) {
    if (showHeroImage) {
        BackHandler(onBack = onDismissRequest)
        InlinePermissionOverlay(onDismiss = onDismissRequest) {
            VirusInstalledAppsPanel(
                title = title,
                message = message,
                primaryText = primaryText,
                onPrimaryAction = onPrimaryAction,
                secondaryText = secondaryText,
                onSecondaryAction = onSecondaryAction,
                showHeroImage = true,
            )
        }
        return
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        VirusInstalledAppsPanel(title, message, primaryText, onPrimaryAction, secondaryText, onSecondaryAction, false)
    }
}

@Composable
private fun VirusInstalledAppsPanel(
    title: String,
    message: String,
    primaryText: String,
    onPrimaryAction: () -> Unit,
    secondaryText: String,
    onSecondaryAction: () -> Unit,
    showHeroImage: Boolean,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .widthIn(max = 343.dp)
                .offset(y = if (showHeroImage) 16.dp else 0.dp)
                .then(
                    if (showHeroImage) {
                        Modifier.background(
                            Brush.verticalGradient(
                                colorStops =
                                    arrayOf(
                                        0f to Color(0xFFDFFFDC),
                                        0.26381f to Color.White,
                                        1f to Color.White,
                                    ),
                            ),
                            VirusInstalledAppsDialogShape,
                        )
                    } else {
                        Modifier
                    },
                ),
        color = if (showHeroImage) Color.Transparent else Color.White,
        shape = VirusInstalledAppsDialogShape,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (showHeroImage) {
                Image(
                    painter = painterResource(R.drawable.virus_app_list_permission),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(width = 137.dp, height = 132.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            Text(
                text = title,
                color = if (showHeroImage) VirusNoticeText else VirusInstalledAppsTitle,
                fontSize = if (showHeroImage) 24.sp else 18.sp,
                lineHeight = if (showHeroImage) 28.sp else 22.sp,
                letterSpacing = if (showHeroImage) 0.72.sp else 0.03.em,
                fontWeight = FontWeight.Medium,
                textAlign = if (showHeroImage) TextAlign.Center else TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = if (showHeroImage) VirusNoticeText else VirusInstalledAppsBody,
                fontSize = if (showHeroImage) 18.sp else 16.sp,
                lineHeight = if (showHeroImage) 21.sp else 20.sp,
                letterSpacing = if (showHeroImage) 0.54.sp else 0.03.em,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(if (showHeroImage) 20.dp else 16.dp))
            if (showHeroImage) {
                VirusNoticeFilledButton(primaryText, onPrimaryAction)
                Spacer(modifier = Modifier.height(12.dp))
                VirusNoticeOutlinedButton(secondaryText, onSecondaryAction)
            } else {
                VirusInstalledAppsFilledButton(primaryText, onPrimaryAction)
                Spacer(modifier = Modifier.height(16.dp))
                VirusInstalledAppsOutlinedButton(secondaryText, onSecondaryAction)
            }
        }
    }
}

@Composable
private fun VirusNoticeFilledButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(VirusInstalledAppsButtonShape)
                .background(Brush.horizontalGradient(listOf(VirusNoticeLime, VirusNoticeGreen)))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = VirusNoticeText,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun VirusNoticeOutlinedButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(46.dp)
                .border(1.56.dp, Color(0xFF999999), VirusInstalledAppsButtonShape)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = Color(0xFF999999),
            fontSize = 20.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun VirusInstalledAppsFilledButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = VirusInstalledAppsButtonShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = VirusInstalledAppsPrimary,
                contentColor = Color.White,
            ),
    ) {
        VirusInstalledAppsButtonText(text, Color.White)
    }
}

@Composable
private fun VirusInstalledAppsOutlinedButton(
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = VirusInstalledAppsButtonShape,
        border = BorderStroke(1.35.dp, VirusInstalledAppsPrimary),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = VirusInstalledAppsPrimary,
            ),
    ) {
        VirusInstalledAppsButtonText(text, VirusInstalledAppsPrimary)
    }
}

@Composable
private fun VirusInstalledAppsButtonText(
    text: String,
    color: Color,
) {
    Text(
        text = text,
        color = color,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}
