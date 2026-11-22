package com.aicleanerstorage.spaceclean.data.local.storage

import android.net.Uri
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.data.local.storage.ScanDirectoryHelper.externalStorageDirectoryOrNull
import com.aicleanerstorage.spaceclean.data.local.storage.ScanDirectoryHelper.isReadableDirectory
import com.aicleanerstorage.spaceclean.domain.rule.FileClassificationRule
import java.io.File

internal object DirectoryFileScanner {
    fun scanPublicFiles(checkCancellation: () -> Unit = {}): List<ManagedFileModel> {
        val root = externalStorageDirectoryOrNull() ?: return emptyList()
        if (!root.exists() || !root.isDirectory) return emptyList()

        return scanPublicFiles(root, checkCancellation) { file, size -> file.toManagedFileItem(size) }
            .distinctBy(::identity)
    }

    internal fun <T> scanPublicFiles(
        root: File,
        checkCancellation: () -> Unit,
        toItem: (File, Long) -> T,
    ): List<T> {
        val results = mutableListOf<T>()
        collectFiles(
            root, results, maxDepth = 1, limit = MAX_FILE_SYSTEM_RESULTS,
            checkCancellation = checkCancellation, toItem = toItem,
        )
        readableDirectories(root, PUBLIC_SCAN_DIRECTORIES, checkCancellation).forEach { directory ->
            collectFiles(
                directory, results, maxDepth = 12, limit = MAX_FILE_SYSTEM_RESULTS,
                checkCancellation = checkCancellation, toItem = toItem,
            )
        }
        return results
    }

    fun scanDocumentFiles(checkCancellation: () -> Unit = {}): List<ManagedFileModel> {
        val root = externalStorageDirectoryOrNull() ?: return emptyList()
        if (!root.exists() || !root.isDirectory) return emptyList()

        val results = mutableListOf<ManagedFileModel>()
        collectFiles(
            directory = root,
            results = results,
            maxDepth = 2,
            limit = MAX_DOCUMENT_FILE_SYSTEM_RESULTS,
            accept = { FileClassificationRule.isDocumentFile(it.name, null) },
            checkCancellation = checkCancellation,
            toItem = { file, size -> file.toManagedFileItem(size) },
        )
        readableDirectories(root, DOCUMENT_SCAN_DIRECTORIES, checkCancellation).forEach { directory ->
            collectFiles(
                directory = directory,
                results = results,
                maxDepth = 16,
                limit = MAX_DOCUMENT_FILE_SYSTEM_RESULTS,
                accept = { FileClassificationRule.isDocumentFile(it.name, null) },
                checkCancellation = checkCancellation,
                toItem = { file, size -> file.toManagedFileItem(size) },
            )
        }
        return results.distinctBy(::identity)
    }

    fun scanDirectories(relativeDirectories: List<String>, checkCancellation: () -> Unit): List<ManagedFileModel> {
        val root = externalStorageDirectoryOrNull() ?: return emptyList()
        if (!root.exists() || !root.isDirectory) return emptyList()

        val results = mutableListOf<ManagedFileModel>()
        readableDirectories(root, relativeDirectories, checkCancellation).forEach { directory ->
            collectFiles(
                directory = directory,
                results = results,
                maxDepth = 18,
                limit = MAX_DIRECTORY_FILE_SYSTEM_RESULTS,
                checkCancellation = checkCancellation,
                toItem = { file, size -> file.toManagedFileItem(size) },
            )
        }
        return results.distinctBy(::identity)
    }

    private fun readableDirectories(root: File, paths: List<String>, checkCancellation: () -> Unit): List<File> =
        paths
            .map { path ->
                checkCancellation()
                File(root, path)
            }
            .filter { it.isReadableDirectory() && it.canonicalFile == File(root.canonicalFile, it.relativeTo(root).path) }
            .distinctBy { runCatching { it.canonicalPath }.getOrDefault(it.absolutePath) }

    private fun <T> collectFiles(
        directory: File,
        results: MutableList<T>,
        maxDepth: Int,
        limit: Int,
        accept: (File) -> Boolean = { true },
        checkCancellation: () -> Unit,
        toItem: (File, Long) -> T,
    ) {
        checkCancellation()
        if (maxDepth < 0 || results.size >= limit) return
        val children = runCatching { directory.listFiles() }.getOrNull().orEmpty()
        for (file in children) {
            checkCancellation()
            if (results.size >= limit) return
            if (java.nio.file.Files.isSymbolicLink(file.toPath())) continue
            if (FileClassificationRule.shouldSkipPath(file.absolutePath, file.name)) continue
            if (runCatching { file.isDirectory }.getOrDefault(false)) {
                collectFiles(file, results, maxDepth - 1, limit, accept, checkCancellation, toItem)
            } else if (runCatching { file.isFile }.getOrDefault(false)) {
                val size = runCatching { file.length() }.getOrDefault(0L)
                if (size > 0L && accept(file)) results += toItem(file, size)
            }
        }
    }

    private fun File.toManagedFileItem(sizeBytes: Long): ManagedFileModel =
        ManagedFileModel(
            id = absolutePath.hashCode().toLong(),
            uri = Uri.fromFile(this).toString(),
            path = absolutePath,
            name = name,
            sizeBytes = sizeBytes,
            modifiedSeconds = lastModified() / 1_000L,
            mimeType = null,
            bucketName = parentFile?.name,
            kind = FileClassificationRule.typeForName(name),
        )

    private fun identity(item: ManagedFileModel): String = FileClassificationRule.identity(item.path, item.uri)

    private val PUBLIC_SCAN_DIRECTORIES =
        listOf("Download", "Downloads", "Documents", "DCIM", "Pictures", "Movies", "Music", "WhatsApp", "Telegram", "Android/media")

    private val DOCUMENT_SCAN_DIRECTORIES =
        listOf(
            "Download", "Downloads", "Documents", "Desktop", "Android/media", "Android/data", "WhatsApp",
            "WhatsApp/Media/WhatsApp Documents", "Telegram", "Telegram/Telegram Documents", "Tencent",
            "Tencent/QQfile_recv", "Tencent/MicroMsg", "WeChat", "DingTalk", "Feishu", "Lark", "BaiduNetdisk",
            "UCDownloads", "Browser", "Chrome", "QQBrowser", "WPS", "Kingsoft", "Office", "Documents/WeChat Files",
        )

    private const val MAX_FILE_SYSTEM_RESULTS = 10_000
    private const val MAX_DOCUMENT_FILE_SYSTEM_RESULTS = 30_000
    private const val MAX_DIRECTORY_FILE_SYSTEM_RESULTS = 30_000
}
