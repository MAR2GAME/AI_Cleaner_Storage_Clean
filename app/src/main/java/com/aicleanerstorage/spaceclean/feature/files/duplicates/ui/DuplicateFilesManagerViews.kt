package com.aicleanerstorage.spaceclean.feature.files.duplicates.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.OutlinedButton
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
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerPrimaryGreen
import com.aicleanerstorage.spaceclean.common.ui.components.FileManagerTextColor
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateFileEntry
import com.aicleanerstorage.spaceclean.feature.files.duplicates.DuplicateGroupItem
import com.aicleanerstorage.spaceclean.feature.files.duplicates.duplicateFileKey
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerDivider
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerListBottomPadding
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerMutedNavy
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.FileManagerNavy
import com.aicleanerstorage.spaceclean.feature.files.shared.ui.SelectionCircle
import com.aicleanerstorage.spaceclean.common.format.FileSizeFormatter

@Composable
internal fun DuplicateFilesGroupListView(
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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = FileManagerListBottomPadding)
    ) {
        if (showWarning) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.file_duplicate_warning),
                        color = FileManagerTextColor,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            onAcceptWarning()
                        },
                        modifier = Modifier
                            .width(72.dp)
                            .height(35.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FileManagerPrimaryGreen),
                        contentPadding = PaddingValues(horizontal = 8.dp)
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
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
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
                            modifier = Modifier.weight(1f)
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onOpen() },
        horizontalAlignment = Alignment.CenterHorizontally
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
            text = pluralStringResource(
                R.plurals.duplicate_files_count,
                group.duplicateCount,
                group.duplicateCount
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
        modifier = Modifier
            .width(152.dp)
            .height(158.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(136.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE3FDEA).copy(alpha = 0.35f))
                .border(1.dp, Color(0xFFC6EFD2).copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 4.dp)
                .size(144.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE3FDEA).copy(alpha = 0.65f))
                .border(1.dp, Color(0xFFC6EFD2).copy(alpha = 0.65f), RoundedCornerShape(12.dp)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 8.dp)
                .size(152.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE3FDEA))
                .border(1.dp, Color(0xFFC6EFD2), RoundedCornerShape(12.dp)),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .size(18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(FileManagerPrimaryGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = duplicateCount.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
            SelectionCircle(
                selected = selected,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable { onToggleSelection() }
            )
            DuplicateFileIconContainer(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 41.dp),
            )
            Text(
                text = sizeLabel,
                color = FileManagerTextColor,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp)
            )
        }
    }
}

@Composable
internal fun DuplicateFilesGroupDetailView(
    group: DuplicateGroupItem,
    selectedFileKeys: Set<String>,
    scrollState: ScrollState,
    onToggleFile: (DuplicateFileEntry) -> Unit,
    onAutoSelect: () -> Unit,
    onToggleGroupSelection: () -> Unit,
) {
    val groupSize = group.files.sumOf { it.realFile?.sizeBytes ?: 0L }
    val groupSizeLabel = remember(groupSize) { FileSizeFormatter.format(groupSize) }
    val groupKeys = group.files.map(::duplicateFileKey).toSet()
    val recommendedKeys = group.files.drop(1).map(::duplicateFileKey).toSet()
    val selectedGroupKeys = selectedFileKeys.intersect(groupKeys)
    val autoSelectedExactly = recommendedKeys.isNotEmpty() && selectedGroupKeys == recommendedKeys

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = FileManagerListBottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(26.dp))
        DuplicateDetailHero()

        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                group.name,
                color = FileManagerTextColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.TopCenter),
            )
            DuplicateOutlineButton(
                text = stringResource(R.string.file_auto_select),
                onClick = onAutoSelect,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
        Spacer(modifier = Modifier.height(34.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(group.files.size.toString(), color = FileManagerTextColor, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${stringResource(R.string.nav_duplicate_files)}($groupSizeLabel)",
                        color = Color(0xFF999999),
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    DuplicateOutlineButton(
                        text = stringResource(if (autoSelectedExactly) R.string.file_unselect else R.string.file_select),
                        onClick = onToggleGroupSelection,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                FileManagerDivider()
                Spacer(modifier = Modifier.height(16.dp))

                group.files.forEachIndexed { index, file ->
                    val fileKey = duplicateFileKey(file)
                    DuplicateFileRow(
                        file = file,
                        selected = fileKey in selectedFileKeys,
                        onToggleSelection = { onToggleFile(file) }
                    )
                    if (index != group.files.lastIndex) {
                        Spacer(modifier = Modifier.height(16.dp))
                        FileManagerDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DuplicateDetailHero() {
    Box(
        modifier = Modifier.size(167.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(119.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
        )
        DuplicateFileIconContainer()
    }
}

@Composable
internal fun DuplicateFileRow(
    file: DuplicateFileEntry,
    selected: Boolean,
    onToggleSelection: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DuplicateFileIcon(modifier = Modifier.size(44.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            val pathPrefix = duplicateFilePathPrefix(file)
            val fileName = duplicateFileName(file)
            Text(
                text = "Path:$pathPrefix\n$fileName",
                color = FileManagerTextColor,
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(file.date, color = FileManagerTextColor, fontSize = 16.sp, lineHeight = 19.sp)
            if (file.note != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    localizedDuplicateFileNote(file.note),
                    color = FileManagerPrimaryGreen,
                    fontSize = 14.sp,
                    lineHeight = 17.sp
                )
            }
        }
        SelectionCircle(
            selected = selected,
            modifier = Modifier.clickable { onToggleSelection() }
        )
    }
}

@Composable
private fun DuplicateOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(26.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, FileManagerPrimaryGreen),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
    ) {
        Text(text, color = FileManagerPrimaryGreen, fontSize = 12.sp)
    }
}

@Composable
private fun localizedDuplicateFileNote(note: String): String =
    when (note) {
        "Removal not recommended" -> stringResource(R.string.file_removal_not_recommended)
        else -> note
    }

@Composable
private fun DuplicateFileIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_file_yellow),
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
private fun DuplicateFileIconContainer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        DuplicateFileIcon(modifier = Modifier.size(44.dp))
    }
}

private fun duplicateFileName(file: DuplicateFileEntry): String =
    file.realFile?.name ?: file.path.substringAfterLast('/').ifBlank { file.path }

private fun duplicateFilePathPrefix(file: DuplicateFileEntry): String {
    val fullPath = file.path
    val fileName = duplicateFileName(file)
    val parent = if (fullPath.endsWith(fileName)) {
        fullPath.removeSuffix(fileName).trimEnd('/')
    } else {
        fullPath.substringBeforeLast('/', missingDelimiterValue = fullPath).trimEnd('/')
    }
    return if (parent.isBlank()) "/" else "$parent/"
}
