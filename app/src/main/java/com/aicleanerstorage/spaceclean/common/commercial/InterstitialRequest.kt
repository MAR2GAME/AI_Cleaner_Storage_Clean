package com.aicleanerstorage.spaceclean.common.commercial

import com.aicleanerstorage.spaceclean.common.ads.AdScene

data class InterstitialRequest(
    val scene: AdScene,
    val requestId: String,
)
