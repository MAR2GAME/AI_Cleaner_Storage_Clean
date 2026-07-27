package com.aicleanerstorage.spaceclean.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.aicleanerstorage.spaceclean.app.notification.NotificationIntentRouteResolver
import com.aicleanerstorage.spaceclean.app.startup.AppLaunchCoordinator
import com.aicleanerstorage.spaceclean.common.ui.theme.StorageCleanAppTheme

class MainActivity : AppCompatActivity() {
    private val launchCoordinator =
        AppLaunchCoordinator(
            targetRouteResolver = NotificationIntentRouteResolver::targetRoute,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        launchCoordinator.onCreate(intent)

        setContent {
            StorageCleanAppTheme {
                AppRoot(launchCoordinator = launchCoordinator)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launchCoordinator.onNewIntent(intent)
    }
}
