package com.aicleanerstorage.spaceclean.app.view.antivirus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanAction
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part.NoVirusResultContent
import com.aicleanerstorage.spaceclean.base.navigation.ToolNavigationTarget

@Composable
fun NoVirusResultScreen(
    onAction: (VirusScanAction) -> Unit,
    onNavigate: (ToolNavigationTarget) -> Unit,
) {
    BackHandler { onAction(VirusScanAction.ResultBackClicked) }
    NoVirusResultContent(
        onNavigateTool = onNavigate,
        onBack = { onAction(VirusScanAction.ResultBackClicked) },
    )
}
