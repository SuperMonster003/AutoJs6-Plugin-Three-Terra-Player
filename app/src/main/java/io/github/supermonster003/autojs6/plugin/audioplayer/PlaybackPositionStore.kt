package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.Context
import androidx.core.content.edit
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.PositionMemoryPolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.PositionRecord

/**
 * Stores exactly one recent target. Opening a different target invalidates the previous position
 * immediately, even if the new target is closed before it reaches a persistable position.
 */
internal class PlaybackPositionStore(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    init {
        // Versions through 1.2.2 kept up to 64 per-file entries. They cannot satisfy the new
        // single-recent-item contract, so remove them once without retaining raw URI data.
        context.applicationContext.getSharedPreferences(LEGACY_PREFERENCES_NAME, Context.MODE_PRIVATE)
            .takeIf { it.all.isNotEmpty() }
            ?.edit { clear() }
    }

    fun save(uriString: String, positionMs: Long, durationMs: Long, nowMs: Long) {
        val key = PositionMemoryPolicy.entryKey(uriString)
        preferences.edit {
            if (PositionMemoryPolicy.shouldPersist(positionMs, durationMs)) {
                putString(
                    KEY_RECENT_POSITION,
                    PositionMemoryPolicy.encode(PositionRecord(key, positionMs, durationMs, nowMs)),
                )
            } else {
                remove(KEY_RECENT_POSITION)
            }
        }
    }

    fun resumePositionMs(uriString: String, nowMs: Long): Long? {
        val targetKey = PositionMemoryPolicy.entryKey(uriString)
        val record = PositionMemoryPolicy.decode(preferences.getString(KEY_RECENT_POSITION, null))
        if (record == null || record.targetKey != targetKey) {
            preferences.edit { remove(KEY_RECENT_POSITION) }
            return null
        }
        return PositionMemoryPolicy.resumePositionMs(record, targetKey, nowMs).also { position ->
            if (position == null) preferences.edit { remove(KEY_RECENT_POSITION) }
        }
    }

    fun clear(uriString: String) {
        val targetKey = PositionMemoryPolicy.entryKey(uriString)
        val record = PositionMemoryPolicy.decode(preferences.getString(KEY_RECENT_POSITION, null))
        if (record?.targetKey == targetKey) preferences.edit { remove(KEY_RECENT_POSITION) }
    }

    fun clearAll() = preferences.edit { clear() }

    private companion object {
        const val PREFERENCES_NAME = "recent_playback_position"
        const val KEY_RECENT_POSITION = "recent"
        const val LEGACY_PREFERENCES_NAME = "playback_positions"
    }
}
