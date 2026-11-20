package com.aicleanerstorage.spaceclean.app.view.home.interaction

import com.aicleanerstorage.spaceclean.testsupport.InMemorySettingsRepository

import androidx.lifecycle.ViewModelStore
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.port.SettingsRepository
import java.lang.reflect.Proxy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScheduler
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
    fun interactionsCancelTheRatingPromptAndExternalPromptDelaysItOnce() = runTest {
        val main = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(main)
        try {
            for (interaction in listOf(HomeAction.FeatureClicked, HomeAction.TabInteracted, null)) {
                val store = ViewModelStore()
                val settings = FakeHomeSettings(0L)
                val model = HomeViewModel(
                    repository = unusedRepository(),
                    appLockRepository = object : AppLockRepository by unusedRepository<AppLockRepository>() {
                        override val lockedAppCountFlow = MutableStateFlow(0)
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

    private class FakeHomeSettings(private var lastPrompt: Long) : SettingsRepository by InMemorySettingsRepository() {
        var promptCount = 0
            private set

        override fun readTemperatureUnit() = "C"
        override fun saveTemperatureUnit(unit: String) = Unit
        override fun readLastAutoRatePromptAt() = lastPrompt
        override fun saveLastAutoRatePromptAt(timestampMillis: Long) {
            lastPrompt = timestampMillis
            promptCount++
        }
        override fun hasShownNotificationCleanerExitPrompt() = false
        override fun saveNotificationCleanerExitPromptShown() = Unit
    }

    // Summary jobs remain queued on the test dispatcher; this test exercises synchronous navigation inputs.
    private inline fun <reified T> unusedRepository(): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ ->
            error("Unexpected summary call: ${method.name}")
        } as T
}
