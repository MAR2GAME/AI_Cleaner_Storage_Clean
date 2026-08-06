package com.aicleanerstorage.spaceclean.app.commercial.policy

import com.aicleanerstorage.spaceclean.common.ads.AdScene
import com.aicleanerstorage.spaceclean.common.ads.adRequestId
import com.aicleanerstorage.spaceclean.common.ads.toAdScene
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent

class FeatureEventAdPolicy {
    fun request(event: FeatureOperationEvent): InterstitialRequest? =
        event.toAdScene()?.let { scene -> InterstitialRequest(scene, event.adRequestId()) }

    fun exitRequest(feature: FeatureKey, reason: FeatureExitReason): InterstitialRequest =
        when (reason) {
            FeatureExitReason.Return -> InterstitialRequest(
                AdScene.ReturnHome(feature),
                "return_home_${feature.name}",
            )
            FeatureExitReason.PermissionRejected -> InterstitialRequest(
                AdScene.PermissionRejected(feature),
                "permission_rejected_${feature.name}",
            )
        }
}
