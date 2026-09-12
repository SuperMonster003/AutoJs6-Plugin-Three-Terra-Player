package io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist

import java.net.URI
import java.net.URLDecoder

/** Converts local references to paths inside an explicitly authorized directory. */
internal object PlaylistLocationPolicy {
    fun relativeSegments(location: String, parentPath: String? = null): List<String>? = runCatching {
        if (location.isBlank() || location.length > 16_384 || location.any(::unsafe)) return null
        val normalized = location.replace('\\', '/')
        val path = when {
            normalized.startsWith("file:", true) -> {
                val uri = URI(normalized.replace(" ", "%20"))
                if (!uri.authority.isNullOrEmpty() || uri.rawQuery != null || uri.rawFragment != null) return null
                uri.path ?: return null
            }
            SCHEME.containsMatchIn(normalized) -> return null
            else -> normalized
        }
        val relative = if (path.startsWith('/')) {
            val parent = parentPath?.replace('\\', '/')?.trimEnd('/') ?: return null
            if (!path.startsWith("$parent/")) return null
            path.removePrefix("$parent/")
        } else path
        val result = ArrayList<String>()
        relative.split('/').forEach { segment ->
            when (segment) {
                "." -> Unit
                ".." -> if (result.isEmpty()) return null else result.removeAt(result.lastIndex)
                else -> {
                    if (!isSafeName(segment)) return null
                    result += segment
                }
            }
        }
        result.takeIf { it.isNotEmpty() }
    }.getOrNull()

    fun decodedLocation(location: String): String? = runCatching {
        // URLDecoder implements forms, so protect literal plus signs in filenames.
        URLDecoder.decode(location.replace("+", "%2B"), "UTF-8").takeIf { it != location }
    }.getOrNull()

    fun contentUri(location: String): String? = runCatching {
        val uri = URI(location)
        location.takeIf {
            uri.scheme == "content" && !uri.isOpaque && !uri.rawAuthority.isNullOrBlank() &&
                uri.rawUserInfo == null && uri.port == -1 && uri.rawQuery == null && uri.rawFragment == null &&
                !uri.rawPath.isNullOrBlank() && location.none(::unsafe)
        }
    }.getOrNull()

    fun isSafeName(value: String): Boolean = value.length in 1..255 && value.isNotBlank() &&
        value != "." && value != ".." && '/' !in value && '\\' !in value && ':' !in value && value.none(::unsafe)

    private fun unsafe(value: Char) = value.isISOControl() || Character.getType(value) == Character.FORMAT.toInt()
    private val SCHEME = Regex("^[A-Za-z][A-Za-z0-9+.-]*:")
}
