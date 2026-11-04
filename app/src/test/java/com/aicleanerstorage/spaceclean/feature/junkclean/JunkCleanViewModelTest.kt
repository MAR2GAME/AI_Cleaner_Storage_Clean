package com.aicleanerstorage.spaceclean.feature.junkclean

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.common.permission.PermissionGateway
import com.aicleanerstorage.spaceclean.common.permission.PermissionNeed
import com.aicleanerstorage.spaceclean.common.permission.PermissionResult
import com.aicleanerstorage.spaceclean.common.permission.PermissionEngine
import com.aicleanerstorage.spaceclean.common.permission.PermissionHandler
import com.aicleanerstorage.spaceclean.common.permission.PermissionType
import com.aicleanerstorage.spaceclean.common.permission.RuntimePermissionDenialStore
import com.aicleanerstorage.spaceclean.app.permission.PermissionCoordinator
import com.aicleanerstorage.spaceclean.feature.junkclean.model.*
import com.aicleanerstorage.spaceclean.feature.junkclean.scanner.SharedScanState
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
    private val context = allocate<TestContext>()
    private val deleteRequest = allocate<PendingIntent>()

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
        assertEquals(JunkCleanPhase.Idle, vm.uiState.value.phase)
        preview(vm)
        val previous = vm.uiState.value
        vm.startScanIfNeeded()
        advanceUntilIdle()
        assertEquals(previous, vm.uiState.value)
        assertEquals(1, repository.scanCalls)
        assertEquals(1, gateway.calls)
    }

    @Test
    fun `stop then resume scanning uses idle without a separate started flag`() = runTest {
        val firstScan = CompletableDeferred<ScanResult>()
        repository.scan = { firstScan.await() }
        val vm = model()
        vm.startScanIfNeeded()
        runCurrent()
        assertEquals(JunkCleanPhase.Scanning, vm.uiState.value.phase)
        vm.requestStopDialog()
        runCurrent()
        assertEquals(JunkCleanPhase.Idle, vm.uiState.value.phase)
        assertTrue(vm.uiState.value.showStopDialog)
        repository.scan = { repository.result }
        vm.dismissStopDialog(resume = true)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Preview, vm.uiState.value.phase)
        assertFalse(vm.uiState.value.showStopDialog)
        assertEquals(2, repository.scanCalls)
    }

    @Test
    fun `entry denial emits one exit without scanning or recording cleanup`() = runTest {
        gateway.answer = { PermissionResult.Denied }
        val vm = model()
        val effects = effects(vm)
        val operations = operations(vm)
        vm.startScanIfNeeded()
        advanceUntilIdle()
        vm.startScanIfNeeded()
        vm.retryScan()
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Idle, vm.uiState.value.phase)
        assertNull(vm.uiState.value.errorMessageRes)
        assertNull(vm.uiState.value.errorMessage)
        vm.clearResult()
        assertEquals(JunkCleanPhase.Idle, vm.uiState.value.phase)
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects)
        assertEquals(1, gateway.calls)
        assertEquals(0, repository.scanCalls)
        assertTrue(operations.isEmpty())
        assertNull(session.cleanupSummary.value)
    }

    @Test
    fun `settings return without storage permission emits rejection exit through real coordinator`() = runTest {
        val coordinator = coordinator()
        val vm = model(coordinator)
        val effects = effects(vm)
        vm.startScanIfNeeded()
        runCurrent()
        coordinator.onDialogSubmit()
        coordinator.markSettingsLaunchObservedPause()
        coordinator.onSettingsReturnIfReady()
        advanceUntilIdle()
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects)
        assertEquals(JunkCleanPhase.Idle, vm.uiState.value.phase)
        assertNull(vm.uiState.value.errorMessageRes)
        assertNull(vm.uiState.value.errorMessage)
        assertEquals(0, repository.scanCalls)
        assertNull(coordinator.session)
    }

    @Test
    fun `dialog cancellation and runtime refusal exit without showing error`() = runTest {
        for (runtime in listOf(false, true)) {
            val coordinator = coordinator(runtime)
            val vm = model(coordinator)
            val effects = effects(vm)
            vm.startScanIfNeeded()
            runCurrent()
            if (runtime) {
                coordinator.onDialogSubmit()
                coordinator.onRuntimeResult(mapOf("storage" to false))
            } else {
                coordinator.dismiss()
            }
            advanceUntilIdle()
            assertEquals(JunkCleanPhase.Idle, vm.uiState.value.phase)
            assertNull(vm.uiState.value.errorMessageRes)
            assertNull(vm.uiState.value.errorMessage)
            assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects)
        }
        assertEquals(0, repository.scanCalls)
    }

    @Test
    fun `delayed permission denial exits without starting scan`() = runTest {
        val answer = CompletableDeferred<PermissionResult>()
        gateway.answer = { answer.await() }
        val vm = model()
        val effects = effects(vm)
        vm.startScanIfNeeded()
        runCurrent()
        assertEquals(JunkCleanPhase.CheckingPermission(JunkPermissionRequest.Scan), vm.uiState.value.phase)
        advanceTimeBy(2_000L)
        runCurrent()
        assertEquals(0, repository.scanCalls)
        assertEquals(0f, vm.uiState.value.progress, 0f)
        assertEquals(0, vm.uiState.value.foundItemCount)
        assertEquals(0L, vm.uiState.value.foundTotalSize)
        answer.complete(PermissionResult.Denied)
        advanceUntilIdle()
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects)
        assertEquals(0, repository.scanCalls)
        assertEquals(JunkCleanPhase.Idle, vm.uiState.value.phase)
        assertEquals(0f, vm.uiState.value.progress, 0f)
    }

    @Test
    fun `clean denial preserves preview and exits without delete memory or completion`() = runTest {
        val vm = model()
        preview(vm)
        val previous = vm.uiState.value
        val effects = effects(vm)
        val operations = operations(vm)
        gateway.answer = { PermissionResult.Denied }
        vm.startCleaning(context)
        assertEquals(JunkCleanPhase.CheckingPermission(JunkPermissionRequest.Clean), vm.uiState.value.phase)
        advanceUntilIdle()
        vm.startCleaning(context)
        vm.startScanIfNeeded()
        vm.retryScan()
        vm.toggleItemSelection("direct")
        vm.toggleCategorySelection(listOf(JunkCategory.CACHE))
        vm.requestStopDialog()
        vm.dismissStopDialog(resume = true)
        vm.showResult()
        advanceUntilIdle()
        assertEquals(previous, vm.uiState.value)
        assertEquals(2, gateway.calls)
        assertEquals(listOf(JunkCleanEffect.Exit(FeatureExitReason.PermissionRejected)), effects)
        assertEquals(0, repository.deleteCalls)
        assertEquals(0, repository.memoryCalls)
        assertFalse(operations.any { it is FeatureOperationEvent.OperationFinished })
        assertNull(session.cleanResult.value)
        assertNull(session.cleanupSummary.value)
    }

    @Test
    fun `scan busy restores entire state and allows later retry without exit`() = runTest {
        val vm = model()
        val previous = vm.uiState.value
        val effects = effects(vm)
        gateway.answer = { PermissionResult.Busy }
        vm.startScanIfNeeded()
        advanceUntilIdle()
        assertEquals(previous, vm.uiState.value)
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
        vm.toggleItemSelection("failed")
        val previous = vm.uiState.value
        val effects = effects(vm)
        gateway.answer = { PermissionResult.Busy }
        vm.startCleaning(context)
        advanceUntilIdle()
        assertEquals(previous, vm.uiState.value)
        assertTrue(effects.isEmpty())
        assertEquals(0, repository.deleteCalls)
        gateway.answer = { PermissionResult.Granted }
        vm.startCleaning(context)
        advanceUntilIdle()
        assertEquals(1, repository.deleteCalls)
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.uiState.value.phase)
        assertNotNull(session.cleanupSummary.value)
    }

    @Test
    fun `repeated scan requests start one permission check and one repository scan`() = runTest {
        val permission = CompletableDeferred<PermissionResult>()
        gateway.answer = { permission.await() }
        val scan = CompletableDeferred<ScanResult>()
        repository.scan = { scan.await() }
        val vm = model()
        vm.startScanIfNeeded()
        vm.retryScan()
        runCurrent()
        vm.retryScan()
        assertEquals(1, gateway.calls)
        permission.complete(PermissionResult.Granted)
        runCurrent()
        vm.retryScan()
        runCurrent()
        assertEquals(1, repository.scanCalls)
        scan.complete(repository.result)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Preview, vm.uiState.value.phase)
    }

    @Test
    fun `repeated clean requests start one permission check and one delete`() = runTest {
        val vm = model()
        preview(vm)
        val permission = CompletableDeferred<PermissionResult>()
        gateway.answer = { permission.await() }
        val deletes = CompletableDeferred<List<JunkDeleteOutcome>>()
        repository.delete = { deletes.await() }
        vm.startCleaning(context)
        vm.startCleaning(context)
        runCurrent()
        vm.startCleaning(context)
        assertEquals(2, gateway.calls)
        permission.complete(PermissionResult.Granted)
        runCurrent()
        assertEquals(JunkCleanPhase.Cleaning, vm.uiState.value.phase)
        vm.startCleaning(context)
        vm.retryScan()
        runCurrent()
        assertEquals(1, repository.deleteCalls)
        assertEquals(1, repository.scanCalls)
        deletes.complete(emptyList())
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.uiState.value.phase)
    }

    @Test
    fun `cancellation while waiting permission prevents late granted scan`() = runTest {
        val answer = CompletableDeferred<PermissionResult>()
        gateway.answer = { answer.await() }
        val vm = model()
        val effects = effects(vm)
        vm.startScanIfNeeded()
        runCurrent()
        vm.cancelActiveOperation()
        answer.complete(PermissionResult.Granted)
        advanceUntilIdle()
        assertEquals(0, repository.scanCalls)
        assertTrue(effects.isEmpty())
        assertNull(session.cleanResult.value)
    }

    @Test
    fun `cancelling clean permission wait prevents deletion and completion`() = runTest {
        val vm = model()
        preview(vm)
        val permission = CompletableDeferred<PermissionResult>()
        gateway.answer = { permission.await() }
        val effects = effects(vm)
        val operations = operations(vm)
        vm.startCleaning(context)
        runCurrent()
        vm.cancelActiveOperation()
        permission.complete(PermissionResult.Granted)
        advanceUntilIdle()
        assertEquals(0, repository.deleteCalls)
        assertEquals(0, repository.memoryCalls)
        assertTrue(effects.isEmpty())
        assertFalse(operations.any { it is FeatureOperationEvent.OperationFinished })
        assertNull(session.cleanupSummary.value)
    }

    @Test
    fun `cancelled deletion cannot publish cleanup summary or completion`() = runTest {
        val vm = model()
        preview(vm)
        val deletion = CompletableDeferred<List<JunkDeleteOutcome>>()
        repository.delete = { withContext(NonCancellable) { deletion.await() } }
        val operations = operations(vm)
        vm.startCleaning(context)
        runCurrent()
        vm.cancelActiveOperation()
        deletion.complete(repository.mixedOutcomes)
        advanceUntilIdle()
        assertNull(session.cleanupSummary.value)
        assertNull(session.cleanResult.value)
        assertFalse(operations.any { it is FeatureOperationEvent.OperationFinished })
    }

    @Test
    fun `permission exception releases request so explicit retry can scan`() = runTest {
        val vm = model()
        gateway.answer = { error("permission unavailable") }
        vm.startScanIfNeeded()
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Failed(JunkOperation.Scan), vm.uiState.value.phase)
        gateway.answer = { PermissionResult.Granted }
        vm.retryScan()
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Preview, vm.uiState.value.phase)
        assertEquals(1, repository.scanCalls)
    }

    @Test
    fun `cancelled scan cannot overwrite a newer scan result`() = runTest {
        val first = CompletableDeferred<ScanResult>()
        repository.scan = { withContext(NonCancellable) { first.await() } }
        val vm = model()
        vm.startScanIfNeeded()
        runCurrent()
        vm.cancelActiveOperation()
        repository.scan = { repository.result }
        vm.retryScan()
        advanceUntilIdle()
        val preview = vm.uiState.value
        first.complete(ScanResult.EMPTY)
        advanceUntilIdle()
        assertEquals(preview, vm.uiState.value)
        assertEquals(2, repository.scanCalls)
    }

    @Test
    fun `scan exception enters error and does not emit permission exit`() = runTest {
        repository.scan = { error("scan failed") }
        val vm = model()
        val effects = effects(vm)
        vm.startScanIfNeeded()
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Failed(JunkOperation.Scan), vm.uiState.value.phase)
        assertTrue(effects.isEmpty())
    }

    @Test
    fun `delete authorization rejection merges pending failures without permission exit`() = runTest {
        val vm = model()
        preview(vm)
        repository.delete = { repository.mixedOutcomes }
        val effects = effects(vm)
        vm.startCleaning(context)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.AwaitingAuthorization, vm.uiState.value.phase)
        assertSame(deleteRequest, (effects.single() as JunkCleanEffect.RequestDeleteAuthorization).deleteRequest)
        vm.handleAuthorizationResult(false)
        vm.handleAuthorizationResult(false)
        advanceUntilIdle()
        assertEquals(0, repository.finalizeCalls)
        assertEquals(listOf(repository.files[0]), session.cleanResult.value!!.cleanedFiles)
        assertEquals(repository.files.drop(1), session.cleanResult.value!!.failedFiles)
        assertEquals(100L, session.cleanResult.value!!.freedSpace)
        assertEquals(20L, session.memoryResult.value!!.freedBytes)
        assertEquals(120L, session.cleanupSummary.value!!.totalFreedBytes)
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.uiState.value.phase)
        assertFalse(effects.any { it is JunkCleanEffect.Exit })
    }

    @Test
    fun `late completion callback does not show result after viewmodel cancellation`() = runTest {
        val vm = model()
        preview(vm)
        vm.startCleaning(context)
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.uiState.value.phase)
        vm.viewModelScope.cancel()
        vm.showResult()
        assertEquals(JunkCleanPhase.CompleteAnimation, vm.uiState.value.phase)
    }

    @Test
    fun `authorized delete merges direct and authorized results once`() = runTest {
        val vm = model()
        preview(vm)
        repository.delete = { repository.mixedOutcomes }
        vm.startCleaning(context)
        advanceUntilIdle()
        vm.handleAuthorizationResult(true)
        vm.handleAuthorizationResult(true)
        advanceUntilIdle()
        assertEquals(1, repository.finalizeCalls)
        assertEquals(listOf(repository.files[0], repository.files[2]), session.cleanResult.value!!.cleanedFiles)
        assertEquals(listOf(repository.files[1]), session.cleanResult.value!!.failedFiles)
        assertEquals(400L, session.cleanResult.value!!.freedSpace)
        assertEquals(420L, session.cleanupSummary.value!!.totalFreedBytes)
    }

    private fun coordinator(runtime: Boolean = false) = PermissionCoordinator(
        context,
        PermissionEngine(
            handlers = listOf(object : PermissionHandler {
                override val permission = PermissionType.StorageFiles
                override fun isGranted(context: Context) = false
                override fun runtimePermissions(context: Context) = if (runtime) listOf("storage") else emptyList()
                override fun settingsIntents(context: Context) = listOf(allocate<Intent>())
            }),
            denialStore = object : RuntimePermissionDenialStore {
                override fun hasDenied(permission: PermissionType) = false
                override fun markDenied(permission: PermissionType) = Unit
            },
            isRuntimePermissionGranted = { _, _ -> false },
            settingsIntentKey = { "storage" },
        ),
    )

    private fun model(permissionGateway: PermissionGateway = gateway) = JunkCleanViewModel(
        repository, session, permissionGateway, dispatcher,
        scanPreviewMinMillis = 0L,
        cleaningAnimationMinMillis = 0L,
        createDeleteRequest = { _, uris ->
            assertEquals(listOf("content://media/external/file/3"), uris)
            deleteRequest
        },
    ).also(models::add)

    private suspend fun TestScope.preview(vm: JunkCleanViewModel) {
        vm.startScanIfNeeded()
        advanceUntilIdle()
        assertEquals(JunkCleanPhase.Preview, vm.uiState.value.phase)
    }

    private fun TestScope.effects(vm: JunkCleanViewModel) = mutableListOf<JunkCleanEffect>().also { items ->
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.effects.toList(items) }
    }

    private fun TestScope.operations(vm: JunkCleanViewModel) = mutableListOf<FeatureOperationEvent>().also { items ->
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.operationEvents.toList(items) }
    }
}

private class RecordingGateway : PermissionGateway {
    var calls = 0
    var answer: suspend () -> PermissionResult = { PermissionResult.Granted }
    override suspend fun ensure(need: PermissionNeed): PermissionResult {
        assertEquals(PermissionNeed.StorageFiles, need)
        calls += 1
        return answer()
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
    var scan: suspend () -> ScanResult = { result }
    var delete: suspend () -> List<JunkDeleteOutcome> = {
        files.map { JunkDeleteOutcome(it, true, it.fileSize) }
    }
    override val scanProgress = flowOf(ScanProgress.IDLE)
    override suspend fun performFullScan(): ScanResult {
        scanCalls += 1
        return scan()
    }
    override suspend fun deleteFileItems(selectedItems: List<CleanItem>): List<JunkDeleteOutcome> {
        deleteCalls += 1
        return delete()
    }
    override suspend fun cleanMemory(): MemoryCleanResult {
        memoryCalls += 1
        return MemoryCleanResult(1, 20L, 100L, 120L)
    }
    override suspend fun cleanFiles(selectedItems: List<CleanItem>): CleanResult = error("unused")
    override suspend fun finalizeAuthorizedDeletes(pendingOutcomes: List<JunkDeleteOutcome>): JunkAuthorizedDeleteResult {
        finalizeCalls += 1
        assertEquals(listOf(mixedOutcomes.last()), pendingOutcomes)
        return JunkAuthorizedDeleteResult(listOf(files[2]), emptyList(), 300L)
    }
}

private class TestContext : ContextWrapper(null) {
    override fun getApplicationContext(): Context = this
    override fun getResources(): Resources = allocate<TestResources>()
}

private class TestResources : Resources(null, null, null) {
    override fun getString(id: Int, vararg formatArgs: Any): String = "Confirm deletion"
}

private inline fun <reified T> allocate(): T {
    val field = sun.misc.Unsafe::class.java.getDeclaredField("theUnsafe").apply { isAccessible = true }
    return (field.get(null) as sun.misc.Unsafe).allocateInstance(T::class.java) as T
}
