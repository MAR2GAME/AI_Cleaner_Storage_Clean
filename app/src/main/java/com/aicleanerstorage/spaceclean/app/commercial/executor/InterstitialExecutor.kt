package com.aicleanerstorage.spaceclean.app.commercial.executor

import com.aicleanerstorage.spaceclean.common.ads.AdScene
import com.aicleanerstorage.spaceclean.common.ads.InterstitialAdRunner
import com.aicleanerstorage.spaceclean.common.ads.once
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest

class InterstitialExecutor(
    private val runtime: InterstitialAdRunner,
) : InterstitialAdRunner {
    fun execute(request: InterstitialRequest, onContinue: () -> Unit = {}) {
        runtime.run(request.scene, request.requestId, once(onContinue))
    }

    override fun run(scene: AdScene?, requestId: String, onContinue: () -> Unit) {
        runtime.run(scene, requestId, once(onContinue))
    }

    override fun runRouteEntry(fromRoute: String?, targetRoute: String?, onContinue: () -> Unit) {
        runtime.runRouteEntry(fromRoute, targetRoute, once(onContinue))
    }
}
