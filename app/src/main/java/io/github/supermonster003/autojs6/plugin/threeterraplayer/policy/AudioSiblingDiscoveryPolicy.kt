package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import io.github.supermonster003.autojs6.plugin.threeterraplayer.ThreeTerraPlayerPlugin
import java.util.Locale

internal data class AudioSiblingItem(
    val relativePath: String,
    val displayName: String,
    val kind: Int,
    val mimeType: String,
    val readable: Boolean,
    val symbolicLink: Boolean,
)

internal data class DiscoveredAudioTrack(
    val relativePath: String,
    val displayName: String,
    val mimeType: String,
)

internal data class DiscoveredAudioQueue(
    val tracks: List<DiscoveredAudioTrack>,
    val startIndex: Int,
)

/** Pure, bounded direct-sibling audio discovery with stable natural filename ordering. */
internal object AudioSiblingDiscoveryPolicy {

    private const val TARGET_KIND_FILE = 1

    fun discover(
        selectedDisplayName: String,
        selectedMimeType: String,
        siblings: List<AudioSiblingItem>,
    ): DiscoveredAudioQueue {
        require(isSafeName(selectedDisplayName))
        require(extension(selectedDisplayName) in ThreeTerraPlayerPlugin.EXTENSIONS)
        val tracksByName = siblings.asSequence()
            .filter { it.kind == TARGET_KIND_FILE && it.readable && !it.symbolicLink }
            .filter { it.relativePath == it.displayName && isSafeName(it.displayName) }
            .filter { extension(it.displayName) in ThreeTerraPlayerPlugin.EXTENSIONS }
            .distinctBy(AudioSiblingItem::relativePath)
            .associate { sibling ->
                sibling.displayName to DiscoveredAudioTrack(
                    relativePath = sibling.relativePath,
                    displayName = sibling.displayName,
                    mimeType = sibling.mimeType.takeIf { it.startsWith("audio/") } ?: "audio/*",
                )
            }
            .toMutableMap()
        tracksByName.putIfAbsent(
            selectedDisplayName,
            DiscoveredAudioTrack(selectedDisplayName, selectedDisplayName, selectedMimeType),
        )
        val sorted = tracksByName.values.sortedWith { first, second ->
            compareNaturally(first.displayName, second.displayName)
        }
        val selectedIndex = sorted.indexOfFirst { it.displayName == selectedDisplayName }
        check(selectedIndex >= 0)
        val maxEntries = ExplorerQueuePolicy.MAX_TARGETS
        val windowStart = (selectedIndex - maxEntries / 2)
            .coerceIn(0, (sorted.size - maxEntries).coerceAtLeast(0))
        val bounded = sorted.drop(windowStart).take(maxEntries).map { track ->
            if (track.displayName == selectedDisplayName) track.copy(relativePath = "") else track
        }
        return DiscoveredAudioQueue(
            tracks = bounded,
            startIndex = bounded.indexOfFirst { it.displayName == selectedDisplayName }.also {
                check(it >= 0)
            },
        )
    }

    fun compareNaturally(first: String, second: String): Int {
        var firstIndex = 0
        var secondIndex = 0
        var stableCaseComparison = 0
        while (firstIndex < first.length && secondIndex < second.length) {
            val firstCharacter = first[firstIndex]
            val secondCharacter = second[secondIndex]
            if (firstCharacter.isDigit() && secondCharacter.isDigit()) {
                val firstEnd = first.consumeDigits(firstIndex)
                val secondEnd = second.consumeDigits(secondIndex)
                val firstDigits = first.substring(firstIndex, firstEnd)
                val secondDigits = second.substring(secondIndex, secondEnd)
                val firstSignificant = firstDigits.trimStart('0').ifEmpty { "0" }
                val secondSignificant = secondDigits.trimStart('0').ifEmpty { "0" }
                val comparison = firstSignificant.length.compareTo(secondSignificant.length)
                    .takeIf { it != 0 }
                    ?: firstSignificant.compareTo(secondSignificant).takeIf { it != 0 }
                    ?: firstDigits.length.compareTo(secondDigits.length)
                if (comparison != 0) return comparison
                firstIndex = firstEnd
                secondIndex = secondEnd
                continue
            }
            val folded = firstCharacter.lowercaseChar().compareTo(secondCharacter.lowercaseChar())
            if (folded != 0) return folded
            if (stableCaseComparison == 0) {
                stableCaseComparison = firstCharacter.compareTo(secondCharacter)
            }
            firstIndex += 1
            secondIndex += 1
        }
        return first.length.compareTo(second.length).takeIf { it != 0 } ?: stableCaseComparison
    }

    private fun String.consumeDigits(start: Int): Int {
        var index = start
        while (index < length && this[index].isDigit()) index += 1
        return index
    }

    private fun extension(value: String): String =
        value.substringAfterLast('.', "").lowercase(Locale.ROOT)

    private fun isSafeName(value: String): Boolean =
        value.length in 1..DisplayNamePolicy.MAX_LENGTH && value.isNotBlank() &&
            value != "." && value != ".." && '/' !in value && '\\' !in value &&
            value.none { it.isISOControl() || Character.getType(it) == Character.FORMAT.toInt() }
}
