package com.aicleanerstorage.spaceclean.feature.files.videos

import com.aicleanerstorage.spaceclean.feature.files.shared.FileOperationPhase
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideosManagerDeletionTest {
    @Test
    fun `zero byte file deletion succeeds when one file was deleted`() = runTest {
        val repository = FakeFileRepository(FileDeleteResult(freedBytes = 0L, deletedCount = 1))
        val viewModel = viewModel(repository) { block -> launch { block() } }

        viewModel.refresh()
        advanceUntilIdle()
        viewModel.toggleSelection(1)
        viewModel.deleteSelectedFiles()
        advanceUntilIdle()

        assertEquals(FileOperationPhase.CompleteAnimation, viewModel.uiState.value.phase)
        assertEquals(0L, viewModel.uiState.value.deletedBytes)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `deletion fails when no selected file was deleted`() = runTest {
        val repository = FakeFileRepository(FileDeleteResult(freedBytes = 0L, deletedCount = 0))
        val viewModel = viewModel(repository) { block -> launch { block() } }

        viewModel.refresh()
        advanceUntilIdle()
        viewModel.toggleSelection(1)
        viewModel.deleteSelectedFiles()
        advanceUntilIdle()

        assertEquals(FileOperationPhase.Browsing, viewModel.uiState.value.phase)
        assertTrue(viewModel.uiState.value.errorMessage?.isNotBlank() == true)
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
