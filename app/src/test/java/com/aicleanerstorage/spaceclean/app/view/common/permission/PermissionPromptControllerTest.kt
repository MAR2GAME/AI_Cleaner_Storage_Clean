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
        val controller = PermissionPromptController(PermissionGate { false }, requiresSettings = { true })

        controller.request(AppPermission.StorageFiles) { result = it }
        assertEquals(AppPermission.StorageFiles, controller.explaining)

        controller.confirmExplanation()
        assertNull(controller.explaining)
        assertEquals(AppPermission.StorageFiles, controller.requesting)
        assertEquals(true, controller.requestingSettingsOnly)

        controller.completeRequest(PermissionResult.Granted)
        assertEquals(PermissionResult.Granted, result)
        assertNull(controller.requesting)
    }

    @Test
    fun `dismissing explanation reports denied`() {
        var result: PermissionResult? = null
        val controller = PermissionPromptController(PermissionGate { false }, requiresSettings = { true })

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
        val controller = PermissionPromptController(PermissionGate { false }, requiresSettings = { true })

        controller.request(AppPermission.StorageFiles) { first = it }
        controller.request(AppPermission.UsageAccess) { second = it }

        assertEquals(AppPermission.StorageFiles, controller.explaining)
        assertNull(second)

        controller.dismissExplanation()
        assertEquals(PermissionResult.Denied, first)
    }

    @Test
    fun `runtime permission requests system directly`() {
        var result: PermissionResult? = null
        val controller = PermissionPromptController(PermissionGate { false }, requiresSettings = { false })

        controller.request(AppPermission.Location) { result = it }

        assertNull(controller.explaining)
        assertEquals(AppPermission.Location, controller.requesting)
        assertEquals(false, controller.requestingSettingsOnly)

        controller.completeRequest(PermissionResult.Granted)
        assertEquals(PermissionResult.Granted, result)
    }

    @Test
    fun `permanent runtime denial falls back to explanation then settings`() {
        var result: PermissionResult? = null
        val controller = PermissionPromptController(PermissionGate { false }, requiresSettings = { false })

        controller.request(AppPermission.Location) { result = it }
        controller.completeRequest(PermissionResult.PermanentlyDenied)

        assertEquals(AppPermission.Location, controller.explaining)
        assertNull(controller.requesting)

        controller.confirmExplanation()
        assertEquals(AppPermission.Location, controller.requesting)
        assertEquals(true, controller.requestingSettingsOnly)

        controller.completeRequest(PermissionResult.RequiresSettings)
        assertEquals(PermissionResult.RequiresSettings, result)
    }

    @Test
    fun `already granted storage permission reports nothing`() {
        val events = mutableListOf<PermissionPromptEvent>()
        val controller =
            PermissionPromptController(
                gate = PermissionGate { true },
                onEvent = events::add,
            )

        controller.request(AppPermission.StorageFiles) {}

        assertEquals(emptyList<PermissionPromptEvent>(), events)
    }

    @Test
    fun `confirming storage explanation reports popup then agree`() {
        val events = mutableListOf<PermissionPromptEvent>()
        val controller =
            PermissionPromptController(
                gate = PermissionGate { false },
                requiresSettings = { true },
                onEvent = events::add,
            )

        controller.request(AppPermission.StorageFiles) {}
        controller.confirmExplanation()
        controller.completeRequest(PermissionResult.Granted)

        assertEquals(
            listOf(
                PermissionPromptEvent.DialogResult(accepted = true),
                PermissionPromptEvent.PermissionResult(AppPermission.StorageFiles, accepted = true),
            ),
            events,
        )
    }

    @Test
    fun `dismissing storage explanation reports popup and disagree`() {
        val events = mutableListOf<PermissionPromptEvent>()
        val controller =
            PermissionPromptController(
                gate = PermissionGate { false },
                requiresSettings = { true },
                onEvent = events::add,
            )

        controller.request(AppPermission.StorageFiles) {}
        controller.dismissExplanation()

        assertEquals(
            listOf(
                PermissionPromptEvent.DialogResult(accepted = false),
                PermissionPromptEvent.PermissionResult(AppPermission.StorageFiles, accepted = false),
            ),
            events,
        )
    }

    @Test
    fun `runtime storage denial reports disagree`() {
        val events = mutableListOf<PermissionPromptEvent>()
        val controller =
            PermissionPromptController(
                gate = PermissionGate { false },
                requiresSettings = { false },
                onEvent = events::add,
            )

        controller.request(AppPermission.StorageFiles) {}
        controller.completeRequest(PermissionResult.Denied)

        assertEquals(
            listOf(PermissionPromptEvent.PermissionResult(AppPermission.StorageFiles, accepted = false)),
            events,
        )
    }

    @Test
    fun `non storage permission reports nothing`() {
        val events = mutableListOf<PermissionPromptEvent>()
        val controller =
            PermissionPromptController(
                gate = PermissionGate { false },
                requiresSettings = { false },
                onEvent = events::add,
            )

        controller.request(AppPermission.Location) {}
        controller.completeRequest(PermissionResult.Granted)

        assertEquals(emptyList<PermissionPromptEvent>(), events)
    }

    @Test
    fun `runtime permission suppresses and restores foreground ad`() {
        var suppressed = 0
        var completed = 0
        val controller =
            PermissionPromptController(
                gate = PermissionGate { false },
                requiresSettings = { false },
                beforeSystemUi = { suppressed++ },
                afterSystemUi = { completed++ },
            )

        controller.request(AppPermission.Location) {}

        assertEquals(1, suppressed)
        assertEquals(0, completed)

        controller.completeRequest(PermissionResult.Granted)
        assertEquals(1, completed)

        controller.completeRequest(PermissionResult.Denied)
        assertEquals(1, completed)
    }

    @Test
    fun `explanation suppresses only when opening system settings`() {
        var suppressed = 0
        var completed = 0
        val controller =
            PermissionPromptController(
                gate = PermissionGate { false },
                requiresSettings = { true },
                beforeSystemUi = { suppressed++ },
                afterSystemUi = { completed++ },
            )

        controller.request(AppPermission.StorageFiles) {}
        assertEquals(0, suppressed)

        controller.confirmExplanation()
        assertEquals(1, suppressed)
        assertEquals(0, completed)

        controller.completeRequest(PermissionResult.Granted)
        assertEquals(1, completed)
    }

    @Test
    fun `dismissed explanation does not suppress foreground ad`() {
        var suppressed = 0
        var completed = 0
        val controller =
            PermissionPromptController(
                gate = PermissionGate { false },
                requiresSettings = { true },
                beforeSystemUi = { suppressed++ },
                afterSystemUi = { completed++ },
            )

        controller.request(AppPermission.StorageFiles) {}
        controller.dismissExplanation()

        assertEquals(0, suppressed)
        assertEquals(0, completed)
    }

    @Test
    fun `requestAll reports granted when every permission is already granted`() {
        var result: Boolean? = null
        val controller = PermissionPromptController(PermissionGate { true })

        controller.requestAll(listOf(AppPermission.UsageAccess, AppPermission.Overlay)) { result = it }

        assertEquals(true, result)
    }

    @Test
    fun `requestAll continues with the next permission after a grant`() {
        val granted = mutableSetOf<AppPermission>()
        var result: Boolean? = null
        val controller =
            PermissionPromptController(
                gate = PermissionGate { it in granted },
                requiresSettings = { true },
            )

        controller.requestAll(listOf(AppPermission.UsageAccess, AppPermission.Overlay)) { result = it }

        assertEquals(AppPermission.UsageAccess, controller.explaining)
        controller.confirmExplanation()
        granted += AppPermission.UsageAccess
        controller.completeRequest(PermissionResult.Granted)
        assertEquals(AppPermission.Overlay, controller.explaining)

        granted += AppPermission.Overlay
        controller.confirmExplanation()
        controller.completeRequest(PermissionResult.Granted)

        assertEquals(true, result)
    }

    @Test
    fun `requestAll stops at the first denied permission`() {
        var result: Boolean? = null
        val controller = PermissionPromptController(PermissionGate { false }, requiresSettings = { true })

        controller.requestAll(listOf(AppPermission.UsageAccess, AppPermission.Overlay)) { result = it }
        controller.dismissExplanation()

        assertEquals(false, result)
        assertNull(controller.explaining)
        assertNull(controller.requesting)
    }
}
