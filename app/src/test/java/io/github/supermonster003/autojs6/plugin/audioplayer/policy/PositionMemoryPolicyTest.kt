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
        val record = PositionRecord(positionMs = 90_000L, durationMs = 300_000L, savedAtMs = now)

        assertNull(PositionMemoryPolicy.resumePositionMs(null, now))
        assertEquals(90_000L, PositionMemoryPolicy.resumePositionMs(record, now))
        assertEquals(
            90_000L,
            PositionMemoryPolicy.resumePositionMs(
                record,
                now + PositionMemoryPolicy.MAX_AGE_MS,
            ),
        )
        assertNull(
            PositionMemoryPolicy.resumePositionMs(
                record,
                now + PositionMemoryPolicy.MAX_AGE_MS + 1,
            ),
        )
        assertNull(PositionMemoryPolicy.resumePositionMs(record, now - 1))
        assertNull(
            PositionMemoryPolicy.resumePositionMs(
                record.copy(positionMs = 2_000L),
                now,
            ),
        )
        assertNull(
            PositionMemoryPolicy.resumePositionMs(
                record.copy(positionMs = 299_000L),
                now,
            ),
        )
    }

    @Test
    fun encodesAndDecodesRecordsLosslessly() {
        val record = PositionRecord(positionMs = 123_456L, durationMs = 654_321L, savedAtMs = now)

        assertEquals(record, PositionMemoryPolicy.decode(PositionMemoryPolicy.encode(record)))
    }

    @Test
    fun rejectsMalformedOrForeignEncodings() {
        assertNull(PositionMemoryPolicy.decode(null))
        assertNull(PositionMemoryPolicy.decode(""))
        assertNull(PositionMemoryPolicy.decode("1|2|3"))
        assertNull(PositionMemoryPolicy.decode("1|2|3|4|5"))
        assertNull(PositionMemoryPolicy.decode("2|1|1|1"))
        assertNull(PositionMemoryPolicy.decode("1|a|1|1"))
        assertNull(PositionMemoryPolicy.decode("1|-1|1|1"))
        assertNull(PositionMemoryPolicy.decode("1|1|-1|1"))
        assertNull(PositionMemoryPolicy.decode("1|1|1|-1"))
    }

    @Test
    fun evictsExpiredFutureDatedAndOverflowingRecordsOldestFirst() {
        val expired = PositionRecord(60_000L, 300_000L, now - PositionMemoryPolicy.MAX_AGE_MS - 1)
        val futureDated = PositionRecord(60_000L, 300_000L, now + 1)
        val entries = buildMap {
            put("expired", expired)
            put("future", futureDated)
            repeat(PositionMemoryPolicy.MAX_ENTRIES + 2) { index ->
                put("fresh-$index", PositionRecord(60_000L, 300_000L, now - index.toLong()))
            }
        }

        val evictions = PositionMemoryPolicy.selectEvictions(entries, now)

        assertTrue("expired" in evictions)
        assertTrue("future" in evictions)
        assertTrue("fresh-${PositionMemoryPolicy.MAX_ENTRIES + 1}" in evictions)
        assertTrue("fresh-${PositionMemoryPolicy.MAX_ENTRIES}" in evictions)
        assertFalse("fresh-0" in evictions)
        assertEquals(4, evictions.size)
    }

    @Test
    fun keepsSmallFreshSetsUntouched() {
        val entries = mapOf(
            "a" to PositionRecord(60_000L, 300_000L, now - 1),
            "b" to PositionRecord(60_000L, 300_000L, now - 2),
        )

        assertTrue(PositionMemoryPolicy.selectEvictions(entries, now).isEmpty())
    }
}
