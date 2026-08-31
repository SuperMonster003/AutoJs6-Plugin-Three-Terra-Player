package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.ExplorerQueuePolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackSessionPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackSessionRecord
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackSessionTrackRecord

internal data class PlaybackSessionSnapshot(
    val request: AudioPlaybackRequest,
    val positionMs: Long,
    val repeatMode: Int,
    val shuffleEnabled: Boolean,
    val playbackSpeed: Float,
)

/**
 * Stores one launcher-restorable standalone queue. Raw content URIs are required to reconstruct
 * the queue, stay in app-private storage, and are accepted only while their persisted read grants
 * remain active. Host-session routes and transient external grants are never stored.
 */
internal class PlaybackSessionStore(context: Context) {

    private val applicationContext = context.applicationContext
    private val preferences = applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
    private val progressPreferences = applicationContext.getSharedPreferences(
        PROGRESS_PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    fun save(snapshot: PlaybackSessionSnapshot, nowMs: Long = System.currentTimeMillis()): Boolean {
        val record = snapshot.toRecord(nowMs)
            ?.let { PlaybackSessionPolicy.validate(it, nowMs) }
            ?.takeIf(::hasPersistedReadAccess)
        if (record == null) {
            clear()
            return false
        }
        preferences.edit {
            clear()
            putInt(KEY_VERSION, STORAGE_VERSION)
            putInt(KEY_TRACK_COUNT, record.tracks.size)
            record.tracks.forEachIndexed { index, track ->
                putString(trackKey(KEY_TRACK_URI_PREFIX, index), track.uriString)
                putString(trackKey(KEY_TRACK_MIME_PREFIX, index), track.mimeType)
                putString(trackKey(KEY_TRACK_NAME_PREFIX, index), track.displayName)
            }
            putInt(KEY_CURRENT_INDEX, record.currentIndex)
            putLong(KEY_POSITION_MS, record.positionMs)
            putInt(KEY_REPEAT_MODE, record.repeatMode)
            putBoolean(KEY_SHUFFLE_ENABLED, record.shuffleEnabled)
            putFloat(KEY_PLAYBACK_SPEED, record.playbackSpeed)
            putLong(KEY_SAVED_AT_MS, record.savedAtMs)
        }
        saveProgress(record.tracks[record.currentIndex].uriString, record.positionMs, nowMs)
        return true
    }

    fun load(nowMs: Long = System.currentTimeMillis()): PlaybackSessionSnapshot? {
        val record = runCatching(::readRecord).getOrNull()
            ?.let { PlaybackSessionPolicy.validate(it, nowMs) }
            ?.takeIf(::hasPersistedReadAccess)
        if (record == null) {
            clear()
            return null
        }
        val tracks = record.tracks.map { track ->
            AudioTrackRequest(
                uri = Uri.parse(track.uriString),
                mimeType = track.mimeType,
                displayName = track.displayName,
            )
        }
        val request = runCatching {
            AudioPlaybackRequest(tracks = tracks, startIndex = record.currentIndex)
        }.getOrNull() ?: run {
            clear()
            return null
        }
        val currentUri = request.currentTrack.uri.toString()
        val rememberedPosition = loadProgress(currentUri, nowMs)
            ?: PlaybackPositionStore(applicationContext).resumePositionMs(currentUri, nowMs)
        return PlaybackSessionSnapshot(
            request = request,
            positionMs = rememberedPosition ?: record.positionMs,
            repeatMode = record.repeatMode,
            shuffleEnabled = record.shuffleEnabled,
            playbackSpeed = record.playbackSpeed,
        )
    }

    fun updatePosition(
        uriString: String,
        positionMs: Long,
        nowMs: Long = System.currentTimeMillis(),
    ): Boolean {
        if (positionMs < 0L || nowMs < 0L) return false
        val currentIndex = runCatching { preferences.getInt(KEY_CURRENT_INDEX, -1) }.getOrDefault(-1)
        val storedUri = preferences.getString(trackKey(KEY_TRACK_URI_PREFIX, currentIndex), null)
        if (storedUri != uriString || !hasPersistedReadAccess(uriString)) return false
        saveProgress(uriString, positionMs, nowMs)
        return true
    }

    fun clear() {
        preferences.edit { clear() }
        progressPreferences.edit { clear() }
    }

    private fun PlaybackSessionSnapshot.toRecord(nowMs: Long): PlaybackSessionRecord? {
        if (request.hostSession != null || request.hostTargetId != null) return null
        if (request.tracks.any { it.hostRelativePath != null }) return null
        return PlaybackSessionRecord(
            tracks = request.tracks.map { track ->
                PlaybackSessionTrackRecord(
                    uriString = track.uri.toString(),
                    mimeType = track.mimeType,
                    displayName = track.displayName,
                )
            },
            currentIndex = request.startIndex,
            positionMs = positionMs.coerceAtLeast(0L),
            repeatMode = repeatMode,
            shuffleEnabled = shuffleEnabled,
            playbackSpeed = playbackSpeed,
            savedAtMs = nowMs,
        )
    }

    private fun readRecord(): PlaybackSessionRecord? {
        if (preferences.getInt(KEY_VERSION, -1) != STORAGE_VERSION) return null
        val count = preferences.getInt(KEY_TRACK_COUNT, -1)
        if (count !in 1..ExplorerQueuePolicy.MAX_TARGETS) return null
        val tracks = (0 until count).map { index ->
            PlaybackSessionTrackRecord(
                uriString = preferences.getString(trackKey(KEY_TRACK_URI_PREFIX, index), null) ?: return null,
                mimeType = preferences.getString(trackKey(KEY_TRACK_MIME_PREFIX, index), null) ?: return null,
                displayName = preferences.getString(trackKey(KEY_TRACK_NAME_PREFIX, index), null) ?: return null,
            )
        }
        return PlaybackSessionRecord(
            tracks = tracks,
            currentIndex = preferences.getInt(KEY_CURRENT_INDEX, -1),
            positionMs = preferences.getLong(KEY_POSITION_MS, -1L),
            repeatMode = preferences.getInt(KEY_REPEAT_MODE, -1),
            shuffleEnabled = preferences.getBoolean(KEY_SHUFFLE_ENABLED, false),
            playbackSpeed = preferences.getFloat(KEY_PLAYBACK_SPEED, Float.NaN),
            savedAtMs = preferences.getLong(KEY_SAVED_AT_MS, -1L),
        )
    }

    private fun hasPersistedReadAccess(record: PlaybackSessionRecord): Boolean {
        val readableUris = applicationContext.contentResolver.persistedUriPermissions
            .asSequence()
            .filter { permission -> permission.isReadPermission }
            .map { permission -> permission.uri.toString() }
            .toSet()
        return record.tracks.all { track -> track.uriString in readableUris }
    }

    private fun hasPersistedReadAccess(uriString: String): Boolean =
        applicationContext.contentResolver.persistedUriPermissions.any { permission ->
            permission.isReadPermission && permission.uri.toString() == uriString
        }

    private fun saveProgress(uriString: String, positionMs: Long, nowMs: Long) {
        progressPreferences.edit {
            putString(KEY_PROGRESS_TARGET, io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PositionMemoryPolicy.entryKey(uriString))
            putLong(KEY_PROGRESS_POSITION_MS, positionMs.coerceAtLeast(0L))
            putLong(KEY_PROGRESS_SAVED_AT_MS, nowMs)
        }
    }

    private fun loadProgress(uriString: String, nowMs: Long): Long? {
        val target = progressPreferences.getString(KEY_PROGRESS_TARGET, null) ?: return null
        if (target != io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PositionMemoryPolicy.entryKey(uriString)) {
            return null
        }
        val positionMs = progressPreferences.getLong(KEY_PROGRESS_POSITION_MS, -1L)
        val savedAtMs = progressPreferences.getLong(KEY_PROGRESS_SAVED_AT_MS, -1L)
        return positionMs.takeIf {
            it >= 0L && savedAtMs in 0L..nowMs && nowMs - savedAtMs <= PlaybackSessionPolicy.MAX_AGE_MS
        }
    }

    private fun trackKey(prefix: String, index: Int): String = "$prefix$index"

    private companion object {
        const val PREFERENCES_NAME = "launcher_playback_session"
        const val PROGRESS_PREFERENCES_NAME = "launcher_playback_progress"
        const val STORAGE_VERSION = 1
        const val KEY_VERSION = "version"
        const val KEY_TRACK_COUNT = "track_count"
        const val KEY_TRACK_URI_PREFIX = "track_uri_"
        const val KEY_TRACK_MIME_PREFIX = "track_mime_"
        const val KEY_TRACK_NAME_PREFIX = "track_name_"
        const val KEY_CURRENT_INDEX = "current_index"
        const val KEY_POSITION_MS = "position_ms"
        const val KEY_REPEAT_MODE = "repeat_mode"
        const val KEY_SHUFFLE_ENABLED = "shuffle_enabled"
        const val KEY_PLAYBACK_SPEED = "playback_speed"
        const val KEY_SAVED_AT_MS = "saved_at_ms"
        const val KEY_PROGRESS_TARGET = "target"
        const val KEY_PROGRESS_POSITION_MS = "position_ms"
        const val KEY_PROGRESS_SAVED_AT_MS = "saved_at_ms"
    }
}
