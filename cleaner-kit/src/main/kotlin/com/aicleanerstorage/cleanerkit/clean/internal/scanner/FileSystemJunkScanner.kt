package com.aicleanerstorage.cleanerkit.clean.internal.scanner

import com.aicleanerstorage.cleanerkit.clean.model.JunkFile
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal abstract class FileSystemJunkScanner : JunkScanner {
    protected abstract fun getRootDirectories(): List<File>
    protected abstract fun isJunkFile(file: File): Boolean

    override suspend fun scan(): List<JunkFile> = withContext(Dispatchers.IO) {
        buildList {
            for (root in getRootDirectories()) {
                if (Files.isSymbolicLink(root.toPath())) continue
                val directory = root.canonicalFile
                directory.walkTopDown()
                    .onEnter {
                        ensureActive()
                        !Files.isSymbolicLink(it.toPath())
                    }
                    .forEach { file ->
                        ensureActive()
                        if (Files.isSymbolicLink(file.toPath()) || !file.isFile || !isJunkFile(file)) return@forEach
                        add(JunkFile(file.path, file.path, file.name, file.length(), category, file.lastModified()))
                    }
            }
        }
    }
}
