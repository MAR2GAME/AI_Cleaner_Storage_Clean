package com.aicleanerstorage.spaceclean.app.view.settings.ui

import com.aicleanerstorage.spaceclean.app.view.settings.interaction.SettingsAction
import com.aicleanerstorage.spaceclean.app.view.settings.interaction.SettingsEvent
import com.aicleanerstorage.spaceclean.app.view.settings.interaction.SettingsViewModel


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.app.view.settings.ui.SettingsScreen
import com.aicleanerstorage.spaceclean.base.designsystem.component.openGooglePlayRatePage

@Composable
fun SettingsRoute(
    navigator: AppNavigator,
    termsUrl: String,
    privacyUrl: String,
    viewModel: SettingsViewModel,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    fun openLink(url: String, track: () -> Unit) {
        track()
    }

    androidx.compose.runtime.LaunchedEffect(viewModel, navigator) {
        viewModel.event.collect { effect ->
            when (effect) {
                SettingsEvent.NavigateBack -> navigator.back()
                SettingsEvent.OpenTerms -> openLink(termsUrl) {
                }
                SettingsEvent.OpenPrivacy -> openLink(privacyUrl) {
                }
                SettingsEvent.RateApp -> openGooglePlayRatePage(context)
                is SettingsEvent.Navigate -> navigator.navigate(effect.destination)
            }
        }
    }
    SettingsScreen(
        state = state,
        onAction = viewModel::dispatch,
        onManagePermissions = { viewModel.dispatch(SettingsAction.NavigationRequested(AppDestination.ManagePermissions)) },
    )
}
