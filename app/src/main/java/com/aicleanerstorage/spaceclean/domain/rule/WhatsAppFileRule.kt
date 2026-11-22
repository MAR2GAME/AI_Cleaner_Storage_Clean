package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppCleanerCategory
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppCleanerGroup
import com.aicleanerstorage.spaceclean.domain.model.whatsapp.WhatsAppFileBucketModel
import java.util.Locale

/**
 * WhatsApp 文件分类规则。
 *
 * 只根据路径、文件名和类型判断“是否 WhatsApp 文件”以及归到哪个分类；
 * 具体扫哪些目录由 data 层决定。
 */
internal object WhatsAppFileRule {
    /**
     * 判断文件是否属于 WhatsApp 目录。
     *
     * @param file 待判断文件。
     * @return 路径、bucket 或文件名包含 WhatsApp 标记时返回 true。
     */
    fun isCandidate(file: ManagedFileModel): Boolean {
        val source = sourceOf(file)
        return WHATSAPP_PATH_MARKERS.any { marker -> marker in source }
    }

    /**
     * 把 WhatsApp 文件归入“缓存/普通文件”和具体分类。
     *
     * @param file 待分类文件。
     * @return 分组与分类组合。
     */
    fun classify(file: ManagedFileModel): WhatsAppFileBucketModel {
        val source = sourceOf(file)
        val extension = file.name.substringAfterLast('.', missingDelimiterValue = "").lowercase(Locale.US)
        val group = if (CACHE_MARKERS.any(source::contains)) WhatsAppCleanerGroup.Cache else WhatsAppCleanerGroup.File
        val category =
            when {
                "database" in source || extension in DATABASE_EXTENSIONS -> WhatsAppCleanerCategory.Databases
                "document" in source || (file.kind == FileKind.Document && extension in DOCUMENT_EXTENSIONS) ->
                    WhatsAppCleanerCategory.Documents
                file.kind == FileKind.Image -> WhatsAppCleanerCategory.Images
                file.kind == FileKind.Video -> WhatsAppCleanerCategory.Videos
                file.kind == FileKind.Audio -> WhatsAppCleanerCategory.Audios
                else -> WhatsAppCleanerCategory.Other
            }
        return WhatsAppFileBucketModel(group, category)
    }

    private fun sourceOf(file: ManagedFileModel): String =
        listOfNotNull(file.path, file.bucketName, file.name).joinToString("/").lowercase(Locale.US)

    private val WHATSAPP_PATH_MARKERS =
        listOf(
            "/whatsapp/",
            "/whatsapp business/",
            "/gbwhatsapp/",
            "/yowhatsapp/",
            "/fmwhatsapp/",
            "com.whatsapp",
            "com.whatsapp.w4b",
        )

    private val CACHE_MARKERS = listOf("cache", "tmp", "temp", ".trash", ".statuses", "thumb")

    private val DATABASE_EXTENSIONS =
        setOf("db", "crypt", "crypt5", "crypt7", "crypt8", "crypt12", "crypt14", "crypt15")

    private val DOCUMENT_EXTENSIONS =
        setOf(
            "pdf",
            "doc",
            "docx",
            "xls",
            "xlsx",
            "ppt",
            "pptx",
            "txt",
            "csv",
            "rtf",
            "zip",
            "rar",
            "7z",
            "apk",
            "json",
            "xml",
        )
}
