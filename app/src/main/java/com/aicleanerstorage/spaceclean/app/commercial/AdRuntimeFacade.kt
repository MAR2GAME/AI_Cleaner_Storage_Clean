package com.aicleanerstorage.spaceclean.app.commercial

import android.content.Context
import com.aicleanerstorage.spaceclean.common.ads.AdPreloadStage
import com.aicleanerstorage.spaceclean.common.ads.AdRuntime
import com.aicleanerstorage.spaceclean.common.ads.InterstitialAdRunner

class AdRuntimeFacade(
    private val runtime: AdRuntime,
) : InterstitialAdRunner by runtime {
    fun preload(stage: AdPreloadStage, context: Context) = runtime.preload(stage, context)
    fun markExternalActivityLaunch() = runtime.markExternalActivityLaunch()
    fun cancelExternalActivityLaunch() = runtime.cancelExternalActivityLaunch()
    fun onHostResumed() = runtime.onHostResumed()
    fun dispose() = runtime.dispose()
}
