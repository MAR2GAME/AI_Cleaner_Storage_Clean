package com.aicleanerstorage.spaceclean.base.permission

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionGateTest {
    private var granted = false
    private var afterSystemUi: (() -> Unit)? = null
    private val requested = mutableListOf<AppPermission>()
    private val openedSettings = mutableListOf<AppPermission>()

    private fun gate(): PermissionGate =
        PermissionGate(
            hasPermission = { granted },
        ).apply {
            attach(
                launchSystemUi = { permission, onDone ->
                    requested += permission
                    afterSystemUi = onDone
                },
                openSettings = { permission -> openedSettings += permission },
            )
        }

    @Test
    fun `result comes from recheck not from launcher callback`() {
        var result: Boolean? = null

        gate().request(AppPermission.PostNotifications) { result = it }
        afterSystemUi?.invoke()

        assertEquals(false, result)
    }

    @Test
    fun `request while another request is in flight is ignored`() {
        val gate = gate()
        var first = 0
        var second = 0

        gate.request(AppPermission.StorageFiles) { first++ }
        gate.request(AppPermission.Location) { second++ }
        afterSystemUi?.invoke()

        assertEquals(listOf(AppPermission.StorageFiles), requested)
        assertEquals(1, first)
        assertEquals(0, second)
    }

    @Test
    fun `unavailable system ui finishes immediately`() {
        granted = true
        var result: Boolean? = null
        val gate =
            PermissionGate(hasPermission = { granted }).apply {
                attach(launchSystemUi = { _, onDone -> onDone() }, openSettings = {})
            }

        gate.request(AppPermission.StorageFiles) { result = it }

        assertEquals(true, result)
    }

    @Test
    fun `request without attached launchers finishes immediately`() {
        var result: Boolean? = null

        PermissionGate(hasPermission = { granted })
            .request(AppPermission.StorageFiles) { result = it }

        assertEquals(false, result)
    }

    @Test
    fun `open settings forwards without requesting runtime permission`() {
        val gate = gate()

        val permissions =
            listOf(
                AppPermission.StorageFiles,
                AppPermission.MediaLocation,
                AppPermission.UsageAccess,
                AppPermission.Location,
                AppPermission.NotificationListener,
                AppPermission.PostNotifications,
            )
        permissions.forEach(gate::openSettings)
        granted = true
        permissions.forEach(gate::openSettings)

        assertEquals(permissions + permissions, openedSettings)
        assertTrue(requested.isEmpty())
    }
}
