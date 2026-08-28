package io.github.supermonster003.autojs6.plugin.audioplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PositionMemoryPolicyTest {

    private val now = 1_787_760_000_000L

    @Test
    fun derivesStableLowercaseHexKeysWithoutExposingUris() {
        val uri = "content://com.example.provider/audio/track.mp3"
        val key = PositionMemoryPolicy.entryKey(uri)

        assertEquals(64, key.length)
        assertTrue(key.all { it in "0123456789abcdef" })
        assertEquals(key, PositionMemoryPolicy.entryKey(uri))
        assertFalse(key == PositionMemoryPolicy.entryKey(uri + "x"))
        assertFalse(key.contains("content"))
    }

    @Test
    fun persistsOnlyMeaningfulMidPlaybackPositions() {
        assertFalse(PositionMemoryPolicy.shouldPersist(0L, 300_000L))
        assertFalse(
            PositionMemoryPolicy.shouldPersist(
                PositionMemoryPolicy.MIN_PERSIST_POSITION_MS - 1,
                300_000L,
            ),
        )
        assertTrue(
            PositionMemoryPolicy.shouldPersist(
                PositionMemoryPolicy.MIN_PERSIST_POSITION_MS,
                300_000L,
            ),
        )
        assertTrue(PositionMemoryPolicy.shouldPersist(150_000L, 300_000L))
        assertFalse(
            PositionMemoryPolicy.shouldPersist(
                300_000L - PositionMemoryPolicy.END_MARGIN_MS + 1,
                300_000L,
            ),
        )
        assertTrue(
            PositionMemoryPolicy.shouldPersist(
                300_000L - PositionMemoryPolicy.END_MARGIN_MS,
                300_000L,
            ),
        )
    }

    @Test
    fun persistsMidPositionsWhenDurationIsUnknown() {
        assertTrue(PositionMemoryPolicy.shouldPersist(60_000L, 0L))
        assertFalse(PositionMemoryPolicy.shouldPersist(1_000L, 0L))
    }

    @Test
    fun resumesOnlyFreshTrustworthyRecords() {
        val key = PositionMemoryPolicy.entryKey("content://example/track.mp3")
        val record = PositionRecord(key, positionMs = 90_000L, durationMs = 300_000L, savedAtMs = now)

        assertNull(PositionMemoryPolicy.resumePositionMs(null, key, now))
        assertEquals(90_000L, PositionMemoryPolicy.resumePositionMs(record, key, now))
        assertNull(PositionMemoryPolicy.resumePositionMs(record, PositionMemoryPolicy.entryKey("content://example/other.mp3"), now))
        assertEquals(
            90_000L,
            PositionMemoryPolicy.resumePositionMs(
                record,
                key,
                now + PositionMemoryPolicy.MAX_AGE_MS,
            ),
        )
        assertNull(
            PositionMemoryPolicy.resumePositionMs(
                record,
                key,
                now + PositionMemoryPolicy.MAX_AGE_MS + 1,
            ),
        )
        assertNull(PositionMemoryPolicy.resumePositionMs(record, key, now - 1))
        assertNull(
            PositionMemoryPolicy.resumePositionMs(
                record.copy(positionMs = 2_000L),
                key,
                now,
            ),
        )
        assertNull(
            PositionMemoryPolicy.resumePositionMs(
                record.copy(positionMs = 299_000L),
                key,
                now,
            ),
        )
    }

    @Test
    fun encodesAndDecodesRecordsLosslessly() {
        val record = PositionRecord(
            PositionMemoryPolicy.entryKey("content://example/track.flac"),
            positionMs = 123_456L,
            durationMs = 654_321L,
            savedAtMs = now,
        )

        assertEquals(record, PositionMemoryPolicy.decode(PositionMemoryPolicy.encode(record)))
    }

    @Test
    fun rejectsMalformedOrForeignEncodings() {
        assertNull(PositionMemoryPolicy.decode(null))
        assertNull(PositionMemoryPolicy.decode(""))
        assertNull(PositionMemoryPolicy.decode("1|2|3|4"))
        assertNull(PositionMemoryPolicy.decode("2|bad-key|1|1|1"))
        val key = PositionMemoryPolicy.entryKey("content://example/track.mp3")
        assertNull(PositionMemoryPolicy.decode("2|$key|a|1|1"))
        assertNull(PositionMemoryPolicy.decode("2|$key|-1|1|1"))
        assertNull(PositionMemoryPolicy.decode("2|$key|1|-1|1"))
        assertNull(PositionMemoryPolicy.decode("2|$key|1|1|-1"))
    }
}
