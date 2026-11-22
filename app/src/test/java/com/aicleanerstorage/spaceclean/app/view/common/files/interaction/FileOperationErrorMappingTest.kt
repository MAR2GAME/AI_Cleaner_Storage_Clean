package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequiredException
import org.junit.Assert.assertEquals
import org.junit.Test

class FileOperationErrorMappingTest {
    @Test
    fun `permission exception maps to permission error`() {
        val mapped =
            PermissionRequiredException(AppPermission.StorageFiles)
                .toFileOperationError(FileOperationError.ScanFailed)

        assertEquals(FileOperationError.PermissionRequired(AppPermission.StorageFiles), mapped)
    }

    @Test
    fun `other exception keeps the caller fallback`() {
        val mapped = IllegalStateException("boom").toFileOperationError(FileOperationError.DeletionFailed)

        assertEquals(FileOperationError.DeletionFailed, mapped)
    }
}
