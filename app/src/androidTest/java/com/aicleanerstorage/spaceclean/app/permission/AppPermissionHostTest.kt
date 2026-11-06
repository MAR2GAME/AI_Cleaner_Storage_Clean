package com.aicleanerstorage.spaceclean.app.permission

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.app.platform.storage.preferences.AppPreferences
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinIsolatedContext
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 33)
class AppPermissionHostTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val mounted = mutableStateOf(true)
    private val route = mutableStateOf("permission-host-test")
    private val granted = mutableSetOf<PermissionType>()
    private val outcomes = mutableListOf<PermissionResult>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val viewModels = ViewModelStore()
    private lateinit var permission: PermissionInterceptor
    private lateinit var notification: NotificationPermissionViewModel
    private lateinit var preferences: PermissionPreferences
    private lateinit var registry: RecordingRegistry
    private lateinit var lifecycle: TestLifecycle
    private lateinit var koin: KoinApplication
    private lateinit var isolatedContext: Context
    private val preferenceName = "permission-host-${UUID.randomUUID()}"

    @Before fun mountHost() {
        compose.runOnUiThread {
            val testContext = InstrumentationRegistry.getInstrumentation().context
            isolatedContext = object : ContextWrapper(compose.activity) {
                override fun getApplicationContext(): Context = this
                override fun getSharedPreferences(name: String, mode: Int) =
                    testContext.getSharedPreferences(preferenceName, mode)
                override fun checkPermission(permission: String, pid: Int, uid: Int) =
                    PackageManager.PERMISSION_DENIED
            }
            preferences = PermissionPreferences(AppPreferences(isolatedContext))
            preferences.saveNotificationRuntimePermissionRequestedBefore()
            notification = NotificationPermissionViewModel(TestApplication(isolatedContext), preferences)
            viewModels.put("notification", notification)
            permission = PermissionInterceptor({ it in granted })
            lifecycle = TestLifecycle().apply { lifecycle.currentState = Lifecycle.State.RESUMED }
            registry = RecordingRegistry {
                val expected = if (it) PermissionInterceptor.Stage.WaitingRuntime else PermissionInterceptor.Stage.WaitingSettings
                assertEquals(expected, permission.request.value?.stage)
            }
            koin = koinApplication {
                modules(module {
                    single { permission }
                    single { preferences }
                })
            }
        }
        compose.setContent {
            KoinIsolatedContext(koin) {
                CompositionLocalProvider(
                    LocalContext provides isolatedContext,
                    LocalLifecycleOwner provides lifecycle,
                    LocalActivityResultRegistryOwner provides registry,
                ) {
                    MaterialTheme {
                        if (mounted.value) {
                            AppPermissionHost(
                                externalActivityTracker = ExternalActivityTracker(),
                                onPermissionFlowActiveChange = {},
                                notificationViewModel = notification,
                                currentRoute = route.value,
                                analyticsReporter = object : AnalyticsReporter {
                                    override fun report(event: AnalyticsEvent) = Unit
                                },
                                onNotificationGranted = {},
                                content = {},
                            )
                        }
                    }
                }
            }
        }
        compose.waitForIdle()
    }

    @After fun unmountHost() {
        compose.runOnIdle {
            mounted.value = false
            scope.cancel()
            viewModels.clear()
        }
        compose.waitForIdle()
        koin.close()
        InstrumentationRegistry.getInstrumentation().context.deleteSharedPreferences(preferenceName)
    }

    @Test fun explanationCancellationCompletesOnceWithoutLaunchingSystem() {
        request()
        click(R.string.cancel)
        compose.runOnIdle {
            assertEquals(listOf(PermissionResult.Denied), outcomes)
            assertNull(permission.request.value)
            assertTrue(registry.launches.isEmpty())
        }
        compose.onNodeWithText(text(R.string.permission_title_required)).assertDoesNotExist()
    }

    @Test fun confirmationAndSynchronousRuntimeCallbackLaunchOnlyOnce() {
        compose.runOnIdle {
            registry.onStarted = { code ->
                granted += PermissionType.Location
                registry.complete(code)
                registry.complete(code)
            }
        }
        request(PermissionTarget.Permission(PermissionType.Location))
        click(R.string.allow)
        compose.runOnIdle {
            assertEquals(1, registry.launches.size)
            assertEquals(listOf(PermissionResult.Granted), outcomes)
            assertNull(permission.request.value)
        }
    }

    @Test fun settingsLifecycleAndActivityResultCompleteOnlyOnce() {
        request()
        click(R.string.allow)
        val code = compose.runOnIdle { registry.launches.single().first }
        compose.runOnIdle {
            lifecycle.lifecycle.currentState = Lifecycle.State.STARTED
            granted += PermissionType.StorageFiles
            lifecycle.lifecycle.currentState = Lifecycle.State.RESUMED
            registry.complete(code)
            registry.complete(code)
        }
        compose.runOnIdle {
            assertEquals(listOf(PermissionResult.Granted), outcomes)
            assertNull(permission.request.value)
        }
    }

    @Test fun settingsReturnWithoutGrantDeniesOnce() {
        request()
        click(R.string.allow)
        compose.runOnIdle {
            val code = registry.launches.single().first
            registry.complete(code)
            registry.complete(code)
        }
        compose.runOnIdle {
            assertEquals(listOf(PermissionResult.Denied), outcomes)
            assertNull(permission.request.value)
        }
    }

    @Test fun earlierSettingsResultCannotFinishNextPermissionStep() {
        request(PermissionTarget.Action(ProtectedAction.AppLockEnableMonitoring))
        click(R.string.submit)
        val first = compose.runOnIdle { registry.launches.single().first }
        compose.runOnIdle {
            granted += PermissionType.UsageAccess
            registry.complete(first)
        }
        click(R.string.manage_permission)
        compose.runOnIdle {
            registry.complete(first)
            assertEquals(PermissionType.Overlay, permission.request.value?.missing)
            assertEquals(PermissionInterceptor.Stage.WaitingSettings, permission.request.value?.stage)
            assertTrue(outcomes.isEmpty())
            granted += PermissionType.Overlay
            registry.complete(registry.launches.last().first)
        }
        compose.runOnIdle { assertEquals(listOf(PermissionResult.Granted), outcomes) }
    }

    @Test fun cancellationBeforeLaunchRemovesExplanation() {
        val job = request()
        compose.runOnIdle { job.cancel() }
        compose.runOnIdle {
            assertTrue(job.isCancelled)
            assertNull(permission.request.value)
            assertTrue(registry.launches.isEmpty())
            assertTrue(outcomes.isEmpty())
        }
        compose.onNodeWithText(text(R.string.permission_title_required)).assertDoesNotExist()
    }

    @Test fun cancellationAfterLaunchStaysBusyUntilSystemReturns() {
        val job = request()
        click(R.string.allow)
        compose.runOnIdle { job.cancel() }
        request()
        compose.runOnIdle {
            assertEquals(listOf(PermissionResult.Busy), outcomes)
            assertEquals(PermissionInterceptor.Stage.WaitingSettings, permission.request.value?.stage)
            registry.complete(registry.launches.single().first)
        }
        compose.runOnIdle {
            assertNull(permission.request.value)
            assertEquals(listOf(PermissionResult.Busy), outcomes)
        }
    }

    @Test fun removingHostCancelsBusinessAndIgnoresLateSystemResult() {
        val job = request()
        click(R.string.allow)
        val code = compose.runOnIdle { registry.launches.single().first }
        compose.runOnIdle { mounted.value = false }
        compose.runOnIdle {
            registry.complete(code)
            assertTrue(job.isCancelled)
            assertNull(permission.request.value)
            assertTrue(outcomes.isEmpty())
        }
    }

    @Test fun notificationPromptWaitsBehindBusinessPermission() {
        request()
        compose.runOnIdle { route.value = AppDestination.Main().route }
        compose.waitUntil(3_000) { notification.uiState.value.customDialogVisible }
        compose.onNodeWithText(text(R.string.notification_enable_title)).assertDoesNotExist()
        assertEquals(0L, preferences.readLastNotificationPermissionCustomPromptAt())
        click(R.string.cancel)
        compose.onNodeWithText(text(R.string.notification_enable_title)).assertIsDisplayed()
        compose.runOnIdle { assertTrue(preferences.readLastNotificationPermissionCustomPromptAt() > 0L) }
    }

    @Test fun pausedHomeDoesNotPromptAndLeavingHomePreventsResumePrompt() {
        compose.runOnIdle {
            lifecycle.lifecycle.currentState = Lifecycle.State.STARTED
            route.value = AppDestination.Main().route
        }
        compose.waitForIdle()
        SystemClock.sleep(450)
        compose.runOnIdle {
            assertFalse(notification.uiState.value.customDialogVisible)
            route.value = "permission-host-test"
        }
        compose.waitForIdle()
        compose.runOnIdle { lifecycle.lifecycle.currentState = Lifecycle.State.RESUMED }
        compose.waitForIdle()
        SystemClock.sleep(450)
        compose.runOnIdle {
            assertFalse(notification.uiState.value.customDialogVisible)
            assertEquals(0L, preferences.readLastNotificationPermissionCustomPromptAt())
        }
    }

    private fun request(target: PermissionTarget = PermissionTarget.Permission(PermissionType.StorageFiles)): Job {
        val job = compose.runOnIdle { scope.launch { outcomes += permission.ensure(target) } }
        compose.waitForIdle()
        return job
    }

    private fun text(resource: Int) = compose.activity.getString(resource)
    private fun click(resource: Int) {
        compose.onNodeWithText(text(resource)).performClick()
        compose.waitForIdle()
    }

    private class TestApplication(context: Context) : Application() {
        init { attachBaseContext(context) }
    }

    private class TestLifecycle : LifecycleOwner {
        override val lifecycle = LifecycleRegistry.createUnsafe(this)
    }

    private class RecordingRegistry(val onLaunching: (Boolean) -> Unit) :
        ActivityResultRegistry(), ActivityResultRegistryOwner {
        override val activityResultRegistry: ActivityResultRegistry get() = this
        val launches = mutableListOf<Pair<Int, Boolean>>()
        var onStarted: (Int) -> Unit = {}

        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?,
        ) {
            val runtime = contract is ActivityResultContracts.RequestMultiplePermissions
            onLaunching(runtime)
            launches += requestCode to runtime
            onStarted(requestCode)
        }

        fun complete(code: Int) {
            if (launches.first { it.first == code }.second) {
                dispatchResult(code, emptyMap<String, Boolean>())
            } else {
                dispatchResult(code, ActivityResult(Activity.RESULT_CANCELED, null))
            }
        }
    }
}
