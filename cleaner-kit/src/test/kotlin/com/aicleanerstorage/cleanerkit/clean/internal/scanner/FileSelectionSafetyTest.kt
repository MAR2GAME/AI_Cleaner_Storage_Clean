package com.aicleanerstorage.cleanerkit.clean.internal.scanner

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus
import com.aicleanerstorage.cleanerkit.internal.storage.inspectFile
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FileSelectionSafetyTest {
    @get:Rule val folder = TemporaryFolder()

    @Test fun scanContainsOnlyOriginalRegularFiles() = runBlocking {
        val root = folder.newFolder("Download")
        val nested = File(root, "MyBackups").apply { mkdir() }
        val first = File(nested, "archive.log").apply { writeText("original") }
        val scanner = object : FileSystemJunkScanner() {
            override val category = JunkCategory.TEMP_FILE
            override fun getRootDirectories() = listOf(root)
            override fun isJunkFile(file: File) = true
        }
        val selection = scanner.scan()
        File(nested, "new.log").writeText("new")
        assertEquals(listOf(first.canonicalPath), selection.map { it.filePath })
        val snapshot = selection.single()
        first.appendText("changed")
        assertEquals(DeleteStatus.Changed, inspectFile(first, snapshot.fileSize, snapshot.lastModified))
        assertEquals(DeleteStatus.Changed, inspectFile(nested, nested.length(), nested.lastModified()))
        first.delete()
        assertEquals(DeleteStatus.AlreadyAbsent, inspectFile(first, snapshot.fileSize, snapshot.lastModified))
    }
}
