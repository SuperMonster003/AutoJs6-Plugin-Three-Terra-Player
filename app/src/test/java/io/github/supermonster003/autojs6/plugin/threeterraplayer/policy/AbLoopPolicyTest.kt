package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AbLoopPolicyTest {

    @Test
    fun pointAllowsUnknownDurationAndRejectsInvalidBounds() {
        assertTrue(AbLoopPolicy.validPoint(0L, null))
        assertTrue(AbLoopPolicy.validPoint(1_000L, 0L))
        assertTrue(AbLoopPolicy.validPoint(1_000L, 1_000L))
        assertFalse(AbLoopPolicy.validPoint(-1L, null))
        assertFalse(AbLoopPolicy.validPoint(1_001L, 1_000L))
    }

    @Test
    fun endRequiresStartMinimumLengthAndDurationBounds() {
        assertFalse(AbLoopPolicy.validEnd(null, 1_000L, 2_000L))
        assertFalse(AbLoopPolicy.validEnd(-1L, 1_000L, 2_000L))
        assertFalse(AbLoopPolicy.validEnd(1_000L, 1_499L, 2_000L))
        assertFalse(AbLoopPolicy.validEnd(1_000L, 2_001L, 2_000L))
        assertTrue(AbLoopPolicy.validEnd(1_000L, 1_500L, 2_000L))
    }

    @Test
    fun seekDecisionRequiresCompleteValidRangeAndReachedEnd() {
        assertFalse(AbLoopPolicy.shouldSeekToStart(null, 2_000L, 2_000L))
        assertFalse(AbLoopPolicy.shouldSeekToStart(1_000L, null, 2_000L))
        assertFalse(AbLoopPolicy.shouldSeekToStart(1_000L, 1_499L, 2_000L))
        assertFalse(AbLoopPolicy.shouldSeekToStart(1_000L, 2_000L, 1_999L))
        assertTrue(AbLoopPolicy.shouldSeekToStart(1_000L, 2_000L, 2_000L))
        assertTrue(AbLoopPolicy.shouldSeekToStart(1_000L, 2_000L, 2_100L))
    }
}
