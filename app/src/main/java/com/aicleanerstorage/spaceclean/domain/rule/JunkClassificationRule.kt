package com.aicleanerstorage.spaceclean.domain.rule

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCandidateModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkSource
import java.util.Locale

/**
 * 候选文件的垃圾分类规则。
 *
 * 只接收枚举好的纯数据与时间快照，不读磁盘、不读系统时间；
 * 平台目录、文件长度和修改时间由 data 层提供。
 */
internal object JunkClassificationRule {
    private const val MIN_CANDIDATE_BYTES = 1024L
    private const val SEVEN_DAYS_MILLIS = 7L * 24 * 60 * 60 * 1000
    private const val THIRTY_DAYS_MILLIS = 30L * 24 * 60 * 60 * 1000

    private val TEMP_EXTENSIONS = setOf("tmp", "temp", "log", "cache", "bak", "backup")
    private val APK_DUPLICATE_MARKERS = listOf("(1)", "(2)", "copy", "duplicate", "backup")
    private val APK_COPY_PATTERN = Regex(".*\\s\\(\\d+\\).*\\.apk$", RegexOption.IGNORE_CASE)
    private val APK_NUMBERED_PATTERN = Regex(".*_\\d+\\.apk$", RegexOption.IGNORE_CASE)
    private val SYSTEM_PATH_MARKERS = listOf("/system/", "/preload/", "/system_app/", "/priv-app/")

    /**
     * 返回候选文件的垃圾类别；不属于任何类别时返回 null。
     *
     * @param candidate 平台枚举出的候选文件。
     * @param nowMillis 分类使用的时间快照，用于判断“过旧”。
     */
    fun categoryOf(
        candidate: JunkCandidateModel,
        nowMillis: Long,
    ): JunkCategory? {
        if (candidate.source == JunkSource.AppCache) return JunkCategory.CACHE
        if (candidate.sizeBytes <= 0) return null
        val extension = candidate.name.substringAfterLast('.', "").lowercase(Locale.US)
        return when {
            isJunkApk(candidate, extension, nowMillis) -> JunkCategory.APK
            isTempFile(candidate, extension) -> JunkCategory.TEMP_FILE
            else -> null
        }
    }

    private fun isJunkApk(
        candidate: JunkCandidateModel,
        extension: String,
        nowMillis: Long,
    ): Boolean {
        if (extension != "apk" || candidate.sizeBytes < MIN_CANDIDATE_BYTES) return false
        if (isSystemPath(candidate.path)) return false
        val name = candidate.name.lowercase(Locale.US)
        val duplicate =
            APK_DUPLICATE_MARKERS.any(name::contains) ||
                APK_COPY_PATTERN.matches(name) ||
                APK_NUMBERED_PATTERN.matches(name)
        return duplicate || isOldDownloadedApk(candidate, nowMillis) || isFileTooOld(candidate, nowMillis)
    }

    private fun isTempFile(
        candidate: JunkCandidateModel,
        extension: String,
    ): Boolean {
        if (candidate.sizeBytes < MIN_CANDIDATE_BYTES) return false
        val name = candidate.name.lowercase(Locale.US)
        return extension in TEMP_EXTENSIONS ||
            name.contains(".tmp") ||
            name.contains(".temp") ||
            name.startsWith("~") ||
            name.startsWith("temp_") ||
            name.startsWith("tmp_")
    }

    private fun isSystemPath(path: String): Boolean {
        val lowerPath = path.lowercase(Locale.US)
        return SYSTEM_PATH_MARKERS.any(lowerPath::contains)
    }

    private fun isOldDownloadedApk(
        candidate: JunkCandidateModel,
        nowMillis: Long,
    ): Boolean {
        if (!candidate.path.lowercase(Locale.US).contains("/download")) return false
        return candidate.modifiedMillis < nowMillis - SEVEN_DAYS_MILLIS
    }

    private fun isFileTooOld(
        candidate: JunkCandidateModel,
        nowMillis: Long,
    ): Boolean = candidate.modifiedMillis < nowMillis - THIRTY_DAYS_MILLIS
}
