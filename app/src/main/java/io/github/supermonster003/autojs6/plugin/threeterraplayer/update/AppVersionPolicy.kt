package io.github.supermonster003.autojs6.plugin.threeterraplayer.update

internal data class ParsedAppVersion(
    val core: List<Int>,
    val prerelease: List<String>,
)

internal object AppVersionPolicy {

    fun parse(raw: String): ParsedAppVersion? {
        val normalized = raw.trim().removePrefix("v").removePrefix("V").substringBefore('+')
        if (normalized.isEmpty()) return null
        val coreText = normalized.substringBefore('-')
        val core = coreText.split('.')
            .takeIf { parts -> parts.size in 1..4 && parts.all { it.isNotEmpty() } }
            ?.map { part -> part.toIntOrNull()?.takeIf { it >= 0 } ?: return null }
            ?: return null
        val prerelease = normalized.substringAfter('-', missingDelimiterValue = "")
            .split('.')
            .filter(String::isNotEmpty)
        return ParsedAppVersion(core, prerelease)
    }

    fun isNewer(candidate: String, current: String): Boolean {
        val candidateVersion = parse(candidate) ?: return false
        val currentVersion = parse(current) ?: return false
        return compare(candidateVersion, currentVersion) > 0
    }

    private fun compare(first: ParsedAppVersion, second: ParsedAppVersion): Int {
        val coreSize = maxOf(first.core.size, second.core.size)
        repeat(coreSize) { index ->
            val compared = (first.core.getOrNull(index) ?: 0).compareTo(second.core.getOrNull(index) ?: 0)
            if (compared != 0) return compared
        }
        if (first.prerelease.isEmpty() && second.prerelease.isNotEmpty()) return 1
        if (first.prerelease.isNotEmpty() && second.prerelease.isEmpty()) return -1
        val prereleaseSize = maxOf(first.prerelease.size, second.prerelease.size)
        repeat(prereleaseSize) { index ->
            val firstPart = first.prerelease.getOrNull(index) ?: return -1
            val secondPart = second.prerelease.getOrNull(index) ?: return 1
            val firstNumber = firstPart.toIntOrNull()
            val secondNumber = secondPart.toIntOrNull()
            val compared = when {
                firstNumber != null && secondNumber != null -> firstNumber.compareTo(secondNumber)
                firstNumber != null -> -1
                secondNumber != null -> 1
                else -> firstPart.compareTo(secondPart, ignoreCase = true)
            }
            if (compared != 0) return compared
        }
        return 0
    }
}
