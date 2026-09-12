package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistLoader
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistParser
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.AudioMimePolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.IntentFlagPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.MimeTypePolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/** Public Android ACTION_VIEW ingress. It never forwards caller extras or broad URI grants. */
class ExternalViewerActivity : Activity() {

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
        val mimeType = MimeTypePolicy.normalize(incoming.type)
            ?.takeIf { AudioMimePolicy.isPotentialAudioMimeType(it) || PlaylistParser.format("", it) != null }
        if (uri == null || mimeType == null) {
            finish()
            return
        }

        val resolution = scope.async(Dispatchers.IO) {
            val source = PlaylistLoader.source(this@ExternalViewerActivity, uri, mimeType)
            if (PlaylistParser.format(source.displayName, source.mimeType) != null) {
                PlaylistActivity.intent(this@ExternalViewerActivity, source)
            } else ContentAudioRequestResolver.resolve(this@ExternalViewerActivity, uri, mimeType, cancellationSignal)
                ?.let { track -> AudioPlaybackContract.playerIntent(this@ExternalViewerActivity, AudioPlaybackRequest(listOf(track)), true) }
        }
        scope.launch {
            val request = withTimeoutOrNull(URI_RESOLUTION_TIMEOUT_MILLIS) { resolution.await() }
            if (request == null) {
                cancellationSignal.cancel()
                resolution.cancel()
            } else {
                runCatching {
                    startActivity(request)
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

    private companion object {
        const val URI_RESOLUTION_TIMEOUT_MILLIS = 15_000L
    }
}
