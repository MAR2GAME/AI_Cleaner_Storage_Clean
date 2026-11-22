package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal abstract class FileSystemJunkScanner : JunkScanner {
    protected abstract fun getRootDirectories(): List<File>
    protected abstract fun isJunkFile(file: File): Boolean

    override suspend fun scan(): List<JunkFileModel> = withContext(Dispatchers.IO) {
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
                        add(
                            JunkFileModel(
                                id = file.path,
                                path = file.path,
                                name = file.name,
                                sizeBytes = file.length(),
                                category = category,
                                modifiedMillis = file.lastModified(),
                            ),
                        )
                    }
            }
        }
    }
}
