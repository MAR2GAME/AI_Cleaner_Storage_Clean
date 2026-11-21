package com.aicleanerstorage.spaceclean.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aicleanerstorage.spaceclean.BuildConfig
import com.aicleanerstorage.spaceclean.app.root.AppRootView
import com.aicleanerstorage.spaceclean.base.designsystem.theme.StorageCleanAppTheme
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionHost
import com.aicleanerstorage.spaceclean.domain.battery.usecase.SyncBackgroundBatterySamplingUseCase
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val syncBackgroundBatterySampling: SyncBackgroundBatterySamplingUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MediaDeletionHost.attach(activityResultRegistry)
        enableEdgeToEdge()
        syncBackgroundBatterySampling.reconcile()

        setContent {
            StorageCleanAppTheme(themeKey = BuildConfig.PRODUCT_THEME_KEY) {
                AppRootView()
            }
        }
    }

    override fun onDestroy() {
        MediaDeletionHost.detach(activityResultRegistry)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        syncBackgroundBatterySampling.reconcile()
    }
}
