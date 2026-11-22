package com.aicleanerstorage.spaceclean.app.view.applock.ui.part

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository
import com.aicleanerstorage.spaceclean.domain.rule.AppLockPinRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Full screen PIN overlay hosted by the App Lock runtime through the WindowManager.
 *
 * Pure input surface: it renders dots and errors, and forwards the entered PIN.
 * Verification and lockout policy belong to the runtime that owns this view.
 */
class LockOverlayView(
    private val context: Context,
    private val repository: AppLockRepository,
    private val scope: CoroutineScope,
    private val onPinComplete: (String) -> Unit,
    private val onCancel: () -> Unit,
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var root: View? = null
    private var pin = ""
    private var verifying = false
    private var lockedUntilMillis = 0L

    fun show(targetPackage: String): Boolean {
        if (root != null) return true
        if (!AppPermission.Overlay.isGranted(context)) return false
        val view = LayoutInflater.from(context).inflate(R.layout.layout_lock_screen_pin, null)
        applyImmersiveFlags(view)
        view.findViewById<TextView>(R.id.tv_hint).text = context.getString(R.string.enter_pin_to_use)
        view.findViewById<TextView>(R.id.tv_error_hint).visibility = View.INVISIBLE
        bindAppInfo(view, targetPackage)
        bindKeypad(view)
        return runCatching {
            windowManager.addView(view, layoutParams())
        }.onSuccess {
            root = view
        }.isSuccess
    }

    fun dismiss() {
        val view = root ?: return
        root = null
        verifying = false
        runCatching { windowManager.removeView(view) }
    }

    fun showError() {
        verifying = false
        clearPin()
        val errorView = root?.findViewById<TextView>(R.id.tv_error_hint) ?: return
        errorView.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(errorView, "translationX", 0f, 20f, -20f, 20f, -20f, 0f).apply {
            duration = 400L
            interpolator = OvershootInterpolator()
            start()
        }
        vibrate()
    }

    fun showLockout(seconds: Long) {
        verifying = false
        lockedUntilMillis = System.currentTimeMillis() + seconds * 1_000L
        clearPin()
        Toast.makeText(context, context.getString(R.string.wait_seconds), Toast.LENGTH_LONG).show()
    }

    @Suppress("DEPRECATION")
    private fun applyImmersiveFlags(view: View) {
        view.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    private fun bindAppInfo(
        view: View,
        targetPackage: String,
    ) {
        scope.launch {
            val name = withContext(Dispatchers.IO) { loadAppName(targetPackage) }
            view.findViewById<TextView>(R.id.tv_app_name).text = name
            val icon = withContext(Dispatchers.IO) { loadAppIcon(targetPackage) }
            if (icon != null) view.findViewById<ImageView>(R.id.iv_app_icon).setImageDrawable(icon)
        }
    }

    private fun bindKeypad(view: View) {
        DIGIT_KEYS.forEach { (id, digit) ->
            view.findViewById<View>(id).setOnClickListener { onDigit(digit) }
        }
        view.findViewById<View>(R.id.btn_delete).setOnClickListener { onDelete() }
        view.findViewById<TextView>(R.id.tv_cancel).setOnClickListener { onCancel() }
    }

    private fun onDigit(digit: Char) {
        val now = System.currentTimeMillis()
        if (now < lockedUntilMillis) {
            val remaining = ((lockedUntilMillis - now) / 1_000L).coerceAtLeast(1L)
            Toast
                .makeText(
                    context,
                    context.getString(R.string.too_many_attempts_retry_seconds, remaining),
                    Toast.LENGTH_SHORT,
                ).show()
            return
        }
        if (verifying || pin.length >= AppLockPinRule.PIN_LENGTH) return
        vibrate()
        pin += digit
        updatePinDots()
        if (pin.length == AppLockPinRule.PIN_LENGTH) {
            verifying = true
            onPinComplete(pin)
        }
    }

    private fun onDelete() {
        if (verifying || pin.isEmpty()) return
        pin = pin.dropLast(1)
        updatePinDots()
    }

    private fun clearPin() {
        pin = ""
        updatePinDots()
    }

    private fun updatePinDots() {
        val view = root ?: return
        PIN_DOT_IDS.forEachIndexed { index, id ->
            view.findViewById<View>(id).setBackgroundResource(
                if (index < pin.length) R.drawable.pin_dot_selected else R.drawable.pin_dot_unselected,
            )
        }
    }

    private fun vibrate() {
        if (!repository.isVibrationEnabled()) return
        runCatching {
            val vibrator =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }
            vibrator.vibrate(VibrationEffect.createOneShot(30L, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun loadAppName(packageName: String): String =
        runCatching {
            val packageManager = context.packageManager
            packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString()
        }.getOrElse {
            packageName.substringAfterLast('.').ifBlank { context.getString(R.string.app_lock) }
        }

    private fun loadAppIcon(packageName: String): Drawable? =
        runCatching { context.packageManager.getApplicationIcon(packageName) }.getOrNull()

    private fun layoutParams(): WindowManager.LayoutParams =
        WindowManager
            .LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }

    private companion object {
        val DIGIT_KEYS: List<Pair<Int, Char>> =
            listOf(
                R.id.btn_0 to '0',
                R.id.btn_1 to '1',
                R.id.btn_2 to '2',
                R.id.btn_3 to '3',
                R.id.btn_4 to '4',
                R.id.btn_5 to '5',
                R.id.btn_6 to '6',
                R.id.btn_7 to '7',
                R.id.btn_8 to '8',
                R.id.btn_9 to '9',
            )

        val PIN_DOT_IDS: List<Int> = listOf(R.id.circle1, R.id.circle2, R.id.circle3, R.id.circle4)
    }
}
