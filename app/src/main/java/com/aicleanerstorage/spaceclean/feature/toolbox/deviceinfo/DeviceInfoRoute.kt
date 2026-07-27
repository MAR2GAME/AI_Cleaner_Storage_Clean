package com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo

import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.*

import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.ui.*

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.feature.FeatureExitReason
import com.aicleanerstorage.spaceclean.common.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.DeviceInfoMode
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.ui.DeviceInfoScreen

@Composable
fun DeviceInfoRoute(
    navigator: AppNavigator,
    viewModel: DeviceInfoViewModel,
    featureFlow: FeatureFlow,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, featureFlow, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                DeviceInfoEffect.Exit ->
                    featureFlow.exit(FeatureKey.DEVICE_INFO, FeatureExitReason.Return) { navigator.back() }
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) viewModel.onAction(DeviceInfoAction.Resumed)
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler { viewModel.onAction(DeviceInfoAction.Back) }

    DeviceInfoScreen(
        state = uiState,
        onAction = viewModel::onAction,
    )
}
