package com.aicleanerstorage.spaceclean.app.view.networkspeed.interaction

import com.aicleanerstorage.spaceclean.domain.port.NetworkRepository
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkScanResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkSpeedModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageInfoModel
import com.aicleanerstorage.spaceclean.domain.model.network.NetworkUsageResultModel
import com.aicleanerstorage.spaceclean.domain.model.network.SpeedTestPhase
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
class NetworkSpeedViewModelTest {
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
    fun `speed test completes into the result phase`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeNetworkRepository())
            advanceUntilIdle()

            viewModel.dispatch(NetworkSpeedAction.SpeedTestRequested)
            advanceUntilIdle()

            assertEquals(NetworkSpeedPhase.Result, viewModel.state.value.phase)
            assertEquals(10.0, viewModel.state.value.speed.downloadMbps)
        }

    @Test
    fun `speed test failure shows the error phase`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeNetworkRepository(failure = IllegalStateException("boom")))
            advanceUntilIdle()

            viewModel.dispatch(NetworkSpeedAction.SpeedTestRequested)
            advanceUntilIdle()

            assertEquals(NetworkSpeedPhase.Error, viewModel.state.value.phase)
            assertEquals(UiText.Res(R.string.speed_test_failed), viewModel.state.value.error)
        }

    @Test
    fun `test is not started without a network`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeNetworkRepository(hasNetwork = false))

            viewModel.dispatch(NetworkSpeedAction.SpeedTestRequested)
            advanceUntilIdle()

            assertEquals(NetworkSpeedPhase.Idle, viewModel.state.value.phase)
        }

    @Test
    fun `back exits the page`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeNetworkRepository())
            val events = mutableListOf<NetworkSpeedEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.event.toList(events)
            }

            viewModel.dispatch(NetworkSpeedAction.BackClicked)
            advanceUntilIdle()

            assertTrue(events.any { it is NetworkSpeedEvent.Exit })
        }

    private fun viewModel(repository: NetworkRepository): NetworkSpeedViewModel =
        NetworkSpeedViewModel(
            repository = repository,
            ioDispatcher = dispatcher,
        )

    private class FakeNetworkRepository(
        private val hasNetwork: Boolean = true,
        private val failure: Throwable? = null,
    ) : NetworkRepository {
        private val lastScanFlow = MutableStateFlow<NetworkScanResultModel?>(null)

        override val lastScan: StateFlow<NetworkScanResultModel?> = lastScanFlow

        override fun readNetworkInfo(): NetworkInfoModel = NetworkInfoModel()

        override fun isNetworkAvailable(): Boolean = hasNetwork

        override fun isWifiConnected(): Boolean = hasNetwork

        override fun hasNetworkUsageAccess(): Boolean = false

        override suspend fun readNetworkUsage(): NetworkUsageResultModel =
            NetworkUsageResultModel(
                usage =
                    NetworkUsageInfoModel(
                        wifiRxBytes = 0L,
                        wifiTxBytes = 0L,
                        cellularRxBytes = 0L,
                        cellularTxBytes = 0L,
                    ),
                isToday = true,
                needsUsageAccess = false,
            )

        override suspend fun runSpeedTest(onProgress: (NetworkSpeedModel) -> Unit): NetworkSpeedModel {
            failure?.let { throw it }
            val result =
                NetworkSpeedModel(
                    downloadMbps = 10.0,
                    uploadMbps = 5.0,
                    latencyMs = 20L,
                    phase = SpeedTestPhase.Done,
                    measured = true,
                )
            onProgress(result)
            return result
        }

        override suspend fun scanWifi(): NetworkScanResultModel =
            NetworkScanResultModel(
                ssid = null,
                deviceIp = null,
                devices = emptyList(),
                hasWifi = hasNetwork,
            )

        override fun clearLastScan() {
            lastScanFlow.value = null
        }
    }
}


