package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import java.net.URI

internal data class PlaybackSessionTrackRecord(
    val uriString: String,
    val mimeType: String,
    val displayName: String,
)

internal data class PlaybackSessionRecord(
    val tracks: List<PlaybackSessionTrackRecord>,
    val currentIndex: Int,
    val positionMs: Long,
    val repeatMode: Int,
    val shuffleEnabled: Boolean,
    val playbackSpeed: Float,
    val savedAtMs: Long,
)

/** Pure validation for the one launcher-restorable standalone playback session. */
internal object PlaybackSessionPolicy {

    const val MAX_AGE_MS = PositionMemoryPolicy.MAX_AGE_MS
    const val REPEAT_MODE_OFF = 0
    const val REPEAT_MODE_ONE = 1
    const val REPEAT_MODE_ALL = 2
    const val MIN_PLAYBACK_SPEED = 0.5f
    const val MAX_PLAYBACK_SPEED = 2f

    private const val MAX_URI_LENGTH = 8_192

    fun validate(record: PlaybackSessionRecord?, nowMs: Long): PlaybackSessionRecord? {
        record ?: return null
        if (record.savedAtMs < 0L || record.savedAtMs > nowMs) return null
        if (nowMs - record.savedAtMs > MAX_AGE_MS) return null
        if (record.tracks.size !in 1..ExplorerQueuePolicy.MAX_TARGETS) return null
        if (record.currentIndex !in record.tracks.indices || record.positionMs < 0L) return null
        if (record.repeatMode !in REPEAT_MODE_OFF..REPEAT_MODE_ALL) return null
        if (!record.playbackSpeed.isFinite()) return null
        if (record.playbackSpeed !in MIN_PLAYBACK_SPEED..MAX_PLAYBACK_SPEED) return null

        val tracks = record.tracks.map { track ->
            normalizeTrack(track) ?: return null
        }
        if (tracks.map(PlaybackSessionTrackRecord::uriString).toSet().size != tracks.size) return null
        return record.copy(tracks = tracks)
    }

    private fun normalizeTrack(track: PlaybackSessionTrackRecord): PlaybackSessionTrackRecord? {
        val uri = track.uriString
            .takeIf { it.length in 1..MAX_URI_LENGTH && it.none(Char::isISOControl) }
            ?.takeIf { it == it.trim() }
            ?.let { raw -> runCatching { URI(raw) }.getOrNull() }
            ?.takeIf { parsed ->
                parsed.scheme.equals("content", ignoreCase = true) &&
                    !parsed.isOpaque &&
                    !parsed.authority.isNullOrBlank() &&
                    !parsed.path.isNullOrBlank() &&
                    parsed.userInfo == null &&
                    parsed.port == -1 &&
                    parsed.query == null &&
                    parsed.fragment == null
            }
            ?: return null
        val mimeType = MimeTypePolicy.normalize(track.mimeType)
            ?.takeIf(MimeTypePolicy::isAudio)
            ?: return null
        val displayName = DisplayNamePolicy.sanitizeIncoming(track.displayName)
            ?.takeIf { it == track.displayName }
            ?: return null
        return PlaybackSessionTrackRecord(uri.toString(), mimeType, displayName)
    }
}
