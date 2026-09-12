package io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist

import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import org.junit.Assert.*
import org.junit.Test

class PlaylistParserTest {
    private fun parse(text: String, extension: String = "m3u") =
        PlaylistParser.parse(ByteArrayInputStream(text.toByteArray()), "list.$extension")

    @Test fun m3uPreservesOrderTitlesAndRepeatedFiles() {
        val entries = parse("\uFEFF#EXTM3U\r\n#EXTINF:10,第一首\r\n\"song 2.mp3\"\r\n#comment\r\nsong1.mp3\r\nsong 2.mp3\r\n")
        assertEquals(listOf("song 2.mp3", "song1.mp3", "song 2.mp3"), entries.map { it.locations.single() })
        assertEquals(listOf("第一首", null, null), entries.map { it.title })
    }

    @Test fun playlistTitlesCannotBecomeFileRoutes() {
        val entries = parse("#EXTINF:-1,../unsafe\t/title\nsafe.mp3")
        assertEquals(".._unsafe_title", entries.single().title)
        assertEquals(listOf("safe.mp3"), entries.single().locations)
    }

    @Test fun plsOrdersNumericIndicesAndIgnoresUntrustedCount() {
        val entries = parse("[playlist]\nFile10=ten.mp3\nTitle2=Second\nFile2=two.mp3\nFile1=first.mp3\nNumberOfEntries=999999999\nVersion=2", "pls")
        assertEquals(listOf("first.mp3", "two.mp3", "ten.mp3"), entries.map { it.locations.single() })
        assertEquals("Second", entries[1].title)
    }

    @Test fun plsHonoursSectionsAndCaseInsensitiveKeys() {
        val entries = parse("[PlAyLiSt]\nfIlE1=first.mp3\n[other]\nFile2=hidden.mp3", "pls")
        assertEquals(listOf("first.mp3"), entries.map { it.locations.single() })
    }

    @Test fun xspfReadsNamespacesEscapesAndAlternativeLocations() {
        val entries = parse("""<playlist xmlns="http://xspf.org/ns/0/" version="1"><title>List</title><trackList><track><location>missing.mp3</location><location>a%20b.mp3</location><title>A &amp; B</title></track></trackList></playlist>""", "xspf")
        assertEquals(PlaylistEntry(listOf("missing.mp3", "a%20b.mp3"), "A & B"), entries.single())
    }

    @Test fun xspfResolvesNestedXmlBases() {
        val entries = parse("""<playlist xml:base="music/"><trackList xml:base="album/"><track><location>song.mp3</location></track></trackList></playlist>""", "xspf")
        assertEquals("music/album/song.mp3", entries.single().locations.single())
    }

    @Test fun xspfIgnoresForeignExtensionsAndMetadataTracks() {
        val entries = parse("""<playlist xmlns="http://xspf.org/ns/0/" xmlns:other="urn:other"><extension><trackList><track><location>metadata.mp3</location></track></trackList></extension><trackList><other:track><other:location>foreign.mp3</other:location></other:track><track><other:location>also-foreign.mp3</other:location><location>song.mp3</location></track></trackList></playlist>""", "xspf")
        assertEquals(listOf(PlaylistEntry(listOf("song.mp3"))), entries)
    }

    @Test fun wplReadsMediaInDocumentOrder() {
        val entries = parse("""<?wpl version="1.0"?><smil><head><title>List</title></head><body><seq><media src="b.mp4"/><media src="a.mp4"/></seq></body></smil>""", "wpl")
        assertEquals(listOf("b.mp4", "a.mp4"), entries.map { it.locations.single() })
    }

    @Test fun asxAliasesKeepOneEntryWithAlternativeRefs() {
        for (format in listOf("asx", "wax", "wvx")) {
            val entries = parse("""<ASX VERSION="3.0"><ENTRY><TITLE>Video</TITLE><REF HREF="a.wmv"/><REF HREF="b.wmv"/></ENTRY></ASX>""", format)
            assertEquals(PlaylistEntry(listOf("a.wmv", "b.wmv"), "Video"), entries.single())
        }
    }

    @Test fun mpcplSkipsCaptureDevicesAndPreservesCommas() {
        val entries = parse("MPCPLAYLIST\n2,type,0\n2,filename,two,parts.mp4\n2,label,Two, parts\n1,type,1\n1,filename,capture\n", "mpcpl")
        assertEquals(PlaylistEntry(listOf("two,parts.mp4"), "Two, parts"), entries.single())
    }

    @Test fun dplPreservesAsterisksInsideValues() {
        val entries = parse("DAUMPLAYLIST\n2*file*two.mp4\n1*file*one.mp4\n1*title*One * star\n", "dpl")
        assertEquals(listOf("one.mp4", "two.mp4"), entries.map { it.locations.single() })
        assertEquals("One * star", entries.first().title)
    }

    @Test fun utf16BomAndLegacyChineseAreDecoded() {
        for (charset in listOf(Charsets.UTF_16, Charset.forName("GB18030"))) {
            val bytes = "#EXTM3U\n音乐.mp3\n".toByteArray(charset)
            val entries = PlaylistParser.parse(ByteArrayInputStream(bytes), "list.m3u")
            assertEquals("音乐.mp3", entries.single().locations.single())
        }
    }

    @Test fun xmlEncodingDeclarationIsRespected() {
        val text = """<?xml version="1.0" encoding="windows-1252"?><asx><entry><ref href="café.wma"/></entry></asx>"""
        val entries = PlaylistParser.parse(ByteArrayInputStream(text.toByteArray(Charset.forName("windows-1252"))), "list.asx")
        assertEquals("café.wma", entries.single().locations.single())
    }

    @Test fun malformedUtf8M3u8IsRejected() {
        expectError(PlaylistError.INVALID) { PlaylistParser.parse(ByteArrayInputStream(byteArrayOf(0xc3.toByte(), 0x28)), "list.m3u8") }
    }

    @Test fun hlsMediaAndMasterManifestsAreNotFlattened() {
        for (tag in listOf("#EXT-X-TARGETDURATION:5", "#EXT-X-STREAM-INF:BANDWIDTH=128000")) {
            expectError(PlaylistError.HLS) { parse("#EXTM3U\n$tag\nsegment.ts", "m3u8") }
        }
    }

    @Test fun rejectsOversizeDocumentsAndExcessEntries() {
        expectError(PlaylistError.TOO_LARGE) { parse("a".repeat(PlaylistParser.MAX_BYTES + 1)) }
        expectError(PlaylistError.TOO_LARGE) { parse("song.mp3\n".repeat(PlaylistParser.MAX_ENTRIES + 1)) }
    }

    @Test fun rejectsDoctypesExternalEntitiesMalformedXmlAndDeepNesting() {
        val documents = listOf(
            """<!DOCTYPE playlist [<!ENTITY x SYSTEM "file:///secret">]><playlist><trackList><track><location>&x;</location></track></trackList></playlist>""",
            "<playlist><trackList></playlist>",
            "<playlist>" + "<extension>".repeat(10_000) + "</extension>".repeat(10_000) + "</playlist>",
        )
        documents.forEach { expectError(PlaylistError.INVALID) { parse(it, "xspf") } }
    }

    @Test fun rejectsWrongFormatHeadersAndBinaryFiles() {
        expectError(PlaylistError.INVALID) { parse("not a playlist", "pls") }
        expectError(PlaylistError.INVALID) { parse("not a playlist", "mpcpl") }
        expectError(PlaylistError.INVALID) { parse("\u0000binary", "m3u") }
        expectError(PlaylistError.INVALID) { parse("<asx/>", "xspf") }
    }

    @Test fun recognizesAliasesWithoutMisclassifyingMediaContainers() {
        assertEquals("m3u8", PlaylistParser.format("PLAYLIST.M3U8", "application/octet-stream"))
        assertEquals("xspf", PlaylistParser.format("opaque", "application/xspf+xml"))
        assertNull(PlaylistParser.format("movie.asf", "video/x-ms-asf"))
        assertNull(PlaylistParser.format("song.wma", "audio/x-ms-wma"))
        assertNull(PlaylistParser.format("disc.mpls"))
        assertNull(PlaylistParser.format("foo.fpl"))
    }

    private fun expectError(reason: PlaylistError, block: () -> Unit) {
        try { block(); fail("Expected $reason") } catch (error: PlaylistException) { assertEquals(reason, error.reason) }
    }
}
