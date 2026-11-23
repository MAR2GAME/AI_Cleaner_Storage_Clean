package com.aicleanerstorage.spaceclean.data.local.storage

import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.util.concurrent.CancellationException

internal object FileContentDigest {
    /** 头部哈希窗口；头部不同的文件内容必然不同，整文件哈希因此可以跳过。 */
    const val PREFIX_HASH_BYTES = 64 * 1024

    /**
     * 头部哈希结果。
     *
     * @property hash 已读字节的 SHA-256。
     * @property isComplete 读取在窗口内已到文件末尾，[hash] 就是全量内容哈希。
     */
    data class PrefixDigest(
        val hash: String,
        val isComplete: Boolean,
    )

    fun hashFile(
        file: File,
        maxBytes: Long = Long.MAX_VALUE,
        checkCancellation: () -> Unit = {},
    ): String? =
        try {
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

    /**
     * 读取文件头部并算哈希。
     *
     * 只读 [PREFIX_HASH_BYTES] 以内的内容，用于在大文件上先排除内容不同的候选；
     * 头部读不满窗口时 [PrefixDigest.isComplete] 为 true，结果可直接当全量哈希复用。
     *
     * @param file 待读取文件。
     * @param checkCancellation 每次读块前的取消检查。
     * @return 头部哈希；文件不存在、为空或读取失败时返回 null。
     */
    fun hashFilePrefix(
        file: File,
        checkCancellation: () -> Unit = {},
    ): PrefixDigest? =
        try {
            checkCancellation()
            if (!file.isFile || file.length() <= 0L) {
                null
            } else {
                file.inputStream().use { digestPrefix(it, PREFIX_HASH_BYTES, checkCancellation) }
            }
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            null
        }

    fun sha256(
        input: InputStream,
        checkCancellation: () -> Unit = {},
    ): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(BUFFER_BYTES)
        while (true) {
            checkCancellation()
            val read = input.read(buffer)
            if (read < 0) break
            if (read > 0) digest.update(buffer, 0, read)
        }
        return digest.digest().toHex()
    }

    /**
     * 读取不超过 [maxBytes] 的内容并算哈希。
     *
     * @param input 输入流，由调用方负责关闭。
     * @param maxBytes 头部窗口大小。
     * @param checkCancellation 每次读块前的取消检查。
     * @return 头部哈希；[PrefixDigest.isComplete] 表示窗口内已读完。
     */
    fun digestPrefix(
        input: InputStream,
        maxBytes: Int,
        checkCancellation: () -> Unit = {},
    ): PrefixDigest {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(minOf(maxBytes, BUFFER_BYTES).coerceAtLeast(1))
        var remaining = maxBytes
        while (remaining > 0) {
            checkCancellation()
            val read = input.read(buffer, 0, minOf(buffer.size, remaining))
            if (read < 0) return PrefixDigest(digest.digest().toHex(), isComplete = true)
            digest.update(buffer, 0, read)
            remaining -= read
        }
        return PrefixDigest(digest.digest().toHex(), isComplete = false)
    }

    private fun ByteArray.toHex(): String {
        val chars = CharArray(size * 2)
        for (index in indices) {
            val value = this[index].toInt() and 0xff
            chars[index * 2] = HEX_DIGITS[value ushr 4]
            chars[index * 2 + 1] = HEX_DIGITS[value and 0x0f]
        }
        return String(chars)
    }

    private const val BUFFER_BYTES = 64 * 1024
    private val HEX_DIGITS = "0123456789abcdef".toCharArray()
}
