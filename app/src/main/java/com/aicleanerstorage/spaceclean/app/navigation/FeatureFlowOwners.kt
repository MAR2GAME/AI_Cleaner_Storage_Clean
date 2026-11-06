package com.aicleanerstorage.spaceclean.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.aicleanerstorage.spaceclean.app.navigation.owner.*
import com.aicleanerstorage.spaceclean.app.runtime.AppRuntimeBindings

@Composable
internal fun FeatureFlowOwners(
    entry: NavBackStackEntry?,
    navController: NavHostController,
    navigator: NavHostAppNavigator,
    runtime: AppRuntimeBindings,
    content: @Composable () -> Unit,
) {
    val owner = remember(entry, navController) { FeatureOwnerRegistry.resolve(entry, navController) }
    if (owner != null && entry != null) {
        key(owner.ownerEntry.id) {
            when (owner) {
                is FeatureOwner.FileManager -> FileManagerRouteOwner(owner, entry, navigator, runtime.featureFlow)
                is FeatureOwner.AntiVirus -> AntiVirusRouteOwner(owner, entry, navigator, runtime)
                is FeatureOwner.JunkClean -> Unit // Its screen and effects have the same entry lifetime.
            }
        }
    }
    content()
}
