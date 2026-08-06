package com.aicleanerstorage.spaceclean.common.ads

import android.app.Activity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import com.pdffox.adv.compose.rememberNativeAd

@Composable
fun AdBannerHost(areaKey: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val banner = remember(context, areaKey) { AdvertiseSdkAdapter.getBanner(context, areaKey) }
    if (banner == null) return
    AndroidView(
        modifier = modifier,
        factory = {
            FrameLayout(it).apply {
                (banner.parent as? ViewGroup)?.removeView(banner)
                addView(banner, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }
        },
    )
    DisposableEffect(banner) {
        onDispose {
            AdvertiseSdkAdapter.destroyBanner(banner as? ViewGroup)
            (banner.parent as? ViewGroup)?.removeView(banner)
        }
    }
}

@Composable
fun NativeAdHost(areaKey: String, modifier: Modifier = Modifier) {
    val nativeAd = rememberNativeAd(areaKey = areaKey, onAdClicked = {})
    val ad = nativeAd.value ?: return
    AndroidView(
        modifier = modifier,
        factory = { context -> createNativeAdView(context) },
        update = { view ->
            (view.headlineView as TextView).text = ad.headline
            (view.bodyView as TextView).apply {
                text = ad.body.orEmpty()
                visibility = if (ad.body.isNullOrBlank()) android.view.View.GONE else android.view.View.VISIBLE
            }
            (view.callToActionView as Button).apply {
                text = ad.callToAction.orEmpty()
                visibility = if (ad.callToAction.isNullOrBlank()) android.view.View.GONE else android.view.View.VISIBLE
            }
            (view.iconView as ImageView).apply {
                setImageDrawable(ad.icon?.drawable)
                visibility = if (ad.icon == null) android.view.View.GONE else android.view.View.VISIBLE
            }
            view.mediaView?.mediaContent = ad.mediaContent
            view.setNativeAd(ad)
        },
    )
}

class RewardedAdLauncher(private val areaKey: String = AdAreaKeys.Rewarded.DEFAULT) {
    fun show(activity: Activity, onReward: (type: String, amount: Int) -> Unit, onClosed: () -> Unit = {}) {
        AdvertiseSdkAdapter.showRewarded(activity, areaKey, onClosed, onReward)
    }
}

private fun createNativeAdView(context: android.content.Context): NativeAdView =
    NativeAdView(context).apply nativeView@ {
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val media = MediaView(context)
            val icon = ImageView(context)
            val headline = TextView(context)
            val body = TextView(context)
            val action = Button(context)
            addView(media, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
            addView(icon, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            addView(headline)
            addView(body)
            addView(action)
            this@nativeView.mediaView = media
            this@nativeView.iconView = icon
            this@nativeView.headlineView = headline
            this@nativeView.bodyView = body
            this@nativeView.callToActionView = action
        }
        addView(content, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }
