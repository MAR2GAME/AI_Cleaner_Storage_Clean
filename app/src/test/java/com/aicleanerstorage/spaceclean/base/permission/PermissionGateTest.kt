package com.aicleanerstorage.spaceclean.base.permission

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionGateTest {
    private var granted = false
    private var rationale = false
    private var afterSystemUi: (() -> Unit)? = null
    private val requested = mutableListOf<AppPermission>()
    private val openedSettings = mutableListOf<AppPermission>()

    private fun gate(): PermissionGate =
        PermissionGate(
            hasPermission = { granted },
            rationale = { rationale },
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
    fun `granted permission runs without launching system ui`() {
        granted = true
        var ran = false

        gate().allow(AppPermission.StorageFiles) { ran = true }

        assertTrue(ran)
        assertTrue(requested.isEmpty())
    }

    @Test
    fun `denied permission launches first and runs only after recheck passes`() {
        var ran = false
        val gate = gate()

        gate.allow(AppPermission.StorageFiles) { ran = true }

        assertEquals(listOf(AppPermission.StorageFiles), requested)
        assertFalse(ran)
        granted = true
        afterSystemUi?.invoke()
        assertTrue(ran)
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
            PermissionGate(hasPermission = { granted }, rationale = { false }).apply {
                attach(launchSystemUi = { _, onDone -> onDone() }, openSettings = {})
            }

        gate.request(AppPermission.StorageFiles) { result = it }

        assertEquals(true, result)
    }

    @Test
    fun `request without attached launchers finishes immediately`() {
        var result: Boolean? = null

        PermissionGate(hasPermission = { granted }, rationale = { false })
            .request(AppPermission.StorageFiles) { result = it }

        assertEquals(false, result)
    }

    @Test
    fun `should show rationale delegates to platform check`() {
        rationale = true
        assertTrue(gate().shouldShowRationale(AppPermission.PostNotifications))

        rationale = false
        assertFalse(gate().shouldShowRationale(AppPermission.PostNotifications))
    }

    @Test
    fun `open settings forwards without requesting runtime permission`() {
        val gate = gate()

        gate.openSettings(AppPermission.PostNotifications)

        assertEquals(listOf(AppPermission.PostNotifications), openedSettings)
        assertTrue(requested.isEmpty())
    }
}
