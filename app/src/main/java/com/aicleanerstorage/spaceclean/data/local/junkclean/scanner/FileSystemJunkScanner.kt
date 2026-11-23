package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCandidateModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkSource
import com.aicleanerstorage.spaceclean.domain.rule.JunkClassificationRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files

internal abstract class FileSystemJunkScanner : JunkScanner {
    protected abstract fun getRootDirectories(): List<File>

    protected open fun sourceOf(file: File): JunkSource = JunkSource.PublicStorage

    protected open fun classify(
        candidate: JunkCandidateModel,
        nowMillis: Long,
    ): JunkCategory? = JunkClassificationRule.categoryOf(candidate, nowMillis)

    protected open fun accepts(result: JunkCategory): Boolean = result == category

    override suspend fun scan(): List<JunkFileModel> =
        withContext(Dispatchers.IO) {
            val nowMillis = System.currentTimeMillis()
            buildList {
                for (root in getRootDirectories()) {
                    if (Files.isSymbolicLink(root.toPath())) continue
                    val directory = root.canonicalFile
                    directory
                        .walkTopDown()
                        .onEnter {
                            ensureActive()
                            !Files.isSymbolicLink(it.toPath())
                        }.forEach { file ->
                            ensureActive()
                            if (Files.isSymbolicLink(file.toPath()) || !file.isFile) return@forEach
                            val candidate =
                                JunkCandidateModel(
                                    path = file.path,
                                    name = file.name,
                                    sizeBytes = file.length(),
                                    modifiedMillis = file.lastModified(),
                                    source = sourceOf(file),
                                )
                            val result = classify(candidate, nowMillis) ?: return@forEach
                            if (!accepts(result)) return@forEach
                            add(
                                JunkFileModel(
                                    id = candidate.path,
                                    path = candidate.path,
                                    name = candidate.name,
                                    sizeBytes = candidate.sizeBytes,
                                    category = result,
                                    modifiedMillis = candidate.modifiedMillis,
                                ),
                            )
                        }
                }
            }
        }
}
