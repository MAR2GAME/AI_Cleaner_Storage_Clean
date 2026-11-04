package com.aicleanerstorage.spaceclean.feature.junkclean.presentation

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.CleanRepository
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.CleanupSummary
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkAuthorizedDeleteResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkDeleteOutcome
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.MemoryCleanResult
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanProgress
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.ScanResult

import androidx.lifecycle.viewModelScope
import androidx.activity.result.IntentSenderRequest
import com.aicleanerstorage.spaceclean.feature.junkclean.ui.launchJunkDeleteAuthorization
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.feature.OperationAction
import com.aicleanerstorage.spaceclean.common.permission.PermissionNeed
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult
import com.aicleanerstorage.spaceclean.common.permission.PermissionType
import com.aicleanerstorage.spaceclean.app.permission.PermissionInterceptor
import com.aicleanerstorage.spaceclean.feature.junkclean.data.session.SharedScanState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class JunkCleanViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val models = mutableListOf<JunkCleanViewModel>()
    private val repository = RecordingRepository()
    private val session = SharedScanState()
    private val gateway = RecordingGateway()

    @Before
    fun setup() = Dispatchers.setMain(dispatcher)

    @After
    fun teardown() {
        models.forEach { it.viewModelScope.cancel() }
        Dispatchers.resetMain()
    }

    @Test
    fun `initial entry uses idle and does not rescan an existing preview`() = runTest {
        val vm = model()
        assertEquals(JunkCleanPhase.Idle, vm.state.value.phase)
        preview(vm)
        val previous = vm.state.value
        vm.dispatch(JunkCleanIntent.Entered)
        advanceUntilIdle()
        assertEquals(previous, vm.state.value)
        assertEquals(1, repository.scanCalls)
        assertEquals(1, gateway.calls)
    }

    @Test
    fun `stop then resume scanning uses idle without a separate started flag`() = runTest {
        val firstScan = CompletableDeferred<ScanResult>()
        repository.scan = { firstScan.await() }
        val vm = model()
        vm.dispatch(JunkCleanIntent.Entered)
        runCurrent()
        assertEquals(JunkCleanPhase.Scanning, vm.state.value.phase)
        vm.dispatch(JunkCleanIntent.Back)
        runCurrent()
        assertEquals(JunkCleanPhase.Idle, vm.state.value.phase)
        assertTrue(vm.state.value.showStopDialog)
        repository.scan = { repository.result }
        vm.dispatch(JunkCleanIntent.ResumeStoppedOperation)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Preview, vm.state.value.phase)
        assertFalse(vm.state.value.showStopDialog)
        assertEquals(2, repository.scanCalls)
    }

    @Test
    fun `entry denial emits one exit without scanning or recording cleanup`() = runTest {
        gateway.answer = { PermissionResult.Denied }
        val vm = model()
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.Entered)
        advanceUntilIdle()
        vm.dispatch(JunkCleanIntent.Entered)
        vm.dispatch(JunkCleanIntent.ScanRequested)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Idle, vm.state.value.phase)
        assertNull(vm.state.value.errorMessageRes)
        assertNull(vm.state.value.errorMessage)
        vm.dispatch(JunkCleanIntent.ExitFinished)
        assertEquals(JunkCleanPhase.Idle, vm.state.value.phase)
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects.filterIsInstance<JunkCleanEffect.Exit>())
        assertEquals(1, gateway.calls)
        assertEquals(0, repository.scanCalls)
        assertTrue(effects.filterIsInstance<JunkCleanEffect.Operation>().isEmpty())
        assertNull(session.cleanupSummary.value)
    }

    @Test
    fun `settings return without storage permission emits rejection exit through real interceptor`() = runTest {
        val permissionInterceptor = permissionInterceptor()
        val vm = model(permissionInterceptor)
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.Entered)
        runCurrent()
        permissionInterceptor.confirm(permissionInterceptor.request.value!!.owner)
        permissionInterceptor.beginLaunch(permissionInterceptor.request.value!!.owner, false)
        permissionInterceptor.markPaused(permissionInterceptor.request.value!!.owner)
        permissionInterceptor.settingsReturned(permissionInterceptor.request.value!!.owner)
        advanceUntilIdle()
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects.filterIsInstance<JunkCleanEffect.Exit>())
        assertEquals(JunkCleanPhase.Idle, vm.state.value.phase)
        assertNull(vm.state.value.errorMessageRes)
        assertNull(vm.state.value.errorMessage)
        assertEquals(0, repository.scanCalls)
        assertNull(permissionInterceptor.request.value)
    }

    @Test
    fun `dialog cancellation and runtime refusal exit without showing error`() = runTest {
        for (runtime in listOf(false, true)) {
            val permissionInterceptor = permissionInterceptor()
            val vm = model(permissionInterceptor)
            val effects = effects(vm)
            vm.dispatch(JunkCleanIntent.Entered)
            runCurrent()
            if (runtime) {
                permissionInterceptor.confirm(permissionInterceptor.request.value!!.owner)
                permissionInterceptor.beginLaunch(permissionInterceptor.request.value!!.owner, true)
                permissionInterceptor.runtimeReturned(permissionInterceptor.request.value!!.owner)
            } else {
                permissionInterceptor.dismiss(permissionInterceptor.request.value!!.owner)
            }
            advanceUntilIdle()
            assertEquals(JunkCleanPhase.Idle, vm.state.value.phase)
            assertNull(vm.state.value.errorMessageRes)
            assertNull(vm.state.value.errorMessage)
            assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects.filterIsInstance<JunkCleanEffect.Exit>())
        }
        assertEquals(0, repository.scanCalls)
    }

    @Test
    fun `delayed permission denial exits without starting scan`() = runTest {
        val answer = CompletableDeferred<PermissionResult>()
        gateway.answer = { answer.await() }
        val vm = model()
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.Entered)
        runCurrent()
        assertEquals(JunkCleanPhase.CheckingPermission(JunkPermissionRequest.Scan), vm.state.value.phase)
        advanceTimeBy(2_000L)
        runCurrent()
        assertEquals(0, repository.scanCalls)
        assertEquals(0f, vm.state.value.progress, 0f)
        assertEquals(0, vm.state.value.foundItemCount)
        assertEquals(0L, vm.state.value.foundTotalSize)
        answer.complete(PermissionResult.Denied)
        advanceUntilIdle()
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects.filterIsInstance<JunkCleanEffect.Exit>())
        assertEquals(0, repository.scanCalls)
        assertEquals(JunkCleanPhase.Idle, vm.state.value.phase)
        assertEquals(0f, vm.state.value.progress, 0f)
    }

    @Test
    fun `clean denial preserves preview and exits without delete memory or completion`() = runTest {
        val vm = model()
        preview(vm)
        val previous = vm.state.value
        val effects = effects(vm)
        gateway.answer = { PermissionResult.Denied }
        vm.dispatch(JunkCleanIntent.CleanSelected)
        assertEquals(JunkCleanPhase.CheckingPermission(JunkPermissionRequest.Clean), vm.state.value.phase)
        advanceUntilIdle()
        vm.dispatch(JunkCleanIntent.CleanSelected)
        vm.dispatch(JunkCleanIntent.Entered)
        vm.dispatch(JunkCleanIntent.ScanRequested)
        vm.dispatch(JunkCleanIntent.ToggleItem("direct"))
        vm.dispatch(JunkCleanIntent.ToggleCategories(listOf(JunkCategory.CACHE)))
        vm.dispatch(JunkCleanIntent.Back)
        vm.dispatch(JunkCleanIntent.ResumeStoppedOperation)
        vm.dispatch(JunkCleanIntent.CompletionAdFinished)
        advanceUntilIdle()
        assertEquals(previous, vm.state.value)
        assertEquals(2, gateway.calls)
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects.filterIsInstance<JunkCleanEffect.Exit>())
        assertEquals(0, repository.deleteCalls)
        assertEquals(0, repository.memoryCalls)
        assertFalse(effects.filterIsInstance<JunkCleanEffect.Operation>().any { it.event is FeatureOperationEvent.OperationFinished })
        assertNull(session.cleanResult.value)
        assertNull(session.cleanupSummary.value)
    }

    @Test
    fun `scan busy restores entire state and allows later retry without exit`() = runTest {
        val vm = model()
        val previous = vm.state.value
        val effects = effects(vm)
        gateway.answer = { PermissionResult.Busy }
        vm.dispatch(JunkCleanIntent.Entered)
        advanceUntilIdle()
        assertEquals(previous, vm.state.value)
        assertTrue(effects.isEmpty())
        assertEquals(0, repository.scanCalls)
        gateway.answer = { PermissionResult.Granted }
        preview(vm)
        assertEquals(1, repository.scanCalls)
    }

    @Test
    fun `clean busy restores exact preview and permits a later granted clean`() = runTest {
        val vm = model()
        preview(vm)
        vm.dispatch(JunkCleanIntent.ToggleItem("failed"))
        val previous = vm.state.value
        val effects = effects(vm)
        val previousEffects = effects.toList()
        gateway.answer = { PermissionResult.Busy }
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        assertEquals(previous, vm.state.value)
        assertEquals(previousEffects, effects)
        assertEquals(0, repository.deleteCalls)
        gateway.answer = { PermissionResult.Granted }
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        assertEquals(1, repository.deleteCalls)
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.state.value.phase)
        assertNotNull(session.cleanupSummary.value)
    }

    @Test
    fun `repeated scan requests start one permission check and one repository scan`() = runTest {
        val permission = CompletableDeferred<PermissionResult>()
        gateway.answer = { permission.await() }
        val scan = CompletableDeferred<ScanResult>()
        repository.scan = { scan.await() }
        val vm = model()
        vm.dispatch(JunkCleanIntent.Entered)
        vm.dispatch(JunkCleanIntent.ScanRequested)
        runCurrent()
        vm.dispatch(JunkCleanIntent.ScanRequested)
        assertEquals(1, gateway.calls)
        permission.complete(PermissionResult.Granted)
        runCurrent()
        vm.dispatch(JunkCleanIntent.ScanRequested)
        runCurrent()
        assertEquals(1, repository.scanCalls)
        scan.complete(repository.result)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Preview, vm.state.value.phase)
    }

    @Test
    fun `repeated clean requests start one permission check and one delete`() = runTest {
        val vm = model()
        preview(vm)
        val permission = CompletableDeferred<PermissionResult>()
        gateway.answer = { permission.await() }
        val deletes = CompletableDeferred<List<JunkDeleteOutcome>>()
        repository.delete = { deletes.await() }
        vm.dispatch(JunkCleanIntent.CleanSelected)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        runCurrent()
        vm.dispatch(JunkCleanIntent.CleanSelected)
        assertEquals(2, gateway.calls)
        permission.complete(PermissionResult.Granted)
        runCurrent()
        assertEquals(JunkCleanPhase.Cleaning, vm.state.value.phase)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        vm.dispatch(JunkCleanIntent.ScanRequested)
        runCurrent()
        assertEquals(1, repository.deleteCalls)
        assertEquals(1, repository.scanCalls)
        deletes.complete(emptyList())
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.state.value.phase)
    }

    @Test
    fun `cancellation while waiting permission prevents late granted scan`() = runTest {
        val answer = CompletableDeferred<PermissionResult>()
        gateway.answer = { answer.await() }
        val vm = model()
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.Entered)
        runCurrent()
        vm.viewModelScope.cancel()
        answer.complete(PermissionResult.Granted)
        advanceUntilIdle()
        assertEquals(0, repository.scanCalls)
        assertTrue(effects.none { it !is JunkCleanEffect.Operation })
        assertNull(session.cleanResult.value)
    }

    @Test
    fun `cancelling clean permission wait prevents deletion and completion`() = runTest {
        val vm = model()
        preview(vm)
        val permission = CompletableDeferred<PermissionResult>()
        gateway.answer = { permission.await() }
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        runCurrent()
        vm.viewModelScope.cancel()
        permission.complete(PermissionResult.Granted)
        advanceUntilIdle()
        assertEquals(0, repository.deleteCalls)
        assertEquals(0, repository.memoryCalls)
        assertTrue(effects.none { it !is JunkCleanEffect.Operation })
        assertFalse(effects.filterIsInstance<JunkCleanEffect.Operation>().any { it.event is FeatureOperationEvent.OperationFinished })
        assertNull(session.cleanupSummary.value)
    }

    @Test
    fun `cancelled deletion cannot publish cleanup summary or completion`() = runTest {
        val vm = model()
        preview(vm)
        val deletion = CompletableDeferred<List<JunkDeleteOutcome>>()
        repository.delete = { withContext(NonCancellable) { deletion.await() } }
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        runCurrent()
        vm.viewModelScope.cancel()
        deletion.complete(repository.mixedOutcomes)
        advanceUntilIdle()
        assertNull(session.cleanupSummary.value)
        assertNull(session.cleanResult.value)
        assertFalse(effects.filterIsInstance<JunkCleanEffect.Operation>().any { it.event is FeatureOperationEvent.OperationFinished })
    }

    @Test
    fun `permission exception releases request so explicit retry can scan`() = runTest {
        val vm = model()
        gateway.answer = { error("permission unavailable") }
        vm.dispatch(JunkCleanIntent.Entered)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Failed(JunkOperation.Scan), vm.state.value.phase)
        gateway.answer = { PermissionResult.Granted }
        vm.dispatch(JunkCleanIntent.ScanRequested)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Preview, vm.state.value.phase)
        assertEquals(1, repository.scanCalls)
    }

    @Test
    fun `cancelled scan cannot overwrite a newer scan result`() = runTest {
        val first = CompletableDeferred<ScanResult>()
        repository.scan = { withContext(NonCancellable) { first.await() } }
        val vm = model()
        vm.dispatch(JunkCleanIntent.Entered)
        runCurrent()
        vm.dispatch(JunkCleanIntent.Back)
        repository.scan = { repository.result }
        vm.dispatch(JunkCleanIntent.ResumeStoppedOperation)
        advanceUntilIdle()
        val preview = vm.state.value
        first.complete(ScanResult.EMPTY)
        advanceUntilIdle()
        assertEquals(preview, vm.state.value)
        assertEquals(2, repository.scanCalls)
    }

    @Test
    fun `scan exception enters error and does not emit permission exit`() = runTest {
        repository.scan = { error("scan failed") }
        val vm = model()
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.Entered)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Failed(JunkOperation.Scan), vm.state.value.phase)
        assertTrue(effects.none { it !is JunkCleanEffect.Operation })
    }

    @Test
    fun `delete authorization rejection merges pending failures without permission exit`() = runTest {
        val vm = model()
        preview(vm)
        repository.delete = { repository.mixedOutcomes }
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.AwaitingAuthorization, vm.state.value.phase)
        assertEquals(listOf("content://media/external/file/3"), effects.filterIsInstance<JunkCleanEffect.RequestDeleteAuthorization>().single().uris)
        assertEquals(1, vm.state.value.pendingAuthorizationCount)
        vm.dispatch(JunkCleanIntent.DeleteAuthorizationResult(false))
        assertEquals(JunkCleanPhase.Cleaning, vm.state.value.phase)
        assertEquals(0, vm.state.value.pendingAuthorizationCount)
        vm.dispatch(JunkCleanIntent.DeleteAuthorizationResult(false))
        advanceUntilIdle()
        assertEquals(0, repository.finalizeCalls)
        assertEquals(listOf(repository.files[0]), session.cleanResult.value!!.cleanedFiles)
        assertEquals(repository.files.drop(1), session.cleanResult.value!!.failedFiles)
        assertEquals(100L, session.cleanResult.value!!.freedSpace)
        assertEquals(20L, session.memoryResult.value!!.freedBytes)
        assertEquals(120L, session.cleanupSummary.value!!.totalFreedBytes)
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.state.value.phase)
        assertFalse(effects.any { it is JunkCleanEffect.Exit })
        assertEquals(1, effects.filterIsInstance<JunkCleanEffect.Operation>().count { it.event is FeatureOperationEvent.OperationFinished })
    }

    @Test
    fun `late completion callback does not show result after viewmodel cancellation`() = runTest {
        val vm = model()
        preview(vm)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.state.value.phase)
        vm.viewModelScope.cancel()
        vm.dispatch(JunkCleanIntent.CompletionAdFinished)
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.state.value.phase)
    }

    @Test
    fun `authorized delete merges direct and authorized results once`() = runTest {
        val vm = model()
        val effects = effects(vm)
        preview(vm)
        repository.delete = { repository.mixedOutcomes }
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        vm.dispatch(JunkCleanIntent.DeleteAuthorizationResult(true))
        vm.dispatch(JunkCleanIntent.DeleteAuthorizationResult(true))
        advanceUntilIdle()
        assertEquals(1, repository.finalizeCalls)
        assertEquals(listOf(repository.files[0], repository.files[2]), session.cleanResult.value!!.cleanedFiles)
        assertEquals(listOf(repository.files[1]), session.cleanResult.value!!.failedFiles)
        assertEquals(400L, session.cleanResult.value!!.freedSpace)
        assertEquals(420L, session.cleanupSummary.value!!.totalFreedBytes)
        assertEquals(0, vm.state.value.pendingAuthorizationCount)
        assertEquals(1, effects.filterIsInstance<JunkCleanEffect.Operation>().count { it.event is FeatureOperationEvent.OperationFinished })
    }

    @Test
    fun `back during permission wait exits once and ignores late permission and inputs`() = runTest {
        val answer = CompletableDeferred<PermissionResult>()
        gateway.answer = { withContext(NonCancellable) { answer.await() } }
        val vm = model()
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.Entered)
        runCurrent()
        val previous = vm.state.value
        vm.dispatch(JunkCleanIntent.Back)
        vm.dispatch(JunkCleanIntent.Back)
        vm.dispatch(JunkCleanIntent.ScanRequested)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        answer.complete(PermissionResult.Granted)
        advanceUntilIdle()
        assertEquals(previous, vm.state.value)
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.Return)), effects)
        assertEquals(0, repository.scanCalls)
        assertEquals(0, repository.deleteCalls)
    }

    @Test
    fun `preview back exits to previous page without changing visible state`() = runTest {
        val vm = model()
        val effects = effects(vm)
        preview(vm)
        val previous = vm.state.value
        vm.dispatch(JunkCleanIntent.Back)
        vm.dispatch(JunkCleanIntent.Back)
        vm.dispatch(JunkCleanIntent.ToggleItem("direct"))
        advanceUntilIdle()
        assertEquals(previous, vm.state.value)
        assertEquals(
            listOf(JunkCleanEffect.Exit(FeatureExitReason.Return, JunkExitTarget.Back)),
            effects.filterIsInstance<JunkCleanEffect.Exit>(),
        )
    }

    @Test
    fun `quitting stopped scan emits one back exit without resuming work`() = runTest {
        repository.scan = { awaitCancellation() }
        val vm = model()
        val effects = effects(vm)
        vm.dispatch(JunkCleanIntent.Entered)
        runCurrent()
        vm.dispatch(JunkCleanIntent.Back)
        assertTrue(vm.state.value.showStopDialog)
        vm.dispatch(JunkCleanIntent.QuitStoppedOperation)
        vm.dispatch(JunkCleanIntent.ResumeStoppedOperation)
        vm.dispatch(JunkCleanIntent.Back)
        advanceUntilIdle()
        assertFalse(vm.state.value.showStopDialog)
        assertEquals(1, repository.scanCalls)
        assertEquals(
            listOf(JunkCleanEffect.Exit(FeatureExitReason.Return)),
            effects.filterIsInstance<JunkCleanEffect.Exit>(),
        )
    }

    @Test
    fun `one effect stream delivers operations then complete exit to home`() = runTest {
        val vm = model()
        val effects = effects(vm)
        preview(vm)
        vm.dispatch(JunkCleanIntent.CompletionAdFinished)
        assertEquals(JunkCleanPhase.Preview, vm.state.value.phase)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.state.value.phase)
        assertEquals(
            listOf(
                FeatureOperationEvent.ScanStarted(FeatureKey.JUNK_CLEAN),
                FeatureOperationEvent.ScanFinished(FeatureKey.JUNK_CLEAN, true),
                FeatureOperationEvent.OperationStarted(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN),
                FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true),
            ),
            effects.filterIsInstance<JunkCleanEffect.Operation>().map { it.event },
        )
        vm.dispatch(JunkCleanIntent.CompletionAdFinished)
        assertEquals(JunkCleanPhase.Complete, vm.state.value.phase)
        vm.dispatch(JunkCleanIntent.ContinueFromResult)
        vm.dispatch(JunkCleanIntent.Back)
        advanceUntilIdle()
        assertEquals(
            listOf(JunkCleanEffect.Exit(FeatureExitReason.Return, JunkExitTarget.HomeAfterComplete)),
            effects.filterIsInstance<JunkCleanEffect.Exit>(),
        )
        assertNotNull(session.cleanupSummary.value)
        vm.dispatch(JunkCleanIntent.ExitFinished)
        assertNull(session.cleanupSummary.value)
        assertNull(session.cleanResult.value)
    }

    @Test
    fun `back during completion animation ignores late ad callback`() = runTest {
        val vm = model()
        val effects = effects(vm)
        preview(vm)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        vm.dispatch(JunkCleanIntent.Back)
        vm.dispatch(JunkCleanIntent.CompletionAdFinished)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.state.value.phase)
        assertEquals(
            listOf(JunkCleanEffect.Exit(FeatureExitReason.Return, JunkExitTarget.HomeAfterComplete)),
            effects.filterIsInstance<JunkCleanEffect.Exit>(),
        )
    }

    @Test
    fun `repository receives only selected domain files`() = runTest {
        val vm = model()
        preview(vm)
        vm.dispatch(JunkCleanIntent.ToggleItem("failed"))
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        assertEquals(listOf(repository.files[0], repository.files[2]), repository.requestedFiles)
    }

    @Test
    fun `authorization effect contains deduplicated snapshot and refusal retains every pending file`() = runTest {
        val outcomes = repository.mixedOutcomes.toMutableList()
        outcomes[1] = outcomes[1].copy(authorizationUri = outcomes[2].authorizationUri)
        repository.delete = { outcomes }
        val vm = model()
        val effects = effects(vm)
        preview(vm)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        val effect = effects.filterIsInstance<JunkCleanEffect.RequestDeleteAuthorization>().single()
        assertEquals(listOf("content://media/external/file/3"), effect.uris)
        assertEquals(1, vm.state.value.pendingAuthorizationCount)
        outcomes.clear()
        assertEquals(listOf("content://media/external/file/3"), effect.uris)
        vm.dispatch(JunkCleanIntent.DeleteAuthorizationResult(false))
        advanceUntilIdle()
        assertEquals(repository.files.drop(1), session.cleanResult.value!!.failedFiles)
        assertEquals(0, repository.finalizeCalls)
    }

    @Test
    fun `direct deletion completes without authorization effect`() = runTest {
        val vm = model()
        val effects = effects(vm)
        preview(vm)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        assertTrue(effects.filterIsInstance<JunkCleanEffect.RequestDeleteAuthorization>().isEmpty())
        assertEquals(0, vm.state.value.pendingAuthorizationCount)
        assertEquals(repository.files, session.cleanResult.value!!.cleanedFiles)
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.state.value.phase)
    }

    @Test
    fun `platform launch failure produces partial result and one completion instead of permission exit`() = runTest {
        repository.delete = { repository.mixedOutcomes }
        val vm = model()
        val effects = effects(vm)
        preview(vm)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        val effect = effects.filterIsInstance<JunkCleanEffect.RequestDeleteAuthorization>().single()
        launchJunkDeleteAuthorization(
            uris = effect.uris,
            sdkInt = 30,
            createRequest = { allocate<IntentSenderRequest>() },
            launch = { throw IllegalStateException("Cannot launch system dialog") },
            onUnavailable = { vm.dispatch(JunkCleanIntent.DeleteAuthorizationResult(false)) },
        )
        assertEquals(JunkCleanPhase.Cleaning, vm.state.value.phase)
        vm.dispatch(JunkCleanIntent.DeleteAuthorizationResult(true))
        advanceUntilIdle()
        assertEquals(0, repository.finalizeCalls)
        assertEquals(listOf(repository.files[0]), session.cleanResult.value!!.cleanedFiles)
        assertEquals(repository.files.drop(1), session.cleanResult.value!!.failedFiles)
        assertEquals(100L, session.cleanResult.value!!.freedSpace)
        assertEquals(20L, session.memoryResult.value!!.freedBytes)
        assertEquals(120L, session.cleanupSummary.value!!.totalFreedBytes)
        assertEquals(0, vm.state.value.pendingAuthorizationCount)
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.state.value.phase)
        assertTrue(effects.filterIsInstance<JunkCleanEffect.Exit>().isEmpty())
        assertEquals(
            listOf(FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true)),
            effects.filterIsInstance<JunkCleanEffect.Operation>().map { it.event }.filterIsInstance<FeatureOperationEvent.OperationFinished>(),
        )
    }

    @Test
    fun `authorization callbacks after exit cannot finalize or publish completion`() = runTest {
        repository.delete = { repository.mixedOutcomes }
        val vm = model()
        val effects = effects(vm)
        preview(vm)
        vm.dispatch(JunkCleanIntent.CleanSelected)
        advanceUntilIdle()
        val previous = vm.state.value
        vm.dispatch(JunkCleanIntent.Back)
        vm.dispatch(JunkCleanIntent.DeleteAuthorizationResult(true))
        vm.dispatch(JunkCleanIntent.DeleteAuthorizationResult(false))
        advanceUntilIdle()
        assertEquals(previous, vm.state.value)
        assertEquals(0, repository.finalizeCalls)
        assertNull(session.cleanResult.value)
        assertNull(session.cleanupSummary.value)
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.Return)), effects.filterIsInstance<JunkCleanEffect.Exit>())
        assertFalse(effects.filterIsInstance<JunkCleanEffect.Operation>().any { it.event is FeatureOperationEvent.OperationFinished })
    }

    private fun permissionInterceptor() = PermissionInterceptor({ false })

    private fun model(permission: com.aicleanerstorage.spaceclean.core.presentation.IntentInterceptor = gateway) = JunkCleanViewModel(
        repository, session, listOf(permission), dispatcher,
        scanPreviewMinMillis = 0L,
        cleaningAnimationMinMillis = 0L,
    ).also(models::add)

    private suspend fun TestScope.preview(vm: JunkCleanViewModel) {
        vm.dispatch(JunkCleanIntent.Entered)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Preview, vm.state.value.phase)
    }

    private fun TestScope.effects(vm: JunkCleanViewModel) = mutableListOf<JunkCleanEffect>().also { items ->
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.effects.toList(items) }
    }

}

private class RecordingGateway : com.aicleanerstorage.spaceclean.core.presentation.IntentInterceptor {
    var calls = 0
    var answer: suspend () -> PermissionResult = { PermissionResult.Granted }
    override suspend fun intercept(policy: com.aicleanerstorage.spaceclean.core.presentation.IntentPolicy): com.aicleanerstorage.spaceclean.core.presentation.InterceptResult {
        assertEquals(PermissionNeed.StorageFiles, policy)
        calls += 1
        return when (answer()) {
            PermissionResult.Granted -> com.aicleanerstorage.spaceclean.core.presentation.InterceptResult.Continue
            PermissionResult.Denied -> com.aicleanerstorage.spaceclean.core.presentation.InterceptResult.Block
            PermissionResult.Busy -> com.aicleanerstorage.spaceclean.core.presentation.InterceptResult.Busy
        }
    }
}

private class RecordingRepository : CleanRepository {
    val files = listOf("direct", "failed", "pending").mapIndexed { index, name ->
        JunkFile(name, "/$name", name, (index + 1) * 100L, JunkCategory.CACHE, 0L)
    }
    val result = ScanResult(files, 600L, 3, mapOf(JunkCategory.CACHE to files))
    val mixedOutcomes get() = listOf(
        JunkDeleteOutcome(files[0], true, 100L),
        JunkDeleteOutcome(files[1], false, 0L),
        JunkDeleteOutcome(files[2], false, 0L, "content://media/external/file/3"),
    )
    var scanCalls = 0
    var deleteCalls = 0
    var memoryCalls = 0
    var finalizeCalls = 0
    var requestedFiles: List<JunkFile> = emptyList()
    var scan: suspend () -> ScanResult = { result }
    var delete: suspend () -> List<JunkDeleteOutcome> = {
        files.map { JunkDeleteOutcome(it, true, it.fileSize) }
    }
    override val scanProgress = flowOf(ScanProgress.IDLE)
    override suspend fun performFullScan(): ScanResult {
        scanCalls += 1
        return scan()
    }
    override suspend fun deleteFileItems(selectedFiles: List<JunkFile>): List<JunkDeleteOutcome> {
        requestedFiles = selectedFiles.toList()
        deleteCalls += 1
        return delete()
    }
    override suspend fun cleanMemory(): MemoryCleanResult {
        memoryCalls += 1
        return MemoryCleanResult(1, 20L, 100L, 120L)
    }
    override suspend fun cleanFiles(selectedFiles: List<JunkFile>): CleanResult = error("unused")
    override suspend fun finalizeAuthorizedDeletes(pendingOutcomes: List<JunkDeleteOutcome>): JunkAuthorizedDeleteResult {
        finalizeCalls += 1
        assertEquals(listOf(mixedOutcomes.last()), pendingOutcomes)
        return JunkAuthorizedDeleteResult(listOf(files[2]), emptyList(), 300L)
    }
}

private inline fun <reified T> allocate(): T {
    val field = sun.misc.Unsafe::class.java.getDeclaredField("theUnsafe").apply { isAccessible = true }
    return (field.get(null) as sun.misc.Unsafe).allocateInstance(T::class.java) as T
}
