package com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.withSelectionIds
import com.aicleanerstorage.spaceclean.app.view.photoprivacy.interaction.PhotoPrivacyAction
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileGroupModel
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
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
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoPrivacySelectionTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `groups seconds and milliseconds by descending local month`() {
        withDefaultTimeZone(TimeZone.getTimeZone("UTC")) {
            val mayFirst = timestampMillis(2026, Calendar.MAY, 1)
            val aprilFirst = timestampMillis(2026, Calendar.APRIL, 1)
            val items =
                listOf(
                    image(1, aprilFirst / 1000L),
                    image(2, mayFirst),
                    image(3, mayFirst / 1000L),
                ).withSelectionIds()

            val groups = buildPrivacyPhotoGroups(items)

            assertEquals(listOf("2026-05", "2026-04"), groups.map { it.title })
            assertEquals(listOf(2L, 3L), groups.first().items.map { it.file.id })
            assertEquals(listOf(1L), groups.last().items.map { it.file.id })
        }
    }

    @Test
    fun `global and group toggles keep selection states synchronized`() =
        runTest {
            val viewModel = viewModel(FakeFileRepository())

            viewModel.dispatch(PhotoPrivacyAction.RefreshRequested)
            advanceUntilIdle()

            val initial = viewModel.state.value
            assertTrue(initial.allSelected)
            assertEquals(initial.visibleIds, initial.selectedIds)

            viewModel.dispatch(PhotoPrivacyAction.VisibleItemsToggled)
            assertTrue(
                viewModel.state.value.selectedIds
                    .isEmpty(),
            )
            assertFalse(viewModel.state.value.allSelected)

            val firstGroupIds = initial.groups.first().ids
            viewModel.dispatch(PhotoPrivacyAction.VisibleSelectionToggled(firstGroupIds))
            assertEquals(firstGroupIds, viewModel.state.value.selectedIds)
            assertFalse(viewModel.state.value.allSelected)

            viewModel.dispatch(PhotoPrivacyAction.VisibleItemsToggled)
            assertTrue(viewModel.state.value.allSelected)
            assertEquals(initial.visibleIds, viewModel.state.value.selectedIds)
        }

    @Test
    fun `removing location data submits only selected photos and rebuilds groups`() =
        runTest {
            val repository = FakeFileRepository()
            val viewModel = viewModel(repository)

            viewModel.dispatch(PhotoPrivacyAction.RefreshRequested)
            advanceUntilIdle()
            viewModel.dispatch(PhotoPrivacyAction.VisibleItemsToggled)
            viewModel.dispatch(PhotoPrivacyAction.SelectionToggled(1))
            viewModel.dispatch(PhotoPrivacyAction.LocationRemovalConfirmed)
            advanceUntilIdle()

            assertEquals(listOf(1L), repository.removedIds)
            assertEquals(FileOperationPhase.Result, viewModel.state.value.phase)
            assertTrue(
                viewModel.state.value.items
                    .none { it.file.id == 1L },
            )
            assertTrue(
                viewModel.state.value.selectedIds
                    .isEmpty(),
            )
        }

    private fun viewModel(repository: ManagedFileRepository): PhotoPrivacyManagerViewModel =
        PhotoPrivacyManagerViewModel(
            repository = repository,
            ioDispatcher = dispatcher,
            scanDelayMillis = 0L,
            removeDelayMillis = 0L,
            completeDelayMillis = 0L,
        )

    private class FakeFileRepository : ManagedFileRepository {
        private var privacyImages =
            listOf(
                image(1, timestampMillis(2026, Calendar.MAY, 1) / 1000L),
                image(2, timestampMillis(2026, Calendar.MAY, 2) / 1000L),
                image(3, timestampMillis(2026, Calendar.APRIL, 1) / 1000L),
            )
        var removedIds: List<Long> = emptyList()
            private set

        override suspend fun loadImages(): List<ManagedFileModel> = emptyList()

        override suspend fun loadVideos(): List<ManagedFileModel> = emptyList()

        override suspend fun loadAudios(): List<ManagedFileModel> = emptyList()

        override suspend fun loadScreenshots(): List<ManagedFileModel> = emptyList()

        override suspend fun loadPrivacyImages(): List<ManagedFileModel> = privacyImages

        override suspend fun loadDocuments(): List<ManagedFileModel> = emptyList()

        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileModel> = emptyList()

        override suspend fun loadDuplicateGroups(): List<DuplicateFileGroupModel> = emptyList()

        override suspend fun deleteDuplicates(selections: List<DuplicateFileSelectionModel>): Result<FileDeleteSummaryModel> =
            Result.failure(UnsupportedOperationException())

        override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> =
            Result.success(FileDeleteSummaryModel(deletedCount = 0, freedBytes = 0L))

        override suspend fun removeLocationData(files: List<ManagedFileModel>): Result<Int> {
            removedIds = files.map { it.id }
            privacyImages = privacyImages.filterNot { candidate -> files.any { it.id == candidate.id } }
            return Result.success(files.size)
        }
    }

    private companion object {
        fun image(
            id: Long,
            modifiedSeconds: Long,
        ): ManagedFileModel =
            ManagedFileModel(
                id = id,
                uri = "content://media/external/images/media/$id",
                path = null,
                name = "image-$id.jpg",
                sizeBytes = id * 1024L,
                modifiedSeconds = modifiedSeconds,
                mimeType = "image/jpeg",
                bucketName = "Camera",
                kind = FileKind.Image,
            )

        fun timestampMillis(
            year: Int,
            month: Int,
            day: Int,
        ): Long =
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).run {
                clear()
                set(year, month, day, 12, 0, 0)
                timeInMillis
            }

        fun withDefaultTimeZone(
            timeZone: TimeZone,
            block: () -> Unit,
        ) {
            val previous = TimeZone.getDefault()
            try {
                TimeZone.setDefault(timeZone)
                block()
            } finally {
                TimeZone.setDefault(previous)
            }
        }
    }
}
