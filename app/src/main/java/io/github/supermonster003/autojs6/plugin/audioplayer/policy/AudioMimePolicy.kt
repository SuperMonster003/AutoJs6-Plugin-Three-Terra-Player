package io.github.supermonster003.autojs6.plugin.audioplayer.policy

import java.util.Locale

/**
 * Keeps the advertised audio extensions independent from Android's device-specific MimeTypeMap.
 *
 * AutoJs6 matches an Explorer Action by extension, but obtains the request MIME type from the
 * platform. Older Android releases and some OEM tables return a wildcard or an application MIME
 * for otherwise supported audio files. Those values are metadata, not an authority boundary, so
 * an allow-listed extension can safely supply a stable audio MIME type after the request source,
 * URI and grants have been validated by the gateway.
 */
internal object AudioMimePolicy {

    private val canonicalMimeByExtension = linkedMapOf(
        "aac" to "audio/aac",
        "ac3" to "audio/ac3",
        "amr" to "audio/amr",
        "awb" to "audio/amr-wb",
        "flac" to "audio/flac",
        "m4a" to "audio/mp4",
        "m4b" to "audio/mp4",
        "m4r" to "audio/mp4",
        "mka" to "audio/x-matroska",
        "mp1" to "audio/mpeg",
        "mp2" to "audio/mpeg",
        "mp3" to "audio/mpeg",
        "mpga" to "audio/mpeg",
        "oga" to "audio/ogg",
        "ogg" to "audio/ogg",
        "opus" to "audio/ogg",
        "wav" to "audio/wav",
        "wave" to "audio/wav",
        "wma" to "audio/x-ms-wma",
    )

    private val supportedApplicationMimeTypes = setOf(
        "application/x-ms-wma",
        "application/vnd.ms-wma",
        "video/x-ms-asf",
    )

    val supportedExtensions: Array<String>
        get() = canonicalMimeByExtension.keys.toTypedArray()

    fun isPotentialAudioMimeType(value: String?): Boolean {
        val normalized = normalizeDeclaredMimeType(value) ?: return false
        return normalized.startsWith(AUDIO_PREFIX) || normalized in supportedApplicationMimeTypes
    }

    /** Returns a normalized audio MIME type, or null when neither MIME nor extension is valid. */
    fun resolve(declaredMimeType: String?, displayName: String): String? {
        val normalized = normalizeDeclaredMimeType(declaredMimeType) ?: return null
        if (normalized.startsWith(AUDIO_PREFIX)) return normalized
        val extension = displayName.substringAfterLast('.', missingDelimiterValue = "")
            .lowercase(Locale.ROOT)
            .takeIf(String::isNotEmpty)
            ?: return null
        return canonicalMimeByExtension[extension]
    }

    private fun normalizeDeclaredMimeType(value: String?): String? {
        val raw = value ?: return null
        if (raw.isEmpty() || raw != raw.trim() || raw.length > MAX_MIME_TYPE_LENGTH) return null
        val normalized = raw.lowercase(Locale.ROOT)
        if (raw != normalized) return null
        if (normalized == WILDCARD_MIME_TYPE) return normalized
        val parts = normalized.split('/')
        if (
            parts.size != 2 ||
            !MIME_TOKEN.matches(parts[0]) ||
            (!MIME_TOKEN.matches(parts[1]) && parts[1] != WILDCARD_SUBTYPE)
        ) {
            return null
        }
        return normalized
    }

    private const val AUDIO_PREFIX = "audio/"
    private const val WILDCARD_MIME_TYPE = "*/*"
    private const val WILDCARD_SUBTYPE = "*"
    private const val MAX_MIME_TYPE_LENGTH = 255
    private val MIME_TOKEN = Regex("[a-z0-9][a-z0-9!#$&^_.+-]*")
}
