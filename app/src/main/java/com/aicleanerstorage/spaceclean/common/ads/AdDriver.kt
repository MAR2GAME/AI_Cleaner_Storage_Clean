package com.aicleanerstorage.spaceclean.common.ads

import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.core.presentation.IntentPolicy

fun interface AdDriver {
    suspend fun show(request: InterstitialRequest)
}

sealed interface AdNeed : IntentPolicy {
    override val priority: Int get() = 200

    data object JunkCleanCompletion : AdNeed
}
