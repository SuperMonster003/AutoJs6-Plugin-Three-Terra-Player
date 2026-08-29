package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import java.security.MessageDigest

/** A single remembered playback position for one audio target. */
internal data class PositionRecord(
    val targetKey: String,
    val positionMs: Long,
    val durationMs: Long,
    val savedAtMs: Long,
)

/**
 * Pure rules for remembering and restoring the one most recently opened audio target.
 *
 * Storage adapters must not add behavior beyond these rules. Raw URIs never become storage keys;
 * only fixed-length digests derived by [entryKey] do.
 */
internal object PositionMemoryPolicy {

    const val MIN_PERSIST_POSITION_MS = 10_000L
    const val END_MARGIN_MS = 5_000L
    const val MAX_AGE_MS = 30L * 24 * 60 * 60 * 1000

    private const val ENCODING_VERSION = "2"
    private const val FIELD_SEPARATOR = '|'
    private const val HEX_DIGITS = "0123456789abcdef"

    fun entryKey(uriString: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(uriString.toByteArray(Charsets.UTF_8))
        return buildString(digest.size * 2) {
            digest.forEach { byte ->
                val value = byte.toInt() and 0xFF
                append(HEX_DIGITS[value ushr 4])
                append(HEX_DIGITS[value and 0x0F])
            }
        }
    }

    /** Positions near the very start or the very end are noise, not progress worth keeping. */
    fun shouldPersist(positionMs: Long, durationMs: Long): Boolean {
        if (positionMs < MIN_PERSIST_POSITION_MS) return false
        if (durationMs > 0 && positionMs > durationMs - END_MARGIN_MS) return false
        return true
    }

    fun resumePositionMs(record: PositionRecord?, targetKey: String, nowMs: Long): Long? {
        record ?: return null
        if (record.targetKey != targetKey) return null
        if (!shouldPersist(record.positionMs, record.durationMs)) return null
        if (record.savedAtMs > nowMs) return null
        if (nowMs - record.savedAtMs > MAX_AGE_MS) return null
        return record.positionMs
    }

    fun encode(record: PositionRecord): String = buildString {
        append(ENCODING_VERSION)
        append(FIELD_SEPARATOR)
        append(record.targetKey)
        append(FIELD_SEPARATOR)
        append(record.positionMs)
        append(FIELD_SEPARATOR)
        append(record.durationMs)
        append(FIELD_SEPARATOR)
        append(record.savedAtMs)
    }

    fun decode(raw: String?): PositionRecord? {
        raw ?: return null
        val parts = raw.split(FIELD_SEPARATOR)
        if (parts.size != 5 || parts[0] != ENCODING_VERSION) return null
        val targetKey = parts[1].takeIf(::isTargetKey) ?: return null
        val positionMs = parts[2].toLongOrNull()?.takeIf { it >= 0 } ?: return null
        val durationMs = parts[3].toLongOrNull()?.takeIf { it >= 0 } ?: return null
        val savedAtMs = parts[4].toLongOrNull()?.takeIf { it >= 0 } ?: return null
        return PositionRecord(targetKey, positionMs, durationMs, savedAtMs)
    }

    private fun isTargetKey(value: String): Boolean =
        value.length == 64 && value.all { it in HEX_DIGITS }
}
