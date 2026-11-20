package com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction

import com.aicleanerstorage.spaceclean.domain.model.FileUri
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileType
import com.aicleanerstorage.spaceclean.domain.model.FileDeleteResultModel
import com.aicleanerstorage.spaceclean.domain.port.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimilarPhotosSelectionTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `global toggle selects only non best photos and preserves manually selected best photo`() = runTest {
        val viewModel = viewModel()

        viewModel.dispatch(FileManagerAction.RefreshRequested)
        advanceUntilIdle()

        val state = viewModel.state.value
        val bestPhotoId = state.items.single { it.bestPhoto }.id
        val bulkIds = state.bulkSelectableIds

        viewModel.dispatch(FileManagerAction.SelectionToggled(bestPhotoId))
        viewModel.dispatch(FileManagerAction.VisibleItemsToggled)

        assertTrue(viewModel.state.value.allSelected)
        assertEquals(bulkIds + bestPhotoId, viewModel.state.value.selectedIds)

        viewModel.dispatch(FileManagerAction.VisibleItemsToggled)

        assertFalse(viewModel.state.value.allSelected)
        assertEquals(setOf(bestPhotoId), viewModel.state.value.selectedIds)
    }

    @Test
    fun `group toggle skips best photo`() = runTest {
        val viewModel = viewModel()

        viewModel.dispatch(FileManagerAction.RefreshRequested)
        advanceUntilIdle()

        val state = viewModel.state.value
        val group = state.groups.single()
        val groupBulkIds = group.items.filterNot { it.bestPhoto }.map { it.id }.toSet()

        viewModel.dispatch(FileManagerAction.GroupToggled(groupBulkIds))

        assertEquals(groupBulkIds, viewModel.state.value.selectedIds)
        assertTrue(group.items.single { it.bestPhoto }.id !in viewModel.state.value.selectedIds)
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

    private fun viewModel(): SimilarPhotosManagerViewModel =
        SimilarPhotosManagerViewModel(
            repository = FakeFileRepository(),
            ioDispatcher = dispatcher,
            scanDelayMillis = 0L,
            deleteDelayMillis = 0L,
            completeDelayMillis = 0L,
        )

    private class FakeFileRepository : FileRepository {
        override suspend fun loadImages(): List<ManagedFileItemModel> = listOf(image(1), image(2), image(3))
        override suspend fun loadVideos(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadAudios(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadScreenshots(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadPrivacyImages(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadDocuments(): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileItemModel> = emptyList()
        override suspend fun loadDuplicateFiles(): List<List<ManagedFileItemModel>> = emptyList()
        override suspend fun loadWhatsAppFiles(): List<ManagedFileItemModel> = emptyList()
        override suspend fun deleteFiles(items: List<ManagedFileItemModel>): FileDeleteResultModel = FileDeleteResultModel(0L, 0)
        override suspend fun removeLocationData(items: List<ManagedFileItemModel>): Int = 0
    }

    private companion object {
        fun image(id: Long): ManagedFileItemModel =
            ManagedFileItemModel(
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
