package com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoAction
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoEvent
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoViewModel
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.DeviceInfoRoute
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.DeviceInfoScreen
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

@Composable
fun DeviceInfoRoute(
    navigator: AppNavigator,
    viewModel: DeviceInfoViewModel,
) {
    val routeScope = rememberCoroutineScope()
    var exiting by remember { mutableStateOf(false) }

    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    CollectEffects(viewModel) { effect ->
        when (effect) {
            DeviceInfoEvent.Exit ->
                {
                    if (!exiting) {
                        exiting = true
                        routeScope.launch {
                            ensureActive()
                            navigator.back()
                        }
                    }
                }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) viewModel.dispatch(DeviceInfoAction.Resumed)
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler { viewModel.dispatch(DeviceInfoAction.BackClicked) }

    DeviceInfoScreen(
        state = uiState,
        onAction = { if (!exiting) viewModel.dispatch(it) },
    )
}
