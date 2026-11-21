package com.aicleanerstorage.devicekit.internal

import android.os.Environment
import android.os.StatFs
import com.aicleanerstorage.devicekit.model.StorageInfo

internal object StorageDataSource {
    fun getInternalStorageInfo(): StorageInfo =
        try {
            val stat = StatFs(Environment.getDataDirectory().absolutePath)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong
            val totalBytes = blockSize * totalBlocks
            val availableBytes = blockSize * availableBlocks
            StorageInfo(totalBytes, availableBytes)
        } catch (e: Exception) {
            StorageInfo(0, 0)
        }
}

