package com.aicleanerstorage.spaceclean.app.integration

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReturnAdPolicyTest {
    @Test
    fun leavingFeatureToMainShowsReturnAd() {
        assertTrue(ReturnAdPolicy.shouldShowReturnAd(AppDestination.JunkClean, backTargetIsMain = true))
    }

    @Test
    fun internalBackDoesNotShowReturnAd() {
        assertFalse(ReturnAdPolicy.shouldShowReturnAd(AppDestination.AntiVirus, backTargetIsMain = false))
    }

    @Test
    fun noSourceNeverShowsReturnAd() {
        assertFalse(ReturnAdPolicy.shouldShowReturnAd(null, backTargetIsMain = true))
    }
}
