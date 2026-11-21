package com.aicleanerstorage.spaceclean.data.files

import com.aicleanerstorage.cleanerkit.files.model.FileUri
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileType
import org.junit.Assert.assertEquals
import org.junit.Test

class ManagedFileMapperTest {
    @Test
    fun `item survives a model round trip`() {
        val item = item(ManagedFileType.Image)

        val restored = item.toModel().toItem()

        assertEquals(item.id, restored.id)
        assertEquals(item.uri, restored.uri)
        assertEquals(item.path, restored.path)
        assertEquals(item.name, restored.name)
        assertEquals(item.sizeBytes, restored.sizeBytes)
        assertEquals(item.modifiedSeconds, restored.modifiedSeconds)
        assertEquals(item.mimeType, restored.mimeType)
        assertEquals(item.bucketName, restored.bucketName)
        assertEquals(item.type, restored.type)
    }

    @Test
    fun `every file type survives the round trip`() {
        ManagedFileType.entries.forEach { type ->
            assertEquals(type, item(type).toModel().toItem().type)
        }
    }

    private fun item(type: ManagedFileType): ManagedFileItem =
        ManagedFileItem(
            id = 7L,
            uri = FileUri("content://media/external/images/media/7"),
            path = "/storage/emulated/0/DCIM/7.jpg",
            name = "7.jpg",
            sizeBytes = 4096L,
            modifiedSeconds = 1_700_000_000L,
            mimeType = "image/jpeg",
            bucketName = "Camera",
            type = type,
        )
}
