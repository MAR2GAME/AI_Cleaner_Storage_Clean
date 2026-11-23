package com.aicleanerstorage.spaceclean.data.local.junkclean

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Process
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkMemoryCleanModel

internal object MemoryCleaner {
    fun clean(context: Context): JunkMemoryCleanModel {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val manager = context.getSystemService(ActivityManager::class.java)
            manager.runningAppProcesses
                .orEmpty()
                .filter { it.pid != Process.myPid() && it.importance >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED }
                .flatMap { it.pkgList.orEmpty().toList() }
                .distinct()
                .filter { it != context.packageName }
                .forEach(manager::killBackgroundProcesses)
        }
        return JunkMemoryCleanModel(freedBytes = 0L)
    }
}
