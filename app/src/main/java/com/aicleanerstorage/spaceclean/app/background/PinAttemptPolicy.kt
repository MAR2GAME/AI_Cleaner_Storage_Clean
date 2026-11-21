package com.aicleanerstorage.spaceclean.app.background

/**
 * Tracks failed PIN attempts and decides when the keypad is temporarily locked.
 *
 * Kept free of Android types so the lockout behaviour can be unit tested.
 */
class PinAttemptPolicy(
    private val maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
    private val lockoutMillis: Long = DEFAULT_LOCKOUT_MILLIS,
) {
    private var failures = 0
    private var lockedUntilMillis = 0L

    /**
     * Records a failed verification. Returns true when this attempt exhausted the
     * allowance and the keypad is now locked out.
     */
    fun registerFailure(nowMillis: Long): Boolean {
        failures += 1
        if (failures < maxAttempts) return false
        failures = 0
        lockedUntilMillis = nowMillis + lockoutMillis
        return true
    }

    /** Starts a new lock screen session: the counter and lockout belong to one session. */
    fun reset() {
        failures = 0
        lockedUntilMillis = 0L
    }

    fun isLockedOut(nowMillis: Long): Boolean = nowMillis < lockedUntilMillis

    fun remainingSeconds(nowMillis: Long): Long = ((lockedUntilMillis - nowMillis) / 1_000L).coerceAtLeast(1L)

    companion object {
        const val DEFAULT_MAX_ATTEMPTS = 5
        const val DEFAULT_LOCKOUT_MILLIS = 30_000L
    }
}
