package com.aicleanerstorage.spaceclean.data.local.storage

import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.NoSuchFileException
import java.nio.file.attribute.BasicFileAttributes

/** A null result means the regular file still matches the scanned metadata. */
internal fun inspectFile(
    file: File,
    bytes: Long,
    modified: Long,
    seconds: Boolean = false,
): DeleteStatus? =
    try {
        val attributes = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java, LinkOption.NOFOLLOW_LINKS)
        val millis = attributes.lastModifiedTime().toMillis()
        if (!attributes.isRegularFile ||
            file.canonicalFile != file.absoluteFile ||
            attributes.size() != bytes ||
            (if (seconds) millis / 1000L else millis) != modified
        ) {
            DeleteStatus.Changed
        } else {
            null
        }
    } catch (_: NoSuchFileException) {
        DeleteStatus.AlreadyAbsent
    } catch (_: IOException) {
        DeleteStatus.Unknown
    } catch (_: SecurityException) {
        DeleteStatus.Unknown
    }

/** Best-effort removal of the inspected path; inspectFile already rejected directories and symlinks. */
internal fun unlinkFile(file: File): Boolean =
    try {
        android.system.Os.remove(file.path)
        true
    } catch (_: android.system.ErrnoException) {
        false
    } catch (_: SecurityException) {
        false
    }
