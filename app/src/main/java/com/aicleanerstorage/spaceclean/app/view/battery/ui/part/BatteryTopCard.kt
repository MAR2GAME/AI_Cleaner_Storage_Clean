package com.aicleanerstorage.spaceclean.app.view.battery.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun BatteryTopCard(
    statusText: String,
    capacityText: String,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(132.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                BatterySummaryLine(
                    label = stringResource(R.string.battery_status),
                    value = localizedBatteryStatus(statusText),
                )
                BatterySummaryLine(
                    label = stringResource(R.string.battery_capacity),
                    value = capacityText,
                )
            }
            Spacer(Modifier.weight(1.0f))
            Box(
                Modifier.padding(0.dp)
                .width(103.63672.dp)
                .height(60.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.battery_info),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
            }
        }
    }
}

internal val BatteryText = Color(0xFF282927)

private val BatteryMutedText = BatteryText.copy(alpha = 0.65f)

@Composable
private fun BatterySummaryLine(
    label: String,
    value: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = BatteryMutedText,
            fontSize = 14.sp,
            lineHeight = 17.sp,
        )
        Text(
            text = value,
            color = BatteryText,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun localizedBatteryStatus(status: String): String =
    when (status) {
        "Charging" -> stringResource(R.string.battery_charging)
        "Discharging" -> stringResource(R.string.battery_discharging)
        "Full" -> stringResource(R.string.battery_full)
        "Not Charging" -> stringResource(R.string.battery_not_charging)
        else -> stringResource(R.string.device_unknown)
    }
