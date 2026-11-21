package com.aicleanerstorage.spaceclean.app.view.duplicates.ui.part

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerDivider
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerListBottomPadding
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.SelectionCircle
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateFileEntry
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.DuplicateGroupItem
import com.aicleanerstorage.spaceclean.app.view.duplicates.interaction.duplicateFileKey
import com.aicleanerstorage.spaceclean.base.designsystem.component.FileManagerPrimaryGreen
import com.aicleanerstorage.spaceclean.base.designsystem.component.FileManagerTextColor
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

@Composable
internal fun DuplicateFileGroupContent(
    group: DuplicateGroupItem,
    selectedFileKeys: Set<String>,
    scrollState: ScrollState,
    onToggleFile: (DuplicateFileEntry) -> Unit,
    onAutoSelect: () -> Unit,
    onToggleGroupSelection: () -> Unit,
) {
    val groupSize = group.files.sumOf { it.realFile?.sizeBytes ?: 0L }
    val groupSizeLabel = remember(groupSize) { FormatUtil.formatSize(groupSize) }
    val groupKeys = group.files.map(::duplicateFileKey).toSet()
    val recommendedKeys =
        group.files
            .drop(1)
            .map(::duplicateFileKey)
            .toSet()
    val selectedGroupKeys = selectedFileKeys.intersect(groupKeys)
    val autoSelectedExactly = recommendedKeys.isNotEmpty() && selectedGroupKeys == recommendedKeys

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = FileManagerListBottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
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
            shape = RoundedCornerShape(12.dp),
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
                        onToggleSelection = { onToggleFile(file) },
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
            modifier =
                Modifier
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
    onToggleSelection: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
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
                    lineHeight = 17.sp,
                )
            }
        }
        SelectionCircle(
            selected = selected,
            modifier = Modifier.clickable { onToggleSelection() },
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

private fun duplicateFileName(file: DuplicateFileEntry): String =
    file.realFile?.name ?: file.path.substringAfterLast('/').ifBlank { file.path }

private fun duplicateFilePathPrefix(file: DuplicateFileEntry): String {
    val fullPath = file.path
    val fileName = duplicateFileName(file)
    val parent =
        if (fullPath.endsWith(fileName)) {
            fullPath.removeSuffix(fileName).trimEnd('/')
        } else {
            fullPath.substringBeforeLast('/', missingDelimiterValue = fullPath).trimEnd('/')
        }
    return if (parent.isBlank()) "/" else "$parent/"
}
