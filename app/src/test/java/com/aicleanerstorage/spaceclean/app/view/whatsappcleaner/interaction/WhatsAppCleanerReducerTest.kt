package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.interaction

import org.junit.Assert.assertEquals
import org.junit.Test

class WhatsAppCleanerReducerTest {
    @Test
    fun `cleaning started enters cleaning with the expected summary`() {
        val started =
            WhatsAppCleanerReducer.cleaningStarted(
                WhatsAppCleanerUiState(phase = WhatsAppCleanerPhase.ScanResult),
                expectedBytes = 300L,
                count = 3,
            )

        assertEquals(WhatsAppCleanerPhase.Cleaning, started.phase)
        assertEquals(300L, started.selectedBytes)
        assertEquals(3, started.selectedCount)
    }

    @Test
    fun `result only moves from complete animation`() {
        assertEquals(
            WhatsAppCleanerPhase.Result,
            WhatsAppCleanerReducer
                .resultRequested(WhatsAppCleanerUiState(phase = WhatsAppCleanerPhase.CompleteAnimation))
                .phase,
        )

        val scanResult = WhatsAppCleanerUiState(phase = WhatsAppCleanerPhase.ScanResult)
        assertEquals(scanResult, WhatsAppCleanerReducer.resultRequested(scanResult))
    }

    @Test
    fun `cancelled cleaning returns to the scan result`() {
        assertEquals(
            WhatsAppCleanerPhase.ScanResult,
            WhatsAppCleanerReducer
                .cleaningCancelled(WhatsAppCleanerUiState(phase = WhatsAppCleanerPhase.Cleaning))
                .phase,
        )

        val idle = WhatsAppCleanerUiState(phase = WhatsAppCleanerPhase.ScanResult)
        assertEquals(idle, WhatsAppCleanerReducer.cleaningCancelled(idle))
    }
}
