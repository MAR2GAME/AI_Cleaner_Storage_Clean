package com.aicleanerstorage.spaceclean.app.view.applock.interaction

import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.domain.applock.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.applock.model.AppLockAppModel
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
import org.junit.Assert.assertFalse
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
    fun `monitoring stays off while permissions are missing`() =
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
            viewModel.dispatch(AppLockAction.MonitoringChanged(enabled = true))

            assertFalse(viewModel.state.value.monitoringEnabled)
        }

    @Test
    fun `monitoring starts when both permissions are granted`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            advanceUntilIdle()

            viewModel.dispatch(
                AppLockAction.StatusesChanged(
                    mapOf(
                        AppPermission.UsageAccess to true,
                        AppPermission.Overlay to true,
                    ),
                ),
            )
            viewModel.dispatch(AppLockAction.MonitoringChanged(enabled = true))

            assertTrue(viewModel.state.value.monitoringEnabled)
        }

    @Test
    fun `permission click opens the matching settings page`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            val events = mutableListOf<AppLockEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.event.toList(events)
            }
            advanceUntilIdle()

            viewModel.dispatch(AppLockAction.PermissionClicked(AppPermission.Overlay))
            advanceUntilIdle()

            assertEquals(listOf(AppLockEvent.OpenPermissionSettings(AppPermission.Overlay)), events)
        }

    @Test
    fun `back on the manage page exits`() =
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

    private fun viewModel(): AppLockViewModel =
        AppLockViewModel(
            repository = FakeAppLockRepository(),
            syncMonitoringService = {},
            ioDispatcher = dispatcher,
        )

    private class FakeAppLockRepository : AppLockRepository {
        private val lockedCount = MutableStateFlow(0)

        override val lockedAppCountFlow: StateFlow<Int> = lockedCount

        override fun isPinSet(): Boolean = false

        override fun savePin(pin: String) = Unit

        override fun verifyPin(pin: String): Boolean = false

        override fun lockedPackages(): Set<String> = emptySet()

        override fun isPackageLocked(packageName: String): Boolean = false

        override fun setPackageLocked(
            packageName: String,
            locked: Boolean,
        ) = Unit

        override fun setLockedPackages(packageNames: Set<String>) = Unit

        override suspend fun lockableApps(): List<AppLockAppModel> = emptyList()

        override fun isMonitoringEnabled(): Boolean = false

        override fun setMonitoringEnabled(enabled: Boolean) = Unit

        override fun isAutoLockEnabled(): Boolean = false

        override fun setAutoLockEnabled(enabled: Boolean) = Unit

        override fun isVibrationEnabled(): Boolean = true

        override fun setVibrationEnabled(enabled: Boolean) = Unit

        override fun hasUsageAccess(): Boolean = false

        override fun hasOverlayPermission(): Boolean = false

        override fun handlePackageAdded(packageName: String) = Unit
    }
}
