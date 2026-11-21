package com.aicleanerstorage.devicekit

import android.content.Context
import com.aicleanerstorage.devicekit.internal.DeviceInfoRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object DeviceKit {
    @JvmOverloads
    fun create(context: Context, ioDispatcher: CoroutineDispatcher = Dispatchers.IO): DeviceInfoRepository =
        DeviceInfoRepositoryImpl(context.applicationContext, ioDispatcher)
}
