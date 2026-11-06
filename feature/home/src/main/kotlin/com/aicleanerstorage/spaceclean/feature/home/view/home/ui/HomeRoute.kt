package com.aicleanerstorage.spaceclean.feature.home.view.home.ui

import com.aicleanerstorage.spaceclean.feature.home.view.home.presentation.HomeIntent
import com.aicleanerstorage.spaceclean.feature.home.view.home.presentation.HomeViewModel
import com.aicleanerstorage.spaceclean.feature.home.view.home.presentation.HomeEffect

import com.aicleanerstorage.spaceclean.feature.home.view.home.ui.HomeRoute

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
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.core.ui.components.openGooglePlayRatePage


import com.aicleanerstorage.spaceclean.feature.home.view.home.ui.HomeScreen

@Composable
fun HomeRoute(
    navigator: AppNavigator,
    viewModel: HomeViewModel,
    analyticsReporter: AnalyticsReporter,
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
                viewModel.dispatch(HomeIntent.RefreshSummary)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(externalBlockingPromptActive) {
        viewModel.dispatch(HomeIntent.ExternalBlockingPromptChanged(externalBlockingPromptActive))
    }

    LaunchedEffect(viewModel) { viewModel.dispatch(HomeIntent.Entered(initialTabIndex)) }
    LaunchedEffect(viewModel, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                HomeEffect.ExitApp -> context.findActivity()?.finish()
                HomeEffect.RateApp -> openGooglePlayRatePage(context)
                is HomeEffect.Navigate -> {
                    effect.destination.featureKey?.let { analyticsReporter.report(AnalyticsEvent.CoreFeatureClicked(it)) }
                    navigator.navigate(effect.destination)
                }
            }
        }
    }
    HomeScreen(
        state = state,
        onAction = viewModel::dispatch,
        onNavigate = { viewModel.dispatch(HomeIntent.Navigate(it.toAppDestination())) },
        onSettingsClick = { viewModel.dispatch(HomeIntent.Navigate(AppDestination.Settings)) },
        onExitPromptView = {
            viewModel.consumeExitPromptForNavigation()
                ?.let(AppDestination::forRoute)
                ?.let { viewModel.dispatch(HomeIntent.Navigate(it)) }
        },
    )

}


private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
