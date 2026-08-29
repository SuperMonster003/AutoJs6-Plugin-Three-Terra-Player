package io.github.supermonster003.autojs6.plugin.threeterraplayer

import java.nio.file.Files
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

/** Guards against path data that AAPT accepts but Android rejects when loading the drawable. */
class VectorDrawablePathDataTest {

    @Test
    fun rejectsTheIncompleteArcGroupThatCrashedToolbarInflation() {
        val failures = validatePathData("M13.5,21a1.5,1.5 0,0 0,0 0,-3")

        assertTrue(
            failures.joinToString(separator = "\n"),
            failures.any { failure -> failure.contains("8 parameters") },
        )
    }

    @Test
    fun everyVectorPathContainsCompleteCommandParameterGroups() {
        val resourceRoot = listOf(
            Path.of("src", "main", "res"),
            Path.of("app", "src", "main", "res"),
        ).firstOrNull { path -> Files.isDirectory(path) }
        requireNotNull(resourceRoot) { "Unable to locate the main Android resource directory" }

        val documentBuilder = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }.newDocumentBuilder()
        val failures = mutableListOf<String>()

        Files.walk(resourceRoot).use { resources ->
            resources
                .filter { file ->
                    Files.isRegularFile(file) &&
                        file.fileName.toString().endsWith(".xml") &&
                        file.parent.fileName.toString().startsWith("drawable")
                }
                .forEach { file ->
                    val document = documentBuilder.parse(file.toFile())
                    if (document.documentElement.tagName != "vector") return@forEach
                    val paths = document.getElementsByTagName("path")
                    for (index in 0 until paths.length) {
                        val element = paths.item(index) as Element
                        val pathData = element.getAttributeNS(ANDROID_NAMESPACE, "pathData")
                        validatePathData(pathData).forEach { failure ->
                            failures += "${resourceRoot.relativize(file)} path[$index]: $failure"
                        }
                    }
                }
        }

        assertTrue(failures.joinToString(separator = "\n"), failures.isEmpty())
    }

    private fun validatePathData(pathData: String): List<String> {
        if (pathData.isBlank()) return listOf("pathData is empty")
        val commands = commandPattern.findAll(pathData).toList()
        if (commands.isEmpty()) return listOf("pathData contains no commands")
        val failures = mutableListOf<String>()
        val prefix = pathData.substring(0, commands.first().range.first)
        if (prefix.isNotBlank()) failures += "unexpected data before the first command: '$prefix'"

        commands.forEachIndexed { index, match ->
            val command = match.value.single()
            val expectedArity = commandArities.getValue(command.uppercaseChar())
            val parametersStart = match.range.last + 1
            val parametersEnd = commands.getOrNull(index + 1)?.range?.first ?: pathData.length
            val parameters = pathData.substring(parametersStart, parametersEnd)
            val numberMatches = numberPattern.findAll(parameters).toList()
            val unknownData = numberPattern.replace(parameters, "")
                .replace(",", "")
                .filterNot(Char::isWhitespace)
            if (unknownData.isNotEmpty()) {
                failures += "command $command contains unexpected data '$unknownData'"
                return@forEachIndexed
            }
            if (expectedArity == 0) {
                if (numberMatches.isNotEmpty()) {
                    failures += "command $command must not contain parameters"
                }
                return@forEachIndexed
            }
            if (numberMatches.isEmpty() || numberMatches.size % expectedArity != 0) {
                failures +=
                    "command $command has ${numberMatches.size} parameters; " +
                    "expected a non-empty multiple of $expectedArity"
                return@forEachIndexed
            }
            if (command.equals('A', ignoreCase = true)) {
                val numbers = numberMatches.map { number -> number.value.toDouble() }
                numbers.chunked(expectedArity).forEachIndexed { groupIndex, group ->
                    if (group[3] !in arcFlags || group[4] !in arcFlags) {
                        failures +=
                            "command $command group $groupIndex has non-binary arc flags " +
                            "${group[3]}, ${group[4]}"
                    }
                }
            }
        }
        return failures
    }

    private companion object {
        const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"

        val commandPattern = Regex("[AaCcHhLlMmQqSsTtVvZz]")
        val numberPattern = Regex("[-+]?(?:(?:\\d+\\.\\d*)|(?:\\.\\d+)|(?:\\d+))(?:[eE][-+]?\\d+)?")
        val arcFlags = setOf(0.0, 1.0)
        val commandArities = mapOf(
            'A' to 7,
            'C' to 6,
            'H' to 1,
            'L' to 2,
            'M' to 2,
            'Q' to 4,
            'S' to 4,
            'T' to 2,
            'V' to 1,
            'Z' to 0,
        )
    }
}
