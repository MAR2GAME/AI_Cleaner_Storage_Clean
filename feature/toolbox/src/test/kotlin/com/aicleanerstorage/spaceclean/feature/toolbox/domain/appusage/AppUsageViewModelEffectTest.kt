package com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage

import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageInfo
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageEffect
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageExitReason
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageIntent
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageViewModel


import com.aicleanerstorage.spaceclean.feature.toolbox.domain.appusage.AppUsageRepository
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
    fun `open app info emits package name without exposing intent`() = runTest {
        val viewModel = AppUsageViewModel(repository = NoAccessRepository())
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }

        viewModel.dispatch(AppUsageIntent.StopApp("com.example.target"))

        assertEquals(AppUsageEffect.OpenAppInfo("com.example.target"), effect.await())
    }

    @Test
    fun `permission rejection exits through domain effect`() = runTest {
        val viewModel = AppUsageViewModel(repository = NoAccessRepository())
        val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effects.first() }

        assertTrue(viewModel.state.value.permissionRequestPending)
        viewModel.dispatch(AppUsageIntent.PermissionRequestConsumed)
        assertFalse(viewModel.state.value.permissionRequestPending)
        viewModel.dispatch(AppUsageIntent.PermissionRejected)

        assertEquals(
            AppUsageEffect.Exit(AppUsageExitReason.PermissionRejected),
            effect.await(),
        )
    }

    private class NoAccessRepository : AppUsageRepository {
        override fun hasAppUsageAccess(): Boolean = false

        override fun resetAppUsagePermissionCache() = Unit

        override suspend fun appUsageBetween(startMillis: Long, endMillis: Long): List<AppUsageInfo> = emptyList()

        override suspend fun runningPackages(packageNames: Set<String>): Set<String> = emptySet()
    }
}

