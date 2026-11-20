package com.aicleanerstorage.spaceclean.base.mvi

import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MviEffectContractTest {
    @Test
    fun `effects queued before collection retain order beyond buffered capacity`() = runTest {
        val subject = Subject()
        val expected = (0 until 1024).toList()
        expected.forEach(subject::dispatch)
        assertEquals(expected, subject.event.take(expected.size).toList())
        subject.dispatch(1024)
        assertEquals(listOf(1024), subject.event.take(1).toList())
        ViewModelStore().apply { put("subject", subject); clear() }
    }

    @Test
    fun `clearing drops pending effects and rejects subsequent sends`() = runTest {
        val subject = Subject()
        repeat(128) { subject.dispatch(it) }
        ViewModelStore().apply { put("subject", subject); clear() }
        subject.dispatch(999)
        val delivered = mutableListOf<Int>()
        try {
            subject.event.toList(delivered)
        } catch (_: CancellationException) {
            // A cancelled channel terminates collection.
        }
        assertTrue(delivered.isEmpty())
    }

    private class Subject : MviViewModel<Unit, Int, Int>(Unit) {
        override fun onAction(intent: Int) = sendEvent(intent)
    }
}
