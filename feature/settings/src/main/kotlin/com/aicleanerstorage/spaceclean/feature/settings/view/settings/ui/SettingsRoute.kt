package com.aicleanerstorage.spaceclean.feature.settings.view.settings.ui

import com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation.SettingsIntent
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation.SettingsEffect
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.presentation.SettingsViewModel


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.core.ui.platform.openUrl
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.core.contracts.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.feature.settings.view.settings.ui.SettingsScreen
import com.aicleanerstorage.spaceclean.core.ui.components.openGooglePlayRatePage

@Composable
fun SettingsRoute(
    navigator: AppNavigator,
    termsUrl: String,
    privacyUrl: String,
    externalActivities: ExternalActivityTracker,
    analyticsReporter: AnalyticsReporter,
    viewModel: SettingsViewModel,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    fun openLink(url: String, track: () -> Unit) {
        externalActivities.markLaunch()
        track()
        if (!context.openUrl(url)) externalActivities.cancelLaunch()
    }

    androidx.compose.runtime.LaunchedEffect(viewModel, navigator) {
        viewModel.effects.collect { effect ->
            when (effect) {
                SettingsEffect.Back -> navigator.back()
                SettingsEffect.OpenTerms -> openLink(termsUrl) {
                    analyticsReporter.report(AnalyticsEvent.TermsOpened("about"))
                }
                SettingsEffect.OpenPrivacy -> openLink(privacyUrl) {
                    analyticsReporter.report(AnalyticsEvent.PrivacyOpened("about"))
                }
                SettingsEffect.RateApp -> openGooglePlayRatePage(context)
                is SettingsEffect.Navigate -> navigator.navigate(effect.destination)
            }
        }
    }
    SettingsScreen(
        state = state,
        onAction = viewModel::dispatch,
        onManagePermissions = { viewModel.dispatch(SettingsIntent.Navigate(AppDestination.ManagePermissions)) },
    )
}
