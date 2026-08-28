package io.github.supermonster003.autojs6.plugin.audioplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AudioInfoPolicyTest {

    @Test
    fun mapsWellKnownMimeTypesToFriendlyCodecLabels() {
        assertEquals("MP3", AudioInfoPolicy.codecLabel("audio/mpeg"))
        assertEquals("AAC", AudioInfoPolicy.codecLabel("audio/mp4"))
        assertEquals("AAC", AudioInfoPolicy.codecLabel("audio/mp4a-latm"))
        assertEquals("FLAC", AudioInfoPolicy.codecLabel("audio/flac"))
        assertEquals("FLAC", AudioInfoPolicy.codecLabel("audio/x-flac"))
        assertEquals("Opus", AudioInfoPolicy.codecLabel("audio/opus"))
        assertEquals("WAV", AudioInfoPolicy.codecLabel("audio/x-wav"))
        assertEquals("PCM", AudioInfoPolicy.codecLabel("audio/raw"))
        assertEquals("MKA", AudioInfoPolicy.codecLabel("audio/x-matroska"))
        assertEquals("AMR", AudioInfoPolicy.codecLabel("audio/3gpp"))
    }

    @Test
    fun normalizesCasingParametersAndUnknownSubtypes() {
        assertEquals("MP3", AudioInfoPolicy.codecLabel("AUDIO/MPEG"))
        assertEquals("MP3", AudioInfoPolicy.codecLabel("audio/mpeg; charset=utf-8"))
        assertEquals("BASIC", AudioInfoPolicy.codecLabel("audio/basic"))
        assertNull(AudioInfoPolicy.codecLabel(null))
        assertNull(AudioInfoPolicy.codecLabel(""))
        assertNull(AudioInfoPolicy.codecLabel("audio/*"))
        assertNull(AudioInfoPolicy.codecLabel("not a mime type"))
        assertNull(AudioInfoPolicy.codecLabel("audio/x-very-long-unknown-subtype"))
    }

    @Test
    fun formatsSampleRatesWithTrimmedKilohertz() {
        assertEquals("44.1 kHz", AudioInfoPolicy.sampleRateLabel(44_100))
        assertEquals("48 kHz", AudioInfoPolicy.sampleRateLabel(48_000))
        assertEquals("22.05 kHz", AudioInfoPolicy.sampleRateLabel(22_050))
        assertEquals("8 kHz", AudioInfoPolicy.sampleRateLabel(8_000))
        assertEquals("192 kHz", AudioInfoPolicy.sampleRateLabel(192_000))
        assertNull(AudioInfoPolicy.sampleRateLabel(null))
        assertNull(AudioInfoPolicy.sampleRateLabel(0))
        assertNull(AudioInfoPolicy.sampleRateLabel(999))
        assertNull(AudioInfoPolicy.sampleRateLabel(1_000_001))
    }

    @Test
    fun formatsBitratesAsRoundedKbps() {
        assertEquals("320 kbps", AudioInfoPolicy.bitrateLabel(320_000))
        assertEquals("128 kbps", AudioInfoPolicy.bitrateLabel(127_501))
        assertEquals("1411 kbps", AudioInfoPolicy.bitrateLabel(1_411_200))
        assertNull(AudioInfoPolicy.bitrateLabel(null))
        assertNull(AudioInfoPolicy.bitrateLabel(0))
        assertNull(AudioInfoPolicy.bitrateLabel(999))
        assertNull(AudioInfoPolicy.bitrateLabel(100_000_001))
    }

    @Test
    fun joinsAvailablePartsAndOmitsMissingOnes() {
        assertEquals(
            "MP3 · 44.1 kHz · 320 kbps",
            AudioInfoPolicy.format("audio/mpeg", 44_100, 320_000),
        )
        assertEquals("MP3 · 320 kbps", AudioInfoPolicy.format("audio/mpeg", null, 320_000))
        assertEquals("44.1 kHz", AudioInfoPolicy.format(null, 44_100, null))
        assertEquals("", AudioInfoPolicy.format(null, null, null))
        assertEquals("", AudioInfoPolicy.format("audio/*", -5, 0))
    }
}
