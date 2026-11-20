package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXTopAppBar

@Composable
internal fun VirusScanPanel(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(VirusFigmaBackground),
    ) {
        CleanXTopAppBar(
            title = stringResource(R.string.anti_virus),
            onBack = onBack,
        )

        content()
    }
}

val VirusFigmaBackground = Color(0xFFF4F4F4)
