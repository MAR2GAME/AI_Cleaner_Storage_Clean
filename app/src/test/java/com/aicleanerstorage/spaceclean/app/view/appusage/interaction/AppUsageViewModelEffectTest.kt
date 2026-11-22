package com.aicleanerstorage.spaceclean.app.view.appusage.interaction

import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageAction
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageEvent
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageExitReason
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageViewModel
import com.aicleanerstorage.spaceclean.domain.port.AppUsageRepository
import com.aicleanerstorage.spaceclean.domain.model.appusage.AppUsageInfoModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUsageViewModelEffectTest {
    @Test
    fun `open app info emits package name without exposing intent`() =
        runTest {
            val viewModel = AppUsageViewModel(repository = NoAccessRepository())
            val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.event.first() }

            viewModel.dispatch(AppUsageAction.StopAppClicked("com.example.target"))

            assertEquals(AppUsageEvent.OpenAppInfo("com.example.target"), effect.await())
        }

    @Test
    fun `permission rejection exits through domain effect`() =
        runTest {
            val viewModel = AppUsageViewModel(repository = NoAccessRepository())
            val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.event.first() }

            assertTrue(viewModel.state.value.permissionRequestPending)
            viewModel.dispatch(AppUsageAction.PermissionRequestConsumed)
            assertFalse(viewModel.state.value.permissionRequestPending)
            viewModel.dispatch(AppUsageAction.PermissionRejected)

            assertEquals(
                AppUsageEvent.Exit(AppUsageExitReason.PermissionRejected),
                effect.await(),
            )
        }

    private class NoAccessRepository : AppUsageRepository {
        override fun hasAppUsageAccess(refresh: Boolean): Boolean = false

        override suspend fun appUsageBetween(
            startMillis: Long,
            endMillis: Long,
        ): List<AppUsageInfoModel> = emptyList()

        override suspend fun runningPackages(packageNames: Set<String>): Set<String> = emptySet()

        override suspend fun foregroundPackage(): String? = null
    }
}
