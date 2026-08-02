package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioUriForwardRequest
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioUriForwardingPolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.DisplayNamePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.MimeTypePolicy

internal data class AudioPlaybackRequest(
    val uri: Uri,
    val mimeType: String,
    val displayName: String,
)

/** The only contract forwarded from an ingress Activity to private playback components. */
internal object AudioPlaybackContract {

    const val ACTION_START_PLAYBACK =
        "io.github.supermonster003.autojs6.plugin.audioplayer.action.START_PLAYBACK"
    const val EXTRA_DISPLAY_NAME =
        "io.github.supermonster003.autojs6.plugin.audioplayer.extra.DISPLAY_NAME"

    private const val ALLOWED_ACTIVITY_FLAGS =
        Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_SINGLE_TOP or
            Intent.FLAG_ACTIVITY_NEW_TASK
    private const val ALLOWED_SERVICE_FLAGS = Intent.FLAG_GRANT_READ_URI_PERMISSION

    fun playerIntent(context: Context, request: AudioPlaybackRequest, startPlayback: Boolean): Intent =
        Intent(context, AudioPlayerActivity::class.java).apply {
            action = if (startPlayback) ACTION_START_PLAYBACK else Intent.ACTION_VIEW
            putReadOnlyPayload(request)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

    fun serviceIntent(context: Context, request: AudioPlaybackRequest): Intent =
        Intent(context, AudioPlaybackService::class.java).apply {
            action = ACTION_START_PLAYBACK
            putReadOnlyPayload(request)
        }

    fun resolvePlayerIntent(intent: Intent?): AudioPlaybackRequest? =
        resolve(intent, setOf(ACTION_START_PLAYBACK, Intent.ACTION_VIEW), ALLOWED_ACTIVITY_FLAGS)

    fun resolveServiceIntent(intent: Intent?): AudioPlaybackRequest? =
        resolve(intent, setOf(ACTION_START_PLAYBACK), ALLOWED_SERVICE_FLAGS)

    private fun resolve(
        intent: Intent?,
        acceptedActions: Set<String>,
        allowedFlags: Int,
    ): AudioPlaybackRequest? {
        intent ?: return null
        if (intent.action !in acceptedActions) return null
        if (intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION == 0) return null
        if (intent.flags and allowedFlags.inv() != 0) return null
        val uri = intent.data?.takeIf { it.scheme == "content" && !it.authority.isNullOrBlank() } ?: return null
        val mimeType = MimeTypePolicy.normalize(intent.type)?.takeIf(MimeTypePolicy::isAudio) ?: return null
        val displayName = DisplayNamePolicy.sanitizeIncoming(
            intent.getStringExtra(EXTRA_DISPLAY_NAME),
        ) ?: return null
        val clipData = intent.clipData ?: return null
        if (clipData.itemCount != 1 || !clipData.getItemAt(0).isExactUri(uri)) return null
        return AudioPlaybackRequest(uri, mimeType, displayName)
    }

    private fun Intent.putReadOnlyPayload(request: AudioPlaybackRequest) {
        val payload = requireNotNull(
            AudioUriForwardingPolicy.forward(
                AudioUriForwardRequest(
                    targetUriString = request.uri.toString(),
                    targetScheme = request.uri.scheme,
                    mimeType = request.mimeType,
                    displayName = request.displayName,
                ),
            ),
        )
        val displayName = requireNotNull(payload.displayName)
        setDataAndType(request.uri, payload.mimeType)
        clipData = ClipData.newRawUri(displayName, request.uri)
        putExtra(EXTRA_DISPLAY_NAME, displayName)
        addFlags(payload.flags)
    }

    private fun ClipData.Item.isExactUri(expected: Uri): Boolean =
        uri == expected && text == null && htmlText == null && intent == null
}
