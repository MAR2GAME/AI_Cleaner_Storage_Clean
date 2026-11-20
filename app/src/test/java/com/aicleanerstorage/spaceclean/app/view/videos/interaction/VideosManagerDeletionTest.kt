package com.aicleanerstorage.spaceclean.app.view.videos.interaction

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerAction
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileOperationPhase
import com.aicleanerstorage.spaceclean.domain.model.FileUri
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileType
import com.aicleanerstorage.spaceclean.domain.model.FileDeleteResultModel
import com.aicleanerstorage.spaceclean.domain.port.FileRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideosManagerDeletionTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `detail input emits typed navigation and keeps list selection in the same state`() = runTest {
        val viewModel = viewModel(FakeFileRepository(FileDeleteResultModel(0L, 0)))
        viewModel.dispatch(FileManagerAction.RefreshRequested)
        advanceUntilIdle()
        viewModel.dispatch(FileManagerAction.SelectionToggled(1))
        val selection = viewModel.state.value.selectedIds
        viewModel.dispatch(FileManagerAction.DetailClicked(0))
        assertEquals(FileManagerEvent.Navigate(AppDestination.VideosDetail(0), false),
            viewModel.event.filterIsInstance<FileManagerEvent.Navigate>().first())
        viewModel.dispatch(FileManagerAction.DetailBackClicked)
        assertEquals(FileManagerEvent.NavigateBack, viewModel.event.first())
        assertEquals(selection, viewModel.state.value.selectedIds)
        assertNull(viewModel.state.value.detailStartIndex)
    }

    @Test
    fun `permission exit is emitted once and subsequent operations are ignored`() = runTest {
        val viewModel = viewModel(FakeFileRepository(FileDeleteResultModel(0L, 0)))
        val effects = mutableListOf<FileManagerEvent>()
        val collector = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.event.collect { effects += it }
        }
        viewModel.dispatch(FileManagerAction.PermissionRejected)
        viewModel.dispatch(FileManagerAction.PermissionRejected)
        viewModel.dispatch(FileManagerAction.RefreshRequested)
        advanceUntilIdle()
        assertEquals(1, effects.size)
        assertTrue(effects.single() is FileManagerEvent.Exit)
        collector.cancel()
    }

    @Test
    fun `zero byte file deletion succeeds when one file was deleted`() = runTest {
        val repository = FakeFileRepository(FileDeleteResultModel(freedBytes = 0L, deletedCount = 1))
        val viewModel = viewModel(repository)

        viewModel.dispatch(FileManagerAction.RefreshRequested)
        advanceUntilIdle()
        viewModel.dispatch(FileManagerAction.SelectionToggled(1))
        viewModel.dispatch(FileManagerAction.DeleteConfirmed)
        advanceUntilIdle()

        assertEquals(FileOperationPhase.Result, viewModel.state.value.phase)
        assertEquals(0L, viewModel.state.value.deletedBytes)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `deletion fails when no selected file was deleted`() = runTest {
        val repository = FakeFileRepository(FileDeleteResultModel(freedBytes = 0L, deletedCount = 0))
        val viewModel = viewModel(repository)

        viewModel.dispatch(FileManagerAction.RefreshRequested)
        advanceUntilIdle()
        viewModel.dispatch(FileManagerAction.SelectionToggled(1))
        viewModel.dispatch(FileManagerAction.DeleteConfirmed)
        advanceUntilIdle()

        assertEquals(FileOperationPhase.Browsing, viewModel.state.value.phase)
        assertTrue(viewModel.state.value.errorMessage?.isNotBlank() == true)
    }

    @Test
    fun `refresh ignores duplicate starts and a cancelled scan cannot replace the retry`() = runTest {
        val releaseFirst = CompletableDeferred<Unit>()
        val releaseSecond = CompletableDeferred<Unit>()
        val fallback = FakeFileRepository(FileDeleteResultModel(0L, 0))
        var loads = 0
        val repository = object : FileRepository by fallback {
            override suspend fun loadVideos(): List<ManagedFileItemModel> {
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
        viewModel.dispatch(FileManagerAction.RefreshRequested)
        viewModel.dispatch(FileManagerAction.RefreshRequested)
        runCurrent()
        assertEquals(1, loads)

        viewModel.dispatch(FileManagerAction.OperationCancellationRequested)
        viewModel.dispatch(FileManagerAction.RefreshRequested)
        runCurrent()
        releaseFirst.complete(Unit)
        runCurrent()
        assertEquals(FileOperationPhase.Scanning, viewModel.state.value.phase)
        assertNull(viewModel.state.value.errorMessage)

        viewModel.dispatch(FileManagerAction.RefreshRequested)
        releaseSecond.complete(Unit)
        advanceUntilIdle()
        assertEquals(2, loads)
        assertEquals(FileOperationPhase.Browsing, viewModel.state.value.phase)
    }

    private fun viewModel(
        repository: FileRepository,
    ): VideosManagerViewModel =
        VideosManagerViewModel(
            repository = repository,
            ioDispatcher = dispatcher,
            scanDelayMillis = 0L,
            deleteDelayMillis = 0L,
            completeDelayMillis = 0L,
        )

    private class FakeFileRepository(
        private val deleteResult: FileDeleteResultModel,
    ) : FileRepository {
        private var videos = listOf(zeroByteVideo())

        override suspend fun loadImages(): List<ManagedFileItemModel> = emptyList()

        override suspend fun loadVideos(): List<ManagedFileItemModel> = videos

        override suspend fun loadAudios(): List<ManagedFileItemModel> = emptyList()

        override suspend fun loadScreenshots(): List<ManagedFileItemModel> = emptyList()

        override suspend fun loadPrivacyImages(): List<ManagedFileItemModel> = emptyList()

        override suspend fun loadDocuments(): List<ManagedFileItemModel> = emptyList()

        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileItemModel> = emptyList()

        override suspend fun loadDuplicateFiles(): List<List<ManagedFileItemModel>> = emptyList()

        override suspend fun loadWhatsAppFiles(): List<ManagedFileItemModel> = emptyList()

        override suspend fun deleteFiles(items: List<ManagedFileItemModel>): FileDeleteResultModel {
            if (deleteResult.deletedCount > 0) videos = emptyList()
            return deleteResult
        }

        override suspend fun removeLocationData(items: List<ManagedFileItemModel>): Int = 0


        private companion object {
            fun zeroByteVideo(): ManagedFileItemModel =
                ManagedFileItemModel(
                    id = 1L,
                    uri = FileUri("content://media/external/video/media/1"),
                    path = null,
                    name = "empty.mp4",
                    sizeBytes = 0L,
                    modifiedSeconds = 1L,
                    mimeType = "video/mp4",
                    bucketName = "Download",
                    type = ManagedFileType.Video,
                )
        }
    }
}
