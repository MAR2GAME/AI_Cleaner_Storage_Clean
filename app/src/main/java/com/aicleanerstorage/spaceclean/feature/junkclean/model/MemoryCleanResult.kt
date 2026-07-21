package com.aicleanerstorage.spaceclean.feature.junkclean.model

import com.aicleanerstorage.spaceclean.common.format.FileSizeFormatter

data class MemoryCleanResult(
    val killedCount: Int,
    val freedBytes: Long,
    val beforeAvailBytes: Long,
    val afterAvailBytes: Long,
) {
    val freedFormatted: String get() = FileSizeFormatter.format(freedBytes)

    val improvementPercent: Int get() =
        if (beforeAvailBytes > 0) {
            ((freedBytes.toFloat() / beforeAvailBytes) * 100).toInt().coerceAtMost(100)
        } else {
            0
        }
}
