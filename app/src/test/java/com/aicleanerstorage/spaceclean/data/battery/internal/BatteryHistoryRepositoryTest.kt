package com.aicleanerstorage.spaceclean.data.battery.internal

import com.aicleanerstorage.spaceclean.data.battery.model.BatteryHistorySampleModel
import java.io.DataOutputStream
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class BatteryHistoryRepositoryTest {
    @get:Rule val folder = TemporaryFolder()

    @Test
    fun existingFileLoadsAndNewSamplesSurviveReopen() {
        val file = folder.newFile("battery_history.bin")
        val now = 2_000_000L
        DataOutputStream(file.outputStream()).use { output ->
            output.writeLong(now - 1000)
            output.writeFloat(Float.NaN)
            output.writeFloat(28f)
        }
        val previous = BatteryHistorySampleModel(now - 1000, null, 28f)
        val next = BatteryHistorySampleModel(now, 100f, 30f)
        val repository = BatteryHistoryRepositoryImpl(file)
        assertEquals(listOf(previous), repository.loadRecent(now))
        repository.append(next, now)
        assertEquals(listOf(previous, next), repository.samples.value)
        assertEquals(repository.samples.value, BatteryHistoryRepositoryImpl(file).loadRecent(now))
    }
}
