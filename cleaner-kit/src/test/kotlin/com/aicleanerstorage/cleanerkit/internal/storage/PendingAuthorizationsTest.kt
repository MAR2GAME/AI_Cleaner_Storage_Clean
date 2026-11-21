package com.aicleanerstorage.cleanerkit.internal.storage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PendingAuthorizationsTest {
    @Test
    fun retryAndDiscardCannotBeCompletedByAnOldCallback() {
        val pending = PendingAuthorizations<String, Int>()
        val a = pending.register("a", 1)
        val b = pending.register("b", 2)
        val c = pending.register("c", 3)
        pending.markRequested(listOf(a, b, c), "request")
        assertNull(pending.pending(listOf(b)))
        val retry = pending.register("a", 4)
        pending.discard(listOf(c))
        assertEquals(mapOf(b to 2), pending.consume("request"))
        assertEquals(emptyMap<String, Int>(), pending.consume("request"))
        assertEquals(listOf(4), pending.pending(listOf(retry)))
        pending.invalidate { true }
        assertNull(pending.pending(listOf(retry)))
    }
}
