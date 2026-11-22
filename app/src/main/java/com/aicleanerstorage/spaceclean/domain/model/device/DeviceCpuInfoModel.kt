package com.aicleanerstorage.spaceclean.domain.model.device

/** CPU 型号与频率信息。 */
data class DeviceCpuInfoModel(
    val hardware: String,
    val model: String,
    val cores: Int,
    val maxFrequency: String,
)
