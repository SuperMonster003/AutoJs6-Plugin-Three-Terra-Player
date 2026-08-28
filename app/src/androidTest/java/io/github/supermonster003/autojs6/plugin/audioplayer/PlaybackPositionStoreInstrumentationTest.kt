package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaybackPositionStoreInstrumentationTest {

    @Test
    fun retainsOnlyTheMostRecentlyOpenedUnfinishedTarget() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val recentPreferences = context.getSharedPreferences(RECENT_PREFERENCES, Context.MODE_PRIVATE)
        val legacyPreferences = context.getSharedPreferences(LEGACY_PREFERENCES, Context.MODE_PRIVATE)
        val recentBackup = recentPreferences.all.toMap()
        val legacyBackup = legacyPreferences.all.toMap()

        try {
            recentPreferences.edit().clear().commit()
            legacyPreferences.edit().clear().commit()
            val store = PlaybackPositionStore(context)
            val first = "content://example/audio/a.mp3"
            val second = "content://example/audio/b.wma"
            val now = 1_787_932_800_000L

            store.save(first, 60_000L, 300_000L, now)
            assertEquals(60_000L, store.resumePositionMs(first, now))

            // Merely opening B invalidates A, even before B has enough progress to save.
            assertNull(store.resumePositionMs(second, now))
            assertNull(store.resumePositionMs(first, now))

            store.save(second, 90_000L, 300_000L, now)
            assertEquals(90_000L, store.resumePositionMs(second, now))

            // A completed item must never leave a resumable record.
            store.save(second, 300_000L, 300_000L, now)
            assertNull(store.resumePositionMs(second, now))
        } finally {
            restore(recentPreferences, recentBackup)
            restore(legacyPreferences, legacyBackup)
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
        const val RECENT_PREFERENCES = "recent_playback_position"
        const val LEGACY_PREFERENCES = "playback_positions"
    }
}
