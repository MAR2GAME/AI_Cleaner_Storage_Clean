package com.aicleanerstorage.spaceclean.app.ui

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.aicleanerstorage.spaceclean.core.contracts.ads.AdDriver
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiRequest
import com.aicleanerstorage.spaceclean.core.contracts.intent.UiResult
import com.aicleanerstorage.spaceclean.core.presentation.intent.UiRequestBroker
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import org.koin.compose.koinInject

@Composable
fun AppUiRequestHost(
    broker: UiRequestBroker = koinInject(),
    adDriver: AdDriver,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val queue = remember { mutableStateListOf<UiRequest>() }
    var current by remember { mutableStateOf<UiRequest?>(null) }

    fun respond(requestId: String, result: UiResult) {
        scope.launch { broker.respond(requestId, result) }
    }

    LaunchedEffect(broker) {
        broker.requests.collect { request ->
            if (current == null) current = request else queue += request
        }
    }

    fun finish(request: UiRequest, result: UiResult) {
        if (current?.requestId != request.requestId) {
            respond(request.requestId, result)
            return
        }
        current = null
        respond(request.requestId, result)
        if (queue.isNotEmpty()) {
            current = queue.removeAt(0)
        }
    }

    DisposableEffect(broker) {
        onDispose {
            scope.launch { broker.cancelAll() }
        }
    }

    val permissionRequest = current as? UiRequest.Permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted ->
        val request = permissionRequest ?: return@rememberLauncherForActivityResult
        val result = if (request.permissions.all { granted[it] == true }) {
            UiResult.Granted
        } else {
            UiResult.Denied
        }
        finish(request, result)
    }

    val activityRequest = current as? UiRequest.LaunchActivity
    val activityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val request = activityRequest ?: return@rememberLauncherForActivityResult
        finish(request, UiResult.ActivityResult(result.resultCode))
    }

    LaunchedEffect(permissionRequest?.requestId) {
        permissionRequest?.let {
            permissionLauncher.launch(it.permissions.toTypedArray())
        }
    }

    LaunchedEffect(activityRequest?.requestId) {
        activityRequest?.let { request ->
            val intent = Intent(request.action).apply {
                request.payload.forEach { (key, value) -> putExtra(key, value) }
            }
            activityLauncher.launch(intent)
        }
    }

    content()

    when (val request = current) {
        is UiRequest.Confirm -> {
            AlertDialog(
                onDismissRequest = { finish(request, UiResult.Cancelled) },
                title = { Text(request.title) },
                text = { Text(request.message) },
                confirmButton = {
                    Button(onClick = { finish(request, UiResult.Confirmed) }) {
                        Text(request.confirmText)
                    }
                },
                dismissButton = {
                    Button(onClick = { finish(request, UiResult.Denied) }) {
                        Text(request.cancelText)
                    }
                },
            )
        }

        is UiRequest.ShowInterstitial -> {
            LaunchedEffect(request.requestId) {
                val result = try {
                    if (!adDriver.isAvailable()) {
                        UiResult.Failed
                    } else {
                        adDriver.play(request.placement)
                        UiResult.Confirmed
                    }
                } catch (error: CancellationException) {
                    throw error
                } catch (_: Exception) {
                    UiResult.Failed
                }
                finish(request, result)
            }
        }

        is UiRequest.LaunchActivity -> Unit

        is UiRequest.Permission, null -> Unit
    }
}
