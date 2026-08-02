package io.github.supermonster003.autojs6.plugin.audioplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MimeTypePolicyTest {

    @Test
    fun normalizesCaseAndValidParameters() {
        assertEquals("audio/mpeg", MimeTypePolicy.normalize(" Audio/MPEG "))
        assertEquals(
            "audio/mpeg",
            MimeTypePolicy.normalize("Audio/MPEG ; charset=UTF-8"),
        )
        assertEquals(
            "audio/ogg",
            MimeTypePolicy.normalize("audio/ogg; codecs=opus; note=\"a;b\""),
        )
    }

    @Test
    fun recognizesOnlyTheAudioFamily() {
        assertTrue(MimeTypePolicy.isAudio("audio/mpeg"))
        assertTrue(MimeTypePolicy.isAudio(" Audio/* ; profile=default "))
        assertFalse(MimeTypePolicy.isAudio("video/mp4"))
        assertFalse(MimeTypePolicy.isAudio("application/ogg"))
        assertFalse(MimeTypePolicy.isAudio("*/*"))
        assertFalse(MimeTypePolicy.isAudio(null))
    }

    @Test
    fun rejectsMalformedMimeTypesAndParameters() {
        listOf(
            null,
            "",
            "audio",
            "/mpeg",
            "audio/",
            "audio/mpeg/extra",
            "audio /mpeg",
            "audio/m peg",
            "audio/\u0000mpeg",
            "audio\uFF0Fmpeg",
            "au*dio/mpeg",
            "*/mpeg",
            "audio/mpeg;",
            "audio/mpeg; charset",
            "audio/mpeg; charset=",
            "audio/mpeg; charset=ut f-8",
            "audio/mpeg; charset=\"unterminated",
            "audio/mpeg; charset=\"ok\"junk",
        ).forEach { mimeType ->
            assertNull(mimeType, MimeTypePolicy.normalize(mimeType))
        }
    }

    @Test
    fun rejectsOverlongInput() {
        assertNull(MimeTypePolicy.normalize("audio/" + "a".repeat(250)))
        assertNull(MimeTypePolicy.normalize("audio/mpeg; note=" + "a".repeat(1024)))
    }
}
