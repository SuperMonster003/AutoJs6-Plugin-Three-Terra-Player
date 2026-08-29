package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

import java.util.Locale

/** Strict, locale-independent MIME parsing used before media routing. */
internal object MimeTypePolicy {

    private const val MAX_MIME_TYPE_LENGTH = 255
    private const val MAX_INPUT_LENGTH = 1024
    private const val AUDIO_TYPE = "audio"

    fun normalize(mimeType: String?): String? {
        if (mimeType == null || mimeType.length > MAX_INPUT_LENGTH) return null
        if (mimeType.any(Char::isISOControl)) return null

        val candidate = mimeType.trim()
        if (candidate.isEmpty()) return null

        val firstParameter = candidate.indexOf(';')
        val baseType = if (firstParameter >= 0) {
            if (!hasValidParameters(candidate, firstParameter)) return null
            candidate.substring(0, firstParameter).trimEnd()
        } else {
            candidate
        }
        if (baseType.length !in 3..MAX_MIME_TYPE_LENGTH) return null

        val separator = baseType.indexOf('/')
        if (separator <= 0 || separator != baseType.lastIndexOf('/') || separator == baseType.lastIndex) {
            return null
        }

        val type = baseType.substring(0, separator)
        val subtype = baseType.substring(separator + 1)
        if (!type.all(::isTokenCharacter) || !subtype.all(::isTokenCharacter)) return null
        if ('*' in type && type != "*") return null
        if ('*' in subtype && subtype != "*") return null
        if (type == "*" && subtype != "*") return null

        return "$type/$subtype".lowercase(Locale.ROOT)
    }

    fun isAudio(mimeType: String?): Boolean = normalize(mimeType)
        ?.substringBefore('/') == AUDIO_TYPE

    private fun hasValidParameters(value: String, firstParameter: Int): Boolean {
        var index = firstParameter
        while (index < value.length) {
            if (value[index] != ';') return false
            index++
            index = value.skipSpaces(index)

            val nameStart = index
            while (index < value.length && isTokenCharacter(value[index])) index++
            if (index == nameStart) return false

            index = value.skipSpaces(index)
            if (index >= value.length || value[index] != '=') return false
            index++
            index = value.skipSpaces(index)
            if (index >= value.length) return false

            if (value[index] == '"') {
                index = value.skipQuotedParameter(index) ?: return false
            } else {
                val valueStart = index
                while (index < value.length && isTokenCharacter(value[index])) index++
                if (index == valueStart) return false
            }

            index = value.skipSpaces(index)
            if (index < value.length && value[index] != ';') return false
        }
        return true
    }

    private fun String.skipSpaces(startIndex: Int): Int {
        var index = startIndex
        while (index < length && this[index] == ' ') index++
        return index
    }

    private fun String.skipQuotedParameter(startIndex: Int): Int? {
        var index = startIndex + 1
        while (index < length) {
            val character = this[index]
            when {
                character == '"' -> return index + 1
                character == '\\' -> {
                    index++
                    if (index >= length || this[index].code !in 0x20..0x7E) return null
                }
                character.code !in 0x20..0x7E -> return null
            }
            index++
        }
        return null
    }

    private fun isTokenCharacter(character: Char): Boolean =
        character in 'a'..'z' ||
            character in 'A'..'Z' ||
            character in '0'..'9' ||
            character in "!#$%&'*+-.^_`|~"
}
