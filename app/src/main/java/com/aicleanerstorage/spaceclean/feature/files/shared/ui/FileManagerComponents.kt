package com.aicleanerstorage.spaceclean.feature.files.shared.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.format.FileSizeFormatter
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerBackgroundColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerCardBackgroundColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerDividerLineColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerMutedTextColor
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerPrimaryGreen
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerTitleColor
import com.aicleanerstorage.spaceclean.common.ui.components.buttons.CleanXGradientBottomActionBar

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
    onClick: () -> Unit,
) {
    CleanXGradientBottomActionBar(
        enabled = enabled,
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
internal fun SelectionCircle(selected: Boolean, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(
            id = if (selected) R.drawable.ic_selected else R.drawable.ic_unselected
        ),
        contentDescription = null,
        modifier = modifier.size(21.dp)
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
