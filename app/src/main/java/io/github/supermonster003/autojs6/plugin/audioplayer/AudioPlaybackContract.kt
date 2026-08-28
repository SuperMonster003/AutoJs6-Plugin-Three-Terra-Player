package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioUriForwardRequest
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioUriForwardingPolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.DisplayNamePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.ExplorerQueuePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.MimeTypePolicy
import org.autojs.plugin.explorer.api.ExplorerActionHostSessionKeys
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

internal data class AudioTrackRequest(
    val uri: Uri,
    val mimeType: String,
    val displayName: String,
    val hostRelativePath: String? = null,
)

internal data class AudioPlaybackRequest(
    val tracks: List<AudioTrackRequest>,
    val startIndex: Int = 0,
    val hostSession: IExplorerActionHostSession? = null,
    val hostTargetId: String? = null,
) {
    constructor(uri: Uri, mimeType: String, displayName: String) : this(
        tracks = listOf(AudioTrackRequest(uri, mimeType, displayName)),
    )

    init {
        require(tracks.size in 1..ExplorerQueuePolicy.MAX_TARGETS)
        require(startIndex in tracks.indices)
        require((hostSession == null) == (hostTargetId == null))
        if (hostSession == null) {
            require(tracks.all { it.hostRelativePath == null })
        } else {
            require(tracks.all { it.hostRelativePath != null })
            // The empty route identifies the originally selected, directly granted target.
            // It is independent of the item that is currently active after queue navigation.
            require(tracks.count { it.hostRelativePath == "" } == 1)
        }
    }

    val currentTrack: AudioTrackRequest
        get() = tracks[startIndex]

    val uri: Uri
        get() = currentTrack.uri

    val mimeType: String
        get() = currentTrack.mimeType

    val displayName: String
        get() = currentTrack.displayName
}

/** The only contract forwarded from an ingress Activity to private playback components. */
internal object AudioPlaybackContract {

    const val ACTION_START_PLAYBACK =
        "io.github.supermonster003.autojs6.plugin.audioplayer.action.START_PLAYBACK"

    const val MEDIA_EXTRA_SOURCE_URI =
        "io.github.supermonster003.autojs6.plugin.audioplayer.media.SOURCE_URI"
    const val MEDIA_EXTRA_SOURCE_MIME_TYPE =
        "io.github.supermonster003.autojs6.plugin.audioplayer.media.SOURCE_MIME_TYPE"
    const val MEDIA_EXTRA_SOURCE_DISPLAY_NAME =
        "io.github.supermonster003.autojs6.plugin.audioplayer.media.SOURCE_DISPLAY_NAME"

    const val HOST_SOURCE_SCHEME = "autojs6-explorer"

    private const val EXTRA_TRACK_MIME_TYPES =
        "io.github.supermonster003.autojs6.plugin.audioplayer.extra.TRACK_MIME_TYPES"
    private const val EXTRA_TRACK_DISPLAY_NAMES =
        "io.github.supermonster003.autojs6.plugin.audioplayer.extra.TRACK_DISPLAY_NAMES"
    private const val EXTRA_START_INDEX =
        "io.github.supermonster003.autojs6.plugin.audioplayer.extra.START_INDEX"
    private const val EXTRA_HOST_REQUEST =
        "io.github.supermonster003.autojs6.plugin.audioplayer.extra.HOST_REQUEST"
    private const val HOST_TARGET_ID = "targetId"
    private const val HOST_RELATIVE_PATHS = "relativePaths"
    private const val HOST_AUDIO_AUTHORITY = "audio"

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
    ): AudioPlaybackRequest? = runCatching {
        intent ?: return null
        if (intent.action !in acceptedActions) return null
        if (intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION == 0) return null
        if (intent.flags and allowedFlags.inv() != 0) return null

        val clipData = intent.clipData ?: return null
        val count = clipData.itemCount
        if (count !in 1..ExplorerQueuePolicy.MAX_TARGETS) return null
        val mimeTypes = intent.getStringArrayListExtra(EXTRA_TRACK_MIME_TYPES)
            ?.takeIf { it.size == count }
            ?: return null
        val displayNames = intent.getStringArrayListExtra(EXTRA_TRACK_DISPLAY_NAMES)
            ?.takeIf { it.size == count }
            ?: return null
        val startIndex = intent.getIntExtra(EXTRA_START_INDEX, -1)
            .takeIf { it in 0 until count }
            ?: return null
        val hostBundle = intent.parcelableBundleExtra(EXTRA_HOST_REQUEST)
        val hostSession: IExplorerActionHostSession?
        val hostTargetId: String?
        val hostRelativePaths: List<String?>
        if (hostBundle == null) {
            hostSession = null
            hostTargetId = null
            hostRelativePaths = List(count) { null }
        } else {
            val binder = hostBundle.getBinder(ExplorerActionHostSessionKeys.BINDER) ?: return null
            if (runCatching { binder.interfaceDescriptor }.getOrNull() != IExplorerActionHostSession.DESCRIPTOR) {
                return null
            }
            hostSession = IExplorerActionHostSession.Stub.asInterface(binder)
            hostTargetId = hostBundle.getString(HOST_TARGET_ID)
                ?.takeIf { it.length in 1..128 && it.none(Char::isWhitespace) }
                ?: return null
            hostRelativePaths = hostBundle.getStringArrayList(HOST_RELATIVE_PATHS)
                ?.takeIf { it.size == count }
                ?: return null
        }

        val tracks = (0 until count).map { index ->
            val item = clipData.getItemAt(index)
            val uri = item.uri ?: return null
            val relativePath = hostRelativePaths[index]
            if (hostSession == null) {
                if (!isUsableContentUri(uri) || relativePath != null) return null
            } else if (relativePath == "") {
                if (!isUsableContentUri(uri)) return null
            } else {
                if (!isHostAudioUri(uri, index) || relativePath?.let(::isSafeHostName) != true) return null
            }
            if (!item.isExactUri(uri)) return null
            val mimeType = MimeTypePolicy.normalize(mimeTypes[index])
                ?.takeIf(MimeTypePolicy::isAudio)
                ?: return null
            val displayName = DisplayNamePolicy.sanitizeIncoming(displayNames[index]) ?: return null
            AudioTrackRequest(uri, mimeType, displayName, relativePath)
        }
        if (tracks.map { it.uri }.toSet().size != tracks.size) return null
        val currentTrack = tracks[startIndex]
        if (intent.data != currentTrack.uri) return null
        if (MimeTypePolicy.normalize(intent.type) != currentTrack.mimeType) return null
        AudioPlaybackRequest(tracks, startIndex, hostSession, hostTargetId)
    }.getOrNull()

    private fun Intent.putReadOnlyPayload(request: AudioPlaybackRequest) {
        val tracks = request.tracks.map { track ->
            val payload = if (track.hostRelativePath == null || track.uri.scheme == ContentResolver.SCHEME_CONTENT) {
                requireNotNull(
                    AudioUriForwardingPolicy.forward(
                        AudioUriForwardRequest(
                            targetUriString = track.uri.toString(),
                            targetScheme = track.uri.scheme,
                            mimeType = track.mimeType,
                            displayName = track.displayName,
                        ),
                    ),
                )
            } else {
                require(track.uri.scheme == HOST_SOURCE_SCHEME && isSafeHostName(track.hostRelativePath))
                AudioUriForwardingPolicy.forward(
                    AudioUriForwardRequest(
                        targetUriString = "content://placeholder/${track.displayName}",
                        targetScheme = ContentResolver.SCHEME_CONTENT,
                        mimeType = track.mimeType,
                        displayName = track.displayName,
                    ),
                ) ?: error("Invalid host audio metadata")
            }
            AudioTrackRequest(
                uri = track.uri,
                mimeType = payload.mimeType,
                displayName = requireNotNull(payload.displayName),
                hostRelativePath = track.hostRelativePath,
            )
        }
        require(tracks.size in 1..ExplorerQueuePolicy.MAX_TARGETS)
        require(request.startIndex in tracks.indices)
        require(tracks.map { it.uri }.toSet().size == tracks.size)

        val currentTrack = tracks[request.startIndex]
        setDataAndType(currentTrack.uri, currentTrack.mimeType)
        clipData = ClipData.newRawUri(tracks.first().displayName, tracks.first().uri).apply {
            tracks.drop(1).forEach { track -> addItem(ClipData.Item(track.uri)) }
        }
        putStringArrayListExtra(EXTRA_TRACK_MIME_TYPES, ArrayList(tracks.map { it.mimeType }))
        putStringArrayListExtra(EXTRA_TRACK_DISPLAY_NAMES, ArrayList(tracks.map { it.displayName }))
        putExtra(EXTRA_START_INDEX, request.startIndex)
        request.hostSession?.let { session ->
            putExtra(
                EXTRA_HOST_REQUEST,
                Bundle().apply {
                    putBinder(ExplorerActionHostSessionKeys.BINDER, session.asBinder())
                    putString(HOST_TARGET_ID, requireNotNull(request.hostTargetId))
                    putStringArrayList(
                        HOST_RELATIVE_PATHS,
                        ArrayList(tracks.map { requireNotNull(it.hostRelativePath) }),
                    )
                },
            )
        }
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    fun hostAudioUri(index: Int): Uri = Uri.Builder()
        .scheme(HOST_SOURCE_SCHEME)
        .authority(HOST_AUDIO_AUTHORITY)
        .appendPath(index.toString())
        .build()

    private fun isUsableContentUri(uri: Uri): Boolean =
        uri.scheme == ContentResolver.SCHEME_CONTENT &&
            uri.isHierarchical &&
            !uri.authority.isNullOrBlank() &&
            uri.userInfo == null &&
            uri.port == -1 &&
            uri.query == null &&
            uri.fragment == null &&
            !uri.encodedPath.isNullOrBlank()

    private fun ClipData.Item.isExactUri(expected: Uri): Boolean =
        uri == expected && text == null && htmlText == null && intent == null

    private fun isHostAudioUri(uri: Uri, expectedIndex: Int): Boolean =
        uri.scheme == HOST_SOURCE_SCHEME && uri.authority == HOST_AUDIO_AUTHORITY &&
            uri.pathSegments == listOf(expectedIndex.toString()) && uri.query == null && uri.fragment == null

    private fun isSafeHostName(value: String): Boolean =
        value.isNotBlank() && value != "." && value != ".." && '/' !in value && '\\' !in value &&
            value.none { it.isISOControl() || Character.getType(it) == Character.FORMAT.toInt() }

    @Suppress("DEPRECATION")
    private fun Intent.parcelableBundleExtra(name: String): Bundle? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(name, Bundle::class.java)
        } else {
            getParcelableExtra(name)
        }
}
