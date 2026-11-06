package com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui

import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanIntent
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.presentation.VirusScanViewModel

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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.navigation.AppDestination
import com.aicleanerstorage.spaceclean.core.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.core.navigation.toAppDestination
import com.aicleanerstorage.spaceclean.core.contracts.intent.ExternalActivityTracker
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.NoVirusResultScreen
import com.aicleanerstorage.spaceclean.feature.antivirus.view.scanflow.ui.ScanVirusResultScreen
import java.io.File



@Composable
fun ScanVirusResultRoute(
    navigator: AppNavigator,
    viewModel: VirusScanViewModel,
    externalActivities: ExternalActivityTracker,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val deletionFailedText = stringResource(R.string.deletion_failed)



    LaunchedEffect(Unit) { viewModel.dispatch(VirusScanIntent.RefreshAdbRisk) }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.dispatch(VirusScanIntent.RefreshAdbRisk)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    DisposableEffect(viewModel) {
        viewModel.dispatch(VirusScanIntent.ResultEntered)
        onDispose { viewModel.dispatch(VirusScanIntent.ResultLeft) }
    }


    DisposableEffect(context, viewModel) {
        val receiver = PackageRemovedReceiver({ packageName -> viewModel.dispatch(VirusScanIntent.RemoveThreatByPackage(packageName)) })
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
        onNavigate = { viewModel.dispatch(VirusScanIntent.Navigate(it.toAppDestination())) },
    )
}
