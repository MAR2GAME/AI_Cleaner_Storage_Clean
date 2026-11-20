package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun VirusScanNoticeDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    VirusDialogPanel(
        onDismiss = onDecline,
        surfaceColor = Color.Transparent,
        panelModifier = Modifier.offset(y = 16.dp).background(
            Brush.verticalGradient(
                0f to Color(0xFFDFFFDC),
                0.26381f to Color.White,
                1f to Color.White,
            ),
            virusDialogShape,
        ),
    ) {
        Image(
            painter = painterResource(R.drawable.virus_app_list_permission),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(width = 137.dp, height = 132.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.virus_app_list_permission_title),
            color = VirusNoticeText,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.72.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.virus_app_list_permission_message),
            color = VirusNoticeText,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            letterSpacing = 0.54.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(20.dp))
        VirusNoticeFilledButton(stringResource(R.string.virus_app_list_permission_agree), onAccept)
        Spacer(modifier = Modifier.height(12.dp))
        VirusNoticeOutlinedButton(stringResource(R.string.virus_app_list_permission_not_now), onDecline)
    }
}

private val VirusNoticeText = Color(0xFF282927)

private val VirusNoticeGreen = Color(0xFF5DD595)

private val VirusNoticeLime = Color(0xFFD0FD91)

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
                .clip(virusDialogButtonShape)
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
                .border(1.56.dp, Color(0xFF999999), virusDialogButtonShape)
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
