package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.media3.common.Player
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaybackSessionStoreInstrumentationTest {

    @Test
    fun refusesQueuesWithoutPersistedDocumentReadAccess() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sessionPreferences = context.getSharedPreferences(SESSION_PREFERENCES, Context.MODE_PRIVATE)
        val progressPreferences = context.getSharedPreferences(PROGRESS_PREFERENCES, Context.MODE_PRIVATE)
        val sessionBackup = sessionPreferences.all.toMap()
        val progressBackup = progressPreferences.all.toMap()

        try {
            sessionPreferences.edit().clear().commit()
            progressPreferences.edit().clear().commit()
            val store = PlaybackSessionStore(context)
            val request = AudioPlaybackRequest(
                uri = Uri.parse("content://example.invalid/audio/track.mp3"),
                mimeType = "audio/mpeg",
                displayName = "track.mp3",
            )

            assertFalse(
                store.save(
                    PlaybackSessionSnapshot(
                        request = request,
                        positionMs = 42_000L,
                        repeatMode = Player.REPEAT_MODE_OFF,
                        shuffleEnabled = false,
                        playbackSpeed = 1f,
                    ),
                    nowMs = 1_788_115_200_000L,
                ),
            )
            assertNull(store.load(nowMs = 1_788_115_200_000L))
            assertTrue(sessionPreferences.all.isEmpty())
            assertTrue(progressPreferences.all.isEmpty())
        } finally {
            restore(sessionPreferences, sessionBackup)
            restore(progressPreferences, progressBackup)
        }
    }

    private fun restore(preferences: SharedPreferences, values: Map<String, *>) {
        preferences.edit().clear().apply {
            values.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is Set<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        putStringSet(key, value as Set<String>)
                    }
                }
            }
        }.commit()
    }

    private companion object {
        const val SESSION_PREFERENCES = "launcher_playback_session"
        const val PROGRESS_PREFERENCES = "launcher_playback_progress"
    }
}
