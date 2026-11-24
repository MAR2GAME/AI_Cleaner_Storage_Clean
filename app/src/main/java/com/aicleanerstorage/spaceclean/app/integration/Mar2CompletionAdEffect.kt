package com.aicleanerstorage.spaceclean.app.integration

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.permission.findActivityOrNull

/** 在页面完成状态首次出现时请求对应的完成广告。 */
@Composable
fun Mar2CompletionAdEffect(
    show: Boolean,
    areaKey: String,
    destination: AppDestination,
) {
    val activity = LocalContext.current.findActivityOrNull()
    val route = destination::class.qualifiedName.orEmpty()
    var shown by remember(destination) { mutableStateOf(false) }

    LaunchedEffect(show) {
        if (!show) {
            shown = false
            return@LaunchedEffect
        }
        if (shown || activity == null) return@LaunchedEffect
        shown = true
        showCompletionAd(activity, areaKey, route)
    }
}

private fun showCompletionAd(
    activity: Activity,
    areaKey: String,
    route: String,
) {
    Mar2AdGateway.showInterstitial(
        activity = activity,
        areaKey = areaKey,
        fromRoute = route,
        toRoute = route,
        onContinue = {},
    )
}
