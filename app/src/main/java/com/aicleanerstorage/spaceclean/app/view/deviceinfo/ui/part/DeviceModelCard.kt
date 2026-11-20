package com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part

import android.os.Build
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.orPlaceholder
import com.aicleanerstorage.spaceclean.base.designsystem.theme.CleanXMutedText
import com.aicleanerstorage.spaceclean.domain.model.DeviceHardwareInfoModel

@Composable
internal fun DeviceModelCard(hardware: DeviceHardwareInfoModel) {
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
                modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                SummaryTextLine(
                    label = stringResource(R.string.home_device_model),
                    value = deviceModelName(hardware.model),
                )
                Spacer(modifier = Modifier.height(8.dp))
                SummaryTextLine(
                    label = stringResource(R.string.home_system_version),
                    value = hardware.androidVersion.orPlaceholder(),
                )
            }

            Spacer(Modifier.weight(1.0f))

            Box(
                Modifier.padding(2.72727.dp)
                    .width(120.dp)
                    .height(96.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.device_model),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
            }

        }
    }
}

internal val DeviceTextColor = Color(0xFF282927)

@Composable
private fun SummaryTextLine(
    label: String,
    value: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = CleanXMutedText,
            fontSize = 14.sp,
            lineHeight = 17.sp,
        )
        Text(
            text = value,
            color = DeviceTextColor,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun deviceModelName(model: String): String {
    val resolvedModel = model.orPlaceholder()
    val manufacturer = Build.MANUFACTURER.orEmpty().trim()
    if (resolvedModel == "--" || manufacturer.isBlank()) return resolvedModel
    return if (resolvedModel.contains(manufacturer, ignoreCase = true)) {
        resolvedModel
    } else {
        "$manufacturer-$resolvedModel"
    }
}
