package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.app.view.audios.interaction.AudiosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.documents.interaction.DocumentsManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.largefiles.interaction.LargeFilesManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.photos.interaction.PhotosManagerViewModel
import com.aicleanerstorage.spaceclean.app.view.screenshots.interaction.ScreenshotsManagerViewModel
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

/** 验证各文件页自己的 loader、tab、排序和列表投影。 */
@OptIn(ExperimentalCoroutinesApi::class)
class FilePageViewModelsTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `photos keep fixed tab ids and limit`() =
        runTest {
            val photos = (0 until 65).map { file(it.toLong(), 10L + it, "DCIM", FileKind.Image) }
            val viewModel =
                PhotosManagerViewModel(
                    repository = FakeFileRepository(images = photos),
                    ioDispatcher = dispatcher,
                    scanDelayMillis = 0L,
                )

            viewModel.dispatch(FilePageAction.InitialLoadRequested)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(60, state.page.items.size)
            assertEquals(listOf("Photo", "Pictures", "DCIM", "Other"), state.tabs.map { it.title })
            viewModel.dispatch(FilePageAction.TabChangeRequested(2))
            assertEquals(60, viewModel.state.value.currentDisplayItems.size)
        }

    @Test
    fun `screenshots have no tabs and can select all visible`() =
        runTest {
            val screenshots = (0 until 65).map { file(it.toLong(), 10L + it, "Screenshots", FileKind.Image) }
            val viewModel =
                ScreenshotsManagerViewModel(
                    repository = FakeFileRepository(screenshots = screenshots),
                    ioDispatcher = dispatcher,
                    scanDelayMillis = 0L,
                )

            viewModel.dispatch(FilePageAction.InitialLoadRequested)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(60, state.page.items.size)
            assertEquals(60, state.displayItems.size)
            assertFalse(state.allSelected)

            viewModel.dispatch(FilePageAction.VisibleItemsToggled)
            assertTrue(viewModel.state.value.allSelected)
        }

    @Test
    fun `audios expose all and music tabs`() =
        runTest {
            val audios =
                listOf(
                    file(1, 10, "Music", FileKind.Audio),
                    file(2, 20, "Music", FileKind.Audio),
                    file(3, 30, "Download", FileKind.Audio),
                )
            val viewModel =
                AudiosManagerViewModel(
                    repository = FakeFileRepository(audios = audios),
                    ioDispatcher = dispatcher,
                    scanDelayMillis = 0L,
                )

            viewModel.dispatch(FilePageAction.InitialLoadRequested)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(listOf("All", "Music"), state.tabs.map { it.title })
            viewModel.dispatch(FilePageAction.TabChangeRequested(1))
            assertEquals(2, viewModel.state.value.visibleDisplayItems.size)
        }

    @Test
    fun `documents sort by size and expose document list items`() =
        runTest {
            val documents =
                listOf(
                    file(1, 100, "Download", FileKind.Document),
                    file(2, 300, "Download", FileKind.Document),
                    file(3, 200, "Download", FileKind.Document),
                )
            val viewModel =
                DocumentsManagerViewModel(
                    repository = FakeFileRepository(documents = documents),
                    ioDispatcher = dispatcher,
                    scanDelayMillis = 0L,
                )

            viewModel.dispatch(FilePageAction.InitialLoadRequested)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(listOf(300L, 200L, 100L), state.page.items.map { it.file.sizeBytes })
            assertTrue(state.visibleListItems.all { it.iconKind == FileListIconKind.Document })
        }

    @Test
    fun `large files sort by size and expose large video icons`() =
        runTest {
            val files =
                listOf(
                    file(1, 2_000, "Movies", FileKind.Video),
                    file(2, 5_000, "Movies", FileKind.Video),
                    file(3, 3_000, "Movies", FileKind.Video),
                )
            val viewModel =
                LargeFilesManagerViewModel(
                    repository = FakeFileRepository(largeFiles = files),
                    ioDispatcher = dispatcher,
                    scanDelayMillis = 0L,
                )

            viewModel.dispatch(FilePageAction.InitialLoadRequested)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(listOf(5_000L, 3_000L, 2_000L), state.page.items.map { it.file.sizeBytes })
            assertEquals(FileListIconKind.LargeVideo, state.visibleListItems.first().iconKind)
        }

    private fun file(
        id: Long,
        size: Long,
        bucket: String,
        kind: FileKind,
    ): ManagedFileModel =
        ManagedFileModel(
            id = id,
            uri = "content://media/file/$id",
            path = "/storage/emulated/0/$bucket/file-$id",
            name = "file-$id",
            sizeBytes = size,
            modifiedSeconds = 1_700_000_000L + id,
            mimeType = null,
            bucketName = bucket,
            kind = kind,
        )

    private class FakeFileRepository(
        private val images: List<ManagedFileModel> = emptyList(),
        private val audios: List<ManagedFileModel> = emptyList(),
        private val screenshots: List<ManagedFileModel> = emptyList(),
        private val documents: List<ManagedFileModel> = emptyList(),
        private val largeFiles: List<ManagedFileModel> = emptyList(),
    ) : ManagedFileRepository {
        override suspend fun loadImages(): List<ManagedFileModel> = images

        override suspend fun loadVideos(): List<ManagedFileModel> = emptyList()

        override suspend fun loadAudios(): List<ManagedFileModel> = audios

        override suspend fun loadScreenshots(): List<ManagedFileModel> = screenshots

        override suspend fun loadPrivacyImages(): List<ManagedFileModel> = emptyList()

        override suspend fun loadDocuments(): List<ManagedFileModel> = documents

        override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileModel> = largeFiles

        override suspend fun loadDuplicateGroups(): List<DuplicateFileGroupModel> = emptyList()

        override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> =
            Result.failure(UnsupportedOperationException())

        override suspend fun deleteDuplicates(selections: List<DuplicateFileSelectionModel>): Result<FileDeleteSummaryModel> =
            Result.failure(UnsupportedOperationException())

        override suspend fun removeLocationData(files: List<ManagedFileModel>): Result<Int> = Result.success(0)
    }
}
