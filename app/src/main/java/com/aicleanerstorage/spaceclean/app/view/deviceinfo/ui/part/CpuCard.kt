package com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.orPlaceholder
import com.aicleanerstorage.spaceclean.domain.model.DeviceHardwareInfoModel

@Composable
internal fun CpuCard(hardware: DeviceHardwareInfoModel) {
    val cpuCoreCount =
        hardware.cpu.cores
            .takeIf { it > 0 }
            ?.toString()
            ?: "--"
    InfoSection(
        title = stringResource(R.string.device_cpu),
        rows =
            listOf(
                stringResource(R.string.device_cpu_hardware) to hardware.cpu.hardware.orPlaceholder(),
                stringResource(R.string.device_cpu_model) to hardware.cpu.model.orPlaceholder(),
                stringResource(R.string.device_cpu_cores) to cpuCoreCount,
                stringResource(R.string.device_cpu_frequency) to hardware.cpu.maxFrequency.orPlaceholder(),
            ),
    )
}
