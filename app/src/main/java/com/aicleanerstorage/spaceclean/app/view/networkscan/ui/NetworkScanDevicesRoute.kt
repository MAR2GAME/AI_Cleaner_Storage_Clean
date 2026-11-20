package com.aicleanerstorage.spaceclean.app.view.networkscan.ui

import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesEvent

import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesAction
import com.aicleanerstorage.spaceclean.app.view.networkscan.interaction.NetworkScanDevicesViewModel

import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanDevicesRoute

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.toAppDestination


import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanDevicesScreen

@Composable
fun NetworkScanDevicesRoute(
    navigator: AppNavigator,
    viewModel: NetworkScanDevicesViewModel,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(viewModel, navigator) {
        viewModel.event.collect { effect ->
            when (effect) {
                NetworkScanDevicesEvent.NavigateBack -> navigator.back()
                is NetworkScanDevicesEvent.Navigate -> navigator.navigate(effect.destination)
            }
        }
    }
    NetworkScanDevicesScreen(
        state = uiState,
        onAction = { action ->
            viewModel.dispatch(action)
        },
        onNavigate = { viewModel.dispatch(NetworkScanDevicesAction.NavigationRequested(it.toAppDestination())) },
    )
}

