package com.aicleanerstorage.spaceclean.app.view.battery.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.displayBatteryTemperatureValue
import java.util.Locale

@Composable
internal fun BatteryInformationCard(uiState: DeviceInfoUiState) {
    val voltageValue =
        uiState.battery.voltage.takeIf { it > 0 }?.let {
            String.format(Locale.US, "%.1f", it / 1000f)
        } ?: "--"
    val metrics =
        listOf(
            BatteryMetricUi(
                iconRes = R.drawable.temperature,
                iconSizeDp = 32,
                label = stringResource(R.string.battery_temperature),
                value =
                    String.format(
                        Locale.US,
                        "%.1f",
                        displayBatteryTemperatureValue(
                            uiState.displayTemperatureC,
                            uiState.temperatureUnit,
                        ),
                    ),
                unit = "\u00B0${uiState.temperatureUnit}",
            ),
            BatteryMetricUi(
                iconRes = R.drawable.voltage,
                label = stringResource(R.string.battery_voltage),
                value = voltageValue,
                unit = if (voltageValue == "--") null else "V",
            ),
            BatteryMetricUi(
                iconRes = R.drawable.battery_health,
                label = stringResource(R.string.battery_health),
                value = localizedBatteryHealth(uiState.battery.health),
            ),
            BatteryMetricUi(
                iconRes = R.drawable.life,
                label = stringResource(R.string.battery_life),
                value = uiState.batteryLifeText,
            ),
        )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
        ) {
            BatterySectionHeader(stringResource(R.string.battery_information))
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                metrics.forEach { metric ->
                    BatteryMetricItem(
                        metric = metric,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private val BatteryMetricLabel = Color(0xFF999999)

private val BatteryDivider = BatteryText.copy(alpha = 0.20f)

@Composable
private fun BatteryMetricItem(
    metric: BatteryMetricUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(metric.iconRes),
                contentDescription = null,
                modifier = Modifier.size(metric.iconSizeDp.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = metric.label.uppercase(),
            color = BatteryMetricLabel,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = metric.value,
                color = BatteryText,
                fontSize = if (metric.value.length > 8) 16.sp else 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            metric.unit?.let { unit ->
                Text(
                    text = unit,
                    color = BatteryText,
                    fontSize = 10.sp,
                    lineHeight = 18.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

private data class BatteryMetricUi(
    val iconRes: Int,
    val label: String,
    val value: String,
    val unit: String? = null,
    val iconSizeDp: Int = 40,
)

@Composable
internal fun BatterySectionHeader(text: String) {
    Column {
        Text(
            text = text,
            color = BatteryText,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp)
                    .height(0.5.dp)
                    .background(BatteryDivider),
        )
    }
}

@Composable
private fun localizedBatteryHealth(health: String): String =
    when (health) {
        "Good" -> stringResource(R.string.battery_health_good)
        "Cold" -> stringResource(R.string.battery_health_cold)
        "Dead" -> stringResource(R.string.battery_health_dead)
        "Overheat" -> stringResource(R.string.battery_health_overheat)
        "Overvoltage" -> stringResource(R.string.battery_health_overvoltage)
        "Failure" -> stringResource(R.string.battery_health_failure)
        else -> stringResource(R.string.device_unknown)
    }
