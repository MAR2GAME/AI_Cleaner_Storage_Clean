package com.aicleanerstorage.spaceclean.app.view.home.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeAction
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeUiState
import com.aicleanerstorage.spaceclean.app.view.home.ui.part.FilesManagerTabContent
import com.aicleanerstorage.spaceclean.app.view.home.ui.part.HomeBackgroundContent
import com.aicleanerstorage.spaceclean.app.view.home.ui.part.HomeExitPromptDialog
import com.aicleanerstorage.spaceclean.app.view.home.ui.part.HomeHeader
import com.aicleanerstorage.spaceclean.app.view.home.ui.part.HomeTabBar
import com.aicleanerstorage.spaceclean.app.view.home.ui.part.HomeTabContent
import com.aicleanerstorage.spaceclean.app.view.home.ui.part.ToolBoxTabContent
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXPagePadding
import com.aicleanerstorage.spaceclean.base.designsystem.component.SettingsRateDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableStatusBarsPadding
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    onSettingsClick: () -> Unit,
    onExitPromptView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val summaryState = state.summary
    val exitPromptSpec = state.exitPromptDestination?.let(::toolNotificationSpec)
    val showAutoRateDialog = state.showAutoRateDialog

    fun openFeature(feature: AppDestination) {
        onNavigate(feature)
    }

    BackHandler(
        enabled = exitPromptSpec == null && !state.externalBlockingPromptActive && !showAutoRateDialog,
        onBack = { onAction(HomeAction.ExitClicked) },
    )

    val scope = rememberCoroutineScope()
    val tabs =
        listOf(
            HomeTab(stringResource(R.string.home_tab_home)),
            HomeTab(stringResource(R.string.home_tab_file_manager)),
            HomeTab(stringResource(R.string.home_tab_toolbox)),
        )
    val pagerState =
        rememberPagerState(
            initialPage = state.selectedTab.coerceIn(0, tabs.lastIndex),
            pageCount = { tabs.size },
        )

    androidx.compose.runtime.LaunchedEffect(state.selectedTab) {
        if (pagerState.currentPage != state.selectedTab) pagerState.scrollToPage(state.selectedTab)
        snapshotFlow { pagerState.settledPage }.drop(1).collect { page ->
            if (page != state.selectedTab) onAction(HomeAction.TabSelected(page))
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        HomeBackgroundContent()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .stableStatusBarsPadding()
                        .stableNavigationBarsPadding()
                        .padding(horizontal = CleanXPagePadding),
            ) {
                HomeHeader(
                    onSettingsClick = onSettingsClick,
                )
                Spacer(modifier = Modifier.height(16.dp))
                HomeTabBar(
                    tabs = tabs.map { it.label },
                    selectedIndex = pagerState.currentPage,
                    onSelected = { index ->
                        val fromIndex = pagerState.currentPage
                        onAction(HomeAction.TabInteracted)
                        if (fromIndex != index) {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        }
                    },
                )
                Spacer(modifier = Modifier.height(19.dp))
                HorizontalPager(
                    state = pagerState,
                    beyondViewportPageCount = 2,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    when (page) {
                        0 ->
                            HomeTabContent(
                                summaryState = summaryState,
                                onFeatureClick = ::openFeature,
                            )
                        1 ->
                            FilesManagerTabContent(
                                onFeatureClick = ::openFeature,
                            )
                        2 ->
                            ToolBoxTabContent(
                                summaryState = summaryState,
                                onFeatureClick = ::openFeature,
                            )
                    }
                }
            }
        }
    }

    if (exitPromptSpec != null) {
        HomeExitPromptDialog(
            spec = exitPromptSpec,
            onExit = {
                onAction(HomeAction.ExitDismissed)
                onAction(HomeAction.ExitConfirmed)
            },
            onViewNow = onExitPromptView,
        )
    }

    if (showAutoRateDialog && !state.externalBlockingPromptActive && exitPromptSpec == null) {
        SettingsRateDialog(
            onDismiss = { onAction(HomeAction.AutoRateDismissed) },
            onRate = { onAction(HomeAction.RateAppClicked) },
        )
    }
}

private data class HomeTab(
    val label: String,
)
