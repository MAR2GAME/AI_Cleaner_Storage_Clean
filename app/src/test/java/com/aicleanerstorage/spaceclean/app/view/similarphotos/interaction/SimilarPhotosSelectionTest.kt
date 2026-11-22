package com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.similarphotos.interaction.SimilarPhotosAction
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.port.SimilarPhotoRepository
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
import org.junit.Assert.assertNotNull
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

            viewModel.dispatch(SimilarPhotosAction.RefreshRequested)
            advanceUntilIdle()

            val state = viewModel.state.value
            val bestPhotoId = state.items.single { it.bestPhoto }.id
            val bulkIds = state.bulkSelectableIds

            viewModel.dispatch(SimilarPhotosAction.SelectionToggled(bestPhotoId))
            viewModel.dispatch(SimilarPhotosAction.VisibleItemsToggled)

            assertTrue(viewModel.state.value.allSelected)
            assertEquals(bulkIds + bestPhotoId, viewModel.state.value.selectedIds)

            viewModel.dispatch(SimilarPhotosAction.VisibleItemsToggled)

            assertFalse(viewModel.state.value.allSelected)
            assertEquals(setOf(bestPhotoId), viewModel.state.value.selectedIds)
        }

    @Test
    fun `group toggle skips best photo`() =
        runTest {
            val viewModel = viewModel()

            viewModel.dispatch(SimilarPhotosAction.RefreshRequested)
            advanceUntilIdle()

            val state = viewModel.state.value
            val group = state.groups.single()
            val groupBulkIds =
                group.items
                    .filterNot { it.bestPhoto }
                    .map { it.id }
                    .toSet()

            viewModel.dispatch(SimilarPhotosAction.GroupToggled(groupBulkIds))

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

    @Test
    fun `delete failure keeps browsing state and reports an error`() =
        runTest(dispatcher) {
            val viewModel = viewModel(deleteResult = Result.failure(IllegalStateException("delete failed")))
            viewModel.dispatch(SimilarPhotosAction.RefreshRequested)
            advanceUntilIdle()
            val item = viewModel.state.value.items.first()
            viewModel.dispatch(SimilarPhotosAction.SelectionToggled(item.id))
            viewModel.dispatch(SimilarPhotosAction.DeleteClicked)
            advanceUntilIdle()
            viewModel.dispatch(SimilarPhotosAction.DeleteConfirmed)
            advanceUntilIdle()

            assertEquals(FileOperationPhase.Browsing, viewModel.state.value.phase)
            assertNotNull(viewModel.state.value.error)
        }
    private fun viewModel(
        deleteResult: Result<FileDeleteSummaryModel>? = null,
    ): SimilarPhotosManagerViewModel =
        SimilarPhotosManagerViewModel(
            repository = FakeSimilarPhotoRepository(deleteResult),
            ioDispatcher = dispatcher,
            scanDelayMillis = 0L,
            deleteDelayMillis = 0L,
            completeDelayMillis = 0L,
        )

    private class FakeSimilarPhotoRepository(
        private val deleteResult: Result<FileDeleteSummaryModel>? = null,
    ) : SimilarPhotoRepository {
        override suspend fun loadGroups(): List<List<ManagedFileModel>> = listOf(listOf(image(1), image(2), image(3)))

        override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> =
            deleteResult ?: Result.success(FileDeleteSummaryModel(files.size, files.sumOf { it.sizeBytes }))
    }

    private companion object {
        fun image(id: Long): ManagedFileModel =
            ManagedFileModel(
                id = id,
                uri = "content://media/external/images/media/$id",
                path = null,
                name = "image-$id.jpg",
                sizeBytes = id * 1024L,
                modifiedSeconds = 1L,
                mimeType = "image/jpeg",
                bucketName = "Camera",
                kind = FileKind.Image,
            )
    }
}


