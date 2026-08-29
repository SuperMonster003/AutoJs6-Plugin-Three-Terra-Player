package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

/** Bounds and fade calculations for the service-owned sleep timer. */
internal object SleepTimerPolicy {

    const val MIN_DURATION_MS = 60_000L
    const val MAX_DURATION_MS = 24L * 60L * 60L * 1000L
    const val FADE_DURATION_MS = 5_000L

    fun deadlineElapsedRealtimeMs(nowElapsedRealtimeMs: Long, durationMs: Long): Long? {
        if (nowElapsedRealtimeMs < 0L || durationMs !in MIN_DURATION_MS..MAX_DURATION_MS) return null
        if (nowElapsedRealtimeMs > Long.MAX_VALUE - durationMs) return null
        return nowElapsedRealtimeMs + durationMs
    }

    fun remainingMs(deadlineElapsedRealtimeMs: Long, nowElapsedRealtimeMs: Long): Long =
        (deadlineElapsedRealtimeMs - nowElapsedRealtimeMs).coerceAtLeast(0L)

    fun fadeVolumeFactor(remainingMs: Long): Float = when {
        remainingMs <= 0L -> 0f
        remainingMs >= FADE_DURATION_MS -> 1f
        else -> remainingMs.toFloat() / FADE_DURATION_MS
    }
}
