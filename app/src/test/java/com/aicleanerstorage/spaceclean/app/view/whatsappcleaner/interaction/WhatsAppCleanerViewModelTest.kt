package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppCleanerCategory
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppCleanerGroup
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppFileBucketModel
import com.aicleanerstorage.spaceclean.domain.port.WhatsAppCleanerRepository
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
    fun `cleaning failure shows the error phase`() =
        runTest(dispatcher) {
            val viewModel =
                viewModel(
                    files = listOf(cacheItem("a.jpg")),
                    deleteResult = Result.failure(IllegalStateException("delete failed")),
                )
            viewModel.dispatch(WhatsAppCleanerAction.ScanStartRequested)
            advanceUntilIdle()
            viewModel.dispatch(WhatsAppCleanerAction.CleanClicked)
            advanceUntilIdle()
            assertEquals(WhatsAppCleanerPhase.Error, viewModel.state.value.phase)
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

    private fun viewModel(
        files: List<ManagedFileModel> = emptyList(),
        deleteResult: Result<FileDeleteSummaryModel>? = null,
    ): WhatsAppCleanerViewModel =
        WhatsAppCleanerViewModel(
            repository = FakeWhatsAppCleanerRepository(files, deleteResult),
            ioDispatcher = dispatcher,
        )

    private fun cacheItem(name: String): ManagedFileModel =
        ManagedFileModel(
            id = name.hashCode().toLong(),
            uri = "content://whatsapp/$name",
            path = "/storage/emulated/0/WhatsApp/cache/$name",
            name = name,
            sizeBytes = 100L,
            modifiedSeconds = 0L,
            mimeType = "image/jpeg",
            bucketName = "WhatsApp cache",
            kind = FileKind.Image,
        )

    private class FakeWhatsAppCleanerRepository(
        private val files: List<ManagedFileModel>,
        private val deleteResult: Result<FileDeleteSummaryModel>?,
    ) : WhatsAppCleanerRepository {
        override suspend fun loadGroups(): Map<WhatsAppFileBucketModel, List<ManagedFileModel>> =
            if (files.isEmpty()) {
                emptyMap()
            } else {
                mapOf(WhatsAppFileBucketModel(WhatsAppCleanerGroup.Cache, WhatsAppCleanerCategory.Images) to files)
            }

        override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> =
            deleteResult ?: Result.success(FileDeleteSummaryModel(files.size, files.sumOf { it.sizeBytes }))
    }
}


