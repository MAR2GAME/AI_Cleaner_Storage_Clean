package com.aicleanerstorage.spaceclean.base.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MainTabRequestTest {
    @Test
    fun `encode and decode round trips every tab`() {
        MainTab.entries.forEach { tab ->
            assertEquals(tab, MainTabRequest.decode(MainTabRequest.encode(tab)))
        }
    }

    @Test
    fun `decode accepts legacy enum value`() {
        assertEquals(MainTab.Toolbox, MainTabRequest.decode(MainTab.Toolbox))
    }

    @Test
    fun `decode ignores unknown or empty values`() {
        assertNull(MainTabRequest.decode("Gallery"))
        assertNull(MainTabRequest.decode(null))
        assertNull(MainTabRequest.decode(42))
    }
}
