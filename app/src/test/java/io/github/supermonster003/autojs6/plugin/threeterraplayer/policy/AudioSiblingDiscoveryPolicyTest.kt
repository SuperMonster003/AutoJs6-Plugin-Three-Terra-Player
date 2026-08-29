package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioSiblingDiscoveryPolicyTest {

    @Test
    fun singleTrackDiscoversNaturalOrderedAudioQueue() {
        val queue = AudioSiblingDiscoveryPolicy.discover(
            selectedDisplayName = "track10.m4a",
            selectedMimeType = "audio/mp4",
            siblings = listOf(
                file("track10.m4a", "audio/mp4"),
                file("track2.flac", "audio/flac"),
                file("track01.mp3", "audio/mpeg"),
            ),
        )

        assertEquals(listOf("track01.mp3", "track2.flac", "track10.m4a"), queue.tracks.map { it.displayName })
        assertEquals(2, queue.startIndex)
        assertEquals("", queue.tracks[queue.startIndex].relativePath)
    }

    @Test
    fun sameNamedSeparatedVideoIsNotAddedToAudioQueue() {
        val queue = AudioSiblingDiscoveryPolicy.discover(
            selectedDisplayName = "【科技杂谈60】USB发展史（1994-2019）.m4a",
            selectedMimeType = "audio/mp4",
            siblings = listOf(
                file("【科技杂谈60】USB发展史（1994-2019）.mp4", "video/mp4"),
                file("【科技杂谈60】USB发展史（1994-2019）.m4a", "audio/mp4"),
                file("沉船求生的唯一方法.m4a", "audio/mp4"),
                file("沉船求生的唯一方法.mp4", "video/mp4"),
            ),
        )

        assertEquals(
            listOf("【科技杂谈60】USB发展史（1994-2019）.m4a", "沉船求生的唯一方法.m4a"),
            queue.tracks.map { it.displayName },
        )
    }

    @Test
    fun unsafeUnreadableAndSymbolicEntriesAreExcluded() {
        val queue = AudioSiblingDiscoveryPolicy.discover(
            selectedDisplayName = "selected.mp3",
            selectedMimeType = "audio/mpeg",
            siblings = listOf(
                file("selected.mp3", "audio/mpeg"),
                file("hidden.flac", "audio/flac").copy(readable = false),
                file("linked.ogg", "audio/ogg").copy(symbolicLink = true),
                file("nested/escape.mp3", "audio/mpeg"),
                file("folder.mp3", "inode/directory").copy(kind = 2),
            ),
        )

        assertEquals(listOf("selected.mp3"), queue.tracks.map { it.displayName })
    }

    @Test
    fun naturalComparatorDefersCaseTieBreakUntilAfterNumericOrdering() {
        assertTrue(AudioSiblingDiscoveryPolicy.compareNaturally("track2.m4a", "Track10.m4a") < 0)
        assertTrue(AudioSiblingDiscoveryPolicy.compareNaturally("Track.m4a", "track.m4a") < 0)
    }

    private fun file(name: String, mimeType: String) = AudioSiblingItem(
        relativePath = name,
        displayName = name,
        kind = 1,
        mimeType = mimeType,
        readable = true,
        symbolicLink = false,
    )
}
