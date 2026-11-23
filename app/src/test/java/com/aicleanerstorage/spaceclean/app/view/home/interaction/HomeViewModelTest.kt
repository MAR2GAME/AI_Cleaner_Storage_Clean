package com.aicleanerstorage.spaceclean.app.view.home.interaction

import androidx.lifecycle.ViewModelStore
import com.aicleanerstorage.spaceclean.domain.model.device.BatteryInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceCpuInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceHardwareInfoModel
import com.aicleanerstorage.spaceclean.domain.model.device.DeviceSensorInfoModel
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.port.AppSettingsRepository
import com.aicleanerstorage.spaceclean.domain.port.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.testsupport.InMemoryAppSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Proxy

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Test fun `restored tab request wins over initial entry regardless of delivery order`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                for (requestFirst in listOf(false, true)) {
                    val store = ViewModelStore()
                    val model =
                        HomeViewModel(
                            repository = unusedRepository(),
                            appLockRepository = unusedRepository(),
                            settingsRepository = FakeHomeSettings(System.currentTimeMillis()),
                            ioDispatcher = dispatcher,
                            autoRateDispatcher = dispatcher,
                        )
                    store.put("home", model)
                    try {
                        val entered = HomeAction.Entered(0)
                        val request = HomeAction.TabSelected(2)
                        (if (requestFirst) listOf(request, entered) else listOf(entered, request))
                            .forEach(model::dispatch)
                        assertEquals(2, model.state.value.selectedTab)
                        model.dispatch(HomeAction.Entered(0))
                        assertEquals(2, model.state.value.selectedTab)
                    } finally {
                        store.clear()
                    }
                }
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun interactionsCancelTheRatingPromptAndExternalPromptDelaysItOnce() =
        runTest {
            val main = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(main)
            try {
                for (interaction in listOf(HomeAction.FeatureClicked, HomeAction.TabInteracted, null)) {
                    val store = ViewModelStore()
                    val settings = FakeHomeSettings(0L)
                    val model =
                        HomeViewModel(
                            repository = unusedRepository(),
                            appLockRepository =
                                object : AppLockRepository by unusedRepository<AppLockRepository>() {
                                    override fun observeLockedAppCount(): Flow<Int> = MutableStateFlow(0)

                                    override fun currentLockedAppCount(): Int = 0
                                },
                            settingsRepository = settings,
                            ioDispatcher = StandardTestDispatcher(TestCoroutineScheduler()),
                            autoRateDispatcher = main,
                            autoRatePromptDelayMillis = 100,
                        )
                    store.put("home", model)
                    try {
                        model.dispatch(HomeAction.ExternalBlockingPromptChanged(true))
                        testScheduler.advanceTimeBy(150)
                        assertEquals(0, settings.promptCount)
                        model.dispatch(HomeAction.ExternalBlockingPromptChanged(false))
                        testScheduler.advanceTimeBy(50)
                        model.dispatch(HomeAction.ExternalBlockingPromptChanged(false))
                        interaction?.let(model::dispatch)
                        testScheduler.advanceTimeBy(49)
                        assertEquals(false, model.state.value.showAutoRateDialog)
                        testScheduler.advanceTimeBy(2)
                        assertEquals(interaction == null, model.state.value.showAutoRateDialog)
                        assertEquals(if (interaction == null) 1 else 0, settings.promptCount)
                    } finally {
                        store.clear()
                    }
                }
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun `repeated refresh requests share a single load`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            try {
                val repository = CountingDeviceInfoRepository()
                val store = ViewModelStore()
                val model =
                    HomeViewModel(
                        repository = repository,
                        appLockRepository =
                            object : AppLockRepository by unusedRepository<AppLockRepository>() {
                                override fun observeLockedAppCount(): Flow<Int> = MutableStateFlow(0)
                            },
                        settingsRepository = FakeHomeSettings(0L),
                        ioDispatcher = dispatcher,
                        autoRateDispatcher = dispatcher,
                        autoRatePromptDelayMillis = 1L,
                    )
                store.put("home", model)

                repeat(500) { model.dispatch(HomeAction.SummaryRefreshRequested) }
                advanceUntilIdle()

                assertEquals(1, repository.batteryReads)
                assertEquals(false, model.state.value.summary.isLoading)
                store.clear()
            } finally {
                Dispatchers.resetMain()
            }
        }

    private class FakeHomeSettings(
        private var lastPrompt: Long,
    ) : AppSettingsRepository by InMemoryAppSettingsRepository() {
        var promptCount = 0
            private set

        override fun readTemperatureUnit() = "C"

        override fun saveTemperatureUnit(unit: String): Result<Unit> = Result.success(Unit)

        override fun readLastAutoRatePromptAt() = lastPrompt

        override fun saveLastAutoRatePromptAt(timestampMillis: Long): Result<Unit> {
            lastPrompt = timestampMillis
            promptCount++
            return Result.success(Unit)
        }

        override fun hasShownNotificationCleanerExitPrompt() = false

        override fun saveNotificationCleanerExitPromptShown(): Result<Unit> = Result.success(Unit)
    }

    private class CountingDeviceInfoRepository : DeviceInfoRepository {
        var batteryReads = 0
            private set

        override suspend fun readCpuUsagePercent(previousPercent: Int?): Int? = null

        override fun batteryInfo(): BatteryInfoModel {
            batteryReads++
            return BatteryInfoModel(levelPercent = 66)
        }

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

    // Summary jobs remain queued on the test dispatcher; this test exercises synchronous navigation inputs.
    private inline fun <reified T> unusedRepository(): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ ->
            error("Unexpected summary call: ${method.name}")
        } as T
}
