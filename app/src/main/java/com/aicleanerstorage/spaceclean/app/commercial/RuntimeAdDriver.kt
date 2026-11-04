package com.aicleanerstorage.spaceclean.app.commercial

import com.aicleanerstorage.spaceclean.common.ads.AdDriver
import com.aicleanerstorage.spaceclean.common.ads.AdRuntime
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class RuntimeAdDriver(private val runtime: AdRuntime) : AdDriver {
    override suspend fun show(request: InterstitialRequest): Unit = suspendCancellableCoroutine { continuation ->
        val handle = runtime.runCancellable(request.scene, request.requestId) {
            if (continuation.isActive) continuation.resume(Unit)
        }
        continuation.invokeOnCancellation { handle.cancel() }
    }
}
