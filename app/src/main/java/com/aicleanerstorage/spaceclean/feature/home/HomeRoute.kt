package com.aicleanerstorage.spaceclean.feature.home

import com.aicleanerstorage.spaceclean.feature.home.*

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.home.HomeSummaryUiState
import com.aicleanerstorage.spaceclean.feature.home.HomeViewModel
import com.aicleanerstorage.spaceclean.feature.home.ui.ToolNotificationSpec
import com.aicleanerstorage.spaceclean.common.ui.theme.LocalAppThemeTokens
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXIconButtonSize
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXPagePadding
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXPillShape
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXSegmentTabs
import com.aicleanerstorage.spaceclean.common.ui.components.cleanXDebouncedClick
import com.aicleanerstorage.spaceclean.feature.settings.ui.SettingsRateDialog
import com.aicleanerstorage.spaceclean.feature.settings.ui.openGooglePlayRatePage
import com.aicleanerstorage.spaceclean.common.ui.components.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.common.ui.components.stableStatusBarsPadding
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXBlue
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXText
import kotlinx.coroutines.launch


import com.aicleanerstorage.spaceclean.feature.home.ui.HomeScreen

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
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner, viewModel) {
        var skipInitialResume = true
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_RESUME) return@LifecycleEventObserver
            if (skipInitialResume) {
                skipInitialResume = false
            } else {
                viewModel.onAction(HomeAction.RefreshSummary)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(externalBlockingPromptActive) {
        viewModel.onAction(HomeAction.ExternalBlockingPromptChanged(externalBlockingPromptActive))
    }

    HomeScreen(
        state = state,
        onAction = { action ->
            when (action) {
                HomeAction.ExitApp -> context.findActivity()?.finish()
                HomeAction.RateApp -> openGooglePlayRatePage(context)
                else -> viewModel.onAction(action)
            }
        },
        onNavigate = { destination ->
            viewModel.onAction(HomeAction.FeatureClicked)
            destination.featureKey?.let { analyticsReporter.report(AnalyticsEvent.CoreFeatureClicked(it)) }
            navigator.open(destination)
        },
        initialTabIndex = initialTabIndex,
    )
}


private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
