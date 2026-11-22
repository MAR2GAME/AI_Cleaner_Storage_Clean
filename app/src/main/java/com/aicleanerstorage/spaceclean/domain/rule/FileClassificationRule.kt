package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import java.util.Locale

/**
 * 文件分类规则。
 *
 * 只接收文件名、MIME 和路径，不读磁盘；平台目录差异由 data 层提供输入。
 * 这里放“什么算文档、什么算媒体、哪些系统路径要跳过”等业务策略，
 * 让 data 层只负责遍历和查询。
 */
internal object FileClassificationRule {
    /**
     * 判断是否媒体文件。
     *
     * @param name 文件名，用于取扩展名。
     * @param mimeType 平台返回的 MIME；为空时只按扩展名判断。
     * @return 图片、视频或音频返回 true。
     */
    fun isMediaFile(
        name: String,
        mimeType: String?,
    ): Boolean {
        val extension = name.lowercase(Locale.US).substringAfterLast('.', missingDelimiterValue = "")
        val mime = mimeType.orEmpty().lowercase(Locale.US)
        return extension in MEDIA_EXTENSIONS ||
            mime.startsWith("image/") ||
            mime.startsWith("video/") ||
            mime.startsWith("audio/")
    }

    /**
     * 判断是否文档文件。
     *
     * @param name 文件名，用于取扩展名。
     * @param mimeType 平台返回的 MIME；为空时按扩展名或文档关键词判断。
     * @return 非媒体且属于文档集合时返回 true。
     */
    fun isDocumentFile(
        name: String,
        mimeType: String?,
    ): Boolean {
        if (isMediaFile(name, mimeType)) return false
        val extension = name.lowercase(Locale.US).substringAfterLast('.', missingDelimiterValue = "")
        val mime = mimeType.orEmpty().lowercase(Locale.US)
        return extension in DOCUMENT_EXTENSIONS ||
            mime.startsWith("text/") ||
            DOCUMENT_MIME_KEYWORDS.any(mime::contains)
    }

    /**
     * 按扩展名推断文件类型。
     *
     * @param name 文件名。
     * @return 图片/视频/音频；无法识别时返回 [FileKind.Document]。
     */
    fun typeForName(name: String): FileKind =
        when (name.lowercase(Locale.US).substringAfterLast('.', missingDelimiterValue = "")) {
            "jpg", "jpeg", "png", "gif", "webp", "bmp", "heic", "heif" -> FileKind.Image
            "mp4", "mkv", "mov", "avi", "webm", "3gp" -> FileKind.Video
            "mp3", "wav", "m4a", "aac", "ogg", "flac" -> FileKind.Audio
            else -> FileKind.Document
        }

    /**
     * 判断文件是否应跳过。
     *
     * 隐藏文件、`lost+found` 和系统目录不参与扫描。
     *
     * @param path 文件绝对路径；为空时只按文件名判断。
     * @param name 文件名。
     * @return 需要跳过返回 true。
     */
    fun shouldSkipPath(
        path: String?,
        name: String,
    ): Boolean {
        val lowerName = name.lowercase(Locale.US)
        val lowerPath = path?.lowercase(Locale.US).orEmpty()
        if (lowerName.startsWith(".") || lowerName == "lost+found") return true
        if (lowerPath.isBlank()) return false
        return SYSTEM_DIRECTORIES.any { lowerPath == it || lowerPath.startsWith("$it/") }
    }

    /**
     * 生成文件身份，用于去重和重复文件判定。
     *
     * 优先使用调用方提供的绝对路径；为空时退回 URI。
     * 规范化（canonicalPath）由 data 层在枚举时完成，规则本身不触盘。
     *
     * @param path 文件绝对路径，可为空。
     * @param uri 平台 URI，作为兜底身份。
     * @return 稳定的文件身份字符串。
     */
    fun identity(
        path: String?,
        uri: String,
    ): String = path?.takeIf(String::isNotBlank) ?: uri

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
            "xml",
            "json",
            "md",
            "markdown",
            "epub",
            "mobi",
            "azw",
            "azw3",
            "fb2",
            "zip",
            "rar",
            "7z",
            "tar",
            "gz",
            "tgz",
            "bz2",
            "xz",
            "odt",
            "ods",
            "odp",
            "ott",
            "ots",
            "otp",
            "wps",
            "et",
            "dps",
            "pages",
            "numbers",
            "key",
            "xps",
            "log",
            "ini",
            "conf",
            "cfg",
            "yaml",
            "yml",
            "toml",
            "properties",
            "sql",
            "db",
            "sqlite",
            "sqlite3",
            "kt",
            "kts",
            "java",
            "js",
            "ts",
            "html",
            "htm",
            "css",
            "py",
            "sh",
            "bat",
            "gradle",
            "c",
            "cpp",
            "h",
            "hpp",
            "cs",
            "go",
            "rs",
            "php",
            "rb",
            "swift",
        )

    private val DOCUMENT_MIME_KEYWORDS =
        listOf(
            "pdf",
            "document",
            "spreadsheet",
            "presentation",
            "msword",
            "officedocument",
            "opendocument",
            "word",
            "excel",
            "powerpoint",
            "wps",
            "csv",
            "rtf",
            "epub",
            "ebook",
            "markdown",
            "javascript",
            "xhtml",
            "html",
            "yaml",
            "json",
            "xml",
            "sqlite",
            "database",
            "archive",
            "compressed",
            "gzip",
            "tar",
            "zip",
            "rar",
            "7z",
        )

    private val MEDIA_EXTENSIONS =
        setOf(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "webp",
            "bmp",
            "heic",
            "heif",
            "tif",
            "tiff",
            "svg",
            "mp4",
            "mkv",
            "mov",
            "avi",
            "webm",
            "3gp",
            "m4v",
            "wmv",
            "flv",
            "mpeg",
            "mpg",
            "mp3",
            "wav",
            "m4a",
            "aac",
            "ogg",
            "oga",
            "flac",
            "amr",
            "wma",
            "opus",
        )

    private val SYSTEM_DIRECTORIES =
        listOf(
            "/system",
            "/lost+found",
            "/preload",
            "/vendor",
            "/mnt",
            "/proc",
            "/sys",
            "/acct",
            "/dev",
            "/config",
            "/oem",
            "/firmware",
            "/cache",
        )
}
