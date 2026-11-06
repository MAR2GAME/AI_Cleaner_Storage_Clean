package com.aicleanerstorage.spaceclean.app.permission

import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionResult
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionTarget
import com.aicleanerstorage.spaceclean.core.contracts.permission.PermissionType
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionInterceptorTest {
    @Test
    fun `granted permission returns immediately`() = runTest {
        val subject = PermissionInterceptor({ true })
        assertEquals(
            PermissionResult.Granted,
            subject.ensure(PermissionTarget.Permission(PermissionType.StorageFiles)),
        )
    }

    @Test
    fun `busy request does not replace current owner`() = runTest {
        val subject = PermissionInterceptor({ false })
        val first = async {
            subject.ensure(PermissionTarget.Permission(PermissionType.StorageFiles))
        }
        runCurrent()
        val owner = subject.request.value
        assertTrue(owner != null)
        assertEquals(
            PermissionResult.Busy,
            subject.ensure(PermissionTarget.Permission(PermissionType.StorageFiles)),
        )
        assertSame(owner, subject.request.value)
        subject.dismiss(owner!!)
        assertEquals(PermissionResult.Denied, first.await())
    }
}
