package com.aicleanerstorage.spaceclean.app.ui

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.IntentSenderRequest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.key
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.CoroutineStart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val queue = remember(broker) { mutableStateListOf<UiRequest>() }
    var current by remember(broker) { mutableStateOf<UiRequest?>(null) }

    fun respond(requestId: String, result: UiResult) {
        scope.launch { broker.respond(requestId, result) }
    }

    LaunchedEffect(broker) {
        broker.requests.collect { request ->
            if (request.requestId !in broker.activeIds.value) return@collect
            if (current == null) current = request else queue += request
        }
    }

    LaunchedEffect(broker) {
        broker.activeIds.collect { active ->
            queue.removeAll { it.requestId !in active }
            if (current?.requestId !in active) {
                current = if (queue.isEmpty()) null else queue.removeAt(0)
            }
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
            scope.launch(NonCancellable, start = CoroutineStart.UNDISPATCHED) { broker.cancelAll() }
        }
    }

    key(broker, current?.requestId) {
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

    val deleteRequest = current as? UiRequest.MediaStoreDelete
    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        val request = deleteRequest ?: return@rememberLauncherForActivityResult
        finish(
            request,
            if (result.resultCode == android.app.Activity.RESULT_OK) UiResult.Confirmed else UiResult.Denied,
        )
    }

    LaunchedEffect(permissionRequest?.requestId) {
        permissionRequest?.let {
            if (it.requestId !in broker.activeIds.value) return@let
            try {
                permissionLauncher.launch(it.permissions.toTypedArray())
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                finish(it, UiResult.Failed)
            }
        }
    }

    LaunchedEffect(activityRequest?.requestId) {
        activityRequest?.let { request ->
            if (request.requestId !in broker.activeIds.value) return@let
            val intent = Intent(request.action).apply {
                request.payload.forEach { (key, value) -> putExtra(key, value) }
            }
            try {
                activityLauncher.launch(intent)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                finish(request, UiResult.Failed)
            }
        }
    }

    LaunchedEffect(deleteRequest?.requestId) {
        deleteRequest?.let { request ->
            if (request.requestId !in broker.activeIds.value) return@let
            runCatching {
                check(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R)
                require(request.uris.isNotEmpty())
                val intent = MediaStore.createDeleteRequest(
                    context.contentResolver,
                    request.uris.map(Uri::parse),
                )
                deleteLauncher.launch(IntentSenderRequest.Builder(intent.intentSender).build())
            }.onFailure { finish(request, UiResult.Failed) }
        }
    }


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

        is UiRequest.Permission, is UiRequest.MediaStoreDelete, null -> Unit
    }
    }
    content()
}
