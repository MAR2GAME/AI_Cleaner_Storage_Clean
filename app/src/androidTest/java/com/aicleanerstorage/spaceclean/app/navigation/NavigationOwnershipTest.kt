package com.aicleanerstorage.spaceclean.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import kotlinx.coroutines.Job
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.aicleanerstorage.spaceclean.app.navigation.owner.FeatureOwner
import com.aicleanerstorage.spaceclean.app.navigation.owner.FeatureOwnerRegistry
import com.aicleanerstorage.spaceclean.app.navigation.owner.FileManagerOwnerState
import com.aicleanerstorage.spaceclean.core.contracts.permission.*
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.navigation.MainTab
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NavigationOwnershipTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var controller: NavHostController
    private lateinit var navigator: NavHostAppNavigator

    private fun mount() {
        compose.setContent {
            controller = rememberNavController()
            val scope = rememberCoroutineScope()
            navigator = remember { NavHostAppNavigator(controller, scope) }
            DisposableEffect(navigator) {
                val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, _, _ ->
                    navigator.sourceChanged(controller.currentBackStackEntry?.id)
                }
                controller.addOnDestinationChangedListener(listener)
                onDispose { controller.removeOnDestinationChangedListener(listener); navigator.close() }
            }
            NavHost(controller, startDestination = AppDestination.Main()) {
                composable<AppDestination.Main> { }
                composable<AppDestination.Settings> { }
                composable<AppDestination.JunkClean> { }
                navigation<PhotosFlow>(startDestination = AppDestination.PhotosManager) {
                    composable<AppDestination.PhotosManager> { }
                    composable<AppDestination.PhotosDetail> { }
                }
            }
        }
        settled()
    }

    private fun settled() {
        compose.waitForIdle()
        compose.waitUntil(5_000) { controller.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED }
    }

    @Test fun pausedSourceWaitsForResumeAndBackCancelsPendingNavigation() {
        mount()
        lateinit var host: LifecycleRegistry
        compose.runOnIdle {
            val owner = object : LifecycleOwner {
                override val lifecycle = LifecycleRegistry(this)
            }
            host = owner.lifecycle
            host.currentState = Lifecycle.State.STARTED
            controller.setLifecycleOwner(owner)
            val entry = controller.currentBackStackEntry!!
            navigator.forEntry(entry).navigate(AppDestination.Settings)
            navigator.forEntry(entry).navigate(AppDestination.Settings)
        }
        compose.runOnIdle {
            assertTrue(controller.currentDestination!!.hasRoute<AppDestination.Main>())
            host.currentState = Lifecycle.State.RESUMED
        }
        settled()
        compose.runOnIdle {
            assertTrue(controller.currentDestination!!.hasRoute<AppDestination.Settings>())
            assertTrue(controller.previousBackStackEntry!!.destination.hasRoute<AppDestination.Main>())
            host.currentState = Lifecycle.State.STARTED
            navigator.forEntry(controller.currentBackStackEntry!!).navigate(AppDestination.JunkClean)
        }
        compose.runOnIdle {
            assertTrue(navigator.forEntry(controller.currentBackStackEntry!!).back())
            host.currentState = Lifecycle.State.RESUMED
        }
        settled()
        compose.runOnIdle {
            assertTrue(controller.currentDestination!!.hasRoute<AppDestination.Main>())
        }
    }

    @Test fun navigatorPreservesBackStackAndRejectsOldEntry() {
        mount()
        lateinit var main: NavBackStackEntry
        compose.runOnIdle {
            main = controller.currentBackStackEntry!!
            navigator.forEntry(main).navigate(AppDestination.Settings)
        }
        settled()
        compose.runOnIdle {
            assertTrue(controller.currentDestination!!.hasRoute<AppDestination.Settings>())
            assertFalse(navigator.forEntry(main).back())
            navigator.forEntry(main).navigate(AppDestination.JunkClean)
            navigator.forEntry(controller.currentBackStackEntry!!).replaceCurrent(AppDestination.JunkClean)
        }
        settled()
        compose.runOnIdle {
            assertTrue(controller.currentDestination!!.hasRoute<AppDestination.JunkClean>())
            assertEquals(main.id, controller.previousBackStackEntry!!.id)
            navigator.forEntry(controller.currentBackStackEntry!!).resetTo(AppDestination.PhotosManager)
        }
        settled()
        compose.runOnIdle {
            assertTrue(controller.currentDestination!!.hasRoute<AppDestination.PhotosManager>())
            assertTrue(controller.previousBackStackEntry!!.destination.hasRoute<AppDestination.Main>())
            navigator.forEntry(controller.currentBackStackEntry!!).navigateTopLevel(MainTab.Files)
        }
        settled()
        compose.runOnIdle {
            assertTrue(controller.currentDestination!!.hasRoute<AppDestination.Main>())
            assertEquals(MainTab.Files.name, controller.currentBackStackEntry!!.savedStateHandle.get<String>(MAIN_TAB_REQUEST))
        }
    }

    @Test fun listAndDetailShareOwnerAndPoppingFlowClearsState() {
        mount()
        lateinit var owner: FeatureOwner.FileManager
        lateinit var state: FileManagerOwnerState
        val operation = Job()
        val uiRequest = Job()
        val permissions = object : PermissionGateway {
            override fun isGranted(permission: PermissionType) = true
            override suspend fun ensure(target: PermissionTarget, mode: PermissionPromptMode) = PermissionResult.Granted
            override suspend fun openSettings(permission: PermissionType) = PermissionResult.Granted
        }
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T = FileManagerOwnerState(permissions) as T
        }
        compose.runOnIdle { navigator.forEntry(controller.currentBackStackEntry!!).navigate(AppDestination.PhotosManager) }
        settled()
        compose.runOnIdle {
            owner = FeatureOwnerRegistry.resolve(controller.currentBackStackEntry, controller) as FeatureOwner.FileManager
            state = ViewModelProvider(owner.ownerEntry, factory)[FileManagerOwnerState::class.java]
            state.permissionState.operationJob = operation
            state.permissionState.uiRequestJob = uiRequest
            navigator.forEntry(controller.currentBackStackEntry!!).navigate(AppDestination.PhotosDetail(2))
        }
        settled()
        compose.runOnIdle {
            val detailOwner = FeatureOwnerRegistry.resolve(controller.currentBackStackEntry, controller) as FeatureOwner.FileManager
            assertSame(owner.ownerEntry, detailOwner.ownerEntry)
            assertSame(state, ViewModelProvider(detailOwner.ownerEntry, factory)[FileManagerOwnerState::class.java])
            navigator.forEntry(controller.currentBackStackEntry!!).backToMain()
        }
        settled()
        compose.runOnIdle {
            assertTrue(state.permissionState.leavingPage)
            assertTrue(operation.isCancelled)
            assertTrue(uiRequest.isCancelled)
            navigator.forEntry(controller.currentBackStackEntry!!).navigate(AppDestination.PhotosManager)
        }
        settled()
        compose.runOnIdle {
            val newOwner = FeatureOwnerRegistry.resolve(controller.currentBackStackEntry, controller) as FeatureOwner.FileManager
            assertNotEquals(owner.ownerEntry.id, newOwner.ownerEntry.id)
            assertNotSame(state, ViewModelProvider(newOwner.ownerEntry, factory)[FileManagerOwnerState::class.java])
        }
    }
}
