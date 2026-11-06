package com.aicleanerstorage.spaceclean.core.contracts.commercial

import com.aicleanerstorage.spaceclean.core.contracts.ads.AdScene

data class InterstitialRequest(
    val scene: AdScene,
    val requestId: String,
)
