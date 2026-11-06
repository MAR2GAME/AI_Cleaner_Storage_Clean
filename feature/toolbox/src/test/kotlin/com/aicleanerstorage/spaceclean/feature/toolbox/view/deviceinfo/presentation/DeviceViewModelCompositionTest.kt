package com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation

import com.aicleanerstorage.spaceclean.feature.toolbox.view.deviceinfo.presentation.DeviceInfoViewModel

import com.aicleanerstorage.spaceclean.feature.toolbox.view.battery.presentation.BatteryInfoViewModel

import androidx.lifecycle.ViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DeviceViewModelCompositionTest {
    @Test
    fun `battery view model composes shared controller instead of inheriting device view model`() {
        assertFalse(DeviceInfoViewModel::class.java.isAssignableFrom(BatteryInfoViewModel::class.java))
        assertEquals(com.aicleanerstorage.spaceclean.core.presentation.MviViewModel::class.java, BatteryInfoViewModel::class.java.superclass)
    }
}

