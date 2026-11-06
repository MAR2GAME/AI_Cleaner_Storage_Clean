package com.aicleanerstorage.spaceclean.feature.files.view.videos.presentation

import com.aicleanerstorage.spaceclean.feature.files.view.videos.presentation.VideosManagerViewModel

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerIntent
import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileManagerEffect
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first

import com.aicleanerstorage.spaceclean.feature.files.view.shared.presentation.FileOperationPhase
import com.aicleanerstorage.spaceclean.core.contracts.files.FileUri
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileType
import com.aicleanerstorage.spaceclean.core.contracts.files.FileDeleteResult
import com.aicleanerstorage.spaceclean.core.contracts.files.FileRepository
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
    @Test
    fun `detail input emits typed navigation and keeps list selection in the same state`() = runTest {
        val viewModel = viewModel(FakeFileRepository(FileDeleteResult(0L, 0))) { block -> launch { block() } }
        viewModel.dispatch(FileManagerIntent.Refresh)
        advanceUntilIdle()
        viewModel.dispatch(FileManagerIntent.ToggleSelection(1))
        val selection = viewModel.state.value.selectedIds
        viewModel.dispatch(FileManagerIntent.OpenDetail(0))
        assertEquals(FileManagerEffect.Navigate(AppDestination.VideosDetail(0), false),
            viewModel.effects.filterIsInstance<FileManagerEffect.Navigate>().first())
        viewModel.dispatch(FileManagerIntent.CloseDetailAndBack)
        assertEquals(FileManagerEffect.Back, viewModel.effects.first())
        assertEquals(selection, viewModel.state.value.selectedIds)
        assertNull(viewModel.state.value.detailStartIndex)
    }

    @Test
    fun `permission exit is emitted once and subsequent operations are ignored`() = runTest {
        val viewModel = viewModel(FakeFileRepository(FileDeleteResult(0L, 0))) { block -> launch { block() } }
        val effects = mutableListOf<FileManagerEffect>()
        val collector = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.collect { effects += it }
        }
        viewModel.dispatch(FileManagerIntent.PermissionRejected)
        viewModel.dispatch(FileManagerIntent.PermissionRejected)
        viewModel.dispatch(FileManagerIntent.Refresh)
        advanceUntilIdle()
        assertEquals(1, effects.size)
        assertTrue(effects.single() is FileManagerEffect.Exit)
        collector.cancel()
    }

    @Test
    fun `zero byte file deletion succeeds when one file was deleted`() = runTest {
        val repository = FakeFileRepository(FileDeleteResult(freedBytes = 0L, deletedCount = 1))
        val viewModel = viewModel(repository) { block -> launch { block() } }

        viewModel.dispatch(FileManagerIntent.Refresh)
        advanceUntilIdle()
        viewModel.dispatch(FileManagerIntent.ToggleSelection(1))
        viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles)
        advanceUntilIdle()

        assertEquals(FileOperationPhase.CompleteAnimation, viewModel.state.value.phase)
        assertEquals(0L, viewModel.state.value.deletedBytes)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `deletion fails when no selected file was deleted`() = runTest {
        val repository = FakeFileRepository(FileDeleteResult(freedBytes = 0L, deletedCount = 0))
        val viewModel = viewModel(repository) { block -> launch { block() } }

        viewModel.dispatch(FileManagerIntent.Refresh)
        advanceUntilIdle()
        viewModel.dispatch(FileManagerIntent.ToggleSelection(1))
        viewModel.dispatch(FileManagerIntent.DeleteSelectedFiles)
        advanceUntilIdle()

        assertEquals(FileOperationPhase.Browsing, viewModel.state.value.phase)
        assertTrue(viewModel.state.value.errorMessage?.isNotBlank() == true)
    }

    private fun viewModel(
        repository: FileRepository,
        testLoader: ((suspend () -> Unit) -> Unit),
    ): VideosManagerViewModel =
        VideosManagerViewModel(
            repository = repository,
            testLoader = testLoader,
            scanDelayMillis = 0L,
            deleteDelayMillis = 0L,
            completeDelayMillis = 0L,
        )

    private class FakeFileRepository(
        private val deleteResult: FileDeleteResult,
    ) : FileRepository {
        private var videos = listOf(zeroByteVideo())

        override suspend fun loadImages(): List<ManagedFileItem> = emptyList()

        override suspend fun loadVideos(): List<ManagedFileItem> = videos

        override suspend fun loadAudios(): List<ManagedFileItem> = emptyList()

        override suspend fun loadScreenshots(): List<ManagedFileItem> = emptyList()

        override suspend fun loadPrivacyImages(): List<ManagedFileItem> = emptyList()

        override suspend fun loadDocuments(): List<ManagedFileItem> = emptyList()

        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileItem> = emptyList()

        override suspend fun loadDuplicateFiles(): List<List<ManagedFileItem>> = emptyList()

        override suspend fun loadWhatsAppFiles(): List<ManagedFileItem> = emptyList()

        override suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult {
            if (deleteResult.deletedCount > 0) videos = emptyList()
            return deleteResult
        }

        override suspend fun removeLocationData(items: List<ManagedFileItem>): Int = 0

        override fun hasAllFilesAccess(): Boolean = false

        private companion object {
            fun zeroByteVideo(): ManagedFileItem =
                ManagedFileItem(
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
