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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeAction
import com.aicleanerstorage.spaceclean.app.view.home.interaction.HomeUiState
import com.aicleanerstorage.spaceclean.app.view.home.interaction.toolNotificationSpec
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

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val summaryState = state.summary
    val exitPromptSpec = state.exitPromptDestination?.let(::toolNotificationSpec)
    val showAutoRateDialog = state.showAutoRateDialog

    fun openFeature(feature: AppDestination) {
        onAction(HomeAction.NavigationRequested(feature))
    }

    BackHandler(
        enabled = exitPromptSpec == null && !state.externalBlockingPromptActive && !showAutoRateDialog,
        onBack = { onAction(HomeAction.ExitClicked) },
    )

    val tabs =
        listOf(
            HomeTab(stringResource(R.string.home_tab_home)),
            HomeTab(stringResource(R.string.home_tab_file_manager)),
            HomeTab(stringResource(R.string.home_tab_toolbox)),
        )

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
                    onSettingsClick = { onAction(HomeAction.NavigationRequested(AppDestination.Settings)) },
                )
                Spacer(modifier = Modifier.height(16.dp))
                HomeTabBar(
                    tabs = tabs.map { it.label },
                    selectedIndex = pagerState.currentPage,
                    onSelected = onTabSelected,
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
            onViewNow = { onAction(HomeAction.ExitPromptViewClicked) },
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
