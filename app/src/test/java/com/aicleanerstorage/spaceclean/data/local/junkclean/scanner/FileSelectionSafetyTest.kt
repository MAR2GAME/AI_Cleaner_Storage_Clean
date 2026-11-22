package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import com.aicleanerstorage.spaceclean.data.local.storage.inspectFile
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FileSelectionSafetyTest {
    @get:Rule val folder = TemporaryFolder()

    @Test fun scanContainsOnlyOriginalRegularFiles() =
        runBlocking {
            val root = folder.newFolder("Download")
            val nested = File(root, "MyBackups").apply { mkdir() }
            val first = File(nested, "archive.log").apply { writeText("original") }
            val scanner =
                object : FileSystemJunkScanner() {
                    override val category = JunkCategory.TEMP_FILE

                    override fun getRootDirectories() = listOf(root)

                    override fun isJunkFile(file: File) = true
                }
            val selection = scanner.scan()
            File(nested, "new.log").writeText("new")
            assertEquals(listOf(first.canonicalPath), selection.map { it.path })
            val snapshot = selection.single()
            first.appendText("changed")
            assertEquals(DeleteStatus.Changed, inspectFile(first, snapshot.sizeBytes, snapshot.modifiedMillis))
            assertEquals(DeleteStatus.Changed, inspectFile(nested, nested.length(), nested.lastModified()))
            first.delete()
            assertEquals(DeleteStatus.AlreadyAbsent, inspectFile(first, snapshot.sizeBytes, snapshot.modifiedMillis))
        }
}
