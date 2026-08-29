package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

/** Pure cardinality and uniqueness checks for Explorer Action v4 playback targets. */
internal object ExplorerQueuePolicy {

    const val MAX_TARGETS = 128

    fun isValidTargetCount(targetCount: Int, multipleAction: Boolean): Boolean =
        if (multipleAction) {
            targetCount in 1..MAX_TARGETS
        } else {
            targetCount == 1
        }

    fun hasUniqueNonBlankValues(values: List<String>): Boolean =
        values.all(String::isNotBlank) && values.toSet().size == values.size
}
