package com.aicleanerstorage.spaceclean.app.view.junkclean.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkDeleteOutcomeModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkMemoryCleanModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanProgressModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkScanResultModel
import com.aicleanerstorage.spaceclean.domain.port.JunkCleanRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
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
        JunkFileModel(
            id = "cache",
            path = "/data/cache/a.tmp",
            name = "a.tmp",
            sizeBytes = 100L,
            category = JunkCategory.CACHE,
            modifiedMillis = 0L,
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
            val viewModel = viewModel(FakeCleanRepository(result = JunkScanResultModel(listOf(cacheFile))))

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

            assertEquals(listOf(JunkCleanEvent.Exit), events)
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
                    result = JunkScanResultModel(listOf(cacheFile)),
                    outcomes = listOf(JunkDeleteOutcomeModel(cacheFile, DeleteStatus.Deleted)),
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
            val gate = CompletableDeferred<Unit>()
            val viewModel = viewModel(FakeCleanRepository(scanGate = gate))

            viewModel.dispatch(JunkCleanAction.ScanRequested)
            viewModel.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = true))
            runCurrent()
            assertEquals(JunkCleanPhase.Scanning, viewModel.state.value.phase)

            viewModel.dispatch(JunkCleanAction.BackClicked)

            assertTrue(viewModel.state.value.showStopDialog)
            assertEquals(JunkCleanPhase.Scanning, viewModel.state.value.stopDialogPhase)
            gate.complete(Unit)
        }

    @Test
    fun cancelledAuthorizationKeepsOnlyUnprocessedFilesInPreview() =
        runTest(dispatcher) {
            val pending = cacheFile.copy(path = "/cache/pending", id = "pending")
            val repository =
                FakeCleanRepository(
                    result = JunkScanResultModel(listOf(cacheFile, pending)),
                    outcomes =
                        listOf(
                            JunkDeleteOutcomeModel(cacheFile, DeleteStatus.Deleted),
                            JunkDeleteOutcomeModel(pending, DeleteStatus.AuthorizationCancelled),
                        ),
                )
            val model = viewModel(repository)
            model.dispatch(JunkCleanAction.Entered)
            model.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Scan, granted = true))
            advanceUntilIdle()
            model.dispatch(JunkCleanAction.CleanClicked)
            model.dispatch(JunkCleanAction.PermissionResultReceived(JunkOperation.Clean, granted = true))
            advanceUntilIdle()

            assertEquals(JunkCleanPhase.Preview, model.state.value.phase)
            assertEquals(
                listOf(pending),
                model.state.value.session.lastScan
                    ?.files,
            )
            assertEquals(
                cacheFile.sizeBytes,
                model.state.value.stoppedSummary
                    ?.freedSpace,
            )
        }

    private fun viewModel(repository: JunkCleanRepository = FakeCleanRepository()): JunkCleanViewModel =
        JunkCleanViewModel(
            cleanRepository = repository,
            ioDispatcher = dispatcher,
            scanPreviewMinMillis = 0L,
            cleaningAnimationMinMillis = 0L,
        )

    private class FakeCleanRepository(
        private val result: JunkScanResultModel = JunkScanResultModel.EMPTY,
        private val failure: Throwable? = null,
        private val outcomes: List<JunkDeleteOutcomeModel> = emptyList(),
        private val scanGate: CompletableDeferred<Unit>? = null,
    ) : JunkCleanRepository {
        override suspend fun scan(onProgress: (JunkScanProgressModel) -> Unit): Result<JunkScanResultModel> =
            failure?.let { Result.failure(it) } ?: run {
                scanGate?.await()
                onProgress(JunkScanProgressModel.IDLE)
                Result.success(result)
            }

        override suspend fun cleanMemory(): Result<JunkMemoryCleanModel> = Result.success(JunkMemoryCleanModel(freedBytes = 0L))

        override suspend fun deleteFiles(
            files: List<JunkFileModel>,
            shouldStop: () -> Boolean,
        ): Result<List<JunkDeleteOutcomeModel>> = Result.success(outcomes)
    }
}
