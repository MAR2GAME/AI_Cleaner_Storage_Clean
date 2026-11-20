package com.aicleanerstorage.networkkit.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NetworkMeasurementTest {
    @Test
    fun missingDirectionAndZeroRateStayDistinct() {
        assertNull(measuredNetworkSpeed(null, null, 10))
        val result = requireNotNull(measuredNetworkSpeed(0.0, null, 10))
        assertEquals(0.0, requireNotNull(result.downloadMbps), 0.0)
        assertNull(result.uploadMbps)
        assertEquals(true, result.measured)
        val precise = requireNotNull(measuredNetworkSpeed(null, 2.34567, null))
        assertEquals(2.34567, requireNotNull(precise.uploadMbps), 0.0)
    }
}
