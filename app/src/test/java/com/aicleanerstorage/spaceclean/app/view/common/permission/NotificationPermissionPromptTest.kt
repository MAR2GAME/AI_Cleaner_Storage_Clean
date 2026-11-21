package com.aicleanerstorage.spaceclean.app.view.common.permission

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class NotificationPermissionPromptTest {
    private val now = timestamp(2026, Calendar.NOVEMBER, 20, 10, 0)
    private val earlierSameDay = timestamp(2026, Calendar.NOVEMBER, 20, 0, 1)
    private val previousDay = timestamp(2026, Calendar.NOVEMBER, 19, 23, 59)
    private val nextDay = timestamp(2026, Calendar.NOVEMBER, 21, 0, 1)

    @Test
    fun `never prompted before is allowed`() {
        assertTrue(canPromptToday(0L, now))
    }

    @Test
    fun `prompted earlier today is blocked`() {
        assertFalse(canPromptToday(earlierSameDay, now))
    }

    @Test
    fun `prompted previous day is allowed`() {
        assertTrue(canPromptToday(previousDay, now))
    }

    @Test
    fun `clock moved backwards is allowed`() {
        assertTrue(canPromptToday(nextDay, now))
    }

    private fun timestamp(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): Long =
        Calendar
            .getInstance()
            .apply {
                set(year, month, day, hour, minute, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
}
