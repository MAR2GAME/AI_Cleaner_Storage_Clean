package com.aicleanerstorage.spaceclean.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.app.AppConfig
import com.aicleanerstorage.spaceclean.app.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.common.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.common.intent.openUrl
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsEvent
import com.aicleanerstorage.spaceclean.common.analytics.AnalyticsReporter
import com.aicleanerstorage.spaceclean.feature.settings.ui.SettingsScreen
import com.aicleanerstorage.spaceclean.feature.settings.ui.openGooglePlayRatePage
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoute(
    navigator: AppNavigator,
    externalActivities: ExternalActivityTracker,
    analyticsReporter: AnalyticsReporter,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    fun openLink(url: String, track: () -> Unit) {
        externalActivities.markLaunch()
        track()
        if (!context.openUrl(url)) externalActivities.cancelLaunch()
    }

    SettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                SettingsAction.Back -> navigator.back()
                SettingsAction.OpenTerms -> openLink(AppConfig.TERMS_OF_SERVICE_URL) {
                    analyticsReporter.report(AnalyticsEvent.TermsOpened("about"))
                }
                SettingsAction.OpenPrivacy -> openLink(AppConfig.PRIVACY_POLICY_URL) {
                    analyticsReporter.report(AnalyticsEvent.PrivacyOpened("about"))
                }
                SettingsAction.RateApp -> openGooglePlayRatePage(context)
                is SettingsAction.TemperatureUnitChanged -> viewModel.onAction(action)
            }
        },
        onNavigate = navigator::open,
    )
}
