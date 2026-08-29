package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

/** Validation and seek decisions for an A-B playback loop. */
internal object AbLoopPolicy {

    const val MIN_LOOP_LENGTH_MS = 500L

    fun validPoint(positionMs: Long, durationMs: Long?): Boolean =
        positionMs >= 0L && (durationMs == null || durationMs <= 0L || positionMs <= durationMs)

    fun validEnd(startMs: Long?, endMs: Long, durationMs: Long?): Boolean =
        startMs != null &&
            validPoint(startMs, durationMs) &&
            validPoint(endMs, durationMs) &&
            endMs >= startMs + MIN_LOOP_LENGTH_MS

    fun shouldSeekToStart(startMs: Long?, endMs: Long?, positionMs: Long): Boolean =
        startMs != null &&
            endMs != null &&
            endMs >= startMs + MIN_LOOP_LENGTH_MS &&
            positionMs >= endMs
}
