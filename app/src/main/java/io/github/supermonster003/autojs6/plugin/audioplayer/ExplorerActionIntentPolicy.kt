package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.ClipData
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioMimePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.ExplorerQueuePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.IntentFlagPolicy
import java.util.UUID
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.ExplorerActionTargetKeys
import org.autojs.plugin.explorer.api.ExplorerActionValues
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

internal data class ExplorerAudioTarget(
    val id: String,
    val track: AudioTrackRequest,
    val declaredSize: Long,
    val lastModified: Long,
)

internal data class ExplorerAudioRequest(
    val requestId: String,
    val parentUri: Uri,
    val parentDisplayPath: String,
    val targets: List<ExplorerAudioTarget>,
    val hostSession: IExplorerActionHostSession?,
)

internal enum class ExplorerRequestRejection {
    MISSING_INTENT,
    ACTION,
    ACTION_ID,
    PROTOCOL_VERSION,
    SOURCE_SURFACE,
    FLAGS,
    REQUEST_ID,
    PARENT_URI,
    PARENT_DISPLAY_PATH,
    TARGETS,
    TARGET_COUNT,
    CLIP_DATA,
    CLIP_COUNT,
    TARGET_ID,
    TARGET_URI,
    TARGET_NOT_DIRECT_CHILD,
    TARGET_CLIP_URI,
    TARGET_DISPLAY_NAME,
    TARGET_KIND,
    TARGET_MIME_TYPE,
    TARGET_SIZE_MISSING,
    TARGET_SIZE,
    TARGET_LAST_MODIFIED_MISSING,
    TARGET_LAST_MODIFIED,
    DUPLICATE_TARGET_ID,
    DUPLICATE_TARGET_URI,
    DATA_URI,
    ENVELOPE_MIME_TYPE,
    SINGLE_TARGET_MIME_MISMATCH,
    HOST_VERSION,
    HOST_SESSION_MISSING,
    HOST_SESSION_BINDER_MISSING,
    HOST_SESSION_DESCRIPTOR,
    UNEXPECTED,
}

internal sealed interface ExplorerRequestResolution {
    data class Accepted(val request: ExplorerAudioRequest) : ExplorerRequestResolution

    data class Rejected(
        val reason: ExplorerRequestRejection,
        val cause: Throwable? = null,
    ) : ExplorerRequestResolution
}

/** Strictly validates the complete Explorer Action v12 read-only contract. */
internal object ExplorerActionIntentPolicy {

    private const val INVALID_DECLARED_SIZE = -1L
    private const val MAX_DECLARED_SIZE = 8L * 1024L * 1024L * 1024L * 1024L
    private const val MAX_DISPLAY_NAME_LENGTH = 256
    private const val MAX_REQUEST_ID_LENGTH = 36

    fun resolve(intent: Intent?): ExplorerAudioRequest? =
        (resolveDetailed(intent) as? ExplorerRequestResolution.Accepted)?.request

    fun resolveDetailed(intent: Intent?): ExplorerRequestResolution = try {
        ExplorerRequestResolution.Accepted(resolveUnchecked(intent))
    } catch (rejection: InvalidExplorerRequest) {
        ExplorerRequestResolution.Rejected(rejection.reason)
    } catch (error: Throwable) {
        ExplorerRequestResolution.Rejected(ExplorerRequestRejection.UNEXPECTED, error)
    }

    private fun resolveUnchecked(intent: Intent?): ExplorerAudioRequest {
        intent ?: reject(ExplorerRequestRejection.MISSING_INTENT)
        if (intent.action != ExplorerActionPluginActions.EXECUTE) reject(ExplorerRequestRejection.ACTION)
        val actionId = intent.getStringExtra(ExplorerActionIntentExtras.ACTION_ID)
        val multipleAction = when (actionId) {
            AudioPlayerPlugin.ACTION_ID -> false
            AudioPlayerPlugin.ACTION_SELECTION_ID -> true
            else -> reject(ExplorerRequestRejection.ACTION_ID)
        }
        val protocolVersion = intent.getIntExtra(
            ExplorerActionIntentExtras.PROTOCOL_VERSION,
            Int.MIN_VALUE,
        )
        // AutoJs6 can retain an older, already-validated action descriptor in memory while a
        // plugin is replaced. V4 through V12 share this read-only request envelope; accepting the
        // advertised compatibility floor keeps that in-flight cache usable without granting any
        // additional URI, write, or host-session capability.
        if (
            protocolVersion !in
            AudioPlayerPlugin.MIN_COMPATIBLE_REQUEST_PROTOCOL_VERSION..AudioPlayerPlugin.PROTOCOL_VERSION
        ) {
            reject(ExplorerRequestRejection.PROTOCOL_VERSION)
        }
        if (
            intent.getStringExtra(ExplorerActionIntentExtras.SOURCE_SURFACE) !=
            ExplorerActionIntentValues.SOURCE_SURFACE_MAIN
        ) {
            reject(ExplorerRequestRejection.SOURCE_SURFACE)
        }
        if (!IntentFlagPolicy.isValidExplorerRequest(intent.flags)) reject(ExplorerRequestRejection.FLAGS)

        val requestId = canonicalRequestId(
            intent.getStringExtra(ExplorerActionIntentExtras.REQUEST_ID),
        ) ?: reject(ExplorerRequestRejection.REQUEST_ID)
        val parentUri = intent.parcelableUriExtra(ExplorerActionIntentExtras.PARENT_URI)
            ?.takeIf(::isPlainContentUri)
            ?: reject(ExplorerRequestRejection.PARENT_URI)
        val parentDisplayPath = validateParentDisplayPath(
            intent.getStringExtra(ExplorerActionIntentExtras.PARENT_DISPLAY_PATH),
        ) ?: reject(ExplorerRequestRejection.PARENT_DISPLAY_PATH)
        val targetBundles = intent.parcelableBundleArrayListExtra(ExplorerActionIntentExtras.TARGETS)
            ?: reject(ExplorerRequestRejection.TARGETS)
        if (!ExplorerQueuePolicy.isValidTargetCount(targetBundles.size, multipleAction)) {
            reject(ExplorerRequestRejection.TARGET_COUNT)
        }

        val clipData = intent.clipData ?: reject(ExplorerRequestRejection.CLIP_DATA)
        if (clipData.itemCount != targetBundles.size) reject(ExplorerRequestRejection.CLIP_COUNT)
        val ids = ArrayList<String>(targetBundles.size)
        val uriStrings = ArrayList<String>(targetBundles.size)
        val targets = targetBundles.mapIndexed { index, target ->
            val id = validateOpaqueId(target.getString(ExplorerActionTargetKeys.ID))
                ?: reject(ExplorerRequestRejection.TARGET_ID)
            ids += id
            val uri = target.parcelableUri(ExplorerActionTargetKeys.URI)
                ?.takeIf(::isPlainContentUri)
                ?: reject(ExplorerRequestRejection.TARGET_URI)
            if (!isStrictDescendant(parentUri, uri)) {
                reject(ExplorerRequestRejection.TARGET_NOT_DIRECT_CHILD)
            }
            if (!clipData.getItemAt(index).isExactUri(uri)) {
                reject(ExplorerRequestRejection.TARGET_CLIP_URI)
            }
            uriStrings += uri.toString()
            val displayName = validateDisplayName(
                target.getString(ExplorerActionTargetKeys.DISPLAY_NAME),
                uri,
            ) ?: reject(ExplorerRequestRejection.TARGET_DISPLAY_NAME)
            if (
                target.getInt(ExplorerActionTargetKeys.KIND, Int.MIN_VALUE) !=
                ExplorerActionValues.TARGET_FILE
            ) {
                reject(ExplorerRequestRejection.TARGET_KIND)
            }
            val mimeType = AudioMimePolicy.resolve(
                target.getString(ExplorerActionTargetKeys.MIME_TYPE),
                displayName,
            ) ?: reject(ExplorerRequestRejection.TARGET_MIME_TYPE)
            if (!target.containsKey(ExplorerActionTargetKeys.SIZE)) {
                reject(ExplorerRequestRejection.TARGET_SIZE_MISSING)
            }
            val declaredSize = target.getLong(ExplorerActionTargetKeys.SIZE, INVALID_DECLARED_SIZE)
                .takeIf { it in 0L..MAX_DECLARED_SIZE }
                ?: reject(ExplorerRequestRejection.TARGET_SIZE)
            if (!target.containsKey(ExplorerActionTargetKeys.LAST_MODIFIED)) {
                reject(ExplorerRequestRejection.TARGET_LAST_MODIFIED_MISSING)
            }
            val lastModified = target.getLong(ExplorerActionTargetKeys.LAST_MODIFIED, Long.MIN_VALUE)
                .takeIf { it >= INVALID_DECLARED_SIZE }
                ?: reject(ExplorerRequestRejection.TARGET_LAST_MODIFIED)
            ExplorerAudioTarget(
                id = id,
                track = AudioTrackRequest(uri, mimeType, displayName),
                declaredSize = declaredSize,
                lastModified = lastModified,
            )
        }
        if (!ExplorerQueuePolicy.hasUniqueNonBlankValues(ids)) {
            reject(ExplorerRequestRejection.DUPLICATE_TARGET_ID)
        }
        if (!ExplorerQueuePolicy.hasUniqueNonBlankValues(uriStrings)) {
            reject(ExplorerRequestRejection.DUPLICATE_TARGET_URI)
        }
        if (intent.data != targets.first().track.uri) reject(ExplorerRequestRejection.DATA_URI)
        val envelopeMimeType = normalizeEnvelopeMimeType(
            value = intent.type,
            targets = targets,
        ) ?: reject(ExplorerRequestRejection.ENVELOPE_MIME_TYPE)
        if (targets.size == 1 && envelopeMimeType != targets.single().track.mimeType) {
            reject(ExplorerRequestRejection.SINGLE_TARGET_MIME_MISMATCH)
        }

        val hostVersion = intent.getLongExtra(ExplorerActionIntentExtras.HOST_VERSION_CODE, Long.MIN_VALUE)
        if (hostVersion < AudioPlayerPlugin.REQUIRED_HOST_VERSION) reject(ExplorerRequestRejection.HOST_VERSION)
        val hostSession = resolveOptionalHostSession(intent, targets.size)

        return ExplorerAudioRequest(
            requestId = requestId,
            parentUri = parentUri,
            parentDisplayPath = parentDisplayPath,
            targets = targets,
            hostSession = hostSession.takeUnless { it === NO_HOST_SESSION },
        )
    }

    private fun normalizeEnvelopeMimeType(value: String?, targets: List<ExplorerAudioTarget>): String? =
        if (targets.size == 1) {
            AudioMimePolicy.resolve(value, targets.single().track.displayName)
        } else {
            value?.takeIf { it == MULTIPLE_TARGET_MIME_TYPE }
        }

    private fun resolveOptionalHostSession(
        intent: Intent,
        targetCount: Int,
    ): IExplorerActionHostSession? {
        val sessionBundle = intent.parcelableBundleExtra(ExplorerActionIntentExtras.HOST_SESSION)
        if (targetCount == 1 && sessionBundle == null) return NO_HOST_SESSION
        if (targetCount > 1 && sessionBundle == null) reject(ExplorerRequestRejection.HOST_SESSION_MISSING)
        val binder = sessionBundle?.getBinder(ExplorerActionHostSessionKeys.BINDER)
            ?: reject(ExplorerRequestRejection.HOST_SESSION_BINDER_MISSING)
        if (runCatching { binder.interfaceDescriptor }.getOrNull() != IExplorerActionHostSession.DESCRIPTOR) {
            reject(ExplorerRequestRejection.HOST_SESSION_DESCRIPTOR)
        }
        return IExplorerActionHostSession.Stub.asInterface(binder)
    }

    private fun reject(reason: ExplorerRequestRejection): Nothing = throw InvalidExplorerRequest(reason)

    private fun validateDisplayName(value: String?, targetUri: Uri): String? {
        val name = value ?: return null
        if (name.length !in 1..MAX_DISPLAY_NAME_LENGTH || name.isBlank()) return null
        if (name == "." || name == "..") return null
        if (name.any(::isUnsafeNameCharacter)) return null
        if (targetUri.pathSegments.lastOrNull() != name) return null
        return name
    }

    private fun validateOpaqueId(value: String?): String? {
        val id = value ?: return null
        if (id.length !in 1..ExplorerActionProtocol.MAX_TARGET_ID_LENGTH) return null
        return id.takeIf { candidate ->
            candidate.none { it.isWhitespace() || isUnsafeUnicodeCharacter(it) }
        }
    }

    private fun canonicalRequestId(value: String?): String? {
        val requestId = value?.takeIf { it.length <= MAX_REQUEST_ID_LENGTH } ?: return null
        val parsed = runCatching { UUID.fromString(requestId) }.getOrNull() ?: return null
        return requestId.takeIf { parsed.toString().equals(requestId, ignoreCase = true) }
    }

    private fun validateParentDisplayPath(value: String?): String? {
        val path = value ?: return null
        if (path.length !in 1..ExplorerActionProtocol.MAX_PARENT_DISPLAY_PATH_LENGTH) return null
        return path.takeIf { candidate -> candidate.none(::isUnsafeUnicodeCharacter) }
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
        return targetSegments.size == parentSegments.size + 1 &&
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

    @Suppress("DEPRECATION")
    private fun Intent.parcelableBundleExtra(name: String): Bundle? = getParcelableExtra(name)

    @Suppress("DEPRECATION")
    private fun Intent.parcelableBundleArrayListExtra(name: String): ArrayList<Bundle>? =
        getParcelableArrayListExtra(name)

    @Suppress("DEPRECATION")
    private fun Bundle.parcelableUri(name: String): Uri? = getParcelable(name)

    private const val MULTIPLE_TARGET_MIME_TYPE = "*/*"

    /** Sentinel used to distinguish an allowed absent session from an invalid session. */
    private val NO_HOST_SESSION = object : IExplorerActionHostSession.Default() {}

    private class InvalidExplorerRequest(
        val reason: ExplorerRequestRejection,
    ) : RuntimeException(null, null, false, false)
}
