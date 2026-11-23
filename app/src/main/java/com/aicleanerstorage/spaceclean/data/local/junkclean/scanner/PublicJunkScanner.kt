package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import android.content.Context
import com.aicleanerstorage.spaceclean.data.local.storage.ScanDirectoryHelper
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCandidateModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.rule.JunkClassificationRule
import java.io.File

internal class PublicJunkScanner(
    private val context: Context,
) : FileSystemJunkScanner() {
    override val category = JunkCategory.RESIDUAL

    override fun getRootDirectories(): List<File> = ScanDirectoryHelper.junkDirectories(context)

    override fun classify(
        candidate: JunkCandidateModel,
        nowMillis: Long,
    ): JunkCategory? = JunkClassificationRule.publicJunkCategory(candidate)

    override fun accepts(result: JunkCategory): Boolean = result == JunkCategory.CACHE || result == JunkCategory.RESIDUAL
}
