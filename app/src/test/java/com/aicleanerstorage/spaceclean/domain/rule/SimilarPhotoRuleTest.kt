package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimilarPhotoRuleTest {
    @Test
    fun `groups photos from the same album day and size bucket`() {
        val groups =
            SimilarPhotoRule.group(
                listOf(
                    photo(1, album = "Camera", size = 1024),
                    photo(2, album = "Camera", size = 2048),
                    photo(3, album = "Download", size = 1024),
                ),
            )

        assertEquals(listOf(listOf(2L, 1L)), groups.map { group -> group.map { it.id } })
    }

    @Test
    fun `does not group a single photo`() {
        assertTrue(SimilarPhotoRule.group(listOf(photo(1))).isEmpty())
    }

    @Test
    fun `respects the input limit`() {
        val files = (1L..3L).map { photo(it, album = "Camera") }
        assertTrue(SimilarPhotoRule.group(files, limit = 1).isEmpty())
    }

    private fun photo(
        id: Long,
        album: String = "Camera",
        size: Long = 1024,
    ) = ManagedFileModel(
        id = id,
        uri = "content://media/external/images/media/$id",
        path = null,
        name = "photo-$id.jpg",
        sizeBytes = size,
        modifiedSeconds = 1L,
        mimeType = "image/jpeg",
        bucketName = album,
        kind = FileKind.Image,
    )
}
