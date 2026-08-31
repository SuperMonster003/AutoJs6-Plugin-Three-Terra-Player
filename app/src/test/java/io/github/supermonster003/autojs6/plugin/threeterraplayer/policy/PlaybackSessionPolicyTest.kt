package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlaybackSessionPolicyTest {

    private val now = 1_788_134_400_000L
    private val first = PlaybackSessionTrackRecord(
        uriString = "content://example.documents/document/audio%3Afirst.mp3",
        mimeType = "audio/mpeg",
        displayName = "First.mp3",
    )
    private val second = PlaybackSessionTrackRecord(
        uriString = "content://example.documents/document/audio%3Asecond.flac",
        mimeType = "audio/flac; charset=binary",
        displayName = "Second.flac",
    )

    @Test
    fun acceptsAndNormalizesFreshStandaloneQueues() {
        val record = validRecord()

        val validated = PlaybackSessionPolicy.validate(record, now)

        assertEquals(record.copy(tracks = listOf(first, second.copy(mimeType = "audio/flac"))), validated)
    }

    @Test
    fun rejectsExpiredOrFutureRecords() {
        val record = validRecord()

        assertEquals(
            record.copy(tracks = listOf(first, second.copy(mimeType = "audio/flac"))),
            PlaybackSessionPolicy.validate(record, now + PlaybackSessionPolicy.MAX_AGE_MS),
        )
        assertNull(PlaybackSessionPolicy.validate(record, now + PlaybackSessionPolicy.MAX_AGE_MS + 1L))
        assertNull(PlaybackSessionPolicy.validate(record.copy(savedAtMs = now + 1L), now))
    }

    @Test
    fun rejectsUnsafeOrNonRestorableTracks() {
        val record = validRecord()

        assertNull(
            PlaybackSessionPolicy.validate(
                record.copy(tracks = listOf(first.copy(uriString = "file:///storage/emulated/0/a.mp3"))),
                now,
            ),
        )
        assertNull(
            PlaybackSessionPolicy.validate(
                record.copy(tracks = listOf(first.copy(mimeType = "video/mp4"))),
                now,
            ),
        )
        assertNull(
            PlaybackSessionPolicy.validate(
                record.copy(tracks = listOf(first.copy(displayName = "\u202Ehidden.mp3"))),
                now,
            ),
        )
        assertNull(PlaybackSessionPolicy.validate(record.copy(tracks = listOf(first, first)), now))
    }

    @Test
    fun rejectsInvalidPlaybackState() {
        val record = validRecord()

        assertNull(PlaybackSessionPolicy.validate(record.copy(currentIndex = 2), now))
        assertNull(PlaybackSessionPolicy.validate(record.copy(positionMs = -1L), now))
        assertNull(PlaybackSessionPolicy.validate(record.copy(repeatMode = 3), now))
        assertNull(PlaybackSessionPolicy.validate(record.copy(playbackSpeed = 0.49f), now))
        assertNull(PlaybackSessionPolicy.validate(record.copy(playbackSpeed = Float.NaN), now))
    }

    private fun validRecord() = PlaybackSessionRecord(
        tracks = listOf(first, second),
        currentIndex = 1,
        positionMs = 42_000L,
        repeatMode = PlaybackSessionPolicy.REPEAT_MODE_ALL,
        shuffleEnabled = true,
        playbackSpeed = 1.25f,
        savedAtMs = now,
    )
}
