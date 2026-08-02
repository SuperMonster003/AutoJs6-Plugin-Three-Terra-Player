package io.github.supermonster003.autojs6.plugin.audioplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentFlagPolicyTest {

    @Test
    fun constantsMatchAndroidIntentLiteralValues() {
        assertEquals(0x00000001, IntentFlagPolicy.FLAG_GRANT_READ_URI_PERMISSION)
        assertEquals(0x00000002, IntentFlagPolicy.FLAG_GRANT_WRITE_URI_PERMISSION)
        assertEquals(0x00000040, IntentFlagPolicy.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        assertEquals(0x00000080, IntentFlagPolicy.FLAG_GRANT_PREFIX_URI_PERMISSION)
        assertEquals(0x10000000, IntentFlagPolicy.FLAG_ACTIVITY_NEW_TASK)
    }

    @Test
    fun explorerRequiresReadAndPrefixAndAllowsOptionalNewTask() {
        val read = IntentFlagPolicy.FLAG_GRANT_READ_URI_PERMISSION
        val prefix = IntentFlagPolicy.FLAG_GRANT_PREFIX_URI_PERMISSION
        val newTask = IntentFlagPolicy.FLAG_ACTIVITY_NEW_TASK

        assertTrue(IntentFlagPolicy.isValidExplorerRequest(read or prefix))
        assertTrue(IntentFlagPolicy.isValidExplorerRequest(read or prefix or newTask))

        assertFalse(IntentFlagPolicy.isValidExplorerRequest(0))
        assertFalse(IntentFlagPolicy.isValidExplorerRequest(read))
        assertFalse(IntentFlagPolicy.isValidExplorerRequest(prefix))
        assertFalse(IntentFlagPolicy.isValidExplorerRequest(read or newTask))
        assertFalse(
            IntentFlagPolicy.isValidExplorerRequest(
                read or IntentFlagPolicy.FLAG_GRANT_WRITE_URI_PERMISSION,
            ),
        )
        assertFalse(
            IntentFlagPolicy.isValidExplorerRequest(
                read or IntentFlagPolicy.FLAG_GRANT_PERSISTABLE_URI_PERMISSION,
            ),
        )
        assertFalse(IntentFlagPolicy.isValidExplorerRequest(read or 0x00000004))
    }

    @Test
    fun externalViewRequiresReadAndRejectsUnsafeGrantFlags() {
        val read = IntentFlagPolicy.FLAG_GRANT_READ_URI_PERMISSION
        val newTask = IntentFlagPolicy.FLAG_ACTIVITY_NEW_TASK

        assertTrue(IntentFlagPolicy.isValidExternalView(IntentFlagPolicy.ACTION_VIEW, read))
        assertTrue(IntentFlagPolicy.isValidExternalView(IntentFlagPolicy.ACTION_VIEW, read or newTask))
        assertFalse(IntentFlagPolicy.isValidExternalView(null, read))
        assertFalse(IntentFlagPolicy.isValidExternalView("android.intent.action.EDIT", read))
        assertFalse(IntentFlagPolicy.isValidExternalView(IntentFlagPolicy.ACTION_VIEW, 0))

        listOf(
            IntentFlagPolicy.FLAG_GRANT_WRITE_URI_PERMISSION,
            IntentFlagPolicy.FLAG_GRANT_PERSISTABLE_URI_PERMISSION,
            IntentFlagPolicy.FLAG_GRANT_PREFIX_URI_PERMISSION,
        ).forEach { rejectedFlag ->
            assertFalse(
                IntentFlagPolicy.isValidExternalView(
                    IntentFlagPolicy.ACTION_VIEW,
                    read or rejectedFlag,
                ),
            )
        }
    }

    @Test
    fun internalForwardingRetainsReadOnly() {
        val everyRelevantFlag =
            IntentFlagPolicy.FLAG_GRANT_READ_URI_PERMISSION or
                IntentFlagPolicy.FLAG_GRANT_WRITE_URI_PERMISSION or
                IntentFlagPolicy.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                IntentFlagPolicy.FLAG_GRANT_PREFIX_URI_PERMISSION or
                IntentFlagPolicy.FLAG_ACTIVITY_NEW_TASK

        assertEquals(
            IntentFlagPolicy.FLAG_GRANT_READ_URI_PERMISSION,
            IntentFlagPolicy.forwardedFlags(everyRelevantFlag),
        )
        assertEquals(
            0,
            IntentFlagPolicy.forwardedFlags(IntentFlagPolicy.FLAG_GRANT_WRITE_URI_PERMISSION),
        )
        assertEquals(
            IntentFlagPolicy.FLAG_GRANT_READ_URI_PERMISSION,
            IntentFlagPolicy.readOnlyForwardFlags(),
        )
    }
}
