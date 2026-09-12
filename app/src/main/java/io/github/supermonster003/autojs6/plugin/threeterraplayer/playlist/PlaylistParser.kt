package io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.StringReader
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.SAXParserFactory
import org.w3c.dom.Element
import org.xml.sax.InputSource
import org.xml.sax.Attributes
import org.xml.sax.SAXParseException
import org.xml.sax.helpers.DefaultHandler

internal data class PlaylistEntry(val locations: List<String>, val title: String? = null)

internal enum class PlaylistError { INVALID, TOO_LARGE, HLS, EMPTY }

internal class PlaylistException(val reason: PlaylistError, cause: Throwable? = null) :
    Exception("Cannot load playlist: $reason", cause)

/** Bounded, Android-free parsers. Locations remain untrusted until resolved against a grant. */
internal object PlaylistParser {
    const val MAX_BYTES = 1_048_576
    const val MAX_ENTRIES = 4_096
    const val MAX_QUEUE_SIZE = 128

    val extensions = arrayOf("m3u", "m3u8", "pls", "xspf", "wpl", "asx", "wax", "wvx", "mpcpl", "dpl")
    val mimeTypes = arrayOf(
        "audio/x-mpegurl", "audio/mpegurl", "application/x-mpegurl", "application/vnd.apple.mpegurl",
        "audio/x-scpls", "application/xspf+xml", "application/vnd.ms-wpl",
        "application/x-mpc-playlist", "application/x-potplayer-playlist",
        "video/x-ms-asf", "application/x-ms-asx", "audio/x-ms-wax", "video/x-ms-wvx",
    )

    fun format(displayName: String, mimeType: String? = null): String? {
        val extension = displayName.substringAfterLast('.', "").lowercase(Locale.ROOT)
        if (extension in extensions) return extension
        // ASF is also a media container: its MIME alone cannot identify an ASX document.
        if (extension == "asf" || extension == "wma" || extension == "wmv") return null
        return when (mimeType?.substringBefore(';')?.trim()?.lowercase(Locale.ROOT)) {
            "audio/x-mpegurl", "audio/mpegurl", "application/x-mpegurl", "application/vnd.apple.mpegurl" -> "m3u"
            "audio/x-scpls" -> "pls"
            "application/xspf+xml" -> "xspf"
            "application/vnd.ms-wpl" -> "wpl"
            "application/x-mpc-playlist" -> "mpcpl"
            "application/x-potplayer-playlist" -> "dpl"
            "application/x-ms-asx", "audio/x-ms-wax", "video/x-ms-wvx" -> "asx"
            else -> null
        }
    }

    fun parse(input: InputStream, displayName: String, mimeType: String? = null): List<PlaylistEntry> {
        val format = format(displayName, mimeType) ?: throw PlaylistException(PlaylistError.INVALID)
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(8_192)
        while (true) {
            if (Thread.currentThread().isInterrupted) throw InterruptedException()
            val read = input.read(buffer, 0, minOf(buffer.size, MAX_BYTES + 1 - output.size()))
            if (read < 0) break
            if (read == 0) continue
            output.write(buffer, 0, read)
            if (output.size() > MAX_BYTES) throw PlaylistException(PlaylistError.TOO_LARGE)
        }
        return parse(output.toByteArray(), format)
    }

    private fun parse(bytes: ByteArray, format: String): List<PlaylistEntry> = try {
        val text = decode(bytes, format).removePrefix("\uFEFF")
        require('\u0000' !in text)
        val entries = when (format) {
            "m3u", "m3u8" -> parseM3u(text)
            "pls" -> parsePls(text)
            "mpcpl", "dpl" -> parseIndexed(text, format)
            else -> parseXml(text, format)
        }
        if (entries.size > MAX_ENTRIES) throw PlaylistException(PlaylistError.TOO_LARGE)
        entries
    } catch (error: PlaylistException) {
        throw error
    } catch (error: Exception) {
        throw PlaylistException(PlaylistError.INVALID, error)
    }

    private fun decode(bytes: ByteArray, format: String): String {
        fun starts(vararg prefix: Int) = bytes.size >= prefix.size &&
            prefix.indices.all { (bytes[it].toInt() and 0xff) == prefix[it] }
        fun strict(charset: Charset, offset: Int = 0) = charset.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes, offset, bytes.size - offset)).toString()
        if (starts(0xef, 0xbb, 0xbf)) return strict(Charsets.UTF_8, 3)
        if (starts(0xff, 0xfe)) return strict(Charsets.UTF_16LE, 2)
        if (starts(0xfe, 0xff)) return strict(Charsets.UTF_16BE, 2)
        if (starts(0x3c, 0x00)) return strict(Charsets.UTF_16LE)
        if (starts(0x00, 0x3c)) return strict(Charsets.UTF_16BE)
        val header = String(bytes, 0, minOf(256, bytes.size), Charsets.ISO_8859_1)
        val declared = Regex("""<\?xml[^>]*encoding\s*=\s*["']([^"']+)["']""", RegexOption.IGNORE_CASE)
            .find(header)?.groupValues?.get(1)
        if (declared != null) return strict(Charset.forName(declared))
        return runCatching { strict(Charsets.UTF_8) }.getOrElse {
            if (format == "m3u8") throw it
            // Common legacy Chinese lists; Western single-byte lists remain a deterministic fallback.
            runCatching { strict(Charset.forName("GB18030")) }
                .getOrElse { strict(Charset.forName("windows-1252")) }
        }
    }

    private fun parseM3u(text: String): List<PlaylistEntry> {
        val entries = ArrayList<PlaylistEntry>()
        var title: String? = null
        text.lineSequence().forEach { raw ->
            val line = raw.trim()
            when {
                line.startsWith("#EXT-X-", true) -> throw PlaylistException(PlaylistError.HLS)
                line.startsWith("#EXTINF:", true) -> title = line.substringAfter(',', "").cleanTitle()
                line.isEmpty() || line.startsWith('#') -> Unit
                else -> {
                    entries += PlaylistEntry(listOf(line.unquote()), title)
                    title = null
                    checkSize(entries.size)
                }
            }
        }
        return entries
    }

    private fun parsePls(text: String): List<PlaylistEntry> {
        val files = sortedMapOf<Int, String>()
        val titles = mutableMapOf<Int, String>()
        var inPlaylist = false
        var foundHeader = false
        text.lineSequence().forEach { raw ->
            val line = raw.trim()
            if (line.startsWith('[')) {
                inPlaylist = line.equals("[playlist]", true)
                foundHeader = foundHeader || inPlaylist
            } else if (inPlaylist && !line.startsWith(';') && !line.startsWith('#')) {
                val match = Regex("(File|Title)([0-9]+)\\s*=(.*)", RegexOption.IGNORE_CASE).matchEntire(line)
                if (match != null) {
                    val index = match.groupValues[2].toIntOrNull()?.takeIf { it > 0 } ?: return@forEach
                    val value = match.groupValues[3].trim().unquote()
                    if (match.groupValues[1].equals("File", true)) files[index] = value else titles[index] = value
                    checkSize(maxOf(files.size, titles.size))
                }
            }
        }
        require(foundHeader)
        return files.map { (index, value) -> PlaylistEntry(listOf(value), titles[index]?.cleanTitle()) }
    }

    private fun parseIndexed(text: String, format: String): List<PlaylistEntry> {
        val lines = text.lineSequence().map(String::trim).filter(String::isNotEmpty).toList()
        require(lines.firstOrNull().equals(if (format == "dpl") "DAUMPLAYLIST" else "MPCPLAYLIST", true))
        val files = sortedMapOf<Int, String>()
        val titles = mutableMapOf<Int, String>()
        val types = mutableMapOf<Int, String>()
        lines.drop(1).forEach { line ->
            val fields = line.split(if (format == "dpl") '*' else ',', limit = 3)
            if (fields.size != 3) return@forEach
            val index = fields[0].toIntOrNull()?.takeIf { it > 0 } ?: return@forEach
            when (fields[1].lowercase(Locale.ROOT)) {
                "file", "filename" -> files[index] = fields[2].unquote()
                "title", "label" -> titles[index] = fields[2]
                "type" -> types[index] = fields[2]
            }
            checkSize(maxOf(files.size, titles.size, types.size))
        }
        return files.filterKeys { format != "mpcpl" || types[it] == null || types[it] == "0" }
            .map { (index, value) -> PlaylistEntry(listOf(value), titles[index]?.cleanTitle()) }
    }

    private fun parseXml(text: String, format: String): List<PlaylistEntry> {
        // Reject declarations before parsing as Android XML implementations expose different features.
        require(!Regex("<!\\s*(DOCTYPE|ENTITY)", RegexOption.IGNORE_CASE).containsMatchIn(text))
        // Check nesting before constructing a DOM: some Android DOM builders recurse while parsing.
        SAXParserFactory.newInstance().newSAXParser().parse(InputSource(StringReader(text)), object : DefaultHandler() {
            private var depth = 0
            override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
                require(++depth <= 32)
            }
            override fun endElement(uri: String?, localName: String?, qName: String?) { depth-- }
            override fun resolveEntity(publicId: String?, systemId: String?): InputSource =
                throw PlaylistException(PlaylistError.INVALID)
            override fun error(error: SAXParseException) = throw error
            override fun fatalError(error: SAXParseException) = throw error
        })
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            isExpandEntityReferences = false
        }
        val builder = factory.newDocumentBuilder().apply {
            setEntityResolver { _, _ -> throw PlaylistException(PlaylistError.INVALID) }
            setErrorHandler(object : DefaultHandler() {
                override fun error(error: SAXParseException) = throw error
                override fun fatalError(error: SAXParseException) = throw error
            })
        }
        val root = builder.parse(InputSource(StringReader(text))).documentElement
        require(root.tag() == when (format) { "xspf" -> "playlist"; "wpl" -> "smil"; else -> "asx" })
        if (format == "xspf") require(root.isXspf())
        val entries = ArrayList<PlaylistEntry>()
        fun walk(element: Element, depth: Int) {
            require(depth <= 32)
            when {
                format == "xspf" && element.tag() == "track" &&
                    element.isXspf() && (element.parentNode as? Element)?.let {
                        it.tag() == "tracklist" && it.isXspf() && it.parentNode === root
                    } == true -> {
                    val locations = element.children().filter { it.tag() == "location" && it.isXspf() }
                        .map { resolveXmlBase(it, it.textContent.trim()) }
                    entries += PlaylistEntry(locations, element.children().firstOrNull { it.tag() == "title" && it.isXspf() }?.textContent?.cleanTitle())
                }
                format == "wpl" && element.tag() == "media" ->
                    entries.add(PlaylistEntry(listOf(resolveXmlBase(element, element.attribute("src"))), element.attribute("title").cleanTitle()))
                format in setOf("asx", "wax", "wvx") && element.tag() == "entry" -> {
                    val base = element.children().firstOrNull { it.tag() == "base" }?.attribute("href")
                        ?: root.children().firstOrNull { it.tag() == "base" }?.attribute("href")
                    val locations = element.children().filter { it.tag() == "ref" }.map { ref ->
                        val location = ref.attribute("href")
                        if (base.isNullOrEmpty()) location else URI(base.replace(" ", "%20")).resolve(location.replace(" ", "%20")).toString()
                    }
                    entries += PlaylistEntry(locations, element.children().firstOrNull { it.tag() == "title" }?.textContent?.cleanTitle())
                }
                format in setOf("asx", "wax", "wvx") && element.tag() == "entryref" ->
                    entries.add(PlaylistEntry(listOf(element.attribute("href"))))
            }
            checkSize(entries.size)
            element.children().forEach { walk(it, depth + 1) }
        }
        walk(root, 0)
        return entries
    }

    private fun resolveXmlBase(element: Element, value: String): String {
        val bases = ArrayList<String>()
        var node: Element? = element
        while (node != null) {
            node.getAttributeNS("http://www.w3.org/XML/1998/namespace", "base").takeIf(String::isNotEmpty)?.let(bases::add)
            node = node.parentNode as? Element
        }
        if (bases.isEmpty()) return value
        return (bases.asReversed() + value).fold(URI("")) { base, part -> base.resolve(part.replace(" ", "%20")) }.toString()
    }

    private fun Element.tag() = (localName ?: tagName).lowercase(Locale.ROOT)
    private fun Element.isXspf() = namespaceURI.isNullOrEmpty() || namespaceURI == "http://xspf.org/ns/0/"
    private fun Element.children(): List<Element> = (0 until childNodes.length).mapNotNull { childNodes.item(it) as? Element }
    private fun Element.attribute(name: String): String = (0 until attributes.length)
        .map { attributes.item(it) }.firstOrNull { it.nodeName.equals(name, true) }?.nodeValue.orEmpty()
    private fun String.unquote() = if (length >= 2 && first() == '"' && last() == '"') substring(1, length - 1) else this
    private fun String.cleanTitle(): String? = filterNot { it.isISOControl() || Character.getType(it) == Character.FORMAT.toInt() }
        .replace('/', '_').replace('\\', '_').trim().take(255).takeIf { it.isNotBlank() && it != "." && it != ".." }
    private fun checkSize(size: Int) { if (size > MAX_ENTRIES) throw PlaylistException(PlaylistError.TOO_LARGE) }
}
