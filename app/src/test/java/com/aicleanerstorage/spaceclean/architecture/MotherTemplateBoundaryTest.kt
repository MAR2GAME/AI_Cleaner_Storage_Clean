package com.aicleanerstorage.spaceclean.architecture

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MotherTemplateBoundaryTest {
    private val root =
        sequenceOf(File("."), File(".."))
            .map(File::getCanonicalFile)
            .first { File(it, "settings.gradle.kts").exists() }

    private fun sources(): Sequence<File> = File(root, "app/src/main/java").walkTopDown().filter { it.extension == "kt" }

    @Test
    fun `manifest component classes exist`() {
        val classes =
            sources()
                .flatMap { file ->
                    val text = file.readText()
                    val pkg =
                        Regex("^package ([\\w.]+)", RegexOption.MULTILINE)
                            .find(text)
                            ?.groupValues
                            ?.get(1) ?: return@flatMap emptySequence()
                    Regex("^(?:(?:public|internal|open)\\s+)*(?:class|object)\\s+(\\w+)", RegexOption.MULTILINE)
                        .findAll(text)
                        .map { "$pkg.${it.groupValues[1]}" }
                }.toSet()
        val declared =
            Regex("android:name=\"(com\\.aicleanerstorage\\.spaceclean[^\"]+)\"")
                .findAll(File(root, "app/src/main/AndroidManifest.xml").readText())
                .map { it.groupValues[1] }
                .toList()
        assertTrue(declared.isNotEmpty())
        assertEquals(emptyList<String>(), declared.filterNot(classes::contains))
    }

    @Test
    fun `data and domain never import app implementations`() {
        val violations =
            sources()
                .filter { "/data/" in it.invariantSeparatorsPath || "/domain/" in it.invariantSeparatorsPath }
                .filter {
                    Regex("(?m)^import com\\.aicleanerstorage\\.spaceclean\\.app\\.").containsMatchIn(it.readText())
                }.map { it.relativeTo(root).path }
                .toList()
        assertEquals(emptyList<String>(), violations)
    }

    @Test
    fun `view never imports data implementations`() {
        val violations =
            sources()
                .filter { "/app/view/" in it.invariantSeparatorsPath }
                .filter {
                    Regex("(?m)^import com\\.aicleanerstorage\\.spaceclean\\.data\\.").containsMatchIn(it.readText())
                }.map { it.relativeTo(root).path }
                .toList()
        assertEquals(emptyList<String>(), violations)
    }

    @Test
    fun `screens do not hold view models repositories or navigation implementations`() {
        val violations =
            sources()
                .filter { it.name.endsWith("Screen.kt") || it.name.endsWith("Screens.kt") }
                .filter {
                    Regex("\\b\\w*(ViewModel|Repository|Navigator)\\b|koinViewModel|rememberLauncherForActivityResult")
                        .containsMatchIn(it.readText())
                }.map { it.relativeTo(root).path }
                .toList()
        assertEquals(emptyList<String>(), violations)
    }

    @Test
    fun `every feature view model uses the single MVI state owner`() {
        val viewModels =
            sources()
                .filter { "/app/view/" in it.invariantSeparatorsPath && it.name.endsWith("ViewModel.kt") }
                .toList()
        assertTrue(viewModels.isNotEmpty())
        viewModels.forEach {
            val code = it.readText()
            assertTrue(it.path, "MviViewModel<" in code)
            assertFalse(it.path, Regex("(MutableStateFlow|MutableSharedFlow)\\s*[<(]").containsMatchIn(code))
        }
    }
}
