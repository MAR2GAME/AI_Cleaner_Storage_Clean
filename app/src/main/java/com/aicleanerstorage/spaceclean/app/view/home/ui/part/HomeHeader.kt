package com.aicleanerstorage.spaceclean.app.view.home.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXIconButtonSize
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXPillShape
import com.aicleanerstorage.spaceclean.base.designsystem.component.cleanXDebouncedClick
import com.aicleanerstorage.spaceclean.base.designsystem.theme.LocalAppThemeTokens

@Composable
internal fun HomeHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 26.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = PrimaryText,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier =
                Modifier
                    .size(CleanXIconButtonSize)
                    .clip(CleanXPillShape)
                    .cleanXDebouncedClick(onClick = onSettingsClick),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "image description",
                contentScale = ContentScale.None
            )
        }
    }
}

private val PrimaryText: Color @Composable @ReadOnlyComposable get() = LocalAppThemeTokens.current.colors.textPrimary
