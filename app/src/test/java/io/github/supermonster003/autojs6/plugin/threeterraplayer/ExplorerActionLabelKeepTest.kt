package io.github.supermonster003.autojs6.plugin.threeterraplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * The host resolves the Explorer Action label of every action by resource name, so release resource shrinking keeps
 * no label unless a keep list names it (the menus then showed the English fallback in every other language). Every
 * label resource named in the catalog sources must be kept and translated.
 */
class ExplorerActionLabelKeepTest {

    @Test
    fun `every catalog label resource is kept from resource shrinking`() {
        val names = catalogLabelResourceNames()
        assertEquals(listOf("action_play_audio", "action_play_audio_selection"), names.sorted())
        val kept = keptResources()
        names.forEach { name -> assertTrue("@string/$name is not kept", "@string/$name" in kept) }
    }

    @Test
    fun `every catalog label resource is translated`() {
        val res = projectRoot().resolve("app/src/main/res")
        catalogLabelResourceNames().forEach { name ->
            VALUES.forEach { dir ->
                val found = Files.list(res.resolve(dir)).use { files ->
                    files.anyMatch { file -> read(file).contains("<string name=\"$name\"") }
                }
                assertTrue("$dir has no $name", found)
            }
        }
    }

    private fun catalogLabelResourceNames(): Set<String> {
        val pattern = Regex("""(?:const val (\w*)LABEL_RESOURCE_NAME\s*=|labelResourceName\s*=)\s*"([a-z0-9_]+)"""")
        val names = linkedSetOf<String>()
        Files.walk(projectRoot().resolve("app/src/main/java")).use { paths ->
            paths.filter { it.toString().endsWith(".kt") }.forEach { file ->
                pattern.findAll(read(file)).forEach { match ->
                    // The first-level menu name is app_name, which the manifest references.
                    if (match.groupValues[1] != "MENU_GROUP_") names += match.groupValues[2]
                }
            }
        }
        return names
    }

    private fun keptResources(): Set<String> {
        val raw = projectRoot().resolve("app/src/main/res/raw")
        return Files.list(raw).use { files ->
            files.filter { it.fileName.toString().startsWith("keep") && it.toString().endsWith(".xml") }
                .toList()
                .flatMap { file ->
                    Regex("""tools:keep="([^"]*)"""").findAll(read(file)).flatMap { match ->
                        match.groupValues[1].split(',').map(String::trim).asSequence()
                    }.toList()
                }
                .toSet()
        }
    }

    private fun read(path: Path): String = String(Files.readAllBytes(path), Charsets.UTF_8)

    private fun projectRoot(): Path {
        var path: Path? = Paths.get("").toAbsolutePath()
        while (path != null) {
            if (Files.isRegularFile(path.resolve("settings.gradle.kts"))) return path
            path = path.parent
        }
        error("Project root not found")
    }

    private companion object {
        val VALUES = listOf(
            "values", "values-en", "values-zh", "values-zh-rHK", "values-zh-rTW", "values-ja",
            "values-ko", "values-fr", "values-es", "values-ru", "values-ar",
        )
    }
}
