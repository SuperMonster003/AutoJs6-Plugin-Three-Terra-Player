package io.github.supermonster003.autojs6.plugin.audioplayer.policy

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExplorerQueuePolicyTest {

    @Test
    fun singleActionAcceptsExactlyOneTarget() {
        assertFalse(ExplorerQueuePolicy.isValidTargetCount(0, multipleAction = false))
        assertTrue(ExplorerQueuePolicy.isValidTargetCount(1, multipleAction = false))
        assertFalse(ExplorerQueuePolicy.isValidTargetCount(2, multipleAction = false))
    }

    @Test
    fun multipleActionAcceptsBoundedNonEmptyQueue() {
        assertFalse(ExplorerQueuePolicy.isValidTargetCount(0, multipleAction = true))
        assertTrue(ExplorerQueuePolicy.isValidTargetCount(1, multipleAction = true))
        assertTrue(
            ExplorerQueuePolicy.isValidTargetCount(
                ExplorerQueuePolicy.MAX_TARGETS,
                multipleAction = true,
            ),
        )
        assertFalse(
            ExplorerQueuePolicy.isValidTargetCount(
                ExplorerQueuePolicy.MAX_TARGETS + 1,
                multipleAction = true,
            ),
        )
    }

    @Test
    fun uniqueValuesRejectBlankOrDuplicateEntries() {
        assertTrue(ExplorerQueuePolicy.hasUniqueNonBlankValues(emptyList()))
        assertTrue(ExplorerQueuePolicy.hasUniqueNonBlankValues(listOf("a", "b")))
        assertFalse(ExplorerQueuePolicy.hasUniqueNonBlankValues(listOf("a", "a")))
        assertFalse(ExplorerQueuePolicy.hasUniqueNonBlankValues(listOf("a", " ")))
    }
}
