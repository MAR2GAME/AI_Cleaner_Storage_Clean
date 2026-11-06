package com.aicleanerstorage.spaceclean.feature.home.view.home.ui

import com.aicleanerstorage.spaceclean.feature.home.view.home.presentation.HomeUiState
import com.aicleanerstorage.spaceclean.feature.home.view.home.presentation.HomeIntent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget
import com.aicleanerstorage.spaceclean.core.navigation.toToolNavigationTarget
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.ui.theme.LocalAppThemeTokens
import com.aicleanerstorage.spaceclean.core.ui.components.CleanXIconButtonSize
import com.aicleanerstorage.spaceclean.core.ui.components.CleanXPagePadding
import com.aicleanerstorage.spaceclean.core.ui.components.CleanXPillShape
import com.aicleanerstorage.spaceclean.core.ui.components.cleanXDebouncedClick
import com.aicleanerstorage.spaceclean.core.ui.components.SettingsRateDialog
import com.aicleanerstorage.spaceclean.core.ui.components.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.core.ui.components.stableStatusBarsPadding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.drop
import androidx.compose.runtime.snapshotFlow


private val PrimaryText: Color @Composable @ReadOnlyComposable get() = LocalAppThemeTokens.current.colors.textPrimary

private data class HomeTab(
    val label: String,
)

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeIntent) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
    onSettingsClick: () -> Unit,
    onExitPromptView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val summaryState = state.summary
    val exitPromptSpec = state.exitPromptRoute?.let(::toolNotificationSpec)
    val showAutoRateDialog = state.showAutoRateDialog

    fun openFeature(feature: FeatureKey) {
        feature.toToolNavigationTarget()?.let(onNavigate)
    }

    BackHandler(
        enabled = exitPromptSpec == null && !state.externalBlockingPromptActive && !showAutoRateDialog,
        onBack = { onAction(HomeIntent.RequestExitPrompt) },
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
            if (page != state.selectedTab) onAction(HomeIntent.TabSelected(page))
        }
    }
    Box(modifier = modifier.fillMaxSize()) {
        HomeBackground()
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
                        onAction(HomeIntent.TabInteracted)
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
                onAction(HomeIntent.DismissExitPrompt)
                onAction(HomeIntent.ExitApp)
            },
            onViewNow = onExitPromptView,
        )
    }

    if (showAutoRateDialog && !state.externalBlockingPromptActive && exitPromptSpec == null) {
        SettingsRateDialog(
            onDismiss = { onAction(HomeIntent.DismissAutoRateDialog) },
            onRate = { onAction(HomeIntent.RateApp) },
        )
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

@Composable
private fun HomeBackground() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F6F6)),
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_bg_figma_bitmap),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth().aspectRatio(375f / 326f),
        )
    }
}

@Composable
private fun HomeTabBar(
    tabs: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(43.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Column(
                modifier = Modifier.clickable { onSelected(index) }.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    color = if (selected) Color(0xFF282927) else Color(0xFF999999),
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier =
                        Modifier
                            .width(
                                when (index) {
                                    1 -> 89.dp
                                    2 -> 60.dp
                                    else -> 39.dp
                                },
                            )
                            .height(2.dp)
                            .background(if (selected) Color(0xFF5DD595) else Color.Transparent),
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 26.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = PrimaryText,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier =
                Modifier
                    .size(CleanXIconButtonSize)
                    .clip(CleanXPillShape)
                    .cleanXDebouncedClick(onClick = onSettingsClick),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "image description",
                contentScale = ContentScale.None
            )
        }
    }
}

