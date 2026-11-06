package com.aicleanerstorage.spaceclean.core.contracts.files

import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.util.concurrent.CancellationException

object DuplicateContentGuard {
    fun hashFile(
        file: File,
        maxBytes: Long = Long.MAX_VALUE,
        checkCancellation: () -> Unit = {},
    ): String? = try {
        checkCancellation()
        val size = file.length()
        val modified = file.lastModified()
        if (!file.isFile || size <= 0L || size > maxBytes) {
            null
        } else {
            val hash = file.inputStream().use { sha256(it, checkCancellation) }
            checkCancellation()
            hash.takeIf { file.isFile && file.length() == size && file.lastModified() == modified }
        }
    } catch (error: CancellationException) {
        throw error
    } catch (_: Exception) {
        null
    }

    fun sha256(input: InputStream, checkCancellation: () -> Unit = {}): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(64 * 1024)
        while (true) {
            checkCancellation()
            val read = input.read(buffer)
            if (read < 0) break
            if (read > 0) digest.update(buffer, 0, read)
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun <T> hasVerifiedCopy(
        candidate: T,
        retainedCandidates: List<T>,
        expectedHash: String?,
        identity: (T) -> String,
        contentHash: (T) -> String?,
    ): Boolean {
        if (expectedHash.isNullOrBlank()) return false
        val candidateId = identity(candidate)
        if (contentHash(candidate) != expectedHash) return false
        return retainedCandidates.any { retained ->
            identity(retained) != candidateId && contentHash(retained) == expectedHash
        }
    }
}
