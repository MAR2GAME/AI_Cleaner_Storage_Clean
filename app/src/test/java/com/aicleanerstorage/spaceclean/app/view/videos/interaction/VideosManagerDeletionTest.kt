package com.aicleanerstorage.spaceclean.app.view.videos.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FilePageAction
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileGroupModel
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideosManagerDeletionTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `detail input emits typed navigation and keeps list selection in the same state`() =
        runTest {
            val viewModel = viewModel(FakeFileRepository(DeleteStatus.Failed))
            viewModel.dispatch(FilePageAction.RefreshRequested)
            advanceUntilIdle()
            viewModel.dispatch(FilePageAction.SelectionToggled(1))
            val selection = viewModel.state.value.selectedIds
            viewModel.dispatch(FilePageAction.DetailClicked(0))
            assertEquals(
                FileManagerEvent.Navigate(AppDestination.VideosDetail(0), false),
                viewModel.event.filterIsInstance<FileManagerEvent.Navigate>().first(),
            )
            viewModel.dispatch(FilePageAction.DetailBackClicked)
            assertEquals(FileManagerEvent.NavigateBack, viewModel.event.first())
            assertEquals(selection, viewModel.state.value.selectedIds)
            assertNull(viewModel.state.value.detailStartIndex)
        }

    @Test
    fun `permission exit is emitted once and subsequent operations are ignored`() =
        runTest {
            val viewModel = viewModel(FakeFileRepository(DeleteStatus.Failed))
            val effects = mutableListOf<FileManagerEvent>()
            val collector =
                backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
                    viewModel.event.collect { effects += it }
                }
            viewModel.dispatch(FilePageAction.PermissionRejected)
            viewModel.dispatch(FilePageAction.PermissionRejected)
            viewModel.dispatch(FilePageAction.RefreshRequested)
            advanceUntilIdle()
            assertEquals(1, effects.size)
            assertTrue(effects.single() is FileManagerEvent.Exit)
            collector.cancel()
        }

    @Test
    fun `zero byte file deletion succeeds when one file was deleted`() =
        runTest {
            val repository = FakeFileRepository(DeleteStatus.Deleted)
            val viewModel = viewModel(repository)

            viewModel.dispatch(FilePageAction.RefreshRequested)
            advanceUntilIdle()
            viewModel.dispatch(FilePageAction.SelectionToggled(1))
            viewModel.dispatch(FilePageAction.DeleteConfirmed)
            advanceUntilIdle()

            assertEquals(FileOperationPhase.Result, viewModel.state.value.phase)
            assertEquals(0L, viewModel.state.value.deletedBytes)
            assertNull(viewModel.state.value.error)
        }

    @Test
    fun `deletion fails when no selected file was deleted`() =
        runTest {
            val repository = FakeFileRepository(DeleteStatus.Failed)
            val viewModel = viewModel(repository)

            viewModel.dispatch(FilePageAction.RefreshRequested)
            advanceUntilIdle()
            viewModel.dispatch(FilePageAction.SelectionToggled(1))
            viewModel.dispatch(FilePageAction.DeleteConfirmed)
            advanceUntilIdle()

            assertEquals(FileOperationPhase.Browsing, viewModel.state.value.phase)
            assertEquals(UiText.Res(R.string.deletion_failed), viewModel.state.value.error)
        }

    @Test
    fun `refresh ignores duplicate starts and a cancelled scan cannot replace the retry`() =
        runTest {
            val releaseFirst = CompletableDeferred<Unit>()
            val releaseSecond = CompletableDeferred<Unit>()
            val fallback = FakeFileRepository(DeleteStatus.Failed)
            var loads = 0
            val repository =
                object : ManagedFileRepository by fallback {
                    override suspend fun loadVideos(): List<ManagedFileModel> {
                        loads++
                        if (loads == 1) {
                            withContext(NonCancellable) { releaseFirst.await() }
                            error("late scan failure")
                        }
                        releaseSecond.await()
                        return fallback.loadVideos()
                    }
                }
            val viewModel = viewModel(repository)
            viewModel.dispatch(FilePageAction.RefreshRequested)
            viewModel.dispatch(FilePageAction.RefreshRequested)
            runCurrent()
            assertEquals(1, loads)

            viewModel.dispatch(FilePageAction.OperationCancellationRequested)
            viewModel.dispatch(FilePageAction.RefreshRequested)
            runCurrent()
            releaseFirst.complete(Unit)
            runCurrent()
            assertEquals(FileOperationPhase.Scanning, viewModel.state.value.phase)
            assertNull(viewModel.state.value.error)

            viewModel.dispatch(FilePageAction.RefreshRequested)
            releaseSecond.complete(Unit)
            advanceUntilIdle()
            assertEquals(2, loads)
            assertEquals(FileOperationPhase.Browsing, viewModel.state.value.phase)
        }

    private fun viewModel(repository: ManagedFileRepository): VideosManagerViewModel =
        VideosManagerViewModel(
            repository = repository,
            ioDispatcher = dispatcher,
            scanDelayMillis = 0L,
            deleteDelayMillis = 0L,
            completeDelayMillis = 0L,
        )

    private class FakeFileRepository(
        private val deleteStatus: DeleteStatus,
    ) : ManagedFileRepository {
        private var videos = listOf(zeroByteVideo())

        override suspend fun loadImages(): List<ManagedFileModel> = emptyList()

        override suspend fun loadVideos(): List<ManagedFileModel> = videos

        override suspend fun loadAudios(): List<ManagedFileModel> = emptyList()

        override suspend fun loadScreenshots(): List<ManagedFileModel> = emptyList()

        override suspend fun loadPrivacyImages(): List<ManagedFileModel> = emptyList()

        override suspend fun loadDocuments(): List<ManagedFileModel> = emptyList()

        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileModel> = emptyList()

        override suspend fun loadDuplicateGroups(): List<DuplicateFileGroupModel> = emptyList()

        override suspend fun deleteDuplicates(selections: List<DuplicateFileSelectionModel>): Result<FileDeleteSummaryModel> =
            Result.failure(UnsupportedOperationException())

        override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> {
            if (deleteStatus == DeleteStatus.Deleted) videos = emptyList()
            val deleted = deleteStatus == DeleteStatus.Deleted
            return Result.success(
                FileDeleteSummaryModel(
                    deletedCount = if (deleted) files.size else 0,
                    freedBytes = if (deleted) files.sumOf { it.sizeBytes } else 0L,
                    skippedCount = if (deleted) 0 else files.size,
                ),
            )
        }

        override suspend fun removeLocationData(files: List<ManagedFileModel>): Result<Int> = Result.success(0)

        private companion object {
            fun zeroByteVideo(): ManagedFileModel =
                ManagedFileModel(
                    id = 1L,
                    uri = "content://media/external/video/media/1",
                    path = null,
                    name = "empty.mp4",
                    sizeBytes = 0L,
                    modifiedSeconds = 1L,
                    mimeType = "video/mp4",
                    bucketName = "Download",
                    kind = FileKind.Video,
                )
        }
    }
}
