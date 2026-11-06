package com.aicleanerstorage.spaceclean.app.navigation.owner

import androidx.compose.runtime.*
import androidx.navigation.NavBackStackEntry
import com.aicleanerstorage.spaceclean.app.navigation.NavHostAppNavigator
import com.aicleanerstorage.spaceclean.app.runtime.AppRuntimeBindings
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanViewModel
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanIntent
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.VirusEffectHandler
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AntiVirusRouteOwner(
    owner: FeatureOwner.AntiVirus,
    entry: NavBackStackEntry,
    navigator: NavHostAppNavigator,
    runtime: AppRuntimeBindings,
) {
    val vm: VirusScanViewModel = koinViewModel(viewModelStoreOwner = owner.ownerEntry)
    DisposableEffect(vm) {
        onDispose { vm.dispatch(VirusScanIntent.CancelScan) }
    }
    val pageNavigator = remember(navigator, entry) { navigator.forEntry(entry) }
    VirusEffectHandler(pageNavigator, vm, runtime.featureFlow, runtime.externalActivities)
}
