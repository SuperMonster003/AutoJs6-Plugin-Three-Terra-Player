package io.github.supermonster003.autojs6.plugin.audioplayer.update

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutomaticUpdatePolicyTest {

    @Test
    fun checksAtMostDailyAndRecoversFromClockChanges() {
        val now = 1_787_760_000_000L
        assertTrue(AppUpdateCoordinator.automaticCheckDue(0L, now))
        assertFalse(AppUpdateCoordinator.automaticCheckDue(now - 23L * 60 * 60 * 1000, now))
        assertTrue(AppUpdateCoordinator.automaticCheckDue(now - 24L * 60 * 60 * 1000, now))
        assertTrue(AppUpdateCoordinator.automaticCheckDue(now + 1L, now))
    }
}
