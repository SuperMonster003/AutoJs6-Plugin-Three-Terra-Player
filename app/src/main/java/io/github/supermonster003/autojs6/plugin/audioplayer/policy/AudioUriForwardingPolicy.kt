package io.github.supermonster003.autojs6.plugin.audioplayer.policy

/** The complete allowlist of source data that may cross into the internal player intent. */
internal data class AudioUriForwardRequest(
    val targetUriString: String?,
    val targetScheme: String?,
    val mimeType: String?,
    val displayName: String?,
)

/** Arbitrary source extras are deliberately absent from this payload. */
internal data class AudioUriForwardPayload(
    val targetUriString: String,
    val mimeType: String,
    val displayName: String?,
    val flags: Int,
)

internal object AudioUriForwardingPolicy {

    private const val CONTENT_SCHEME = "content"

    fun forward(request: AudioUriForwardRequest): AudioUriForwardPayload? {
        val targetUriString = request.targetUriString
            ?.takeIf(String::isNotBlank)
            ?.takeIf { it == it.trim() }
            ?.takeIf { value -> value.none(Char::isISOControl) }
            ?: return null

        val suppliedScheme = request.targetScheme ?: return null
        if (!suppliedScheme.equals(CONTENT_SCHEME, ignoreCase = true)) return null
        if (!extractScheme(targetUriString).equals(CONTENT_SCHEME, ignoreCase = true)) return null

        val mimeType = MimeTypePolicy.normalize(request.mimeType)
            ?.takeIf(MimeTypePolicy::isAudio)
            ?: return null

        return AudioUriForwardPayload(
            targetUriString = targetUriString,
            mimeType = mimeType,
            displayName = DisplayNamePolicy.sanitizeIncoming(request.displayName),
            flags = IntentFlagPolicy.readOnlyForwardFlags(),
        )
    }

    private fun extractScheme(uriString: String): String? {
        val separator = uriString.indexOf(':')
        if (separator <= 0) return null

        val scheme = uriString.substring(0, separator)
        if (scheme.first() !in 'a'..'z' && scheme.first() !in 'A'..'Z') return null
        if (scheme.drop(1).any { character ->
                character !in 'a'..'z' &&
                    character !in 'A'..'Z' &&
                    character !in '0'..'9' &&
                    character !in "+-."
            }
        ) {
            return null
        }
        return scheme
    }
}
