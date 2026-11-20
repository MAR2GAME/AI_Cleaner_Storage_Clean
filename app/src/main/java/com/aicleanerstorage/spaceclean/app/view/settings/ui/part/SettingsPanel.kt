package com.aicleanerstorage.spaceclean.app.view.settings.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXTopAppBar
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableStatusBarsPadding

@Composable
internal fun SettingsPanel(
    title: String,
    onBack: () -> Unit,
    cardMinHeight: Dp,
    modifier: Modifier = Modifier,
    cardContent: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(SettingsFigmaBackground),
    ) {
        CleanXTopAppBar(
            title = title,
            onBack = onBack,
        )
        Surface(
            modifier =
                Modifier
                    .stableStatusBarsPadding()
                    .padding(horizontal = SettingsFigmaHorizontalPadding)
                    .padding(top = SettingsFigmaCardTop)
                    .fillMaxWidth()
                    .heightIn(min = cardMinHeight),
            color = Color.White,
            shape = SettingsFigmaCardShape,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Top,
                content = cardContent,
            )
        }
    }
}

private val SettingsFigmaBackground = Color(0xFFF4F4F4)

private val SettingsFigmaCardShape = RoundedCornerShape(12.dp)

private val SettingsFigmaCardTop = 80.dp

private val SettingsFigmaHorizontalPadding = 16.dp
