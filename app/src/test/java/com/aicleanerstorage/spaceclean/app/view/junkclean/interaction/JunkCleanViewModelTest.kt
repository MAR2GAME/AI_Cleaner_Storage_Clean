package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.cleanerkit.clean.CleanRepository
import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkDeleteOutcome
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import com.aicleanerstorage.cleanerkit.clean.model.MemoryCleanResult
import com.aicleanerstorage.cleanerkit.clean.model.ScanProgress
import com.aicleanerstorage.cleanerkit.clean.model.ScanResult
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
class JunkCleanViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val cacheFile =
        JunkFile(
            id = "cache",
            filePath = "/data/cache/a.tmp",
            fileName = "a.tmp",
            fileSize = 100L,
            category = JunkCategory.CACHE,
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `entered asks for permission before scanning`() =
        runTest(dispatcher) {
            val viewModel = viewModel()

            viewModel.dispatch(JunkCleanAction.Entered)

            assertEquals(JunkCleanPhase.CheckingPermission(JunkOperation.Scan), viewModel.state.value.phase)
        }

    @Test
    fun `granted permission scans and publishes the preview`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeCleanRepository(result = ScanResult(listOf(cacheFile))))

            viewModel.dispatch(JunkCleanAction.Entered)
            viewModel.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = true))
            advanceUntilIdle()

            assertEquals(JunkCleanPhase.Preview, viewModel.state.value.phase)
            assertEquals(
                1,
                viewModel.state.value.groups
                    .sumOf { it.items.size },
            )
            assertEquals(100L, viewModel.state.value.foundTotalSize)
        }

    @Test
    fun `denied permission exits the page`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            val events = mutableListOf<JunkCleanEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.event.toList(events)
            }

            viewModel.dispatch(JunkCleanAction.Entered)
            viewModel.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = false))
            advanceUntilIdle()

            assertTrue(events.any { it is JunkCleanEvent.Exit })
        }

    @Test
    fun `scan failure maps to the domain error`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeCleanRepository(failure = IllegalStateException("boom")))

            viewModel.dispatch(JunkCleanAction.Entered)
            viewModel.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = true))
            advanceUntilIdle()

            assertEquals(JunkCleanPhase.Failed(JunkOperation.Scan), viewModel.state.value.phase)
            assertEquals(UiText.Res(R.string.scan_failed), viewModel.state.value.error)
        }

    @Test
    fun `cleaning selected files completes with the summary`() =
        runTest(dispatcher) {
            val repository =
                FakeCleanRepository(
                    result = ScanResult(listOf(cacheFile)),
                    outcomes = listOf(JunkDeleteOutcome(cacheFile, DeleteStatus.Deleted)),
                )
            val viewModel = viewModel(repository)

            viewModel.dispatch(JunkCleanAction.Entered)
            viewModel.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = true))
            advanceUntilIdle()
            viewModel.dispatch(JunkCleanAction.CleanClicked)

            assertEquals(JunkCleanPhase.CheckingPermission(JunkOperation.Clean), viewModel.state.value.phase)

            viewModel.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Clean, granted = true))
            advanceUntilIdle()

            assertEquals(JunkCleanPhase.Complete, viewModel.state.value.phase)
            assertEquals(1, viewModel.state.value.cleanResult.cleanedCount)
            assertEquals(100L, viewModel.state.value.cleanResult.freedSpace)
        }

    @Test
    fun `back while scanning shows the stop dialog`() =
        runTest(dispatcher) {
            val viewModel = viewModel()

            viewModel.dispatch(JunkScanStarted)
            viewModel.dispatch(JunkCleanAction.BackClicked)

            assertTrue(viewModel.state.value.showStopDialog)
            assertEquals(JunkCleanPhase.Scanning, viewModel.state.value.stopDialogPhase)
        }

    private fun viewModel(repository: CleanRepository = FakeCleanRepository()): JunkCleanViewModel =
        JunkCleanViewModel(
            cleanRepository = repository,
            ioDispatcher = dispatcher,
            scanPreviewMinMillis = 0L,
            cleaningAnimationMinMillis = 0L,
        )

    private class FakeCleanRepository(
        private val result: ScanResult = ScanResult.EMPTY,
        private val failure: Throwable? = null,
        private val outcomes: List<JunkDeleteOutcome> = emptyList(),
    ) : CleanRepository {
        private val progress = MutableStateFlow(ScanProgress.IDLE)
        private val resultFlow = MutableStateFlow<ScanResult?>(null)

        override val scanProgress: StateFlow<ScanProgress> = progress
        override val scanResult: StateFlow<ScanResult?> = resultFlow

        override fun clearSession() {
            resultFlow.value = null
        }

        override fun removeCleanedFiles(files: List<JunkFile>) = Unit

        override suspend fun performFullScan(): ScanResult {
            failure?.let { throw it }
            resultFlow.value = result
            return result
        }

        override suspend fun cleanMemory(): MemoryCleanResult = MemoryCleanResult(freedBytes = 0L)

        override suspend fun deleteFileItems(selectedFiles: List<JunkFile>): List<JunkDeleteOutcome> = outcomes
    }
}
