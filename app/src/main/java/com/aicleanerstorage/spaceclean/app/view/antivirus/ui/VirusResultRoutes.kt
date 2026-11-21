package com.aicleanerstorage.spaceclean.app.view.antivirus.ui

import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanAction
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanViewModel

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.base.navigation.AppDestination
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.NoVirusResultScreen
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.ScanVirusResultScreen
import java.io.File



@Composable
fun ScanVirusResultRoute(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current



    LaunchedEffect(Unit) { viewModel.dispatch(VirusScanAction.AdbRiskRefreshRequested) }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.dispatch(VirusScanAction.AdbRiskRefreshRequested)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(viewModel) {
        viewModel.dispatch(VirusScanAction.ResultEntered)
        onDispose { viewModel.dispatch(VirusScanAction.ResultLeft) }
    }


    DisposableEffect(context, viewModel) {
        val receiver = PackageRemovedReceiver({ packageName -> viewModel.dispatch(VirusScanAction.PackageThreatRemovalRequested(packageName)) })
        val filter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED).apply { addDataScheme("package") }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, filter)
        }
        onDispose { runCatching { context.unregisterReceiver(receiver) } }
    }

    ScanVirusResultScreen(
        state = state,
        onAction = viewModel::dispatch,
    )
}

@Composable
fun NoVirusResultRoute(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
) {
    NoVirusResultScreen(
        onAction = viewModel::dispatch,
        onNavigate = { viewModel.dispatch(VirusScanAction.NavigationRequested(it)) },
    )
}
