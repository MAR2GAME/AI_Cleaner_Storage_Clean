package com.aicleanerstorage.spaceclean.feature.junkclean.ui

import androidx.activity.result.IntentSenderRequest
import kotlinx.coroutines.CancellationException
import org.junit.Assert.*
import org.junit.Test

class JunkDeleteAuthorizationLauncherTest {
    private val uris = listOf("content://media/external/file/3")

    @Test
    fun `supported request is created and launched without reporting rejection`() {
        val request = request()
        var creations = 0
        var launches = 0
        launchJunkDeleteAuthorization(
            uris = uris,
            sdkInt = 30,
            createRequest = { assertEquals(uris, it); creations++; request },
            launch = { assertSame(request, it); launches++ },
            onUnavailable = { fail("Unexpected refusal") },
        )
        assertEquals(1, creations)
        assertEquals(1, launches)
    }

    @Test
    fun `unsupported platform and empty uris reject once without creating request`() {
        for ((sdk, values) in listOf(29 to uris, 30 to emptyList<String>())) {
            var refusals = 0
            var creations = 0
            launchJunkDeleteAuthorization(
                uris = values,
                sdkInt = sdk,
                createRequest = { creations++; null },
                launch = { fail("Must not launch") },
                onUnavailable = { refusals++ },
            )
            assertEquals(1, refusals)
            assertEquals(0, creations)
        }
    }

    @Test
    fun `creation returning null or throwing rejects once without launching`() {
        for (throws in listOf(false, true)) {
            var creations = 0
            var refusals = 0
            launchJunkDeleteAuthorization(
                uris = uris,
                sdkInt = 30,
                createRequest = {
                    creations++
                    if (throws) throw IllegalArgumentException("Invalid URI")
                    null
                },
                launch = { fail("Must not launch") },
                onUnavailable = { refusals++ },
            )
            assertEquals(1, creations)
            assertEquals(1, refusals)
        }
    }

    @Test
    fun `launch failure rejects once`() {
        var launches = 0
        var refusals = 0
        launchJunkDeleteAuthorization(
            uris = uris,
            sdkInt = 30,
            createRequest = { request() },
            launch = { launches++; throw IllegalStateException("Launcher unavailable") },
            onUnavailable = { refusals++ },
        )
        assertEquals(1, launches)
        assertEquals(1, refusals)
    }

    @Test
    fun `creation and launch cancellation propagate without refusal`() {
        for (duringCreation in listOf(true, false)) {
            val cancellation = CancellationException("Page disposed")
            var refusals = 0
            try {
                launchJunkDeleteAuthorization(
                    uris = uris,
                    sdkInt = 30,
                    createRequest = { if (duringCreation) throw cancellation else request() },
                    launch = { throw cancellation },
                    onUnavailable = { refusals++ },
                )
                fail("Cancellation must propagate")
            } catch (error: CancellationException) {
                assertSame(cancellation, error)
            }
            assertEquals(0, refusals)
        }
    }

    @Test
    fun `failure callback exceptions are not caught and reported a second time`() {
        var refusals = 0
        val failure = IllegalStateException("Callback failed")
        try {
            launchJunkDeleteAuthorization(
                uris = uris,
                sdkInt = 29,
                createRequest = { error("Must not create") },
                launch = { fail("Must not launch") },
                onUnavailable = { refusals++; throw failure },
            )
            fail("Callback failure must propagate")
        } catch (error: IllegalStateException) {
            assertSame(failure, error)
        }
        assertEquals(1, refusals)
    }

    private fun request(): IntentSenderRequest {
        val field = sun.misc.Unsafe::class.java.getDeclaredField("theUnsafe").apply { isAccessible = true }
        return (field.get(null) as sun.misc.Unsafe).allocateInstance(IntentSenderRequest::class.java) as IntentSenderRequest
    }
}
