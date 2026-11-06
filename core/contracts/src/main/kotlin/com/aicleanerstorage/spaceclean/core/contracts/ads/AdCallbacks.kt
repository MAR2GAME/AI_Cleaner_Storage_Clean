package com.aicleanerstorage.spaceclean.core.contracts.ads

import java.util.concurrent.atomic.AtomicBoolean

fun once(block: () -> Unit): () -> Unit {
    val called = AtomicBoolean(false)
    return {
        if (called.compareAndSet(false, true)) {
            block()
        }
    }
}
