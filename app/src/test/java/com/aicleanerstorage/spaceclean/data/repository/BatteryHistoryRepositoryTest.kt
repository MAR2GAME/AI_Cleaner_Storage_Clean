package com.aicleanerstorage.spaceclean.data.repository

import com.aicleanerstorage.spaceclean.domain.model.battery.BatteryHistorySampleModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.DataOutputStream

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
        val repository = DefaultBatteryHistoryRepository(file)
        assertEquals(listOf(previous), repository.loadRecent(now))
        repository.append(next, now)
        assertEquals(listOf(previous, next), repository.loadRecent(now))
        assertEquals(repository.loadRecent(now), DefaultBatteryHistoryRepository(file).loadRecent(now))
    }
}
