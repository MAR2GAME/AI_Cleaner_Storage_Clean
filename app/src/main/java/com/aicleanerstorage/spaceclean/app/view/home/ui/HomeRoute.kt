package com.aicleanerstorage.spaceclean.app.view.home.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.integration.Mar2Analytics
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeAction
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeEvent
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeViewModel
import com.aicleanerstorage.spaceclean.app.view.home.ui.HomeRoute
import com.aicleanerstorage.spaceclean.app.view.home.ui.HomeScreen
import com.aicleanerstorage.spaceclean.base.designsystem.component.openGooglePlayRatePage
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

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
    val routeScope = rememberCoroutineScope()
    val pagerState =
        rememberPagerState(
            initialPage = state.selectedTab.coerceIn(0, HOME_TAB_COUNT - 1),
            pageCount = { HOME_TAB_COUNT },
        )

    DisposableEffect(lifecycleOwner, viewModel) {
        var skipInitialResume = true
        val observer =
            LifecycleEventObserver { _, event ->
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
    LaunchedEffect(state.selectedTab) {
        if (pagerState.currentPage != state.selectedTab) pagerState.scrollToPage(state.selectedTab)
        snapshotFlow { pagerState.settledPage }.drop(1).collect { page ->
            if (page != state.selectedTab) viewModel.dispatch(HomeAction.TabSelected(page))
        }
    }
    CollectEffects(viewModel) { effect ->
        when (effect) {
            HomeEvent.ExitApp -> context.findActivity()?.finish()
            HomeEvent.RateApp -> openGooglePlayRatePage(context)
            is HomeEvent.TrackRatePopup -> Mar2Analytics.trackRatePopup(effect.ifOk)
            is HomeEvent.Navigate -> {
                Mar2Analytics.trackCoreFeatureClicked(effect.destination)
                navigator.navigate(effect.destination)
            }
        }
    }
    HomeScreen(
        state = state,
        onAction = viewModel::dispatch,
        pagerState = pagerState,
        onTabSelected = { index ->
            viewModel.dispatch(HomeAction.TabInteracted)
            if (pagerState.currentPage != index) {
                routeScope.launch { pagerState.animateScrollToPage(index) }
            }
        },
    )
}

private const val HOME_TAB_COUNT = 3

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
