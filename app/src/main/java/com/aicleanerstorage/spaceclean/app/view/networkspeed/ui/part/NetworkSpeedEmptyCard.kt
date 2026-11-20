package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedCardBg
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedCardRadius
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedNavy
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.NetworkSpeedNavyMuted

@Composable
internal fun NetworkSpeedEmptyCard(
    title: String,
    message: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NetworkSpeedCardBg,
        shape = RoundedCornerShape(NetworkSpeedCardRadius),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                color = NetworkSpeedNavy,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                color = NetworkSpeedNavyMuted,
                fontSize = 15.sp,
                lineHeight = 20.sp,
            )
        }
    }
}
