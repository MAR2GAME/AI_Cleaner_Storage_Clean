package com.aicleanerstorage.spaceclean.app.view.common.permission

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionGate
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PermissionPromptControllerTest {
    @Test
    fun `granted permission skips explanation`() {
        var result: PermissionResult? = null
        val controller = PermissionPromptController(PermissionGate { true })

        controller.request(AppPermission.StorageFiles) { result = it }

        assertEquals(PermissionResult.Granted, result)
        assertNull(controller.explaining)
        assertNull(controller.requesting)
    }

    @Test
    fun `confirmation moves from explanation to system request`() {
        var result: PermissionResult? = null
        val controller = PermissionPromptController(PermissionGate { false })

        controller.request(AppPermission.StorageFiles) { result = it }
        assertEquals(AppPermission.StorageFiles, controller.explaining)

        controller.confirmExplanation()
        assertNull(controller.explaining)
        assertEquals(AppPermission.StorageFiles, controller.requesting)

        controller.completeRequest(PermissionResult.Granted)
        assertEquals(PermissionResult.Granted, result)
        assertNull(controller.requesting)
    }

    @Test
    fun `dismissing explanation reports denied`() {
        var result: PermissionResult? = null
        val controller = PermissionPromptController(PermissionGate { false })

        controller.request(AppPermission.StorageFiles) { result = it }
        controller.dismissExplanation()

        assertEquals(PermissionResult.Denied, result)
        assertNull(controller.explaining)
        assertNull(controller.requesting)
    }

    @Test
    fun `request is ignored while another is in flight`() {
        var first: PermissionResult? = null
        var second: PermissionResult? = null
        val controller = PermissionPromptController(PermissionGate { false })

        controller.request(AppPermission.StorageFiles) { first = it }
        controller.request(AppPermission.UsageAccess) { second = it }

        assertEquals(AppPermission.StorageFiles, controller.explaining)
        assertNull(second)

        controller.dismissExplanation()
        assertEquals(PermissionResult.Denied, first)
    }
}
