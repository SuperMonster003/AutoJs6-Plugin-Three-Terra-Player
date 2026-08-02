package io.github.supermonster003.autojs6.plugin.audioplayer.policy

/**
 * Android intent values are repeated as integer literals so this boundary policy remains a pure
 * Kotlin/JVM unit. Keep the values synchronized with android.content.Intent.
 */
internal object IntentFlagPolicy {

    const val ACTION_VIEW = "android.intent.action.VIEW"

    const val FLAG_GRANT_READ_URI_PERMISSION = 0x00000001
    const val FLAG_GRANT_WRITE_URI_PERMISSION = 0x00000002
    const val FLAG_GRANT_PERSISTABLE_URI_PERMISSION = 0x00000040
    const val FLAG_GRANT_PREFIX_URI_PERMISSION = 0x00000080
    const val FLAG_ACTIVITY_NEW_TASK = 0x10000000

    private const val EXPLORER_REQUIRED_FLAGS =
        FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_PREFIX_URI_PERMISSION

    private const val EXPLORER_ALLOWED_FLAGS =
        EXPLORER_REQUIRED_FLAGS or FLAG_ACTIVITY_NEW_TASK

    private const val EXTERNAL_REJECTED_FLAGS =
        FLAG_GRANT_WRITE_URI_PERMISSION or
            FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
            FLAG_GRANT_PREFIX_URI_PERMISSION

    /** Explorer v2 requires read and prefix grants and may launch into a new task. */
    fun isValidExplorerRequest(flags: Int): Boolean =
        flags and EXPLORER_REQUIRED_FLAGS == EXPLORER_REQUIRED_FLAGS &&
            flags and EXPLORER_ALLOWED_FLAGS.inv() == 0

    /** External VIEW callers must provide read access and no expandable or durable grants. */
    fun isValidExternalView(action: String?, flags: Int): Boolean =
        action == ACTION_VIEW && isValidExternalView(flags)

    fun isValidExternalView(flags: Int): Boolean =
        hasReadPermission(flags) && flags and EXTERNAL_REJECTED_FLAGS == 0

    /** Drops every source bit except a read grant. */
    fun forwardedFlags(sourceFlags: Int): Int =
        sourceFlags and FLAG_GRANT_READ_URI_PERMISSION

    fun readOnlyForwardFlags(): Int = FLAG_GRANT_READ_URI_PERMISSION

    private fun hasReadPermission(flags: Int): Boolean =
        flags and FLAG_GRANT_READ_URI_PERMISSION != 0
}
