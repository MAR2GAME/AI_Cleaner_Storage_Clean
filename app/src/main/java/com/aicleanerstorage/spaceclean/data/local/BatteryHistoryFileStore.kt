package com.aicleanerstorage.spaceclean.data.local

import com.aicleanerstorage.spaceclean.domain.model.BatteryHistorySampleModel
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.File
import java.io.FileOutputStream

internal class BatteryHistoryFileStore(private val historyFile: File) {
    fun readAllRecords(): List<BatteryHistorySampleModel> {
        if (!historyFile.exists()) return emptyList()
        val result = mutableListOf<BatteryHistorySampleModel>()
        try {
            DataInputStream(BufferedInputStream(historyFile.inputStream())).use { input ->
                while (true) {
                    val timestamp = input.readLong()
                    val rawCurrent = input.readFloat()
                    val temperature = input.readFloat()
                    result +=
                        BatteryHistorySampleModel(
                            timestampMillis = timestamp,
                            currentMa = rawCurrent.takeIf { it.isFinite() },
                            temperatureC = temperature,
                        )
                }
            }
        } catch (_: EOFException) {
            return result
        } catch (_: Exception) {
            return result
        }
    }

    fun appendRecord(sample: BatteryHistorySampleModel) {
        runCatching {
            historyFile.parentFile?.mkdirs()
            DataOutputStream(BufferedOutputStream(FileOutputStream(historyFile, true))).use { output ->
                output.writeLong(sample.timestampMillis)
                output.writeFloat(sample.currentMa ?: Float.NaN)
                output.writeFloat(sample.temperatureC)
            }
        }
    }

    fun rewrite(samples: List<BatteryHistorySampleModel>) {
        runCatching {
            historyFile.parentFile?.mkdirs()
            val tmpFile = File(historyFile.parentFile, "${historyFile.name}.tmp")
            DataOutputStream(BufferedOutputStream(FileOutputStream(tmpFile, false))).use { output ->
                samples.forEach { sample ->
                    output.writeLong(sample.timestampMillis)
                    output.writeFloat(sample.currentMa ?: Float.NaN)
                    output.writeFloat(sample.temperatureC)
                }
            }
            if (historyFile.exists()) {
                historyFile.delete()
            }
            if (!tmpFile.renameTo(historyFile)) {
                tmpFile.copyTo(historyFile, overwrite = true)
                tmpFile.delete()
            }
        }
    }

}
