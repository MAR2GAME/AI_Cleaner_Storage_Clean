package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun VirusSafeResultPanel(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(Color(0xFFD0FD91), VirusResultGreen))),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_virus_safe_check_figma),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
        )
    }
}

val VirusResultGreen = Color(0xFF5DD595)
