package com.aicleanerstorage.spaceclean.app.background

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PinAttemptPolicyTest {
    @Test
    fun locksOutAfterMaxAttempts() {
        val policy = PinAttemptPolicy(maxAttempts = 3, lockoutMillis = 30_000L)

        assertFalse(policy.registerFailure(1_000L))
        assertFalse(policy.registerFailure(2_000L))
        assertTrue(policy.registerFailure(3_000L))
        assertTrue(policy.isLockedOut(3_000L))
        assertEquals(30L, policy.remainingSeconds(3_000L))
    }

    @Test
    fun unlockAfterWindowExpires() {
        val policy = PinAttemptPolicy(maxAttempts = 1, lockoutMillis = 30_000L)

        assertTrue(policy.registerFailure(1_000L))
        assertTrue(policy.isLockedOut(30_999L))
        assertFalse(policy.isLockedOut(31_000L))
    }

    @Test
    fun attemptsResetAfterLockout() {
        val policy = PinAttemptPolicy(maxAttempts = 2, lockoutMillis = 10_000L)

        assertFalse(policy.registerFailure(0L))
        assertTrue(policy.registerFailure(1L))
        assertFalse(policy.registerFailure(2L))
    }

    @Test
    fun resetStartsAFreshSession() {
        val policy = PinAttemptPolicy(maxAttempts = 2, lockoutMillis = 30_000L)

        assertFalse(policy.registerFailure(1_000L))
        assertTrue(policy.registerFailure(2_000L))
        assertTrue(policy.isLockedOut(2_000L))

        policy.reset()

        assertFalse(policy.isLockedOut(2_000L))
        assertFalse(policy.registerFailure(3_000L))
    }
}
