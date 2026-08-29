package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AudioUriForwardingPolicyTest {

    @Test
    fun createsSanitizedReadOnlyPayloadForContentAudio() {
        val payload = AudioUriForwardingPolicy.forward(
            request(
                uri = "content://media/external/audio/42",
                scheme = "CONTENT",
                mimeType = " Audio/MPEG ; charset=binary ",
                displayName = " \u0000track\u202Egpj.mp3\n ",
            ),
        )

        assertEquals(
            AudioUriForwardPayload(
                targetUriString = "content://media/external/audio/42",
                mimeType = "audio/mpeg",
                displayName = "trackgpj.mp3",
                flags = IntentFlagPolicy.FLAG_GRANT_READ_URI_PERMISSION,
            ),
            payload,
        )
    }

    @Test
    fun rejectsNonContentAndSchemeConfusion() {
        listOf(
            request(uri = "file:///sdcard/Music/track.mp3", scheme = "file"),
            request(uri = "file:///sdcard/Music/track.mp3", scheme = "content"),
            request(uri = "content://media/audio/42", scheme = "file"),
            request(uri = "https://example.test/track.mp3", scheme = "https"),
            request(uri = "contentx://media/audio/42", scheme = "content"),
            request(uri = "content://media/audio/42", scheme = null),
            request(uri = null, scheme = "content"),
            request(uri = " content://media/audio/42", scheme = "content"),
            request(uri = "content://media/audio/42\n", scheme = "content"),
        ).forEach { input ->
            assertNull(input.toString(), AudioUriForwardingPolicy.forward(input))
        }
    }

    @Test
    fun rejectsNonAudioOrMalformedMimeType() {
        listOf(
            "video/mp4",
            "application/octet-stream",
            "audio",
            "audio/m peg",
            null,
        ).forEach { mimeType ->
            assertNull(
                mimeType,
                AudioUriForwardingPolicy.forward(request(mimeType = mimeType)),
            )
        }
    }

    @Test
    fun blankNameIsOmittedAndVisibleFullwidthNameIsPreserved() {
        assertNull(
            AudioUriForwardingPolicy.forward(request(displayName = " \u202E\u0000 "))
                ?.displayName,
        )
        assertEquals(
            "\uFF34\uFF52\uFF41\uFF43\uFF4B\uFF0E\uFF4D\uFF50\uFF13",
            AudioUriForwardingPolicy.forward(
                request(
                    displayName =
                        " \uFF34\uFF52\uFF41\uFF43\uFF4B\uFF0E\uFF4D\uFF50\uFF13 ",
                ),
            )
                ?.displayName,
        )
    }

    @Test
    fun arbitraryExtrasAreNotModeledInForwardPayload() {
        val payloadFields = AudioUriForwardPayload::class.java.declaredFields
            .filterNot { field -> field.isSynthetic || field.name.startsWith("$") }
            .map { field -> field.name }
            .toSet()

        assertEquals(
            setOf("targetUriString", "mimeType", "displayName", "flags"),
            payloadFields,
        )
    }

    private fun request(
        uri: String? = "content://media/external/audio/42",
        scheme: String? = "content",
        mimeType: String? = "audio/mpeg",
        displayName: String? = "track.mp3",
    ) = AudioUriForwardRequest(
        targetUriString = uri,
        targetScheme = scheme,
        mimeType = mimeType,
        displayName = displayName,
    )
}
