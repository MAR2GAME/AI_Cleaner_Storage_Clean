package com.aicleanerstorage.spaceclean.app.view.common.permission

import org.junit.Assert.assertEquals
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

    @Test
    fun splashPromptUsesNoticeFlagOne() {
        assertEquals(1, notificationNoticeFlag(onSplash = true, hasCompletedCleanup = false))
        assertEquals(1, notificationNoticeFlag(onSplash = true, hasCompletedCleanup = true))
    }

    @Test
    fun homePromptUsesNoticeFlagTwoBeforeCleanup() {
        assertEquals(2, notificationNoticeFlag(onSplash = false, hasCompletedCleanup = false))
    }

    @Test
    fun homePromptUsesNoticeFlagThreeAfterCleanup() {
        assertEquals(3, notificationNoticeFlag(onSplash = false, hasCompletedCleanup = true))
    }
}
