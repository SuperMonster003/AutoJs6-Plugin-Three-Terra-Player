package io.github.supermonster003.autojs6.plugin.threeterraplayer

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackCompletionBehavior
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackPreferencePolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppPreferenceStore
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaybackPreferencesInstrumentationTest {

    @Test
    fun everyPublishedPlaybackPreferenceRoundTripsThroughSharedPreferences() {
        val store = AppPreferenceStore(InstrumentationRegistry.getInstrumentation().targetContext)
        val originalSpeed = store.defaultPlaybackSpeed()
        val originalSeek = store.seekIncrementMs()
        val originalCompletion = store.completionBehavior()
        try {
            PlaybackPreferencePolicy.SPEED_OPTIONS.forEach { value ->
                store.saveDefaultPlaybackSpeed(value)
                assertEquals(value, store.defaultPlaybackSpeed())
            }
            PlaybackPreferencePolicy.SEEK_INCREMENT_OPTIONS_MS.forEach { value ->
                store.saveSeekIncrementMs(value)
                assertEquals(value, store.seekIncrementMs())
            }
            PlaybackCompletionBehavior.entries.forEach { value ->
                store.saveCompletionBehavior(value)
                assertEquals(value, store.completionBehavior())
            }
        } finally {
            store.saveDefaultPlaybackSpeed(originalSpeed)
            store.saveSeekIncrementMs(originalSeek)
            store.saveCompletionBehavior(originalCompletion)
        }
    }
}
