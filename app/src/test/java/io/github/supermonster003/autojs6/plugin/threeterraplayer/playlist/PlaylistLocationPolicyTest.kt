package io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist

import org.junit.Assert.*
import org.junit.Test

class PlaylistLocationPolicyTest {
    @Test fun resolvesRelativePathsAndWindowsSeparatorsWithinTheGrantedRoot() {
        assertEquals(listOf("track.mp3"), PlaylistLocationPolicy.relativeSegments("./track.mp3"))
        assertEquals(listOf("album", "track.mp3"), PlaylistLocationPolicy.relativeSegments("album\\track.mp3"))
        assertEquals(listOf("track.mp3"), PlaylistLocationPolicy.relativeSegments("album/../track.mp3"))
    }

    @Test fun absolutePathsRequireTheExactHostParent() {
        assertEquals(listOf("a b.mp3"), PlaylistLocationPolicy.relativeSegments("file:///music/a%20b.mp3", "/music"))
        assertEquals(listOf("a.mp3"), PlaylistLocationPolicy.relativeSegments("/music/a.mp3", "/music"))
        assertNull(PlaylistLocationPolicy.relativeSegments("/music2/a.mp3", "/music"))
        assertNull(PlaylistLocationPolicy.relativeSegments("file:///music/a.mp3"))
        assertNull(PlaylistLocationPolicy.relativeSegments("file://remote/music/a.mp3", "/music"))
    }

    @Test fun rejectsTraversalRemoteSchemesAndMalformedPaths() {
        listOf("../secret.mp3", "folder/../../secret.mp3", "https://example.com/a.mp3", "http://example.com/a.mp3", "C:\\music\\a.mp3", "//server/a.mp3", "a//b.mp3", "a\u0000.mp3", "\u202Ea.mp3").forEach {
            assertNull(it, PlaylistLocationPolicy.relativeSegments(it))
        }
    }

    @Test fun percentDecodingPreservesLiteralPlusAndCannotEscapeTheRoot() {
        assertEquals("a+b c.mp3", PlaylistLocationPolicy.decodedLocation("a+b%20c.mp3"))
        assertNull(PlaylistLocationPolicy.relativeSegments(requireNotNull(PlaylistLocationPolicy.decodedLocation("%2e%2e/secret.mp3"))))
        assertNull(PlaylistLocationPolicy.decodedLocation("bad%2"))
    }

    @Test fun contentReferencesMustBePlainContentUris() {
        assertEquals("content://test/document/id", PlaylistLocationPolicy.contentUri("content://test/document/id"))
        listOf("file:///a", "content://user@test/path", "content://test:80/path", "content://test/path?q=x", "content://test/path#x").forEach {
            assertNull(it, PlaylistLocationPolicy.contentUri(it))
        }
    }

    @Test fun queuePreservesDuplicatesAndSelectsFirstAccessibleAlternative() {
        val loaded = PlaylistLoader.resolve(listOf(
            PlaylistEntry(listOf("missing", "a"), "First"), PlaylistEntry(listOf("a"), "Again"), PlaylistEntry(listOf("missing")),
        )) { location, title -> if (location == "a") PlaylistMedia(null, "a", requireNotNull(title), "audio/mpeg") else null }
        assertEquals(listOf("First", "Again"), loaded.items.map { it.displayName })
        assertEquals(listOf("a", "a"), loaded.items.map { it.relativePath })
        assertEquals(1, loaded.skipped)
    }

    @Test fun queueLimitCountsOnlyPlayableItemsAndReportsOverflow() {
        val entries = List(130) { PlaylistEntry(listOf("a")) }
        var lookups = 0
        val loaded = PlaylistLoader.resolve(entries) { _, _ -> lookups++; PlaylistMedia(null, "a", "A", "audio/mpeg") }
        assertEquals(128, loaded.items.size)
        assertEquals(2, loaded.skipped)
        assertEquals(128, lookups)
    }
}
