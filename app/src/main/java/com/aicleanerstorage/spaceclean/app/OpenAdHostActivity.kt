package com.aicleanerstorage.spaceclean.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ads.AdRuntime
import com.aicleanerstorage.spaceclean.common.ads.ColdStartAdSession
import com.aicleanerstorage.spaceclean.common.ui.theme.LocalAppThemeTokens
import com.aicleanerstorage.spaceclean.common.ui.theme.StorageCleanAppTheme
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OpenAdHostActivity : ComponentActivity() {
    private val completed = AtomicBoolean(false)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val loadingVisible = mutableStateOf(true)
    private var startupJob: Job? = null
    private var session: ColdStartAdSession? = null
    private var openAdLoaded = false
    private var openAdEverLoaded = false
    private var hostCreatedAtMillis = 0L
    private var pendingFinishHostRunnable: Runnable? = null
    private val watchdog =
        OpenAdHostWatchdog(
            timeoutMillis = OPEN_AD_HOST_FOCUS_TIMEOUT_MS,
            schedule = { delayMillis, block ->
                val runnable = Runnable(block)
                mainHandler.postDelayed(runnable, delayMillis)
                OpenAdHostWatchdogCancellation { mainHandler.removeCallbacks(runnable) }
            },
            onTimeout = {
                Log.w(TAG, "host_watchdog_timeout")
                complete()
            },
        )
    private val stateReceiver: ResultReceiver? by lazy {
        IntentCompat.getParcelableExtra(
            intent,
            EXTRA_OPEN_AD_STATE_RECEIVER,
            ResultReceiver::class.java,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hostCreatedAtMillis = SystemClock.elapsedRealtime()
        setContent {
            StorageCleanAppTheme {
                if (loadingVisible.value) OpenAdLoadingOverlay()
            }
        }
        Log.d(TAG, "host_created")
        setResult(Activity.RESULT_CANCELED)
        onBackPressedDispatcher.addCallback(this) {}
        startupJob =
            lifecycleScope.launch {
                try {
                    val app = application as? MyApp
                    if (app == null || !app.sdkInitialization.awaitAdvertiseReady()) {
                        complete()
                        return@launch
                    }
                    if (isFinishing || isDestroyed) return@launch
                    val createdSession =
                        AdRuntime(activityProvider = { this@OpenAdHostActivity }).runColdStart(
                            context = applicationContext,
                            onOpenAdStateChanged = ::dispatchOpenAdState,
                            onFinished = ::complete,
                        )
                    session = createdSession
                    if (completed.get()) createdSession.cancel()
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Throwable) {
                    Log.e(TAG, "Cold start open ad host failed", error)
                    complete()
                }
            }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d(TAG, "window_focus_changed hasFocus=$hasFocus adLoaded=$openAdLoaded")
        watchdog.onWindowFocusChanged(hasFocus)
    }

    override fun onDestroy() {
        startupJob?.cancel()
        startupJob = null
        pendingFinishHostRunnable?.let(mainHandler::removeCallbacks)
        pendingFinishHostRunnable = null
        watchdog.dispose()
        session?.cancel()
        session = null
        super.onDestroy()
    }

    private fun complete() {
        if (!completed.compareAndSet(false, true)) return
        val finishHost: () -> Unit = {
            val finishNow =
                Runnable {
                    pendingFinishHostRunnable = null
                    watchdog.dispose()
                    session?.cancel()
                    setResult(Activity.RESULT_OK)
                    loadingVisible.value = false
                    overridePendingTransition(0, 0)
                    mainHandler.postDelayed(
                        {
                            if (!isFinishing && !isDestroyed) {
                                finish()
                                overridePendingTransition(0, 0)
                            }
                        },
                        OPEN_AD_HOST_FINISH_DELAY_MS,
                    )
                }

            val remainingLoadingMillis =
                remainingOpenAdLoadingMillis(
                    openAdLoaded = openAdEverLoaded,
                    hostCreatedAtMillis = hostCreatedAtMillis,
                    nowMillis = SystemClock.elapsedRealtime(),
                    minVisibleMillis = OPEN_AD_LOADING_MIN_VISIBLE_MS,
                )
            if (remainingLoadingMillis > 0L) {
                pendingFinishHostRunnable = finishNow
                mainHandler.postDelayed(finishNow, remainingLoadingMillis)
            } else {
                finishNow.run()
            }
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            finishHost()
        } else {
            mainHandler.post(finishHost)
        }
    }

    private fun dispatchOpenAdState(active: Boolean) {
        if (active) {
            Log.d(TAG, "ad_loaded")
            openAdEverLoaded = true
            runOnUiThread { loadingVisible.value = false }
        } else if (openAdLoaded) {
            Log.d(TAG, "ad_closed")
        }
        openAdLoaded = active
        watchdog.onAdStateChanged(active)
        stateReceiver?.send(
            OPEN_AD_STATE_CHANGED,
            Bundle().apply { putBoolean(OPEN_AD_ACTIVE, active) },
        )
    }

    private companion object {
        const val TAG = "OpenAdHostActivity"
        const val OPEN_AD_LOADING_MIN_VISIBLE_MS = 600L
        const val OPEN_AD_HOST_FOCUS_TIMEOUT_MS = 5_000L
        const val OPEN_AD_HOST_FINISH_DELAY_MS = 50L
    }
}

@Composable
private fun OpenAdLoadingOverlay() {
    val colors = LocalAppThemeTokens.current.colors
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(min = 184.dp, max = 240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.cardBackground)
                    .padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = colors.primary,
                trackColor = colors.primarySoft,
                strokeWidth = 3.dp,
            )
            Text(
                text = stringResource(R.string.open_ad_loading),
                color = colors.navy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

internal class OpenAdHostContract : ActivityResultContract<ResultReceiver, Unit>() {
    override fun createIntent(context: Context, input: ResultReceiver): Intent =
        Intent(context, OpenAdHostActivity::class.java)
            .putExtra(EXTRA_OPEN_AD_STATE_RECEIVER, input)

    override fun parseResult(resultCode: Int, intent: Intent?) = Unit
}

private const val EXTRA_OPEN_AD_STATE_RECEIVER = "open_ad_state_receiver"
internal const val OPEN_AD_STATE_CHANGED = 1
internal const val OPEN_AD_ACTIVE = "open_ad_active"

internal fun remainingOpenAdLoadingMillis(
    openAdLoaded: Boolean,
    hostCreatedAtMillis: Long,
    nowMillis: Long,
    minVisibleMillis: Long,
): Long {
    if (openAdLoaded) return 0L
    return (minVisibleMillis - (nowMillis - hostCreatedAtMillis)).coerceAtLeast(0L)
}
