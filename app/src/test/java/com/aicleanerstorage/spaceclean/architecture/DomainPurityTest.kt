package com.aicleanerstorage.spaceclean.architecture

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class DomainPurityTest {
    private val root =
        sequenceOf(File("."), File(".."))
            .map(File::getCanonicalFile)
            .first { File(it, "settings.gradle.kts").exists() }

    @Test
    fun `domain does not import platform file or clock APIs`() {
        val forbidden =
            listOf(
                "java.io.File",
                "System.currentTimeMillis",
                "android.",
                "androidx.",
                "Dispatchers",
            )
        val violations =
            File(root, "app/src/main/java/com/aicleanerstorage/spaceclean/domain")
                .walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .flatMap { file ->
                    val text = file.readText()
                    forbidden
                        .filter { marker -> text.contains(marker) }
                        .map { marker -> "${file.relativeTo(root).path}: $marker" }
                }.toList()
        assertEquals(emptyList<String>(), violations)
    }
}
