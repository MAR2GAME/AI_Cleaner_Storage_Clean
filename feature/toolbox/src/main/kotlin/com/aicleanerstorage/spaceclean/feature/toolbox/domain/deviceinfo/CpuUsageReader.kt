package com.aicleanerstorage.spaceclean.feature.toolbox.domain.deviceinfo

fun interface CpuUsageReader {
    suspend fun readUsagePercent(previousPercent: Int?): Int?
}

