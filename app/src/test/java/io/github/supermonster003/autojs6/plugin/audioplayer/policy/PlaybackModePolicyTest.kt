package io.github.supermonster003.autojs6.plugin.audioplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackModePolicyTest {

    @Test
    fun currentPrioritizesRepeatOneThenShuffleThenSequential() {
        assertEquals(
            PlaybackMode.REPEAT_ONE,
            PlaybackModePolicy.current(true, PlaybackModePolicy.REPEAT_MODE_ONE),
        )
        assertEquals(
            PlaybackMode.SHUFFLE,
            PlaybackModePolicy.current(true, PlaybackModePolicy.REPEAT_MODE_ALL),
        )
        assertEquals(
            PlaybackMode.SEQUENTIAL,
            PlaybackModePolicy.current(false, PlaybackModePolicy.REPEAT_MODE_OFF),
        )
        assertEquals(
            PlaybackMode.SEQUENTIAL,
            PlaybackModePolicy.current(false, 99),
        )
    }

    @Test
    fun nextCyclesAllModes() {
        assertEquals(PlaybackMode.SHUFFLE, PlaybackModePolicy.next(PlaybackMode.SEQUENTIAL))
        assertEquals(PlaybackMode.REPEAT_ONE, PlaybackModePolicy.next(PlaybackMode.SHUFFLE))
        assertEquals(PlaybackMode.SEQUENTIAL, PlaybackModePolicy.next(PlaybackMode.REPEAT_ONE))
    }

    @Test
    fun stateMapsModesToMedia3Properties() {
        PlaybackModePolicy.state(PlaybackMode.SEQUENTIAL).also { state ->
            assertFalse(state.shuffleEnabled)
            assertEquals(PlaybackModePolicy.REPEAT_MODE_OFF, state.repeatMode)
        }
        PlaybackModePolicy.state(PlaybackMode.SHUFFLE).also { state ->
            assertTrue(state.shuffleEnabled)
            assertEquals(PlaybackModePolicy.REPEAT_MODE_ALL, state.repeatMode)
        }
        PlaybackModePolicy.state(PlaybackMode.REPEAT_ONE).also { state ->
            assertFalse(state.shuffleEnabled)
            assertEquals(PlaybackModePolicy.REPEAT_MODE_ONE, state.repeatMode)
        }
    }
}
