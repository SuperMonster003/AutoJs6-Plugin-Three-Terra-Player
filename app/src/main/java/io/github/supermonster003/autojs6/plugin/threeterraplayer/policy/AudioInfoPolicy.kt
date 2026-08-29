package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import java.util.Locale
import kotlin.math.roundToLong

/**
 * Formats untrusted technical stream properties into a short display line.
 *
 * Out-of-range or unparsable values are dropped silently; an empty result means the caller
 * should hide the info line entirely.
 */
internal object AudioInfoPolicy {

    const val SEPARATOR = " · "

    private const val MIN_SAMPLE_RATE_HZ = 1_000
    private const val MAX_SAMPLE_RATE_HZ = 1_000_000
    private const val MIN_BITRATE_BPS = 1_000
    private const val MAX_BITRATE_BPS = 100_000_000
    private const val MAX_CODEC_LABEL_LENGTH = 12

    private val CODEC_LABELS = mapOf(
        "mpeg" to "MP3",
        "mp3" to "MP3",
        "mpga" to "MP3",
        "mp4" to "AAC",
        "mp4a-latm" to "AAC",
        "aac" to "AAC",
        "aac-adts" to "AAC",
        "flac" to "FLAC",
        "x-flac" to "FLAC",
        "ogg" to "OGG",
        "vorbis" to "Vorbis",
        "opus" to "OPUS",
        "wav" to "WAV",
        "x-wav" to "WAV",
        "vnd.wave" to "WAV",
        "wave" to "WAV",
        "raw" to "PCM",
        "ac3" to "AC-3",
        "eac3" to "E-AC-3",
        "amr" to "AMR",
        "3gpp" to "AMR",
        "amr-wb" to "AMR-WB",
        "x-matroska" to "MKA",
        "matroska" to "MKA",
        "midi" to "MIDI",
        "x-ms-wma" to "WMA",
        "wma" to "WMA",
    )

    fun format(mimeType: String?, sampleRateHz: Int?, bitrateBps: Int?): String =
        listOfNotNull(
            sampleRateLabel(sampleRateHz),
            codecLabel(mimeType),
            bitrateLabel(bitrateBps),
        ).joinToString(SEPARATOR)

    fun codecLabel(mimeType: String?): String? {
        val normalized = MimeTypePolicy.normalize(mimeType) ?: return null
        val subtype = normalized.substringAfter('/', missingDelimiterValue = "")
        if (subtype.isEmpty() || subtype == "*") return null
        CODEC_LABELS[subtype]?.let { return it }
        val cleaned = subtype.removePrefix("x-").removePrefix("vnd.")
        if (cleaned.isEmpty() || cleaned.length > MAX_CODEC_LABEL_LENGTH) return null
        return cleaned.uppercase(Locale.ROOT)
    }

    fun sampleRateLabel(sampleRateHz: Int?): String? {
        val rate = sampleRateHz?.takeIf { it in MIN_SAMPLE_RATE_HZ..MAX_SAMPLE_RATE_HZ } ?: return null
        val kilohertz = rate / 1000.0
        val text = if (rate % 1000 == 0) {
            (rate / 1000).toString()
        } else {
            String.format(Locale.ROOT, "%.3f", kilohertz).trimEnd('0').trimEnd('.')
        }
        return "$text kHz"
    }

    fun bitrateLabel(bitrateBps: Int?): String? {
        val bitrate = bitrateBps?.takeIf { it in MIN_BITRATE_BPS..MAX_BITRATE_BPS } ?: return null
        return "${(bitrate / 1000.0).roundToLong()} kbps"
    }
}
