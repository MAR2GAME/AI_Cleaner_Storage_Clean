package com.aicleanerstorage.spaceclean.app.view.common.permission

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionUiCopyTest {
    @Test
    fun `every permission has a label and description`() {
        AppPermission.entries.forEach { permission ->
            assertTrue(permission.labelRes() != 0)
            assertTrue(permission.descriptionRes() != 0)
        }
    }
}
