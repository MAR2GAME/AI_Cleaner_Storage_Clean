package com.aicleanerstorage.spaceclean.data.local.deviceinfo

import android.os.Environment
import android.os.StatFs
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel

internal object StorageDataSource {
    fun getInternalStorageInfo(): CapacityInfoModel =
        try {
            val stat = StatFs(Environment.getDataDirectory().absolutePath)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong
            val totalBytes = blockSize * totalBlocks
            val availableBytes = blockSize * availableBlocks
            CapacityInfoModel(totalBytes, availableBytes)
        } catch (e: Exception) {
            CapacityInfoModel(0, 0)
        }
}

