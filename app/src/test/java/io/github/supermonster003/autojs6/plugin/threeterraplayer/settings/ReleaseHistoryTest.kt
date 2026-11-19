package io.github.supermonster003.autojs6.plugin.threeterraplayer.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

class ReleaseHistoryTest {
    private fun first(tag: String) = ReleaseHistoryAssetPolicy.candidatesFor(Locale.forLanguageTag(tag)).first()

    @Test fun mapsChineseLocalesAndFallsBackToEnglish() {
        assertEquals("doc/CHANGELOG-zh-Hant-HK.md", first("zh-HK"))
        assertEquals("doc/CHANGELOG-zh-Hant-HK.md", first("zh-Hant-MO"))
        assertEquals("doc/CHANGELOG-zh-Hant-TW.md", first("zh-TW"))
        assertEquals("doc/CHANGELOG-zh-Hant-TW.md", first("zh-Hant"))
        assertEquals("doc/CHANGELOG-zh-Hans.md", first("zh-Hans-CN"))
        assertEquals("doc/CHANGELOG-zh-Hans.md", first("zh-Hans-HK"))
        assertEquals(listOf("doc/CHANGELOG-ar.md", "doc/CHANGELOG-en.md"), ReleaseHistoryAssetPolicy.candidatesFor(Locale.forLanguageTag("ar-EG")))
        assertEquals(listOf("doc/CHANGELOG-en.md"), ReleaseHistoryAssetPolicy.candidatesFor(Locale.forLanguageTag("de-DE")))
        assertEquals(listOf("doc/CHANGELOG-en.md"), ReleaseHistoryAssetPolicy.candidatesFor(Locale.ENGLISH))
    }

    /** A missing, blank or unparsable localized changelog falls back to English; nothing usable gives the empty state. */
    @Test fun skipsMissingBlankOrUnparsableLocalizedAssetsAndReportsCompleteFailure() {
        val candidates = ReleaseHistoryAssetPolicy.candidatesFor(Locale.FRANCE)
        val english = "## v1.0.0\n\n###### 2026/10/04\n\n* `Feature` English note\n"
        fun load(french: String?) = loadReleaseHistory(candidates) { path ->
            when {
                path.endsWith("-en.md") -> english
                french == null -> throw FileNotFoundException(path)
                else -> french
            }
        }
        assertEquals("English note", load(null).single().items.single().text)
        assertEquals("English note", load(" ").single().items.single().text)
        assertEquals("English note", load("### Historique\n").single().items.single().text)
        assertEquals("Note", load("## v1.0.0\n\n* `Nouveauté` Note\n").single().items.single().text)
        assertTrue(loadReleaseHistory(candidates) { " " }.isEmpty())
        assertTrue(loadReleaseHistory(candidates) { throw FileNotFoundException(it) }.isEmpty())
    }

    /** The bundled changelogs parse into every version they list, the same versions in both languages. */
    @Test fun parsesEveryVersionOfTheBundledChangelogs() {
        val doc = findProjectRoot().resolve("app/src/main/assets/doc")
        val versionLine = Regex("^#{1,2}\\s+v\\S+\\s*$")
        val versions = listOf("CHANGELOG-zh-Hans.md", "CHANGELOG-en.md").map { name ->
            val markdown = String(Files.readAllBytes(doc.resolve(name)), Charsets.UTF_8)
            val expected = markdown.lineSequence().count { versionLine.matches(it.trim()) }
            val entries = ReleaseHistoryParser.parse(markdown)
            assertTrue("$name lists no version", expected > 0)
            assertEquals(name, expected, entries.size)
            assertTrue("$name has a version without items", entries.all { it.items.isNotEmpty() && it.date != null })
            entries.map { it.version }
        }
        assertEquals(versions[0], versions[1])
    }

    private fun findProjectRoot(): Path = generateSequence(Paths.get("").toAbsolutePath()) { it.parent }
        .first { Files.isDirectory(it.resolve("app/src/main/assets/doc")) }
}

class ReleaseHistoryParserTest {
    @Test fun parsesTheGeneratedChangelogDialect() {
        val markdown = """
            ******

            ### Release History

            ******

            ## v2.1.0

            ###### 2026/10/13

            * `Hint` First note
            * `Improved` Second note

            ## v2.0.0

            ###### 2026/10/04

            * `Feature` Third note
            * Untagged note
        """.trimIndent()
        val entries = ReleaseHistoryParser.parse(markdown)
        assertEquals(listOf("v2.1.0", "v2.0.0"), entries.map { it.version })
        assertEquals("2026/10/13", entries[0].date)
        assertEquals(listOf("Hint" to "First note", "Improved" to "Second note"), entries[0].items.map { it.tag to it.text })
        assertEquals(listOf("Feature" to "Third note", null to "Untagged note"), entries[1].items.map { it.tag to it.text })
    }

    @Test fun parsesTheSingleHashHeadingToo() {
        val entries = ReleaseHistoryParser.parse("# v1.0.0\n\n* `Feature` Note\n")
        assertEquals("v1.0.0", entries.single().version)
        assertEquals(1, entries.single().items.size)
    }

    /** The page names each category once and lists its items beneath; the collapsed summary counts them. */
    @Test fun groupsItemsByCategoryInTheOrderTheyFirstAppear() {
        val entry = ReleaseHistoryParser.parse(
            "## v1.2.0\n\n* `Hint` A\n* `Added` B\n* `Added` C\n* `Fixed` D\n* `Added` E\n* Untagged F\n",
        ).single()
        assertEquals(
            listOf("Hint" to listOf("A"), "Added" to listOf("B", "C", "E"), "Fixed" to listOf("D"), null to listOf("Untagged F")),
            entry.groups.map { it.tag to it.items },
        )
        assertEquals(listOf("Hint" to 1, "Added" to 3, "Fixed" to 1), entry.categoryCounts)
    }

    @Test fun ignoresSectionTitlesThatAreNotVersions() {
        assertTrue(ReleaseHistoryParser.parse("### Release History\n\n* `Feature` Orphan\n").isEmpty())
    }
}
