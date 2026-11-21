package com.aicleanerstorage.spaceclean.app.view.antivirus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanAction
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part.NoVirusResultContent

@Composable
fun NoVirusResultScreen(onAction: (VirusScanAction) -> Unit) {
    BackHandler { onAction(VirusScanAction.ResultBackClicked) }
    NoVirusResultContent(
        onNavigateTool = { destination -> onAction(VirusScanAction.NavigationRequested(destination)) },
        onBack = { onAction(VirusScanAction.ResultBackClicked) },
    )
}
