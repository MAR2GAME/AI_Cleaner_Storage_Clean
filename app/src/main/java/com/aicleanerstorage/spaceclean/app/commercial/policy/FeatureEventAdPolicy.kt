package com.aicleanerstorage.spaceclean.app.commercial.policy

import com.aicleanerstorage.spaceclean.core.contracts.ads.AdScene
import com.aicleanerstorage.spaceclean.core.contracts.ads.adRequestId
import com.aicleanerstorage.spaceclean.core.contracts.ads.toAdScene
import com.aicleanerstorage.spaceclean.core.contracts.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent

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
