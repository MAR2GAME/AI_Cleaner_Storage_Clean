package com.aicleanerstorage.spaceclean.app.view.duplicates.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerListBottomPadding
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerMutedNavy
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerNavy
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPrimaryGreen
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerTextColor
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.SelectionCircle
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateGroupItem
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.duplicateFileKey

@Composable
internal fun DuplicateFileGroupList(
    groups: List<DuplicateGroupItem>,
    selectedFileKeys: Set<String>,
    allSelected: Boolean,
    showWarning: Boolean,
    scrollState: ScrollState,
    onToggleAll: () -> Unit,
    onOpenGroup: (DuplicateGroupItem) -> Unit,
    onToggleGroupSelection: (DuplicateGroupItem) -> Unit,
    onAcceptWarning: () -> Unit,
) {
    val duplicateFileCount = remember(groups) { groups.sumOf { it.files.size } }
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = FileManagerListBottomPadding),
    ) {
        if (showWarning) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.file_duplicate_warning),
                        color = FileManagerTextColor,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = {
                            onAcceptWarning()
                        },
                        modifier =
                            Modifier
                                .width(72.dp)
                                .height(35.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FileManagerPrimaryGreen),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    ) {
                        Text(stringResource(R.string.file_got_it), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(duplicateFileCount.toString(), color = FileManagerNavy, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.nav_duplicate_files), color = FileManagerMutedNavy, fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    stringResource(if (allSelected) R.string.file_unselect_all else R.string.file_select_all),
                    color = FileManagerPrimaryGreen,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                SelectionCircle(selected = allSelected, modifier = Modifier.clickable { onToggleAll() })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            groups.chunked(2).forEach { rowGroups ->
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    rowGroups.forEach { group ->
                        DuplicateGroupCard(
                            group = group,
                            selected = group.files.any { duplicateFileKey(it) in selectedFileKeys },
                            onOpen = { onOpenGroup(group) },
                            onToggleSelection = { onToggleGroupSelection(group) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(2 - rowGroups.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
internal fun DuplicateGroupCard(
    group: DuplicateGroupItem,
    selected: Boolean,
    onOpen: () -> Unit,
    onToggleSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable { onOpen() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DuplicateGroupArtwork(
            duplicateCount = group.duplicateCount,
            selected = selected,
            sizeLabel = group.sizeLabel,
            onToggleSelection = onToggleSelection,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = group.name,
            color = FileManagerTextColor,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text =
                pluralStringResource(
                    R.plurals.duplicate_files_count,
                    group.duplicateCount,
                    group.duplicateCount,
                ),
            color = FileManagerTextColor,
            fontSize = 14.sp,
            lineHeight = 17.sp,
        )
    }
}

@Composable
private fun DuplicateGroupArtwork(
    duplicateCount: Int,
    selected: Boolean,
    sizeLabel: String,
    onToggleSelection: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .width(152.dp)
                .height(158.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .size(136.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3FDEA).copy(alpha = 0.35f))
                    .border(1.dp, Color(0xFFC6EFD2).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 4.dp)
                    .size(144.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3FDEA).copy(alpha = 0.65f))
                    .border(1.dp, Color(0xFFC6EFD2).copy(alpha = 0.65f), RoundedCornerShape(12.dp)),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 8.dp)
                    .size(152.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3FDEA))
                    .border(1.dp, Color(0xFFC6EFD2), RoundedCornerShape(12.dp)),
        ) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .size(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(FileManagerPrimaryGreen),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = duplicateCount.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                )
            }
            SelectionCircle(
                selected = selected,
                unselectedIconRes = R.drawable.ic_unselected_green,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clickable { onToggleSelection() },
            )
            DuplicateFileIconContainer(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 41.dp),
            )
            Text(
                text = sizeLabel,
                color = FileManagerTextColor,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 14.dp),
            )
        }
    }
}

@Composable
internal fun DuplicateFileIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_file_yellow),
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
internal fun DuplicateFileIconContainer(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        DuplicateFileIcon(modifier = Modifier.size(44.dp))
    }
}
