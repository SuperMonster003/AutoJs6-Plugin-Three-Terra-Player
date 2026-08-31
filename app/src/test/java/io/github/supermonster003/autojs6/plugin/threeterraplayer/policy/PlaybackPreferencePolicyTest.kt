package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackPreferencePolicyTest {

    @Test
    fun acceptsEveryPublishedSpeedAndSeekIncrement() {
        PlaybackPreferencePolicy.SPEED_OPTIONS.forEach { speed ->
            assertEquals(speed, PlaybackPreferencePolicy.normalizeSpeed(speed))
        }
        PlaybackPreferencePolicy.SEEK_INCREMENT_OPTIONS_MS.forEach { increment ->
            assertEquals(increment, PlaybackPreferencePolicy.normalizeSeekIncrementMs(increment))
        }
    }

    @Test
    fun invalidNumericPreferencesFallBackWithoutReachingMedia3() {
        listOf(Float.NaN, Float.POSITIVE_INFINITY, -1f, 0f, 1.1f, 4f).forEach { value ->
            assertEquals(
                PlaybackPreferencePolicy.DEFAULT_SPEED,
                PlaybackPreferencePolicy.normalizeSpeed(value),
            )
        }
        listOf(Long.MIN_VALUE, -1L, 0L, 1_000L, 60_000L, Long.MAX_VALUE).forEach { value ->
            assertEquals(
                PlaybackPreferencePolicy.DEFAULT_SEEK_INCREMENT_MS,
                PlaybackPreferencePolicy.normalizeSeekIncrementMs(value),
            )
        }
    }

    @Test
    fun completionBehaviorRejectsUnknownOrMissingValues() {
        PlaybackCompletionBehavior.entries.forEach { behavior ->
            assertEquals(
                behavior,
                PlaybackPreferencePolicy.completionBehavior(behavior.name),
            )
        }
        assertEquals(
            PlaybackCompletionBehavior.STOP,
            PlaybackPreferencePolicy.completionBehavior("future-value"),
        )
        assertEquals(
            PlaybackCompletionBehavior.STOP,
            PlaybackPreferencePolicy.completionBehavior(null),
        )
    }

    @Test
    fun explicitStopAfterCurrentOverridesEveryPersistentCompletionPreference() {
        PlaybackCompletionBehavior.entries.forEach { selected ->
            assertEquals(
                PlaybackCompletionBehavior.STOP,
                PlaybackPreferencePolicy.effectiveCompletionBehavior(
                    stopAfterCurrent = true,
                    selected = selected,
                ),
            )
            assertEquals(
                selected,
                PlaybackPreferencePolicy.effectiveCompletionBehavior(
                    stopAfterCurrent = false,
                    selected = selected,
                ),
            )
        }
    }
}
