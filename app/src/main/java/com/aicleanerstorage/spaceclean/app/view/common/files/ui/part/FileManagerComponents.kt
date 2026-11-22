package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

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
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXSelectionCircle
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerBackgroundColor
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerCardBackgroundColor
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerDividerLineColor
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerMutedTextColor
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPrimaryGradient
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPrimaryGreen
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerTextColor
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerTitleColor
import com.aicleanerstorage.spaceclean.base.designsystem.component.buttons.CleanXGradientBottomActionBar
import com.aicleanerstorage.spaceclean.base.designsystem.component.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

val FileManagerListBottomPadding = 112.dp
val FileManagerPageBrush =
    Brush.verticalGradient(
        colors = listOf(FileManagerBackgroundColor, FileManagerBackgroundColor),
    )
val FileManagerCardColor = FileManagerCardBackgroundColor
val FileManagerNavy = FileManagerTitleColor
val FileManagerMutedNavy = FileManagerMutedTextColor
val FileManagerDividerColor = FileManagerDividerLineColor

@Composable
fun FileManagerDeleteBottomBar(
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
                stringResource(R.string.file_delete_size, FormatUtil.formatSize(selectedSizeBytes))
            } else {
                stringResource(R.string.file_delete)
            },
        onClick = onClick,
    )
}

@Composable
fun FileManagerBottomActionBar(
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
            gradient = FileManagerPrimaryGradient,
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
fun SelectionCircle(
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
fun FileManagerSelectAllAction(
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
fun localizedFileManagerTabTitle(title: String): String =
    when (title) {
        "All" -> stringResource(R.string.file_all)
        "Photo" -> stringResource(R.string.file_photo)
        "Pictures" -> stringResource(R.string.file_pictures)
        "Download" -> stringResource(R.string.file_download)
        "Music" -> stringResource(R.string.file_music)
        "Other" -> stringResource(R.string.file_other)
        else -> title
    }
