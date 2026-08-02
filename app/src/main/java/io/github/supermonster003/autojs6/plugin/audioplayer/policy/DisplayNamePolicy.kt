package io.github.supermonster003.autojs6.plugin.audioplayer.policy

/**
 * Treats a display name as untrusted presentation data.
 *
 * The result is suitable for showing in plugin UI. It must never be used as a path.
 */
internal object DisplayNamePolicy {

    const val MAX_LENGTH = 256

    fun sanitize(displayName: String): String {
        val filtered = displayName.filterNot(::isUnsafeFormattingCharacter).trim()
        if (filtered.length <= MAX_LENGTH) return filtered

        var endIndex = MAX_LENGTH
        if (
            Character.isHighSurrogate(filtered[endIndex - 1]) &&
            Character.isLowSurrogate(filtered[endIndex])
        ) {
            endIndex--
        }
        return filtered.substring(0, endIndex)
    }

    /**
     * Sanitizes an optional Explorer-provided name and rejects names with no visible content.
     */
    fun sanitizeIncoming(displayName: String?): String? = displayName
        ?.let(::sanitize)
        ?.takeIf(String::isNotEmpty)

    /**
     * Returns true only when an incoming name is already in its canonical safe form.
     */
    fun isSafeIncoming(displayName: String?): Boolean {
        val sanitized = sanitizeIncoming(displayName) ?: return false
        return sanitized == displayName
    }

    private fun isUnsafeFormattingCharacter(character: Char): Boolean =
        character.isISOControl() ||
            Character.getType(character) == Character.FORMAT.toInt()
}
