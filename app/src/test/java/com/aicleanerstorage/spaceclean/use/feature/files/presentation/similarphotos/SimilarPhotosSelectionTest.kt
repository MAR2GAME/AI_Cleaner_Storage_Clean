package com.aicleanerstorage.spaceclean.feature.files.similarphotos

import com.aicleanerstorage.spaceclean.feature.files.shared.FileUri
import com.aicleanerstorage.spaceclean.feature.files.shared.ManagedFileItem
import com.aicleanerstorage.spaceclean.feature.files.shared.ManagedFileType
import com.aicleanerstorage.spaceclean.feature.files.shared.data.FileDeleteResult
import com.aicleanerstorage.spaceclean.feature.files.shared.data.FileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimilarPhotosSelectionTest {
    @Test
    fun `global toggle selects only non best photos and preserves manually selected best photo`() = runTest {
        val viewModel = viewModel { block -> launch { block() } }

        viewModel.refresh()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val bestPhotoId = state.items.single { it.bestPhoto }.id
        val bulkIds = state.bulkSelectableIds

        viewModel.toggleSelection(bestPhotoId)
        viewModel.toggleVisibleItems()

        assertTrue(viewModel.uiState.value.allSelected)
        assertEquals(bulkIds + bestPhotoId, viewModel.uiState.value.selectedIds)

        viewModel.toggleVisibleItems()

        assertFalse(viewModel.uiState.value.allSelected)
        assertEquals(setOf(bestPhotoId), viewModel.uiState.value.selectedIds)
    }

    @Test
    fun `group toggle skips best photo`() = runTest {
        val viewModel = viewModel { block -> launch { block() } }

        viewModel.refresh()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val group = state.groups.single()
        val groupBulkIds = group.items.filterNot { it.bestPhoto }.map { it.id }.toSet()

        viewModel.toggleGroup(groupBulkIds)

        assertEquals(groupBulkIds, viewModel.uiState.value.selectedIds)
        assertTrue(group.items.single { it.bestPhoto }.id !in viewModel.uiState.value.selectedIds)
    }

    @Test
    fun `group with only best photo is not all selected`() {
        val state = SimilarPhotosManagerUiState(
            groups = listOf(
                SimilarPhotoGroup(
                    count = 1,
                    items = listOf(SimilarPhotoItem(id = 1, file = image(1), bestPhoto = true)),
                )
            )
        )

        assertTrue(state.bulkSelectableIds.isEmpty())
        assertFalse(state.allSelected)
    }

    private fun viewModel(testLoader: ((suspend () -> Unit) -> Unit)): SimilarPhotosManagerViewModel =
        SimilarPhotosManagerViewModel(
            repository = FakeFileRepository(),
            testLoader = testLoader,
            scanDelayMillis = 0L,
            deleteDelayMillis = 0L,
            completeDelayMillis = 0L,
        )

    private class FakeFileRepository : FileRepository {
        override suspend fun loadImages(): List<ManagedFileItem> = listOf(image(1), image(2), image(3))
        override suspend fun loadVideos(): List<ManagedFileItem> = emptyList()
        override suspend fun loadAudios(): List<ManagedFileItem> = emptyList()
        override suspend fun loadScreenshots(): List<ManagedFileItem> = emptyList()
        override suspend fun loadPrivacyImages(): List<ManagedFileItem> = emptyList()
        override suspend fun loadDocuments(): List<ManagedFileItem> = emptyList()
        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileItem> = emptyList()
        override suspend fun loadDuplicateFiles(): List<List<ManagedFileItem>> = emptyList()
        override suspend fun loadWhatsAppFiles(): List<ManagedFileItem> = emptyList()
        override suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult = FileDeleteResult(0L, 0)
        override suspend fun removeLocationData(items: List<ManagedFileItem>): Int = 0
        override fun hasAllFilesAccess(): Boolean = false
    }

    private companion object {
        fun image(id: Long): ManagedFileItem =
            ManagedFileItem(
                id = id,
                uri = FileUri("content://media/external/images/media/$id"),
                path = null,
                name = "image-$id.jpg",
                sizeBytes = id * 1024L,
                modifiedSeconds = 1L,
                mimeType = "image/jpeg",
                bucketName = "Camera",
                type = ManagedFileType.Image,
            )
    }
}
