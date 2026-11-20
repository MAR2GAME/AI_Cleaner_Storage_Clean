package com.aicleanerstorage.spaceclean.base.permission

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

internal class MediaStoreDeleteRequest {
    private var pending: CompletableDeferred<Boolean>? = null

    suspend fun request(
        uris: List<String>,
        supported: Boolean,
        launch: (List<String>) -> Unit,
    ): Boolean {
        currentCoroutineContext().ensureActive()
        if (!supported || uris.isEmpty() || pending != null) return false
        val result = CompletableDeferred<Boolean>()
        pending = result
        try {
            launch(uris)
        } catch (cancelled: CancellationException) {
            pending = null
            throw cancelled
        } catch (_: Exception) {
            pending = null
            return false
        }
        return result.await()
    }

    fun complete(confirmed: Boolean) {
        val result = pending
        pending = null
        result?.complete(confirmed)
    }

    fun dispose() {
        val result = pending
        pending = null
        result?.cancel()
    }
}

@Composable
fun rememberMediaStoreDeleteRequest(): suspend (List<String>) -> Boolean {
    val context = LocalContext.current
    val request = remember { MediaStoreDeleteRequest() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        request.complete(result.resultCode == Activity.RESULT_OK)
    }
    DisposableEffect(request) {
        onDispose { request.dispose() }
    }
    return remember(context, launcher, request) {
        { uris ->
            request.request(uris, supported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { values ->
                val intent = MediaStore.createDeleteRequest(context.contentResolver, values.map(Uri::parse))
                launcher.launch(IntentSenderRequest.Builder(intent.intentSender).build())
            }
        }
    }
}
