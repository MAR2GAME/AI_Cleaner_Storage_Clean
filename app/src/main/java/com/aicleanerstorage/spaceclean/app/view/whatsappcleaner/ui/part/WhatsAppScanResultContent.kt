package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerGroupItem
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerSubItem
import com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction.WhatsAppCleanerUiState
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXMutedText
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXSelectionCircle
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXText
import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import com.aicleanerstorage.spaceclean.data.whatsapp.WhatsAppCleanerCategory
import com.aicleanerstorage.spaceclean.data.whatsapp.WhatsAppCleanerGroup

@Composable
internal fun WhatsAppScanResultContent(
    uiState: WhatsAppCleanerUiState,
    onToggleGroup: (WhatsAppCleanerGroup) -> Unit,
    onToggleCategory: (WhatsAppCleanerGroup, WhatsAppCleanerCategory) -> Unit,
    onToggleExpanded: (WhatsAppCleanerGroup) -> Unit,
) {
//    Box(
//        modifier =
//            Modifier
//                .fillMaxWidth()
//                .height(WhatsAppResultHeroHeight)
//                .background(
//                    Color(0xFFE7EAF4),
//                    RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
//                ),
//    )
    Spacer(modifier = Modifier.height(WhatsAppHeaderTopGap))
    SummaryCard(totalBytes = uiState.scannedBytes)
    Spacer(modifier = Modifier.height(WhatsAppHeaderBottomGap))

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        uiState.groups.forEach { group ->
            GroupCard(
                groupItem = group,
                onToggleGroup = { onToggleGroup(group.group) },
                onToggleExpanded = { onToggleExpanded(group.group) },
                onToggleCategory = { category -> onToggleCategory(group.group, category) },
            )
        }
    }

    Spacer(modifier = Modifier.height(WhatsAppResultBottomPadding))
}

private val ResultCardBg = Color.White

private val Divider15 = Color(0xFFE1E6EF)

internal val CardRadius = 10.dp

private val WhatsAppResultBottomPadding = 104.dp

private val WhatsAppResultHeroHeight = 126.dp

private val WhatsAppHeaderTopGap = 46.dp

private val WhatsAppHeaderBottomGap = 36.dp

private val WhatsAppLogoOuterSize = 84.dp

private val WhatsAppLogoInnerSize = 64.dp

private val WhatsAppLogoSize = 44.dp

private val WhatsAppGroupRowHeight = 62.dp

private val WhatsAppCategoryTouchSize = 42.dp

private val WhatsAppCategoryIconBoxSize = 36.dp

private val WhatsAppCategoryIconSize = 23.dp

private val WhatsAppSelectionSize = 22.dp

private val WhatsAppSelectionSmallSize = 16.dp

private val WhatsAppUnitBottomPadding = 5.dp

@Composable
private fun SummaryCard(totalBytes: Long) {
    val sizeLabel = splitSizeLabel(totalBytes)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(WhatsAppLogoOuterSize)
                    .clip(CircleShape)
                    .background(Color(0xFFEFFDF2)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(WhatsAppLogoInnerSize)
                        .clip(CircleShape)
                        .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_whats_app_cleaner),
                    contentDescription = null,
                    modifier = Modifier.size(WhatsAppLogoSize),
                )
            }
        }
        Spacer(modifier = Modifier.width(22.dp))
        Column {
            Text(
                text = stringResource(R.string.occupying),
                color = Color(0xFF5F6876),
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = sizeLabel.main,
                    color = Color(0xFF2D2F38),
                    fontSize = 42.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = sizeLabel.unit,
                    color = Color(0xFF2D2F38),
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = WhatsAppUnitBottomPadding),
                )
            }
        }
    }
}

@Composable
private fun GroupCard(
    groupItem: WhatsAppCleanerGroupItem,
    onToggleGroup: () -> Unit,
    onToggleExpanded: () -> Unit,
    onToggleCategory: (WhatsAppCleanerCategory) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ResultCardBg,
        shape = RoundedCornerShape(CardRadius),
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(WhatsAppGroupRowHeight)
                        .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SelectionBadge(
                    selected = groupItem.selected,
                    enabled = groupItem.hasFiles,
                    modifier = Modifier.clickable(enabled = groupItem.hasFiles, onClick = onToggleGroup),
                )
                Spacer(modifier = Modifier.width(18.dp))
                Text(
                    text = stringResource(groupItem.group.titleRes),
                    color = CleanXText,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = compactSizeLabel(groupItem.totalBytes),
                    color = CleanXText,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = if (groupItem.expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CleanXMutedText,
                    modifier =
                        Modifier
                            .size(WhatsAppSelectionSize)
                            .clickable(onClick = onToggleExpanded),
                )
            }

            if (groupItem.expanded) {
                HorizontalDivider(color = Divider15)
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    groupItem.children.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowItems.forEach { child ->
                                CategoryTile(
                                    item = child,
                                    onToggle = { onToggleCategory(child.category) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTile(
    item: WhatsAppCleanerSubItem,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(CardRadius))
                .clickable(enabled = item.hasFiles, onClick = onToggle),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(WhatsAppCategoryTouchSize),
            contentAlignment = Alignment.TopEnd,
        ) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(WhatsAppCategoryIconBoxSize)
                        .clip(RoundedCornerShape(8.dp))
                        .background(item.category.iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(item.category.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(WhatsAppCategoryIconSize),
                )
            }
            SelectionBadge(
                selected = item.hasFiles && item.selected,
                enabled = item.hasFiles,
                modifier = Modifier.size(WhatsAppSelectionSmallSize),
                size = WhatsAppSelectionSmallSize,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(item.category.titleRes),
            color = CleanXText,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = compactSizeLabel(item.totalBytes),
            color = CleanXMutedText,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SelectionBadge(
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = WhatsAppSelectionSize,
) {
    CleanXSelectionCircle(
        selected = selected,
        enabled = enabled,
        modifier = modifier,
        size = size,
    )
}

private val WhatsAppCleanerGroup.titleRes: Int
    get() =
        when (this) {
            WhatsAppCleanerGroup.Cache -> R.string.cache
            WhatsAppCleanerGroup.File -> R.string.file
        }

private val WhatsAppCleanerCategory.titleRes: Int
    get() =
        when (this) {
            WhatsAppCleanerCategory.Images -> R.string.images
            WhatsAppCleanerCategory.Videos -> R.string.videos
            WhatsAppCleanerCategory.Audios -> R.string.whatsapp_category_audios
            WhatsAppCleanerCategory.Documents -> R.string.whatsapp_category_documents
            WhatsAppCleanerCategory.Databases -> R.string.whatsapp_category_databases
            WhatsAppCleanerCategory.Other -> R.string.other
        }

private val WhatsAppCleanerCategory.iconRes: Int
    get() =
        when (this) {
            WhatsAppCleanerCategory.Images -> R.drawable.ic_photos
            WhatsAppCleanerCategory.Videos -> R.drawable.ic_videos
            WhatsAppCleanerCategory.Audios -> R.drawable.ic_audios
            WhatsAppCleanerCategory.Documents -> R.drawable.ic_documents
            WhatsAppCleanerCategory.Databases -> R.drawable.ic_file_blue
            WhatsAppCleanerCategory.Other -> R.drawable.ic_file
        }

private val WhatsAppCleanerCategory.iconBackground: Color
    get() =
        when (this) {
            WhatsAppCleanerCategory.Images -> Color(0xFFF7ECFF)
            WhatsAppCleanerCategory.Videos -> Color(0xFFFFECF5)
            WhatsAppCleanerCategory.Audios -> Color(0xFFEAF4FF)
            WhatsAppCleanerCategory.Documents -> Color(0xFFFFF6E5)
            WhatsAppCleanerCategory.Databases -> Color(0xFFEFF3FF)
            WhatsAppCleanerCategory.Other -> Color(0xFFE9FFF9)
        }

internal data class SizeLabel(
    val main: String,
    val unit: String,
)

internal fun splitSizeLabel(bytes: Long): SizeLabel {
    val compact = compactSizeLabel(bytes)
    val main = compact.takeWhile { it.isDigit() || it == '.' }.ifBlank { "0" }
    val unit = compact.drop(main.length).ifBlank { "B" }
    return SizeLabel(main = main, unit = unit)
}

internal fun compactSizeLabel(bytes: Long): String = FormatUtil.formatSize(bytes).replace(" ", "")
