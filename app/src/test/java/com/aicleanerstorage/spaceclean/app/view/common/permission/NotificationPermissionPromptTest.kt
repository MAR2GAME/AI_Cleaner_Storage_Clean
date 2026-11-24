package com.aicleanerstorage.spaceclean.app.view.common.permission

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationPermissionPromptTest {
    @Test
    fun freshSplashCanRequestOnAndroid13() {
        assertTrue(shouldRequestOnSplash(onSplash = true, attempted = false, sdkInt = 33))
    }

    @Test
    fun alreadyAttemptedSplashSkipsRepeatRequest() {
        assertFalse(shouldRequestOnSplash(onSplash = true, attempted = true, sdkInt = 33))
    }

    @Test
    fun leavingSplashSkipsSplashRequest() {
        assertFalse(shouldRequestOnSplash(onSplash = false, attempted = false, sdkInt = 33))
    }

    @Test
    fun android12HasNoRuntimeNotificationRequest() {
        assertFalse(shouldRequestOnSplash(onSplash = true, attempted = false, sdkInt = 32))
    }
}
