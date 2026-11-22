package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import com.aicleanerstorage.spaceclean.data.local.storage.ScanDirectoryHelper
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import java.io.File

internal class TempFileScanner : FileSystemJunkScanner() {
    override val category: JunkCategory = JunkCategory.TEMP_FILE

    override fun getRootDirectories(): List<File> = ScanDirectoryHelper.commonPublicDirectories()
}
