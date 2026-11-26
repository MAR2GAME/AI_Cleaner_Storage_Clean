package com.aicleanerstorage.spaceclean.app.view.applock.interaction

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.domain.model.applock.AppLockAppModel
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class AppLockViewModelTest {
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
    fun `statuses drive the missing permission list`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            advanceUntilIdle()

            viewModel.dispatch(
                AppLockAction.StatusesChanged(
                    mapOf(
                        AppPermission.UsageAccess to true,
                        AppPermission.Overlay to false,
                    ),
                ),
            )

            assertEquals(listOf(AppPermission.Overlay), viewModel.state.value.missingPermissions)
        }

    @Test
    fun `monitoring change is applied because route owns permission gating`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            advanceUntilIdle()

            viewModel.dispatch(AppLockAction.MonitoringChanged(enabled = true))

            assertTrue(viewModel.state.value.monitoringEnabled)
        }

    @Test
    fun `pin setup starts for selected apps`() =
        runTest(dispatcher) {
            val viewModel = viewModel(apps = listOf(lockedApp()))
            advanceUntilIdle()

            viewModel.dispatch(AppLockAction.PinCreationRequested)

            assertEquals(AppLockPage.Pin, viewModel.state.value.page)
            assertEquals(AppLockPinStep.Create, viewModel.state.value.pinStep)
        }

    @Test
    fun `verifying the pin requests overlay permission`() =
        runTest(dispatcher) {
            val viewModel = viewModel(apps = listOf(lockedApp()), pinSet = true, pinMatches = true)
            val events = mutableListOf<AppLockEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.event.toList(events)
            }
            advanceUntilIdle()

            repeat(4) { viewModel.dispatch(AppLockAction.PinDigitClicked('1')) }
            advanceUntilIdle()

            assertEquals(AppLockPage.Manage, viewModel.state.value.page)
            assertTrue(events.contains(AppLockEvent.RequestPermission(AppPermission.Overlay)))
        }

    @Test
    fun `creating the pin requests overlay permission`() =
        runTest(dispatcher) {
            val viewModel = viewModel(apps = listOf(lockedApp()))
            val events = mutableListOf<AppLockEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.event.toList(events)
            }
            advanceUntilIdle()

            viewModel.dispatch(AppLockAction.PinCreationRequested)
            repeat(4) { viewModel.dispatch(AppLockAction.PinDigitClicked('1')) }
            advanceUntilIdle()
            repeat(4) { viewModel.dispatch(AppLockAction.PinDigitClicked('1')) }
            advanceUntilIdle()

            assertEquals(AppLockPage.Manage, viewModel.state.value.page)
            assertTrue(events.contains(AppLockEvent.RequestPermission(AppPermission.Overlay)))
        }

    @Test
    fun `back exits the page`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            val events = mutableListOf<AppLockEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.event.toList(events)
            }
            advanceUntilIdle()

            viewModel.dispatch(AppLockAction.BackClicked)
            advanceUntilIdle()

            assertTrue(events.any { it is AppLockEvent.Exit })
        }

    private fun lockedApp(): AppLockAppModel = AppLockAppModel("com.example.app", "Example", isLocked = true)

    private fun viewModel(
        apps: List<AppLockAppModel> = emptyList(),
        pinSet: Boolean = false,
        pinMatches: Boolean = false,
    ): AppLockViewModel =
        AppLockViewModel(
            repository = FakeAppLockRepository(apps, pinSet, pinMatches),
            syncMonitoringService = {},
            ioDispatcher = dispatcher,
        )

    private class FakeAppLockRepository(
        private val apps: List<AppLockAppModel> = emptyList(),
        private val pinSet: Boolean = false,
        private val pinMatches: Boolean = false,
    ) : AppLockRepository {
        private val lockedCount = MutableStateFlow(0)

        override fun observeLockedAppCount(): Flow<Int> = lockedCount

        override fun currentLockedAppCount(): Int = 0

        override fun isPinSet(): Boolean = pinSet

        override fun savePin(pin: String) = Unit

        override fun verifyPin(pin: String): Boolean = pinMatches

        override fun lockedPackages(): Set<String> = emptySet()

        override fun isPackageLocked(packageName: String): Boolean = false

        override fun setPackageLocked(
            packageName: String,
            locked: Boolean,
        ) = Unit

        override fun setLockedPackages(packageNames: Set<String>) = Unit

        override suspend fun lockableApps(): List<AppLockAppModel> = apps

        override fun isMonitoringEnabled(): Boolean = false

        override fun setMonitoringEnabled(enabled: Boolean) = Unit

        override fun isAutoLockEnabled(): Boolean = false

        override fun setAutoLockEnabled(enabled: Boolean) = Unit

        override fun isVibrationEnabled(): Boolean = true

        override fun setVibrationEnabled(enabled: Boolean) = Unit

        override fun handlePackageAdded(packageName: String) = Unit
    }
}
