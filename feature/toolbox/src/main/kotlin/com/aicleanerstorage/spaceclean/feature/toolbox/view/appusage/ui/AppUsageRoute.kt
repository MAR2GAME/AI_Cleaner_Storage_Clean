package com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageEffect
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageExitReason
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageIntent
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.presentation.AppUsageViewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.*

import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.ui.AppUsageRoute


import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.view.appusage.ui.AppUsageScreen

@Composable
fun AppUsageRoute(
    navigator: AppNavigator,
    viewModel: AppUsageViewModel,
    featureFlow: FeatureFlow,
    externalActivities: ExternalActivityTracker,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    LaunchedEffect(viewModel, context, externalActivities) {
        viewModel.effects.collect { effect ->
            when (effect) {
                    is AppUsageEffect.OpenAppInfo -> {
                        val intent = Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${effect.packageName}"),
                        )
                        try {
                            externalActivities.markLaunch()
                            context.startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            externalActivities.cancelLaunch()
                        } catch (_: Exception) {
                            externalActivities.cancelLaunch()
                        }
                    }
                    is AppUsageEffect.Exit -> {
                        val reason = when (effect.reason) {
                            AppUsageExitReason.Return -> FeatureExitReason.Return
                            AppUsageExitReason.PermissionRejected -> FeatureExitReason.PermissionRejected
                        }
                    if (!exiting) {
                        exiting = true
                        routeScope.launch {
                            featureFlow.exit(FeatureKey.APP_USAGE, reason)
                            ensureActive()
                            navigator.back()
                        }
                    }
                    }
                }
            }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.dispatch(AppUsageIntent.Resumed)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler { viewModel.dispatch(AppUsageIntent.Back) }

    AppUsageScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}

