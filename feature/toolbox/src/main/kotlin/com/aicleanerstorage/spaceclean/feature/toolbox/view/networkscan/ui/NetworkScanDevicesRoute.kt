package com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanDevicesEffect

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanDevicesIntent
import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.presentation.NetworkScanDevicesViewModel

import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui.NetworkScanDevicesRoute

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination


import com.aicleanerstorage.spaceclean.feature.toolbox.view.networkscan.ui.NetworkScanDevicesScreen

@Composable
fun NetworkScanDevicesRoute(
    navigator: AppNavigator,
    viewModel: NetworkScanDevicesViewModel,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(viewModel, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                NetworkScanDevicesEffect.Back -> navigator.back()
                is NetworkScanDevicesEffect.Navigate -> navigator.navigate(effect.destination)
            }
        }
    }
    NetworkScanDevicesScreen(
        state = uiState,
        onAction = { action ->
            viewModel.dispatch(action)
        },
        onNavigate = { viewModel.dispatch(NetworkScanDevicesIntent.Navigate(it.toAppDestination())) },
    )
}

