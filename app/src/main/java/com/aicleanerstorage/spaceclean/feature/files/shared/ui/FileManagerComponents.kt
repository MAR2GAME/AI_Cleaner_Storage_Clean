package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.format.FileSizeFormatter
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXSelectionCircle
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerBackgroundColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerCardBackgroundColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerDividerLineColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerMutedTextColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerPrimaryGreen
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerPrimaryGradient
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerTextColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerTitleColor
import com.aicleanerstorage.spaceclean.common.ui.components.buttons.CleanXGradientBottomActionBar
import com.aicleanerstorage.spaceclean.common.ui.components.stableNavigationBarsPadding

internal val FileManagerListBottomPadding = 112.dp
internal val FileManagerPageBrush = Brush.verticalGradient(
    colors = listOf(FileManagerBackgroundColor, FileManagerBackgroundColor)
)
internal val FileManagerCardColor = FileManagerCardBackgroundColor
internal val FileManagerNavy = FileManagerTitleColor
internal val FileManagerMutedNavy = FileManagerMutedTextColor
internal val FileManagerDividerColor = FileManagerDividerLineColor

@Composable
internal fun FileManagerDeleteBottomBar(
    enabled: Boolean,
    selectedSizeBytes: Long,
    keepActiveAppearance: Boolean = false,
    onClick: () -> Unit,
) {
    FileManagerBottomActionBar(
        enabled = enabled,
        keepActiveAppearance = keepActiveAppearance,
        text =
            if (enabled) {
                stringResource(R.string.file_delete_size, FileSizeFormatter.format(selectedSizeBytes))
            } else {
                stringResource(R.string.file_delete)
            },
        onClick = onClick,
    )
}

@Composable
internal fun FileManagerBottomActionBar(
    enabled: Boolean,
    text: String,
    keepActiveAppearance: Boolean = false,
    onClick: () -> Unit,
) {
    if (enabled || !keepActiveAppearance) {
        CleanXGradientBottomActionBar(
            enabled = enabled,
            text = text,
            onClick = onClick,
        )
        return
    }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .stableNavigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(FileManagerPrimaryGradient),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = FileManagerTextColor,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
internal fun SelectionCircle(
    selected: Boolean,
    modifier: Modifier = Modifier,
    @DrawableRes unselectedIconRes: Int = R.drawable.ic_unselected,
) {
    CleanXSelectionCircle(
        selected = selected,
        modifier = modifier,
        size = 21.dp,
        unselectedIconRes = unselectedIconRes,
    )
}

@Composable
internal fun FileManagerSelectAllAction(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(
        modifier = modifier.clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(if (selected) R.string.file_unselect_all else R.string.file_select_all),
            color = FileManagerPrimaryGreen,
            fontSize = if (compact) 16.sp else 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
        )
        SelectionCircle(selected = selected)
    }
}

@Composable
internal fun localizedFileManagerTabTitle(title: String): String =
    when (title) {
        "All" -> stringResource(R.string.file_all)
        "Photo" -> stringResource(R.string.file_photo)
        "Pictures" -> stringResource(R.string.file_pictures)
        "Download" -> stringResource(R.string.file_download)
        "Music" -> stringResource(R.string.file_music)
        "Other" -> stringResource(R.string.file_other)
        else -> title
    }
