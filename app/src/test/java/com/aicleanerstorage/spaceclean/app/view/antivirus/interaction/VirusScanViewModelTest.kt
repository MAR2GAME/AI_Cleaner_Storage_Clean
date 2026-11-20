package com.aicleanerstorage.spaceclean.app.view.antivirus.interaction

import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.base.coroutines.AppDispatchers
import com.aicleanerstorage.spaceclean.domain.model.VirusScanMode
import com.aicleanerstorage.spaceclean.domain.model.VirusThreatModel
import com.aicleanerstorage.spaceclean.domain.port.VirusScanner
import com.aicleanerstorage.spaceclean.domain.model.VirusScanItemModel
import com.aicleanerstorage.spaceclean.domain.port.VirusSecurityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VirusScanViewModelTest {
    private val main = TestCoroutineScheduler()
    private val io = TestCoroutineScheduler()
    private val engine = FakeScanEngine()
    private val events = mutableListOf<VirusScanEvent>()
    private lateinit var viewModel: VirusScanViewModel

    @Before
    fun setUp() {
        val dispatcher = StandardTestDispatcher(main)
        Dispatchers.setMain(dispatcher)
        viewModel = VirusScanViewModel(
            engine, FakeSecurityRepository(),
            dispatchers = AppDispatchers(io = StandardTestDispatcher(io), default = dispatcher, main = dispatcher),
        )
        viewModel.viewModelScope.launch { viewModel.event.collect { events.add(it) } }
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun cancelledScanCallbacksCannotChangeTheNextScan() {
        val first = startScan()
        viewModel.dispatch(VirusScanAction.ScanCancellationRequested)
        val second = startScan()
        first.onScanProgress(item)
        first.onScanFinished()
        main.runCurrent()
        io.runCurrent()
        main.runCurrent()
        assertTrue(viewModel.state.value.isScanning)
        assertTrue(viewModel.state.value.threats.isEmpty())
        assertTrue(events.isEmpty())
        second.onScanFinished()
        finishDisplay()
        assertEquals(listOf(VirusScanEvent.NavigateToResult(false)), events)
    }

    @Test
    fun resultWaitsForThreatDetails() {
        val listener = startScan()
        listener.onScanProgress(item)
        listener.onScanFinished()
        finishDisplay()
        assertFalse(viewModel.state.value.scanCompleted)
        assertTrue(events.isEmpty())
        io.runCurrent()
        main.runCurrent()
        assertTrue(viewModel.state.value.scanCompleted)
        assertEquals(listOf("pkg"), viewModel.state.value.threats.map { it.id })
        assertEquals(1, viewModel.state.value.appThreatCount)
        assertEquals(listOf(VirusScanEvent.NavigateToResult(true)), events)
    }

    @Test
    fun duplicateTerminalCallbacksEmitOneNavigation() {
        val listener = startScan()
        listener.onScanFinished()
        listener.onScanFinished()
        listener.onScanInterrupt()
        finishDisplay()
        listener.onScanFinished()
        main.advanceTimeBy(10_000)
        main.runCurrent()
        assertEquals(listOf(VirusScanEvent.NavigateToResult(false)), events)
    }

    private fun startScan(): VirusScanner.Listener {
        viewModel.dispatch(VirusScanAction.ScanStartRequested(VirusScanMode.Quick))
        main.runCurrent()
        return engine.listener
    }

    private fun finishDisplay() {
        main.runCurrent()
        main.advanceTimeBy(VirusScanMode.Quick.minDurationMillis + 1)
        main.runCurrent()
    }

    private val item = VirusScanItemModel("pkg", null, null, null, null, 8)

    private class FakeScanEngine : VirusScanner {
        lateinit var listener: VirusScanner.Listener
        override fun startScan(mode: VirusScanMode, listener: VirusScanner.Listener) { this.listener = listener }
        override fun cancelScan() = Unit
    }

    private class FakeSecurityRepository : VirusSecurityRepository {
        override fun appLabel(packageName: String) = packageName
        override fun threatFor(item: VirusScanItemModel, isFile: Boolean) =
            VirusThreatModel(requireNotNull(item.packageName), item.packageName, null, "", "", isFile, null)
        override fun hasInstalledAppsAccess() = true
        override fun hasAdbRisk() = false

        override fun isScanNoticeAccepted() = true
        override fun setScanNoticeAccepted() = Unit
        override fun hasInstalledAppsAccessFailedBefore() = false
        override fun setInstalledAppsAccessFailed(failed: Boolean) = Unit
    }
}
