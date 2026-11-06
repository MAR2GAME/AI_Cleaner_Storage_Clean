package com.aicleanerstorage.spaceclean.feature.home.view.home.presentation

import androidx.lifecycle.ViewModelStore
import com.aicleanerstorage.spaceclean.core.contracts.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.core.contracts.device.DeviceInfoRepository
import com.aicleanerstorage.spaceclean.core.contracts.settings.SettingsRepository
import java.lang.reflect.Proxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Test fun `restored tab request wins over initial entry regardless of delivery order`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            for (requestFirst in listOf(false, true)) {
                val store = ViewModelStore()
                val model = HomeViewModel(
                    repository = unusedRepository(),
                    appLockRepository = unusedRepository(),
                    settingsRepository = object : SettingsRepository {
                        override fun readTemperatureUnit() = "C"
                        override fun saveTemperatureUnit(unit: String) = Unit
                        override fun readLastAutoRatePromptAt() = System.currentTimeMillis()
                        override fun saveLastAutoRatePromptAt(timestampMillis: Long) = Unit
                        override fun hasShownNotificationCleanerExitPrompt() = false
                        override fun saveNotificationCleanerExitPromptShown() = Unit
                    },
                    ioDispatcher = dispatcher,
                    autoRateDispatcher = dispatcher,
                )
                store.put("home", model)
                try {
                    val entered = HomeIntent.Entered(0)
                    val request = HomeIntent.TabSelected(2)
                    (if (requestFirst) listOf(request, entered) else listOf(entered, request))
                        .forEach(model::dispatch)
                    assertEquals(2, model.state.value.selectedTab)
                    model.dispatch(HomeIntent.Entered(0))
                    assertEquals(2, model.state.value.selectedTab)
                } finally {
                    store.clear()
                }
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    // Summary jobs remain queued on the test dispatcher; this test exercises synchronous navigation inputs.
    private inline fun <reified T> unusedRepository(): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ ->
            error("Unexpected summary call: ${method.name}")
        } as T
}
