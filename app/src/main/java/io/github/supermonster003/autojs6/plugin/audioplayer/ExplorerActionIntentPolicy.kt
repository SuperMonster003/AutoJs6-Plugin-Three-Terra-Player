package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.ClipData
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.IntentFlagPolicy
import java.util.Locale
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions

internal data class ExplorerAudioRequest(
    val targetUri: Uri,
    val parentUri: Uri,
    val displayName: String,
    val declaredSize: Long,
    val mimeType: String,
)

/** Strictly validates the complete Explorer Action v2 read-only contract. */
internal object ExplorerActionIntentPolicy {

    private const val REQUIRED_CLIP_ITEM_COUNT = 2
    private const val INVALID_DECLARED_SIZE = -1L
    private const val MAX_DECLARED_SIZE = 8L * 1024L * 1024L * 1024L * 1024L
    private const val MAX_DISPLAY_NAME_LENGTH = 256
    fun resolve(intent: Intent?): ExplorerAudioRequest? = runCatching {
        resolveUnchecked(intent)
    }.getOrNull()

    private fun resolveUnchecked(intent: Intent?): ExplorerAudioRequest? {
        intent ?: return null
        if (intent.action != ExplorerActionPluginActions.EXECUTE) return null
        if (intent.getStringExtra(ExplorerActionIntentExtras.ACTION_ID) != AudioPlayerPlugin.ACTION_ID) return null
        if (
            intent.getIntExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, Int.MIN_VALUE) !=
            AudioPlayerPlugin.PROTOCOL_VERSION
        ) {
            return null
        }
        if (
            intent.getStringExtra(ExplorerActionIntentExtras.SOURCE_SURFACE) !=
            ExplorerActionIntentValues.SOURCE_SURFACE_MAIN
        ) {
            return null
        }
        if (!IntentFlagPolicy.isValidExplorerRequest(intent.flags)) return null

        val targetUri = intent.data?.takeIf(::isPlainContentUri) ?: return null
        val parentUri = intent.parcelableUriExtra(ExplorerActionIntentExtras.PARENT_URI)
            ?.takeIf(::isPlainContentUri)
            ?: return null
        if (!isStrictDescendant(parentUri, targetUri)) return null

        val clipData = intent.clipData ?: return null
        if (clipData.itemCount != REQUIRED_CLIP_ITEM_COUNT) return null
        if (!clipData.getItemAt(ExplorerActionIntentValues.CLIP_ITEM_TARGET_INDEX).isExactUri(targetUri)) return null
        if (!clipData.getItemAt(ExplorerActionIntentValues.CLIP_ITEM_PARENT_INDEX).isExactUri(parentUri)) return null

        val displayName = validateDisplayName(
            intent.getStringExtra(ExplorerActionIntentExtras.DISPLAY_NAME),
            targetUri,
        ) ?: return null
        if (!intent.hasExtra(ExplorerActionIntentExtras.SIZE)) return null
        val declaredSize = intent.getLongExtra(ExplorerActionIntentExtras.SIZE, INVALID_DECLARED_SIZE)
            .takeIf { it in 0L..MAX_DECLARED_SIZE }
            ?: return null
        val hostVersion = intent.getLongExtra(ExplorerActionIntentExtras.HOST_VERSION_CODE, Long.MIN_VALUE)
        if (hostVersion < AudioPlayerPlugin.REQUIRED_HOST_VERSION) return null
        val mimeType = normalizeAudioMimeType(intent.type) ?: return null

        return ExplorerAudioRequest(targetUri, parentUri, displayName, declaredSize, mimeType)
    }

    internal fun normalizeAudioMimeType(value: String?): String? {
        val raw = value ?: return null
        if (raw.isEmpty() || raw != raw.trim()) return null
        val normalized = raw.lowercase(Locale.ROOT)
        if (raw != normalized) return null
        val parts = normalized.split('/')
        if (parts.size != 2 || parts[0] != "audio") return null
        val subtype = parts[1]
        if (subtype.isEmpty() || subtype.length > 127) return null
        if (!MIME_TOKEN.matches(subtype) && subtype != "*") return null
        return normalized
    }

    private fun validateDisplayName(value: String?, targetUri: Uri): String? {
        val name = value ?: return null
        if (name.length !in 1..MAX_DISPLAY_NAME_LENGTH || name.isBlank()) return null
        if (name == "." || name == "..") return null
        if (name.any(::isUnsafeNameCharacter)) return null
        if (targetUri.pathSegments.lastOrNull() != name) return null
        return name
    }

    private fun isPlainContentUri(uri: Uri): Boolean {
        if (!uri.isHierarchical || uri.scheme != ContentResolver.SCHEME_CONTENT) return false
        if (uri.authority.isNullOrBlank() || uri.host.isNullOrBlank()) return false
        if (uri.userInfo != null || uri.port != -1 || uri.query != null || uri.fragment != null) return false
        val encodedPath = uri.encodedPath ?: return false
        if (!encodedPath.startsWith('/') || encodedPath.length <= 1) return false
        if (encodedPath.split('/').drop(1).any(String::isEmpty)) return false
        return uri.pathSegments.isNotEmpty() && uri.pathSegments.none { segment ->
            segment.isEmpty() || segment == "." || segment == ".." || segment.any(::isUnsafeUriCharacter)
        }
    }

    private fun isStrictDescendant(parentUri: Uri, targetUri: Uri): Boolean {
        if (parentUri.scheme != targetUri.scheme || parentUri.authority != targetUri.authority) return false
        val parentSegments = parentUri.pathSegments
        val targetSegments = targetUri.pathSegments
        return targetSegments.size > parentSegments.size &&
            targetSegments.take(parentSegments.size) == parentSegments
    }

    private fun ClipData.Item.isExactUri(expected: Uri): Boolean =
        uri == expected && text == null && htmlText == null && intent == null

    private fun isUnsafeNameCharacter(character: Char): Boolean =
        character == '/' || character == '\\' || isUnsafeUnicodeCharacter(character)

    private fun isUnsafeUriCharacter(character: Char): Boolean =
        character == '/' || character == '\\' || isUnsafeUnicodeCharacter(character)

    private fun isUnsafeUnicodeCharacter(character: Char): Boolean =
        character.isISOControl() || Character.getType(character) == Character.FORMAT.toInt()

    @Suppress("DEPRECATION")
    private fun Intent.parcelableUriExtra(name: String): Uri? = getParcelableExtra(name)

    private val MIME_TOKEN = Regex("[a-z0-9][a-z0-9!#$&^_.+-]*")
}
