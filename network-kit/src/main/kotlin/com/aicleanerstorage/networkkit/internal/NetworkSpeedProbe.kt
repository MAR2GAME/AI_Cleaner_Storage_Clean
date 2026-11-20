package com.aicleanerstorage.networkkit.internal

import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object NetworkSpeedProbe {
    suspend fun measureLatency(url: URL): Long? =
        withContext(Dispatchers.IO) {
            runCatching {
                val start = System.nanoTime()
                val connection =
                    url.openConnection() as HttpURLConnection
                connection.connectTimeout = 2000
                connection.readTimeout = 2000
                connection.requestMethod = "GET"
                connection.inputStream.close()
                max(1, (System.nanoTime() - start) / 1_000_000L)
            }.getOrNull()
        }

    suspend fun measureDownloadMbps(url: URL): Double? =
        withContext(Dispatchers.IO) {
            runCatching {
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 7000
                val start = System.nanoTime()
                var total = 0L
                connection.inputStream.use { input ->
                    val buffer = ByteArray(32 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read <= 0) break
                        total += read
                    }
                }
                val secs = (System.nanoTime() - start) / 1_000_000_000.0
                if (total > 0L && secs > 0.0) (total * 8.0 / secs) / 1_000_000.0 else null
            }.getOrNull()
        }

    suspend fun measureUploadMbps(url: URL): Double? =
        withContext(Dispatchers.IO) {
            runCatching {
                val payload = ByteArray(256 * 1024) { (it % 251).toByte() }
                val connection =
                    url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 7000
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/octet-stream")
                val start = System.nanoTime()
                connection.outputStream.use { it.write(payload) }
                connection.inputStream.close()
                val secs = (System.nanoTime() - start) / 1_000_000_000.0
                if (secs > 0.0) (payload.size * 8.0 / secs) / 1_000_000.0 else null
            }.getOrNull()
        }
}
