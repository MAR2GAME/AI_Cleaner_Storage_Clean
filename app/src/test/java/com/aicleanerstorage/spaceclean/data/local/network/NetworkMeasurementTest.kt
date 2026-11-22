package com.aicleanerstorage.spaceclean.data.local.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NetworkMeasurementTest {
    @Test
    fun missingDirectionAndZeroRateStayDistinct() {
        assertNull(NetworkSpeedProbe.measuredSpeed(null, null, 10))
        val result = requireNotNull(NetworkSpeedProbe.measuredSpeed(0.0, null, 10))
        assertEquals(0.0, requireNotNull(result.downloadMbps), 0.0)
        assertNull(result.uploadMbps)
        assertEquals(true, result.measured)
        val precise = requireNotNull(NetworkSpeedProbe.measuredSpeed(null, 2.34567, null))
        assertEquals(2.34567, requireNotNull(precise.uploadMbps), 0.0)
    }
}
