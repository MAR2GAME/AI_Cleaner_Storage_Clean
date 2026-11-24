package com.aicleanerstorage.spaceclean.app.view.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.integration.Mar2Analytics
import com.aicleanerstorage.spaceclean.app.view.settings.interaction.SettingsEvent
import com.aicleanerstorage.spaceclean.app.view.settings.interaction.SettingsViewModel
import com.aicleanerstorage.spaceclean.app.view.settings.ui.SettingsScreen
import com.aicleanerstorage.spaceclean.base.designsystem.component.openGooglePlayRatePage
import com.aicleanerstorage.spaceclean.base.mvi.CollectEffects
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.util.ExternalLinkUtil

@Composable
fun SettingsRoute(
    navigator: AppNavigator,
    termsUrl: String,
    privacyUrl: String,
    viewModel: SettingsViewModel,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    fun openLink(
        url: String,
        track: () -> Unit,
    ) {
        track()
        ExternalLinkUtil.open(context, url)
    }

    CollectEffects(viewModel) { effect ->
        when (effect) {
            SettingsEvent.NavigateBack -> navigator.back()
            SettingsEvent.OpenTerms ->
                openLink(termsUrl) {
                    Mar2Analytics.trackTermsOpened(Mar2Analytics.ABOUT)
                }
            SettingsEvent.OpenPrivacy ->
                openLink(privacyUrl) {
                    Mar2Analytics.trackPrivacyOpened(Mar2Analytics.ABOUT)
                }
            SettingsEvent.RateApp -> openGooglePlayRatePage(context)
            is SettingsEvent.TrackRatePopup -> Mar2Analytics.trackRatePopup(effect.ifOk)
            is SettingsEvent.Navigate -> navigator.navigate(effect.destination)
        }
    }
    SettingsScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}
