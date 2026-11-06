package com.aicleanerstorage.spaceclean.app.navigation.owner

import androidx.compose.runtime.remember
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.navigation.ToolNavigationTarget
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator
import com.aicleanerstorage.spaceclean.core.ui.components.ToolFeatureBanners
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureFlow
import com.aicleanerstorage.spaceclean.core.presentation.intent.UiRequestBroker
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.ui.JunkCleanRoute
import com.aicleanerstorage.spaceclean.feature.junkclean.view.junkclean.presentation.JunkCleanViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.Composable

@Composable
internal fun JunkCleanRouteOwner(
    owner: FeatureOwner.JunkClean,
    navigator: NavHostAppNavigator,
    featureFlow: FeatureFlow,
) {
    val entry = owner.ownerEntry
    val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
    val viewModel: JunkCleanViewModel = koinViewModel(viewModelStoreOwner = entry)
    val uiRequestBroker: UiRequestBroker = org.koin.compose.koinInject()
    JunkCleanRoute(
        viewModel = viewModel,
        featureFlow = featureFlow,
        uiRequestBroker = uiRequestBroker,
        onNavigateBack = { pageNavigator.back() },
        onNavigateHome = { pageNavigator.backToMain() },
        onNavigateHomeAfterComplete = { pageNavigator.backToMain() },
        resultTools = { modifier, enabled ->
            ToolFeatureBanners(
                modifier = modifier,
                excludedDestinations = setOf(ToolNavigationTarget.JunkClean),
                onNavigateTool = { target -> if (enabled) pageNavigator.resetTo(target.toAppDestination()) },
            )
        },
    )
}
