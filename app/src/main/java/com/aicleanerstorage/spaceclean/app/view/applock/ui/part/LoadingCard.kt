package com.aicleanerstorage.spaceclean.app.view.applock.ui.part

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.app.view.applock.ui.part.AppLockCardColor
import com.aicleanerstorage.spaceclean.app.view.applock.ui.part.AppLockCardRadius
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXBlue

@Composable
internal fun LoadingCard() {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(160.dp),
        color = AppLockCardColor,
        shape = RoundedCornerShape(AppLockCardRadius),
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CleanXBlue)
        }
    }
}
