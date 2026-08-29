package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SleepTimerPolicyTest {

    @Test
    fun deadlineAcceptsOnlyBoundedDurationsAndSafeClockValues() {
        assertEquals(
            61_000L,
            SleepTimerPolicy.deadlineElapsedRealtimeMs(1_000L, SleepTimerPolicy.MIN_DURATION_MS),
        )
        assertEquals(
            SleepTimerPolicy.MAX_DURATION_MS,
            SleepTimerPolicy.deadlineElapsedRealtimeMs(0L, SleepTimerPolicy.MAX_DURATION_MS),
        )
        assertNull(SleepTimerPolicy.deadlineElapsedRealtimeMs(-1L, SleepTimerPolicy.MIN_DURATION_MS))
        assertNull(SleepTimerPolicy.deadlineElapsedRealtimeMs(0L, SleepTimerPolicy.MIN_DURATION_MS - 1L))
        assertNull(SleepTimerPolicy.deadlineElapsedRealtimeMs(0L, SleepTimerPolicy.MAX_DURATION_MS + 1L))
        assertNull(SleepTimerPolicy.deadlineElapsedRealtimeMs(Long.MAX_VALUE, SleepTimerPolicy.MIN_DURATION_MS))
    }

    @Test
    fun remainingNeverBecomesNegative() {
        assertEquals(500L, SleepTimerPolicy.remainingMs(1_500L, 1_000L))
        assertEquals(0L, SleepTimerPolicy.remainingMs(1_000L, 1_000L))
        assertEquals(0L, SleepTimerPolicy.remainingMs(500L, 1_000L))
    }

    @Test
    fun fadeFactorCoversBeforeDuringAndAfterFade() {
        assertEquals(1f, SleepTimerPolicy.fadeVolumeFactor(10_000L), 0f)
        assertEquals(1f, SleepTimerPolicy.fadeVolumeFactor(SleepTimerPolicy.FADE_DURATION_MS), 0f)
        assertEquals(0.5f, SleepTimerPolicy.fadeVolumeFactor(SleepTimerPolicy.FADE_DURATION_MS / 2L), 0.001f)
        assertEquals(0f, SleepTimerPolicy.fadeVolumeFactor(0L), 0f)
        assertEquals(0f, SleepTimerPolicy.fadeVolumeFactor(-1L), 0f)
    }
}
