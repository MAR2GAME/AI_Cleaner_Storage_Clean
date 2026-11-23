package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import org.junit.Assert.assertEquals
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
}
