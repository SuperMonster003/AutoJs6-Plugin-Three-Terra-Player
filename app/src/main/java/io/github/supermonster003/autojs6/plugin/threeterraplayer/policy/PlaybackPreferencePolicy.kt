package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import kotlin.math.abs

internal enum class PlaybackCompletionBehavior {
    STOP,
    REWIND_PAUSED,
    REPLAY,
}

/** Validates the persisted playback defaults before they reach Media3. */
internal object PlaybackPreferencePolicy {

    const val DEFAULT_SPEED = 1f
    const val DEFAULT_SEEK_INCREMENT_MS = 10_000L
    val SPEED_OPTIONS = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
    val SEEK_INCREMENT_OPTIONS_MS = listOf(5_000L, 10_000L, 15_000L, 30_000L)

    fun normalizeSpeed(value: Float): Float = SPEED_OPTIONS.firstOrNull { option ->
        value.isFinite() && abs(option - value) <= FLOAT_TOLERANCE
    } ?: DEFAULT_SPEED

    fun normalizeSeekIncrementMs(value: Long): Long =
        value.takeIf(SEEK_INCREMENT_OPTIONS_MS::contains) ?: DEFAULT_SEEK_INCREMENT_MS

    fun completionBehavior(raw: String?): PlaybackCompletionBehavior = raw
        ?.let { value -> runCatching { PlaybackCompletionBehavior.valueOf(value) }.getOrNull() }
        ?: PlaybackCompletionBehavior.STOP

    fun effectiveCompletionBehavior(
        stopAfterCurrent: Boolean,
        selected: PlaybackCompletionBehavior,
    ): PlaybackCompletionBehavior = if (stopAfterCurrent) PlaybackCompletionBehavior.STOP else selected

    private const val FLOAT_TOLERANCE = 0.0001f
}
