package com.aicleanerstorage.spaceclean.data.local.junkclean

import android.app.ActivityManager
import android.content.Context
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkMemoryCleanModel
import kotlinx.coroutines.delay

internal object MemoryCleaner {
    /**
     * Samples the change in system-wide available memory; this does not measure disk space.
     */
    suspend fun clean(context: Context): JunkMemoryCleanModel {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val beforeMem = ActivityManager.MemoryInfo().also { activityManager.getMemoryInfo(it) }
        val beforeAvail = beforeMem.availMem

        delay(300L)

        val afterMem = ActivityManager.MemoryInfo().also { activityManager.getMemoryInfo(it) }
        val afterAvail = afterMem.availMem
        val freed = (afterAvail - beforeAvail).coerceAtLeast(0)

        return JunkMemoryCleanModel(freed)
    }
}
