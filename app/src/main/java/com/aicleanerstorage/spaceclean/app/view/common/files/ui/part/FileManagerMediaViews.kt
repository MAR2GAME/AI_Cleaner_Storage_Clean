package com.aicleanerstorage.spaceclean.app.view.common.files.ui.part

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.LruCache
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDateGroupDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileImageGroupDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerTabDisplayItem
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerListBottomPadding
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerPrimaryGreen
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.FileManagerTextColor
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.SelectionCircle
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.part.localizedFileManagerTabTitle
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXPillTabs
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXTabItem
import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.max
import android.util.Size as AndroidSize

private const val FILE_MANAGER_GRID_THUMBNAIL_PX = 512
private const val FILE_MANAGER_MAX_DETAIL_IMAGE_PX = 2048
private const val FILE_MANAGER_BITMAP_CACHE_KB = 24 * 1024

private data class FileManagerBitmapCacheKey(
    val uri: String,
    val quality: FileManagerImageQuality,
    val targetSizePx: Int,
)

private val FileManagerBitmapCache =
    object : LruCache<FileManagerBitmapCacheKey, Bitmap>(FILE_MANAGER_BITMAP_CACHE_KB) {
        override fun sizeOf(
            key: FileManagerBitmapCacheKey,
            value: Bitmap,
        ): Int = value.byteCount / 1024
    }

/** 清空缩略图缓存；系统低内存时由 App 调用，优先保住进程。 */
internal fun trimFileManagerBitmapCache() {
    FileManagerBitmapCache.evictAll()
}

@Composable
fun FileManagerScreenshotGridView(params: FileManagerScreenshotGridParams) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(params.scrollState)
                .padding(bottom = FileManagerListBottomPadding),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                FileManagerGroupHeader(
                    leading = params.items.size.toString(),
                    trailing = stringResource(R.string.nav_screenshots),
                    selected = params.allSelected,
                    onClick = params.onToggleAll,
                )
                ScreenshotRows(params)
            }
        }
    }
}

@Composable
private fun ScreenshotRows(params: FileManagerScreenshotGridParams) {
    params.items.chunked(3).forEach { rowItems ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            rowItems.forEach { item ->
                FileManagerThumbnail(
                    FileManagerThumbnailParams(
                        item = item,
                        selected = item.id in params.selectedIds,
                        onOpen = { params.onOpenDetail(item) },
                        onToggleSelection = { params.onSelect(item.id) },
                        modifier = Modifier.weight(1f),
                    ),
                )
            }
            repeat(3 - rowItems.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FileManagerSimilarPhotosView(params: FileManagerSimilarPhotosParams) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(params.scrollState)
                .padding(bottom = FileManagerListBottomPadding),
    ) {
        FileManagerSelectAllAction(selected = params.allSelected, onClick = params.onToggleAll)
        Spacer(modifier = Modifier.height(16.dp))
        FileManagerSimilarPhotosTotalRow(
            totalCount = params.totalCount,
            totalSizeBytes = params.totalSizeBytes,
        )
        Spacer(modifier = Modifier.height(20.dp))
        SimilarPhotoGroupList(params)
    }
}

@Composable
private fun SimilarPhotoGroupList(params: FileManagerSimilarPhotosParams) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        params.groups.forEach { group ->
            val groupIds =
                group.items
                    .filterNot { it.bestPhoto }
                    .map { it.id }
                    .toSet()
            FileManagerSimilarPhotoGroupCard(
                group = group,
                selectedIds = params.selectedIds,
                selected = groupIds.isNotEmpty() && params.selectedIds.containsAll(groupIds),
                onToggleGroup = { params.onToggleGroup(group) },
                onSelect = params.onSelect,
                onOpenDetail = params.onOpenDetail,
            )
        }
    }
}

@Composable
private fun FileManagerSimilarPhotosTotalRow(
    totalCount: Int,
    totalSizeBytes: Long,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = totalCount.toString(),
            color = FileManagerTextColor,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${stringResource(
                R.string.file_photos,
            )} ${FormatUtil.formatSize(totalSizeBytes)} ${stringResource(R.string.file_in_total)}",
            color = FileManagerMutedNavy,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun FileManagerSimilarPhotoGroupCard(
    group: FileImageGroupDisplayItem,
    selectedIds: Set<Int>,
    selected: Boolean,
    onToggleGroup: () -> Unit,
    onSelect: (Int) -> Unit,
    onOpenDetail: (FileImageDisplayItem) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FileManagerSimilarPhotoGroupHeader(
                count = group.count,
                selected = selected,
                onClick = onToggleGroup,
            )
            Column(verticalArrangement = Arrangement.spacedBy(3.5.dp)) {
                group.items.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.5.dp),
                    ) {
                        rowItems.forEach { item ->
                            FileManagerSimilarPhotoTile(
                                item = item,
                                selected = item.id in selectedIds,
                                onOpen = { onOpenDetail(item) },
                                onToggleSelection = { onSelect(item.id) },
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

@Composable
private fun FileManagerSimilarPhotoGroupHeader(
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 16.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = count.toString(),
            color = FileManagerTextColor,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.file_similar),
            color = Color(0xFF999999),
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.clickable { onClick() },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(if (selected) R.string.file_unselect_all else R.string.file_select_all),
                color = FileManagerPrimaryGreen,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal,
            )
            SelectionCircle(selected = selected)
        }
    }
}

@Composable
private fun FileManagerSimilarPhotoTile(
    item: FileImageDisplayItem,
    selected: Boolean,
    onOpen: () -> Unit,
    onToggleSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(112f / 114f)
                .background(Brush.linearGradient(item.colors))
                .clickable { onOpen() },
    ) {
        FileManagerRealImage(item = item)
        if (item.bestPhoto) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 18.dp, bottom = 10.dp)
                        .background(FileManagerPrimaryGreen, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.file_best_photo),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
        SelectionCircle(
            selected = selected,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .clickable { onToggleSelection() },
        )
    }
}

@Composable
fun FileManagerPhotoPrivacyView(params: FileManagerPhotoPrivacyParams) {
    val itemCount = params.groups.sumOf { it.items.size }
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = FileManagerListBottomPadding),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = stringResource(R.string.file_photo_privacy_desc),
                color = Color(0xFF282927),
                fontSize = 18.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        PrivacySummaryRow(itemCount = itemCount, allSelected = params.allSelected, onToggleAll = params.onToggleAll)
        Spacer(modifier = Modifier.height(16.dp))
        PrivacyGroupList(params)
    }
}

@Composable
private fun PrivacySummaryRow(
    itemCount: Int,
    allSelected: Boolean,
    onToggleAll: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(itemCount.toString(), color = FileManagerNavy, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(6.dp))
        Text(stringResource(R.string.file_photos), color = FileManagerMutedNavy, fontSize = 18.sp)
        Spacer(modifier = Modifier.weight(1f))
        FileManagerSelectAllAction(selected = allSelected, onClick = onToggleAll)
    }
}

@Composable
private fun PrivacyGroupList(params: FileManagerPhotoPrivacyParams) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        params.groups.forEach { group ->
            val groupIds = group.items.map { it.id }.toSet()
            PrivacyGroupCard(params, group, groupIds)
        }
    }
}

@Composable
private fun PrivacyGroupCard(
    params: FileManagerPhotoPrivacyParams,
    group: FileImageDateGroupDisplayItem,
    groupIds: Set<Int>,
) {
    val groupSelected = groupIds.isNotEmpty() && params.selectedIds.containsAll(groupIds)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.padding(start = 10.dp, top = 16.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(group.title, color = FileManagerNavy, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.weight(1f))
                FileManagerSelectAllAction(
                    selected = groupSelected,
                    onClick = { params.onToggleGroup(groupIds) },
                    compact = true,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                group.items.chunked(3).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        rowItems.forEach { item ->
                            FileManagerThumbnail(
                                FileManagerThumbnailParams(
                                    item = item,
                                    selected = item.id in params.selectedIds,
                                    onOpen = { params.onSelect(item.id) },
                                    onToggleSelection = { params.onSelect(item.id) },
                                    modifier = Modifier.weight(1f),
                                ),
                            )
                        }
                        repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
fun FileManagerGroupHeader(
    leading: String,
    trailing: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(leading, color = FileManagerNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(4.dp))
        Text(trailing, color = FileManagerMutedNavy, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            stringResource(if (selected) R.string.file_unselect_all else R.string.file_select_all),
            color = FileManagerPrimaryGreen,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        SelectionCircle(selected = selected, modifier = Modifier.clickable { onClick() })
    }
}

@Composable
fun FileManagerThumbnail(params: FileManagerThumbnailParams) {
    Box(
        modifier =
            params.modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(params.item.colors))
                .clickable { params.onOpen() },
    ) {
        FileManagerRealImage(item = params.item)
        if (params.item.bestPhoto) BestPhotoBadge()
        if (params.showPlayBadge) PlayBadge()
        SelectionCircle(
            selected = params.selected,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable { params.onToggleSelection() },
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.BestPhotoBadge() {
    Box(
        modifier =
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(24.dp)
                .background(Color.Black.copy(alpha = 0.32f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.file_best_photo), color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.PlayBadge() {
    Icon(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = null,
        tint = Color.White,
        modifier =
            Modifier
                .align(Alignment.Center)
                .size(31.dp),
    )
}

@Composable
fun FileManagerGalleryBrowserView(params: FileManagerGalleryParams) {
    val allSelected =
        params.items.isNotEmpty() && params.selectedIds.containsAll(params.items.map { it.id })
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(params.scrollState)
                .padding(bottom = FileManagerListBottomPadding),
    ) {
        CleanXPillTabs(
            items = params.tabs.map { tab -> CleanXTabItem(localizedFileManagerTabTitle(tab.title)) },
            selectedIndex = params.selectedTabIndex,
            onSelected = params.onTabSelected,
        )
        Spacer(modifier = Modifier.height(18.dp))
        GalleryCard(params, allSelected)
    }
}

@Composable
private fun GalleryCard(
    params: FileManagerGalleryParams,
    allSelected: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val selectedTab = params.tabs.getOrNull(params.selectedTabIndex)
            FileManagerGalleryGroupHeader(
                title = galleryTitle(params.selectedTabIndex, selectedTab),
                sizeLabel = selectedTab?.sizeLabel?.takeIf { it.isNotBlank() },
                selected = allSelected,
                onClick = params.onSelectAll,
            )
            GalleryRows(params)
        }
    }
}

@Composable
private fun galleryTitle(
    selectedTabIndex: Int,
    selectedTab: FileManagerTabDisplayItem?,
): String =
    if (selectedTabIndex == 0) {
        stringResource(R.string.device_screen)
    } else {
        localizedFileManagerTabTitle(selectedTab?.title ?: stringResource(R.string.file_photo))
    }

@Composable
private fun GalleryRows(params: FileManagerGalleryParams) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        params.items.chunked(3).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    FileManagerGalleryTile(
                        item = item,
                        selected = item.id in params.selectedIds,
                        onOpen = { params.onOpenDetail(item) },
                        onToggleSelection = { params.onSelect(item.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - rowItems.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun FileManagerGalleryGroupHeader(
    title: String,
    sizeLabel: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (sizeLabel == null) title else "$title ($sizeLabel)",
            color = FileManagerNavy,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(if (selected) R.string.file_unselect_all else R.string.file_select_all),
            color = FileManagerPrimaryGreen,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        SelectionCircle(selected = selected, modifier = Modifier.clickable { onClick() })
    }
}

@Composable
fun FileManagerGalleryTile(
    item: FileImageDisplayItem,
    selected: Boolean,
    onOpen: () -> Unit,
    onToggleSelection: () -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(item.colors))
                .clickable { onOpen() },
    ) {
        FileManagerRealImage(item = item)
        SelectionCircle(
            selected = selected,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable { onToggleSelection() },
        )
    }
}

@Composable
fun FileManagerRealImage(
    item: FileImageDisplayItem,
    contentScale: ContentScale = ContentScale.Crop,
    quality: FileManagerImageQuality = FileManagerImageQuality.Grid,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val targetSizePx =
        remember(quality, configuration.screenWidthDp, configuration.screenHeightDp, density) {
            when (quality) {
                FileManagerImageQuality.Grid -> FILE_MANAGER_GRID_THUMBNAIL_PX
                FileManagerImageQuality.Detail ->
                    with(density) {
                        max(configuration.screenWidthDp.dp.roundToPx(), configuration.screenHeightDp.dp.roundToPx())
                            .coerceAtLeast(FILE_MANAGER_GRID_THUMBNAIL_PX)
                            .coerceAtMost(FILE_MANAGER_MAX_DETAIL_IMAGE_PX)
                    }
            }
        }
    val bitmap by produceState<Bitmap?>(initialValue = null, item.uri, quality, targetSizePx) {
        val uri = item.uri?.let { Uri.parse(it) }
        value =
            if (uri == null) {
                null
            } else {
                withContext(Dispatchers.IO) {
                    loadFileManagerBitmap(context, uri, quality, targetSizePx)
                }
            }
    }

    val loadedBitmap = bitmap
    if (loadedBitmap != null) {
        Image(
            bitmap = loadedBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
        )
    } else {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.18f),
                radius = size.minDimension * 0.48f,
                center = Offset(size.width * 0.68f, size.height * 0.34f),
            )
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.12f),
                topLeft = Offset(0f, size.height * 0.72f),
                size = Size(size.width, size.height * 0.28f),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            )
        }
    }
}

private fun loadFileManagerBitmap(
    context: Context,
    uri: Uri,
    quality: FileManagerImageQuality,
    targetSizePx: Int,
): Bitmap? {
    val cacheKey = FileManagerBitmapCacheKey(uri.toString(), quality, targetSizePx)
    FileManagerBitmapCache.get(cacheKey)?.let { return it }
    val bitmap =
        runCatching {
            when (quality) {
                FileManagerImageQuality.Grid -> loadFileManagerGridBitmap(context, uri, targetSizePx)
                FileManagerImageQuality.Detail -> loadFileManagerDetailBitmap(context, uri, targetSizePx)
            }
        }.getOrNull()
    if (bitmap != null) {
        FileManagerBitmapCache.put(cacheKey, bitmap)
    }
    return bitmap
}

private fun loadFileManagerGridBitmap(
    context: Context,
    uri: Uri,
    targetSizePx: Int,
): Bitmap {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return context.contentResolver.loadThumbnail(uri, AndroidSize(targetSizePx, targetSizePx), null)
    }
    return decodeSampledBitmap(context, uri, targetSizePx)
        ?: run {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
}

private fun loadFileManagerDetailBitmap(
    context: Context,
    uri: Uri,
    targetSizePx: Int,
): Bitmap? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val sourceSize = info.size
            val largestSide = max(sourceSize.width, sourceSize.height)
            if (largestSide > targetSizePx) {
                val scale = targetSizePx.toFloat() / largestSide.toFloat()
                decoder.setTargetSize(
                    (sourceSize.width * scale).toInt().coerceAtLeast(1),
                    (sourceSize.height * scale).toInt().coerceAtLeast(1),
                )
            }
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    }
    return decodeSampledBitmap(context, uri, targetSizePx)
}

private fun decodeSampledBitmap(
    context: Context,
    uri: Uri,
    targetSizePx: Int,
): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, bounds)
    }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    val options =
        BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, targetSizePx)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
    return context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, options)
    }
}

private fun calculateInSampleSize(
    width: Int,
    height: Int,
    targetSizePx: Int,
): Int {
    val largestSide = max(width, height)
    if (largestSide <= targetSizePx) return 1
    val ratio = ceil(largestSide.toDouble() / targetSizePx.toDouble()).toInt()
    var sampleSize = 1
    while (sampleSize * 2 <= ratio) {
        sampleSize *= 2
    }
    return sampleSize
}
