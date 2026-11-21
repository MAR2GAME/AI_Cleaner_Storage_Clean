package com.aicleanerstorage.spaceclean.app.view.splash.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.aicleanerstorage.spaceclean.app.view.splash.interaction.SplashUiState
import com.aicleanerstorage.spaceclean.app.view.splash.ui.SplashScreen
import com.aicleanerstorage.spaceclean.base.designsystem.theme.StorageCleanAppTheme

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun PreviewSplashScreen() {
    StorageCleanAppTheme {
        SplashScreen(
            state = SplashUiState(),
            onAction = {},
        )
    }
}
