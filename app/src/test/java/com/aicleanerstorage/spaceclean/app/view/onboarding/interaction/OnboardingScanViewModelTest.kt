package com.aicleanerstorage.spaceclean.app.view.onboarding.interaction

import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceCpuInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceHardwareInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceSensorInfoModel
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.testsupport.InMemoryAppSettingsRepository
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
class OnboardingScanViewModelTest {
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
    fun `get started completes onboarding in one round`() =
        runTest(dispatcher) {
            val settings = InMemoryAppSettingsRepository()
            val viewModel = OnboardingScanViewModel(FakeDeviceInfoRepository(), settings)
            val events = mutableListOf<OnboardingEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.event.toList(events)
            }
            advanceUntilIdle()

            viewModel.dispatch(OnboardingProgressUpdated(ONBOARDING_SCAN_ROW_COUNT + 1))
            viewModel.dispatch(OnboardingAction.GetStartedClicked)
            advanceUntilIdle()

            assertEquals(
                listOf(AppDestination.Main()),
                events.filterIsInstance<OnboardingEvent.FinishOnboarding>().map { it.destination },
            )
            assertTrue(settings.isOnboardingScanCompleted())
        }

    private class FakeDeviceInfoRepository : DeviceInfoRepository {
        override suspend fun readCpuUsagePercent(previousPercent: Int?): Int? = null

        override fun batteryInfo(): BatteryInfoModel = BatteryInfoModel(levelPercent = 66)

        override fun memoryInfo(): CapacityInfoModel =
            CapacityInfoModel(
                totalBytes = 8L * 1024 * 1024 * 1024,
                availableBytes = 4L * 1024 * 1024 * 1024,
            )

        override fun internalStorageInfo(): CapacityInfoModel =
            CapacityInfoModel(
                totalBytes = 128L * 1024 * 1024 * 1024,
                availableBytes = 64L * 1024 * 1024 * 1024,
            )

        override fun hardwareInfo(): DeviceHardwareInfoModel =
            DeviceHardwareInfoModel(
                model = "fixture",
                androidVersion = "Android 16",
                screenSize = "1260x2800",
                screenDensity = "560",
                multiTouchSupported = true,
                sensors = DeviceSensorInfoModel(false, false, false, false, false, false, false),
                cpu = DeviceCpuInfoModel(hardware = "fixture", model = "fixture", cores = 8, maxFrequency = "3.2GHz"),
            )

        override fun cpuTemperatureC(): Float? = 30f

        override fun batteryCurrentNowMa(): Float? = 100f

        override fun batteryCurrentAverageMa(): Float? = 90f
    }
}
