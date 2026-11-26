package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.MainTab
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FileManagerPermissionStateTest {
    @Test
    fun `requestPermission falls back to denied without requester`() {
        val state = FileManagerPermissionState(setOf(AppPermission.StorageFiles), initialGranted = false)
        var result: Boolean? = null

        state.requestPermission { result = it }

        assertEquals(false, result)
    }

    @Test
    fun `requestPermission delegates to attached requester`() {
        val state = FileManagerPermissionState(setOf(AppPermission.StorageFiles), initialGranted = false)
        state.attachPermissionRequester { onResult -> onResult(true) }
        var result: Boolean? = null

        state.requestPermission { result = it }

        assertEquals(true, result)
    }

    @Test
    fun `leaving to home lands on the files tab`() {
        val state = FileManagerPermissionState(setOf(AppPermission.StorageFiles), initialGranted = true)
        val navigator = RecordingNavigator()

        state.leaveHome(navigator)

        assertEquals(listOf(AppDestination.Main(initialTab = MainTab.Files)), navigator.resetDestinations)
        assertTrue(state.leavingPage)
    }

    private class RecordingNavigator : AppNavigator {
        val resetDestinations = mutableListOf<AppDestination>()

        override fun navigate(destination: AppDestination) = Unit

        override fun replace(destination: AppDestination) = Unit

        override fun resetToRoot(destination: AppDestination) {
            resetDestinations += destination
        }

        override fun back(): Boolean = false
    }
}
