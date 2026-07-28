package com.aicleanerstorage.spaceclean.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.aicleanerstorage.spaceclean.app.background.BackgroundBatterySamplingController
import com.aicleanerstorage.spaceclean.app.notification.PersistentNotificationService
import com.aicleanerstorage.spaceclean.app.notification.NotificationIntentRouteResolver
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchCoordinator
import com.aicleanerstorage.spaceclean.common.ui.theme.StorageCleanAppTheme
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val backgroundBatterySamplingController: BackgroundBatterySamplingController by inject()
    private val launchCoordinator =
        AppLaunchCoordinator(
            targetRouteResolver = NotificationIntentRouteResolver::targetRoute,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        launchCoordinator.onCreate(intent)
        backgroundBatterySamplingController.reconcile()

        setContent {
            StorageCleanAppTheme {
                AppRoot(
                    launchCoordinator = launchCoordinator,
                    onNotificationPermissionGranted = {
                        backgroundBatterySamplingController.reconcile()
                        PersistentNotificationService.refresh(this)
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launchCoordinator.onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        backgroundBatterySamplingController.reconcile()
        PersistentNotificationService.refresh(this)
    }
}
