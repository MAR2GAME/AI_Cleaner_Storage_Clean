package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

/**
 * 构建照片页的固定分组 tab。
 *
 * 每个 tab 携带 itemIds，切换时不需要再按标题二次过滤。
 *
 * @param items 已加载的照片。
 * @return Photo / Pictures / DCIM / Other 四个 tab。
 */
fun buildPhotoTabs(items: List<IndexedFileItem>): List<FileManagerTabDisplayItem> {
    fun IndexedFileItem.matchesFolder(folder: String): Boolean =
        file.bucketName?.contains(folder, ignoreCase = true) == true ||
            file.path?.contains("/$folder/", ignoreCase = true) == true

    val pictures = items.filter { it.matchesFolder("Pictures") }
    val dcim = items.filter { it.matchesFolder("DCIM") || it.matchesFolder("Camera") }
    val used = (pictures + dcim).map { it.id }.toSet()
    val other = items.filter { it.id !in used }

    fun tab(
        title: String,
        tabItems: List<IndexedFileItem>,
    ) = FileManagerTabDisplayItem(
        title = title,
        sizeLabel = totalSizeLabel(tabItems.map { it.file }).takeIf { it.isNotBlank() }.orEmpty(),
        itemIds = tabItems.map { it.id }.toSet(),
    )

    return listOf(
        tab("Photo", items),
        tab("Pictures", pictures),
        tab("DCIM", dcim),
        tab("Other", other),
    )
}
