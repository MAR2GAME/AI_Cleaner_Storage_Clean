package com.aicleanerstorage.spaceclean.app.view.applock.interaction

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import org.junit.Assert.assertEquals
import org.junit.Test

class AppLockActionPermissionTest {
    @Test
    fun `select apps package toggle needs no permission`() {
        val action = AppLockAction.PackageToggled("com.example.app")

        assertEquals(emptyList<AppPermission>(), action.requiredPermissions(AppLockPage.SelectApps))
    }

    @Test
    fun `protected package toggle needs usage access`() {
        val action = AppLockAction.PackageToggled("com.example.app")

        assertEquals(
            listOf(AppPermission.UsageAccess),
            action.requiredPermissions(AppLockPage.Manage),
        )
        assertEquals(
            listOf(AppPermission.UsageAccess),
            action.requiredPermissions(AppLockPage.Search),
        )
    }

    @Test
    fun `select all and pin creation need usage access`() {
        assertEquals(
            listOf(AppPermission.UsageAccess),
            AppLockAction.AllAppsSelectionToggled.requiredPermissions(AppLockPage.Manage),
        )
        assertEquals(
            listOf(AppPermission.UsageAccess),
            AppLockAction.PinCreationRequested.requiredPermissions(AppLockPage.SelectApps),
        )
    }

    @Test
    fun `enabling monitoring needs usage access and overlay`() {
        assertEquals(
            listOf(AppPermission.UsageAccess, AppPermission.Overlay),
            AppLockAction.MonitoringChanged(enabled = true).requiredPermissions(AppLockPage.Settings),
        )
    }

    @Test
    fun `disabling monitoring needs no permission`() {
        assertEquals(
            emptyList<AppPermission>(),
            AppLockAction.MonitoringChanged(enabled = false).requiredPermissions(AppLockPage.Settings),
        )
    }
}
