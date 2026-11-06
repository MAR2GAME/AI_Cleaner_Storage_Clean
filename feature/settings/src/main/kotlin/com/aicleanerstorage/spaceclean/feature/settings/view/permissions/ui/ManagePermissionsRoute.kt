package com.aicleanerstorage.spaceclean.feature.settings.view.permissions.ui

import com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation.ManagePermissionsIntent
import com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation.ManagePermissionsViewModel
import com.aicleanerstorage.spaceclean.feature.settings.view.permissions.presentation.ManagePermissionsEffect

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.feature.settings.view.permissions.ui.ManagePermissionsScreen

@Composable
fun ManagePermissionsRoute(navigator: AppNavigator, viewModel: ManagePermissionsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.dispatch(ManagePermissionsIntent.Refresh)
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(viewModel) { viewModel.dispatch(ManagePermissionsIntent.Refresh) }
    LaunchedEffect(viewModel, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ManagePermissionsEffect.Back -> navigator.back()
                is ManagePermissionsEffect.Navigate -> navigator.navigate(effect.destination)
            }
        }
    }
    ManagePermissionsScreen(state = state, onAction = viewModel::dispatch)
}
