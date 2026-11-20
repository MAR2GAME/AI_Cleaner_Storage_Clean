package com.aicleanerstorage.spaceclean.app.view.applock.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockBackground
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXPrimaryButton
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding

@Composable
internal fun AppLockBottomBar(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppLockBackground)
            .stableNavigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        CleanXPrimaryButton(
            text = text,
            onClick = onClick,
            enabled = enabled,
            height = 52.dp,
            fontSize = 20.sp
        )
    }
}
