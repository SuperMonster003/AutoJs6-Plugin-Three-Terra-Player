package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.app.Activity
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.widget.Toast
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistError
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistException
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistLoader
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistParser
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

/** Signature-protected bridge from AutoJs6 into private playback components. */
class ExplorerActionActivity : Activity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val cancellationSignal = CancellationSignal()
    private var hostSession: IExplorerActionHostSession? = null
    private var playlistFailure: Exception? = null
    private var playlistLoaded: io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.LoadedPlaylist? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            finish()
            return
        }
        val explorerRequest = when (val resolution = ExplorerActionIntentPolicy.resolveDetailed(intent)) {
            is ExplorerRequestResolution.Accepted -> resolution.request
            is ExplorerRequestResolution.Rejected -> {
                if (resolution.cause == null) {
                    Log.w(TAG, "Rejected Explorer Action request: ${resolution.reason}")
                } else {
                    Log.w(TAG, "Rejected Explorer Action request: ${resolution.reason}", resolution.cause)
                }
                Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_LONG).show()
                finish()
                return
            }
        }
        hostSession = explorerRequest.hostSession
        val requestBuilder = scope.async(Dispatchers.IO) {
            buildPlaybackRequest(explorerRequest)
        }
        scope.launch {
            val request = withTimeoutOrNull(READABILITY_TIMEOUT_MS) { requestBuilder.await() }
            if (request == null) {
                cancellationSignal.cancel()
                requestBuilder.cancel()
                if (playlistFailure != null) {
                    PlaylistActivity.showError(this@ExplorerActionActivity, requireNotNull(playlistFailure))
                } else Toast.makeText(
                    this@ExplorerActionActivity,
                    R.string.error_cannot_read_file,
                    Toast.LENGTH_LONG,
                ).show()
            } else {
                playlistLoaded?.let { PlaylistActivity.showSkipped(this@ExplorerActionActivity, it) }
                runCatching {
                    startActivity(
                        AudioPlaybackContract.playerIntent(
                            this@ExplorerActionActivity,
                            request,
                            startPlayback = true,
                        ),
                    )
                }.onSuccess {
                    if (request.hostSession != null) {
                        // AudioPlayerActivity and then AudioPlaybackService now own this capability.
                        hostSession = null
                    }
                }.onFailure {
                    Toast.makeText(this@ExplorerActionActivity, R.string.error_cannot_read_file, Toast.LENGTH_LONG)
                        .show()
                }
            }
            closeHostSession()
            finish()
        }
    }

    override fun onDestroy() {
        cancellationSignal.cancel()
        scope.cancel()
        closeHostSession()
        super.onDestroy()
    }

    private fun canRead(request: ExplorerAudioRequest): Boolean = request.targets.all { target ->
        runCatching {
            contentResolver.openFileDescriptor(target.track.uri, "r", cancellationSignal)
                ?.use { true }
                ?: false
        }.getOrDefault(false)
    }

    private fun buildPlaybackRequest(request: ExplorerAudioRequest): AudioPlaybackRequest? {
        if (!canRead(request)) return null
        if (request.targets.any { PlaylistParser.format(it.track.displayName, it.track.mimeType) != null }) {
            return try {
                if (request.targets.size != 1 || request.hostSession == null) throw PlaylistException(PlaylistError.INVALID)
                val target = request.targets.single()
                val loaded = PlaylistLoader.host(
                    this, PlaylistSource(target.track.uri, target.track.displayName, target.track.mimeType),
                    request.hostSession, target.id, request.parentDisplayPath, PlaylistPlayback::mediaMime,
                )
                if (loaded.items.isEmpty()) throw PlaylistException(PlaylistError.EMPTY)
                playlistLoaded = loaded
                AudioPlaybackRequest(
                    tracks = loaded.items.mapIndexed { index, item ->
                        AudioTrackRequest(AudioPlaybackContract.hostAudioUri(index), item.mimeType, item.displayName, item.relativePath, preferDisplayName = true)
                    },
                    hostSession = request.hostSession,
                    hostTargetId = target.id,
                )
            } catch (error: Exception) {
                playlistFailure = error
                null
            }
        }
        if (request.targets.size != 1 || request.hostSession == null) {
            return AudioPlaybackRequest(tracks = request.targets.map(ExplorerAudioTarget::track))
        }
        val selected = request.targets.single()
        val queue = runCatching { ExplorerSiblingAudioClient.discover(request) }.getOrNull()
            ?: return AudioPlaybackRequest(selected.track.uri, selected.track.mimeType, selected.track.displayName)
        if (queue.tracks.size <= 1) {
            return AudioPlaybackRequest(selected.track.uri, selected.track.mimeType, selected.track.displayName)
        }
        val tracks = queue.tracks.mapIndexed { index, discovered ->
            if (index == queue.startIndex) {
                selected.track.copy(hostRelativePath = "")
            } else {
                AudioTrackRequest(
                    uri = AudioPlaybackContract.hostAudioUri(index),
                    mimeType = discovered.mimeType,
                    displayName = discovered.displayName,
                    hostRelativePath = discovered.relativePath,
                )
            }
        }
        return AudioPlaybackRequest(
            tracks = tracks,
            startIndex = queue.startIndex,
            hostSession = request.hostSession,
            hostTargetId = selected.id,
        )
    }

    private fun closeHostSession() {
        hostSession?.let { session -> runCatching { session.close() } }
        hostSession = null
    }

    private companion object {
        const val TAG = "AudioExplorerGateway"
        const val READABILITY_TIMEOUT_MS = 15_000L
    }
}
