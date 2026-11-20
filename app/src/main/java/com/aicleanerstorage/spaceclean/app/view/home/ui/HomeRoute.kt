package com.aicleanerstorage.spaceclean.app.view.home.ui

import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeAction
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeViewModel
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeEvent

import com.aicleanerstorage.spaceclean.app.view.home.ui.HomeRoute

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.designsystem.component.openGooglePlayRatePage


import com.aicleanerstorage.spaceclean.app.view.home.ui.HomeScreen

@Composable
fun HomeRoute(
    navigator: AppNavigator,
    viewModel: HomeViewModel,
    externalBlockingPromptActive: Boolean = false,
    initialTabIndex: Int = 0,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner, viewModel) {
        var skipInitialResume = true
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_RESUME) return@LifecycleEventObserver
            if (skipInitialResume) {
                skipInitialResume = false
            } else {
                viewModel.dispatch(HomeAction.SummaryRefreshRequested)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(externalBlockingPromptActive) {
        viewModel.dispatch(HomeAction.ExternalBlockingPromptChanged(externalBlockingPromptActive))
    }

    LaunchedEffect(viewModel) { viewModel.dispatch(HomeAction.Entered(initialTabIndex)) }
    LaunchedEffect(viewModel, navigator) {
        viewModel.event.collect { effect ->
            when (effect) {
                HomeEvent.ExitApp -> context.findActivity()?.finish()
                HomeEvent.RateApp -> openGooglePlayRatePage(context)
                is HomeEvent.Navigate -> {
                    navigator.navigate(effect.destination)
                }
            }
        }
    }
    HomeScreen(
        state = state,
        onAction = viewModel::dispatch,
        onNavigate = { viewModel.dispatch(HomeAction.NavigationRequested(it)) },
        onSettingsClick = { viewModel.dispatch(HomeAction.NavigationRequested(AppDestination.Settings)) },
        onExitPromptView = {
            viewModel.consumeExitPromptForNavigation()
                ?.let { viewModel.dispatch(HomeAction.NavigationRequested(it)) }
        },
    )

}


private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
