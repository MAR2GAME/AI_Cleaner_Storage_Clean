package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.interaction

import com.aicleanerstorage.spaceclean.domain.notificationcleaner.port.NotificationCleanerRepository
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.model.BlockableNotificationAppModel
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.model.BlockedNotificationModel
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.model.NotificationCleanerSnapshotModel
import com.aicleanerstorage.spaceclean.domain.notificationcleaner.model.PostedNotificationModel
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
class NotificationCleanerViewModelTest {
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
    fun `missing access opens onboarding`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeRepository(hasAccess = false))
            advanceUntilIdle()

            assertEquals(NotificationCleanerPage.Onboarding, viewModel.state.value.page)
            assertEquals(false, viewModel.state.value.hasAccess)
        }

    @Test
    fun `access scan finishes on the status page`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeRepository(hasAccess = true, blockedCount = 2))
            advanceUntilIdle()

            assertEquals(NotificationCleanerPage.Status, viewModel.state.value.page)
            assertEquals(2, viewModel.state.value.blockedCount)
        }

    @Test
    fun `tidy up clears blocked notifications and completes`() =
        runTest(dispatcher) {
            val repository = FakeRepository(hasAccess = true, blockedCount = 2, clearedCount = 2)
            val viewModel = viewModel(repository)
            advanceUntilIdle()

            viewModel.dispatch(NotificationCleanerAction.TidyUpClicked)
            advanceUntilIdle()
            assertEquals(NotificationCleanerPage.Cleaning, viewModel.state.value.page)
            assertEquals(2, viewModel.state.value.cleanedCount)

            viewModel.dispatch(NotificationCleanerAction.CleaningAnimationFinished)
            assertEquals(NotificationCleanerPage.Complete, viewModel.state.value.page)

            viewModel.dispatch(NotificationCleanerAction.CompletionAnimationFinished)
            assertEquals(NotificationCleanerPage.Complete, viewModel.state.value.page)
            assertEquals(true, viewModel.state.value.completionResultVisible)
        }

    @Test
    fun `enabling blocking persists the preference`() =
        runTest(dispatcher) {
            val repository = FakeRepository(hasAccess = true)
            val viewModel = viewModel(repository)
            advanceUntilIdle()

            viewModel.dispatch(NotificationCleanerAction.BlockingEnableRequested)
            advanceUntilIdle()

            assertEquals(true, viewModel.state.value.enabled)
            assertEquals(true, repository.enabled)
        }

    @Test
    fun `back exits when nothing is running`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeRepository(hasAccess = true))
            val events = mutableListOf<NotificationCleanerEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.event.toList(events)
            }
            advanceUntilIdle()

            viewModel.dispatch(NotificationCleanerAction.BackClicked)
            advanceUntilIdle()

            assertTrue(events.any { it is NotificationCleanerEvent.Exit })
        }

    private fun viewModel(repository: FakeRepository): NotificationCleanerViewModel =
        NotificationCleanerViewModel(
            repository = repository,
            ioDispatcher = dispatcher,
        )

    private class FakeRepository(
        var hasAccess: Boolean = true,
        var blockedCount: Int = 0,
        private val clearedCount: Int = 0,
    ) : NotificationCleanerRepository {
        var enabled: Boolean = false

        override fun shouldBlock(packageName: String): Boolean = false

        override fun recordBlockedNotification(posted: PostedNotificationModel) = Unit

        override fun hasNotificationListenerAccess(): Boolean = hasAccess

        override fun notificationSnapshot(includeApps: Boolean): NotificationCleanerSnapshotModel =
            NotificationCleanerSnapshotModel(
                hasAccess = hasAccess,
                enabled = enabled,
                blockedCount = blockedCount,
                blockedCountsByPackage = if (blockedCount > 0) mapOf("com.a" to blockedCount) else emptyMap(),
                blockedNotifications =
                    if (blockedCount > 0) {
                        listOf(
                            BlockedNotificationModel(
                                id = "1",
                                packageName = "com.a",
                                appName = "A",
                                title = "title",
                                text = "text",
                                postTimeMillis = 0L,
                                hasLaunchIntent = false,
                            ),
                        )
                    } else {
                        emptyList()
                    },
                apps = emptyList(),
                selectedPackages = emptySet(),
            )

        override fun setNotificationBlockingEnabled(enabled: Boolean) {
            this.enabled = enabled
        }

        override fun clearBlockedNotifications(): Int {
            blockedCount = 0
            return clearedCount
        }

        override fun selectedNotificationPackages(): Set<String> = emptySet()

        override fun notificationApps(): List<BlockableNotificationAppModel> = emptyList()

        override fun setNotificationPackageSelected(
            packageName: String,
            selected: Boolean,
        ) = Unit
    }
}
