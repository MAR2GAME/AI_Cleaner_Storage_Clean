package com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction

import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.cleanerkit.files.model.FileDeleteResult
import com.aicleanerstorage.cleanerkit.files.model.FileUri
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileType
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimilarPhotosSelectionTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `global toggle selects only non best photos and preserves manually selected best photo`() =
        runTest {
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
    fun `group toggle skips best photo`() =
        runTest {
            val viewModel = viewModel()

            viewModel.dispatch(FileManagerAction.RefreshRequested)
            advanceUntilIdle()

            val state = viewModel.state.value
            val group = state.groups.single()
            val groupBulkIds =
                group.items
                    .filterNot { it.bestPhoto }
                    .map { it.id }
                    .toSet()

            viewModel.dispatch(FileManagerAction.GroupToggled(groupBulkIds))

            assertEquals(groupBulkIds, viewModel.state.value.selectedIds)
            assertTrue(group.items.single { it.bestPhoto }.id !in viewModel.state.value.selectedIds)
        }

    @Test
    fun `group with only best photo is not all selected`() {
        val state =
            SimilarPhotosManagerUiState(
                groups =
                    listOf(
                        SimilarPhotoGroup(
                            count = 1,
                            items = listOf(SimilarPhotoItem(id = 1, file = image(1), bestPhoto = true)),
                        ),
                    ),
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
        override suspend fun loadImages(): List<ManagedFileItem> = listOf(image(1), image(2), image(3))

        override suspend fun loadVideos(): List<ManagedFileItem> = emptyList()

        override suspend fun loadAudios(): List<ManagedFileItem> = emptyList()

        override suspend fun loadScreenshots(): List<ManagedFileItem> = emptyList()

        override suspend fun loadPrivacyImages(): List<ManagedFileItem> = emptyList()

        override suspend fun loadDocuments(): List<ManagedFileItem> = emptyList()

        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileItem> = emptyList()

        override suspend fun loadDuplicateFiles(): List<List<ManagedFileItem>> = emptyList()

        override suspend fun loadFilesInDirectories(
            relativeDirectories: List<String>,
            matchesMedia: (ManagedFileItem) -> Boolean,
        ): List<ManagedFileItem> = emptyList()

        override suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult = FileDeleteResult(emptyList())

        override suspend fun removeLocationData(items: List<ManagedFileItem>): Int = 0
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
