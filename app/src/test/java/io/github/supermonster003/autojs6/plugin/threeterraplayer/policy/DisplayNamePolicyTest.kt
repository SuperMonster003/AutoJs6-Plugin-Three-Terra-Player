package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplayNamePolicyTest {

    @Test
    fun stripsIsoControlsAndBidiFormattingCharacters() {
        val unsafe = buildString {
            append('\u0000')
            append('\u001F')
            append('\u007F')
            append('\u0085')
            append('\u009F')
            append('\u061C')
            append('\u200E')
            append('\u200F')
            append('\u202A')
            append('\u202B')
            append('\u202C')
            append('\u202D')
            append('\u202E')
            append(" reportgpj.exe ")
            append('\u2066')
            append('\u2067')
            append('\u2068')
            append('\u2069')
            append('\u200B')
            append('\u200C')
            append('\u200D')
            append('\uFEFF')
            append('\r')
            append('\n')
        }

        assertEquals("reportgpj.exe", DisplayNamePolicy.sanitize(unsafe))
    }

    @Test
    fun preservesVisibleUnicodeIncludingFullwidthCharacters() {
        assertEquals(
            "\uFF34\uFF52\uFF41\uFF43\uFF4B\uFF0E\uFF4D\uFF50\uFF13",
            DisplayNamePolicy.sanitize(
                "  \uFF34\uFF52\uFF41\uFF43\uFF4B\uFF0E\uFF4D\uFF50\uFF13  ",
            ),
        )
    }

    @Test
    fun trimsToMaximumLengthWithoutSplittingSurrogatePair() {
        assertEquals(
            "a".repeat(DisplayNamePolicy.MAX_LENGTH),
            DisplayNamePolicy.sanitize("a".repeat(300)),
        )

        val beforeBoundary = "a".repeat(DisplayNamePolicy.MAX_LENGTH - 1)
        val result = DisplayNamePolicy.sanitize(beforeBoundary + "\uD83C\uDFB5tail")
        assertEquals(beforeBoundary, result)
        assertFalse(result.last().isHighSurrogate())
    }

    @Test
    fun safelyValidatesIncomingExplorerNames() {
        assertNull(DisplayNamePolicy.sanitizeIncoming(null))
        assertNull(DisplayNamePolicy.sanitizeIncoming(" \u0000\u202E "))
        assertEquals(
            "track.mp3",
            DisplayNamePolicy.sanitizeIncoming(" \u202Etrack.mp3\n"),
        )

        assertTrue(
            DisplayNamePolicy.isSafeIncoming(
                "\uFF34\uFF52\uFF41\uFF43\uFF4B.mp3",
            ),
        )
        assertFalse(DisplayNamePolicy.isSafeIncoming(" \u202Etrack.mp3"))
        assertFalse(DisplayNamePolicy.isSafeIncoming("a".repeat(300)))
        assertFalse(DisplayNamePolicy.isSafeIncoming(""))
    }
}
