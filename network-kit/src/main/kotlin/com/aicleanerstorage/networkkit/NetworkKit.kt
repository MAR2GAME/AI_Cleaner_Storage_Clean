package com.aicleanerstorage.networkkit

import android.content.Context
import com.aicleanerstorage.networkkit.internal.NetworkRepositoryImpl

object NetworkKit {
    @JvmOverloads
    fun create(context: Context, endpoints: NetworkSpeedEndpoints = NetworkSpeedEndpoints()): NetworkRepository =
        NetworkRepositoryImpl(context.applicationContext, endpoints)
}
