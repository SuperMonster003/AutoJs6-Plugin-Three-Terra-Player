package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.PositionMemoryPolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.PositionRecord

/**
 * SharedPreferences adapter for [PositionMemoryPolicy].
 *
 * Keys are digests of the target URI, never the URI itself. Corrupt entries are dropped on the
 * next write instead of being interpreted.
 */
internal class PlaybackPositionStore(context: Context) {

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun save(uriString: String, positionMs: Long, durationMs: Long, nowMs: Long) {
        val key = PositionMemoryPolicy.entryKey(uriString)
        preferences.edit {
            if (PositionMemoryPolicy.shouldPersist(positionMs, durationMs)) {
                putString(
                    key,
                    PositionMemoryPolicy.encode(PositionRecord(positionMs, durationMs, nowMs)),
                )
            } else {
                remove(key)
            }
            evictionKeys(nowMs, protectedKey = key).forEach { remove(it) }
        }
    }

    fun resumePositionMs(uriString: String, nowMs: Long): Long? {
        val raw = preferences.getString(PositionMemoryPolicy.entryKey(uriString), null)
        return PositionMemoryPolicy.resumePositionMs(PositionMemoryPolicy.decode(raw), nowMs)
    }

    fun clear(uriString: String) {
        preferences.edit { remove(PositionMemoryPolicy.entryKey(uriString)) }
    }

    private fun evictionKeys(nowMs: Long, protectedKey: String): Set<String> {
        val decoded = mutableMapOf<String, PositionRecord>()
        val corrupt = mutableSetOf<String>()
        preferences.all.forEach { (key, value) ->
            val record = (value as? String)?.let(PositionMemoryPolicy::decode)
            if (record != null) decoded[key] = record else corrupt.add(key)
        }
        return corrupt + PositionMemoryPolicy.selectEvictions(decoded, nowMs) - protectedKey
    }

    private companion object {
        const val PREFERENCES_NAME = "playback_positions"
    }
}
