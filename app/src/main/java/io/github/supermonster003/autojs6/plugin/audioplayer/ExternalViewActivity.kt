package io.github.supermonster003.autojs6.plugin.audioplayer

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.OpenableColumns
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.DisplayNamePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.IntentFlagPolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.MimeTypePolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/** Public Android ACTION_VIEW ingress. It never forwards caller extras or broad URI grants. */
class ExternalViewActivity : Activity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val cancellationSignal = CancellationSignal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            finish()
            return
        }
        val incoming = intent
        val uri = validateExternalEnvelope(incoming)
        val mimeType = MimeTypePolicy.normalize(incoming.type)?.takeIf(MimeTypePolicy::isAudio)
        if (uri == null || mimeType == null) {
            finish()
            return
        }

        val resolution = scope.async(Dispatchers.IO) {
            resolveRequest(contentResolver, uri, mimeType, cancellationSignal)
        }
        scope.launch {
            val request = withTimeoutOrNull(URI_RESOLUTION_TIMEOUT_MILLIS) { resolution.await() }
            if (request == null) {
                cancellationSignal.cancel()
                resolution.cancel()
            } else {
                runCatching {
                    startActivity(AudioPlaybackContract.playerIntent(this@ExternalViewActivity, request, true))
                }
            }
            finish()
        }
    }

    override fun onDestroy() {
        cancellationSignal.cancel()
        scope.cancel()
        super.onDestroy()
    }

    private fun validateExternalEnvelope(intent: Intent): Uri? {
        if (!IntentFlagPolicy.isValidExternalView(intent.action, intent.flags)) return null
        return intent.data?.takeIf { uri ->
            uri.scheme == ContentResolver.SCHEME_CONTENT &&
                !uri.authority.isNullOrBlank() &&
                uri.isHierarchical &&
                uri.userInfo == null &&
                uri.port == -1 &&
                uri.query == null &&
                uri.fragment == null &&
                !uri.encodedPath.isNullOrBlank()
        }
    }

    private fun resolveRequest(
        resolver: ContentResolver,
        uri: Uri,
        declaredMimeType: String,
        signal: CancellationSignal,
    ): AudioPlaybackRequest? {
        val readable = runCatching {
            resolver.openFileDescriptor(uri, "r", signal)?.use { true } ?: false
        }.getOrDefault(false)
        if (!readable) return null
        val resolverMimeType = runCatching { resolver.getType(uri) }.getOrNull()
            ?.let(MimeTypePolicy::normalize)
        if (resolverMimeType != null && !MimeTypePolicy.isAudio(resolverMimeType)) return null
        val name = resolveDisplayName(resolver, uri, signal)
        return AudioPlaybackRequest(uri, resolverMimeType ?: declaredMimeType, name)
    }

    private fun resolveDisplayName(
        resolver: ContentResolver,
        uri: Uri,
        signal: CancellationSignal,
    ): String {
        val queried = runCatching {
            resolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null,
                signal,
            )?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst() && !cursor.isNull(index)) cursor.getString(index) else null
            }
        }.getOrNull()
        return sequenceOf(queried, uri.lastPathSegment?.substringAfterLast('/'), getString(R.string.unknown_audio))
            .filterNotNull()
            .map(DisplayNamePolicy::sanitize)
            .first(String::isNotBlank)
    }

    private companion object {
        const val URI_RESOLUTION_TIMEOUT_MILLIS = 15_000L
    }
}
