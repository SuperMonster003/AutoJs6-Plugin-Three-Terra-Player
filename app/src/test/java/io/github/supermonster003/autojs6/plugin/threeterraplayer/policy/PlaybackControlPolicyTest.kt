package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackControlPolicyTest {

    @Test
    fun emptyConnectedQueueDisablesEveryPlaybackControl() {
        val availability = PlaybackControlPolicy.resolve(
            controllerConnected = true,
            mediaItemCount = 0,
            pendingPlaybackTarget = false,
        )

        assertFalse(availability.playPause)
        assertFalse(availability.mediaActions)
        assertFalse(availability.queue)
        assertFalse(availability.playbackTools)
    }

    @Test
    fun pendingRequestKeepsOnlyPlayAvailableBeforeControllerConnects() {
        val availability = PlaybackControlPolicy.resolve(
            controllerConnected = false,
            mediaItemCount = 0,
            pendingPlaybackTarget = true,
        )

        assertTrue(availability.playPause)
        assertFalse(availability.mediaActions)
        assertFalse(availability.queue)
        assertFalse(availability.playbackTools)
    }

    @Test
    fun populatedQueueEnablesPlaybackActionsAndTools() {
        val availability = PlaybackControlPolicy.resolve(
            controllerConnected = true,
            mediaItemCount = 1,
            pendingPlaybackTarget = false,
        )

        assertTrue(availability.playPause)
        assertTrue(availability.mediaActions)
        assertTrue(availability.queue)
        assertTrue(availability.playbackTools)
    }
}
