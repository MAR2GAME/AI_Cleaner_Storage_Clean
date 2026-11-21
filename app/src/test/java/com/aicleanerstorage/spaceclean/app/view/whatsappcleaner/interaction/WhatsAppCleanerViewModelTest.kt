package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction

import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus
import com.aicleanerstorage.cleanerkit.files.model.FileDeleteOutcome
import com.aicleanerstorage.cleanerkit.files.model.FileDeleteResult
import com.aicleanerstorage.cleanerkit.files.model.FileUri
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileType
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WhatsAppCleanerViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `scan loads cache files into the result phase`() =
        runTest(dispatcher) {
            val viewModel =
                viewModel(
                    files = listOf(cacheItem("a.jpg"), cacheItem("b.jpg")),
                )

            viewModel.dispatch(WhatsAppCleanerAction.ScanStartRequested)
            advanceUntilIdle()

            assertEquals(WhatsAppCleanerPhase.ScanResult, viewModel.state.value.phase)
            assertEquals(
                2,
                viewModel.state.value.groups
                    .sumOf { it.children.sumOf { child -> child.files.size } },
            )
            assertEquals(200L, viewModel.state.value.selectedBytes)
        }

    @Test
    fun `cleaning selected files reaches the result phase`() =
        runTest(dispatcher) {
            val viewModel =
                viewModel(
                    files = listOf(cacheItem("a.jpg"), cacheItem("b.jpg")),
                )
            viewModel.dispatch(WhatsAppCleanerAction.ScanStartRequested)
            advanceUntilIdle()

            viewModel.dispatch(WhatsAppCleanerAction.CleanClicked)
            advanceUntilIdle()

            assertEquals(WhatsAppCleanerPhase.Result, viewModel.state.value.phase)
            assertEquals(2, viewModel.state.value.deletedCount)
        }

    @Test
    fun `permission denial shows the error phase`() =
        runTest(dispatcher) {
            val viewModel = viewModel()

            viewModel.dispatch(WhatsAppCleanerAction.PermissionDenied)

            assertEquals(WhatsAppCleanerPhase.Error, viewModel.state.value.phase)
            assertEquals(UiText.Res(R.string.permission_denied), viewModel.state.value.error)
        }

    @Test
    fun `back exits the page`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            val events = mutableListOf<WhatsAppCleanerEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.event.toList(events)
            }
            advanceUntilIdle()

            viewModel.dispatch(WhatsAppCleanerAction.BackClicked)
            advanceUntilIdle()

            assertTrue(events.any { it is WhatsAppCleanerEvent.Exit })
        }

    private fun viewModel(files: List<ManagedFileItem> = emptyList()): WhatsAppCleanerViewModel =
        WhatsAppCleanerViewModel(
            repository = FakeFileRepository(files),
            ioDispatcher = dispatcher,
        )

    private fun cacheItem(name: String): ManagedFileItem =
        ManagedFileItem(
            id = name.hashCode().toLong(),
            uri = FileUri("content://whatsapp/$name"),
            path = "/storage/emulated/0/WhatsApp/cache/$name",
            name = name,
            sizeBytes = 100L,
            modifiedSeconds = 0L,
            mimeType = "image/jpeg",
            bucketName = "WhatsApp cache",
            type = ManagedFileType.Image,
        )

    private class FakeFileRepository(
        private val files: List<ManagedFileItem>,
    ) : FileRepository {
        override suspend fun loadImages(): List<ManagedFileItem> = emptyList()

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
        ): List<ManagedFileItem> = files

        override suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult =
            FileDeleteResult(
                outcomes = items.map { FileDeleteOutcome(it, DeleteStatus.Deleted) },
            )

        override suspend fun removeLocationData(items: List<ManagedFileItem>): Int = 0
    }
}
