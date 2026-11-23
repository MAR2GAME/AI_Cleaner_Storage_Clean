package com.aicleanerstorage.spaceclean.data.local.files

import android.net.Uri
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.aicleanerstorage.spaceclean.data.local.junkclean.JunkFileDeleter
import com.aicleanerstorage.spaceclean.data.local.junkclean.MemoryCleaner
import com.aicleanerstorage.spaceclean.data.local.junkclean.scanner.FileSystemJunkScanner
import com.aicleanerstorage.spaceclean.data.local.storage.MediaDeletionAuthorization
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.RandomAccessFile

class FileDeletionDataSourceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule val directory = TemporaryFolder(context.cacheDir)

    @Test
    fun missingFileCompletesWithoutInflatingFreedSpace() =
        runBlocking {
            val tenMiB = 10L * 1024 * 1024
            val files =
                listOf("existing", "absent").map { name ->
                    directory.newFile(name).canonicalFile.apply {
                        RandomAccessFile(this, "rw").use { it.setLength(tenMiB) }
                    }
                }
            val selected =
                files.map { file ->
                    ManagedFileModel(
                        id = 1L,
                        uri = Uri.fromFile(file).toString(),
                        path = file.path,
                        name = file.name,
                        sizeBytes = file.length(),
                        modifiedSeconds = file.lastModified() / 1000L,
                        mimeType = null,
                        bucketName = null,
                        kind = FileKind.Document,
                    )
                }
            assertTrue(files.last().delete())
            val summary = FileDeletionDataSource(context, null, MediaDeletionAuthorization()).delete(selected).toSummary()

            assertEquals(2, summary.processedCount)
            assertEquals(1, summary.alreadyAbsentCount)
            assertEquals(tenMiB, summary.freedBytes)
            assertFalse(files.first().exists())
        }

    @Test
    fun cleanSkipsChangedFilesAndLeavesLaterArrivalsInDirectory() =
        runBlocking {
            val root = directory.newFolder("cache").canonicalFile
            val previous = File(root, "previous").apply { writeText("before") }
            val changed = File(root, "changed").apply { writeText("before") }
            val scanner =
                object : FileSystemJunkScanner() {
                    override val category = JunkCategory.CACHE

                    override fun getRootDirectories() = listOf(root)
                }
            val scanned = scanner.scan()
            assertEquals(2, scanned.size)
            changed.appendText(" update")
            val arrival = File(root, "new").apply { writeText("after") }
            val statuses =
                scanned.associate { item ->
                    item.name to JunkFileDeleter.delete(context, item, checkCancellation = {}) { error("Unexpected media request") }.status
                }
            assertEquals(DeleteStatus.Deleted, statuses[previous.name])
            assertEquals(DeleteStatus.Changed, statuses[changed.name])
            assertTrue(arrival.isFile)
            assertTrue(changed.isFile)
        }

    @Test
    fun modernAndroidDoesNotReportUnattributableMemory() {
        assumeTrue(Build.VERSION.SDK_INT >= 34)
        assertEquals(0L, MemoryCleaner.clean(context).freedBytes)
    }
}
