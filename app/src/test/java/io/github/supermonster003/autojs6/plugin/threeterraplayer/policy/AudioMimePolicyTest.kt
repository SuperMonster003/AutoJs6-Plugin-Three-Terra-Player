package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AudioMimePolicyTest {

    @Test
    fun exposesEveryCanonicalExtensionInStableOrder() {
        assertArrayEquals(
            arrayOf(
                "aac", "ac3", "amr", "awb", "flac", "m4a", "m4b", "m4r", "mka",
                "mp1", "mp2", "mp3", "mpga", "oga", "ogg", "opus", "wav", "wave", "wma",
            ),
            AudioMimePolicy.supportedExtensions,
        )
    }

    @Test
    fun preservesValidNormalizedAudioMimeType() {
        assertEquals("audio/x-wav", AudioMimePolicy.resolve("audio/x-wav", "track.wav"))
        assertEquals("audio/*", AudioMimePolicy.resolve("audio/*", "track.unknown"))
    }

    @Test
    fun resolvesSparsePlatformMimeValuesFromAdvertisedExtension() {
        val cases = mapOf(
            "recording.ac3" to "audio/ac3",
            "book.M4B" to "audio/mp4",
            "ring.m4r" to "audio/mp4",
            "layer.mp1" to "audio/mpeg",
            "stream.oga" to "audio/ogg",
            "track.ogg" to "audio/ogg",
            "voice.opus" to "audio/ogg",
            "sample.wave" to "audio/wav",
            "legacy.wma" to "audio/x-ms-wma",
        )

        cases.forEach { (displayName, expected) ->
            val platformValue = if (displayName.endsWith(".oga") || displayName.endsWith(".ogg")) {
                "application/ogg"
            } else {
                "*/*"
            }
            assertEquals(expected, AudioMimePolicy.resolve(platformValue, displayName))
        }
    }

    @Test
    fun recognizesAudioAndLegacyApplicationMimeTypesForPublicRouting() {
        assertEquals(true, AudioMimePolicy.isPotentialAudioMimeType("audio/x-ms-wma"))
        assertEquals(true, AudioMimePolicy.isPotentialAudioMimeType("application/x-ms-wma"))
        assertEquals(true, AudioMimePolicy.isPotentialAudioMimeType("video/x-ms-asf"))
        assertEquals(false, AudioMimePolicy.isPotentialAudioMimeType("application/pdf"))
    }

    @Test
    fun rejectsMalformedMimeAndUnknownFallbackExtension() {
        listOf(null, "", " audio/mpeg", "Audio/MPEG", "audio", "audio/mpeg; charset=utf-8")
            .forEach { value -> assertNull(AudioMimePolicy.resolve(value, "track.mp3")) }
        assertNull(AudioMimePolicy.resolve("*/*", "track.txt"))
        assertNull(AudioMimePolicy.resolve("application/octet-stream", "track"))
    }
}
