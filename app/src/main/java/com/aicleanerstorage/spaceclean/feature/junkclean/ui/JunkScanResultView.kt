package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.feature.junkclean.model.CategoryCleanGroup
import com.aicleanerstorage.spaceclean.feature.junkclean.model.CleanItem
import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkFile
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXSelectionCircle
import com.aicleanerstorage.spaceclean.common.ui.components.buttons.CleanXGradientBottomActionBar
import com.aicleanerstorage.spaceclean.feature.junkclean.SelectionSummary

private val CleanXText = Color(0xFF2D3748)
private val CleanXMutedText = Color(0xFF8190A5)
private val ResultCardBg = Color.White
private val ResultNavy = Color(0xFF282927)
private val ResultOrange = Color(0xFFFC7941)
private val ResultDivider = Color(0xFFD9D9D9)
private val ResultIconBg = Color(0xA6EFFFF2)

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun JunkScanResultView(
    groups: List<CategoryCleanGroup>,
    checkedEmptyCategories: Set<JunkCategory>,
    selectedSummary: SelectionSummary,
    onToggleCategorySelection: (List<JunkCategory>) -> Unit,
    onToggleItem: (CleanItem) -> Unit,
) {
    val rows = displayGroups(groups)
    var expandedIndex by remember { mutableStateOf(-1) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(121.dp))
        SummaryCard(totalSize = selectedSummary.checkedSize)
        Spacer(modifier = Modifier.height(24.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ResultCardBg,
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp)) {
                rows.forEachIndexed { rowIndex, row ->
                    val items = row.group.items
                    CategoryGroupSection(
                        row = row,
                        checked =
                            row.group.items.takeIf { it.isNotEmpty() }?.all { it.isChecked }
                                ?: (row.group.category in checkedEmptyCategories),
                        expanded = expandedIndex == rowIndex && items.isNotEmpty(),
                        onToggleExpanded = {
                            expandedIndex = if (expandedIndex == rowIndex) -1 else rowIndex
                        },
                        onToggleCategorySelection = {
                            onToggleCategorySelection(row.categories)
                        },
                        onToggleItem = onToggleItem,
                    )
                    if (rowIndex < rows.lastIndex) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(ResultDivider),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
internal fun AwaitingAuthorizationView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.authorization_required),
            color = CleanXText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = CleanXMutedText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}

@Composable
internal fun JunkCleanErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.error),
            color = CleanXText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = CleanXMutedText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}

@Composable
private fun SummaryCard(totalSize: Long) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(96.dp),
        color = ResultCardBg,
        shape = RoundedCornerShape(12.dp),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val illustrationSize = if (maxWidth < 320.dp) 72.dp else 88.dp

            Image(
                painter = painterResource(id = R.drawable.junk_result_bucket_figma),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(illustrationSize),
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp, end = illustrationSize + 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.occupying).let { if (it.endsWith(":")) it else "$it:" },
                    color = ResultNavy,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = displayMainSize(totalSize),
                    color = ResultOrange,
                    fontSize = 33.85.sp,
                    lineHeight = 39.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = displayUnit(totalSize),
                    color = ResultNavy,
                    fontSize = 18.46.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
internal fun JunkRemoveButton(
    selectedSummary: SelectionSummary,
    onClick: () -> Unit,
) {
    val onlyZeroByteSelection =
        selectedSummary.checkedCount == 0 && selectedSummary.checkedEmptyCategoryCount > 0
    val enabled = selectedSummary.checkedCount > 0
    val buttonText =
        when {
            selectedSummary.checkedCount > 0 ->
                stringResource(R.string.remove_size, compactSizeLabel(selectedSummary.checkedSize))
            onlyZeroByteSelection -> stringResource(R.string.result_zero_byte_selection_button)
            else -> stringResource(R.string.result_select_items_button)
        }

    CleanXGradientBottomActionBar(
        text = buttonText,
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
private fun CategoryGroupSection(
    row: ResultDisplayRow,
    checked: Boolean,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onToggleCategorySelection: () -> Unit,
    onToggleItem: (CleanItem) -> Unit,
) {
    val group = row.group
    val items = group.items

    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier =
                    Modifier
                        .weight(1f)
                        .clickable(enabled = items.isNotEmpty()) { onToggleExpanded() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(row.titleRes),
                    color = ResultNavy,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = ResultNavy,
                    modifier = Modifier.size(24.dp),
                )
            }
            Row(
                modifier = Modifier.width(156.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = group.formattedTotalSize,
                    color = ResultNavy,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.width(8.dp))
                CleanXSelectionCircle(
                    selected = checked,
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clickable { onToggleCategorySelection() },
                )
            }
        }

        if (expanded && items.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items.forEach { item ->
                    JunkItemCard(
                        item = item,
                        modifier = Modifier.width(55.dp),
                        onClick = { onToggleItem(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun JunkItemCard(
    item: CleanItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier =
            modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ResultIconBg),
                contentAlignment = Alignment.Center,
            ) {
                JunkItemIcon()
            }
            CleanXSelectionCircle(
                selected = item.isChecked,
                modifier = Modifier.align(Alignment.TopEnd),
                size = 21.dp,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.fileName.ifBlank { stringResource(R.string.unnamed_file) },
            color = Color(0xA62D3748),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.formattedSize,
            color = Color(0xA62D3748),
            fontSize = 12.sp,
            lineHeight = 14.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun JunkItemIcon() {
    Image(
        painter = painterResource(R.drawable.ic_junk_result_file),
        contentDescription = null,
        modifier = Modifier.size(24.dp),
    )
}

private fun displayMainSize(size: Long): String {
    val formatted = JunkFile.formatFileSize(size)
    return formatted.takeWhile { it.isDigit() || it == '.' }.ifBlank { "0" }
}

private fun displayUnit(size: Long): String {
    val formatted = JunkFile.formatFileSize(size)
    return formatted.dropWhile { it.isDigit() || it == '.' }.trim().ifBlank { "B" }
}

private fun compactSizeLabel(size: Long): String {
    val mainSize = displayMainSize(size)
    val unit = displayUnit(size)
    return "$mainSize$unit"
}

private data class ResultDisplayRow(
    @StringRes val titleRes: Int,
    val group: CategoryCleanGroup,
    val categories: List<JunkCategory>,
)

private fun displayGroups(groups: List<CategoryCleanGroup>): List<ResultDisplayRow> {
    fun row(
        @StringRes titleRes: Int,
        category: JunkCategory,
    ): ResultDisplayRow {
        val index = groups.indexOfFirst { it.category == category }
        val group = groups.getOrNull(index) ?: CategoryCleanGroup(category, emptyList())
        return ResultDisplayRow(titleRes = titleRes, group = group, categories = listOf(category))
    }

    val residualGroup =
        mergeDisplayGroup(
            category = JunkCategory.RESIDUAL,
            groups = groups,
            categories = listOf(JunkCategory.RESIDUAL, JunkCategory.DUPLICATE),
        )
    val otherGroup =
        mergeDisplayGroup(
            category = JunkCategory.LARGE_FILE,
            groups = groups,
            categories = listOf(JunkCategory.LARGE_FILE),
        )

    return listOf(
        row(R.string.junk_group_system_cache, JunkCategory.CACHE),
        row(R.string.junk_group_ad_junk_files, JunkCategory.TEMP_FILE),
        ResultDisplayRow(
            R.string.junk_group_residual_junks,
            residualGroup,
            listOf(JunkCategory.RESIDUAL, JunkCategory.DUPLICATE),
        ),
        row(R.string.junk_group_obsolete_apks, JunkCategory.APK),
        ResultDisplayRow(
            R.string.junk_group_other_junk_files,
            otherGroup,
            listOf(JunkCategory.LARGE_FILE),
        ),
    )
}

private fun mergeDisplayGroup(
    category: JunkCategory,
    groups: List<CategoryCleanGroup>,
    categories: List<JunkCategory>,
): CategoryCleanGroup =
    CategoryCleanGroup(
        category = category,
        items = categories.flatMap { displayCategory ->
            groups.firstOrNull { it.category == displayCategory }?.items.orEmpty()
        },
    )
