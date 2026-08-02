package io.github.supermonster003.autojs6.plugin.audioplayer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn as ExperimentalOptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/** Owns audio playback so it can continue after the player Activity leaves the foreground. */
class AudioPlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) stopPlaybackService()
        }

        override fun onPlayerError(error: PlaybackException) {
            stopPlaybackService()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val attributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
        player = ExoPlayer.Builder(this)
            .build()
            .apply {
                setAudioAttributes(attributes, true)
                setHandleAudioBecomingNoisy(true)
                setWakeMode(C.WAKE_MODE_LOCAL)
                addListener(playerListener)
            }
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val superResult = super.onStartCommand(intent, flags, startId)
        if (intent?.action != AudioPlaybackContract.ACTION_START_PLAYBACK) return superResult
        val request = AudioPlaybackContract.resolveServiceIntent(intent)
        if (request == null || !play(request)) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelfResult(startId)
        }
        return START_NOT_STICKY
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onDestroy() {
        if (::mediaSession.isInitialized) mediaSession.release()
        if (::player.isInitialized) {
            player.removeListener(playerListener)
            player.release()
        }
        super.onDestroy()
    }

    @ExperimentalOptIn(markerClass = [UnstableApi::class])
    private fun play(request: AudioPlaybackRequest): Boolean = runCatching {
        val item = MediaItem.Builder()
            .setUri(request.uri)
            .setMimeType(request.mimeType)
            .setMediaId(request.uri.toString().hashCode().toUInt().toString(16))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(request.displayName)
                    .setIsPlayable(true)
                    .build(),
            )
            .build()
        mediaSession.setSessionActivity(createSessionActivity(request))
        player.setMediaItem(item)
        player.prepare()
        player.play()
        true
    }.getOrDefault(false)

    private fun createSessionActivity(request: AudioPlaybackRequest): PendingIntent =
        PendingIntent.getActivity(
            this,
            SESSION_ACTIVITY_REQUEST_CODE,
            AudioPlaybackContract.playerIntent(this, request, startPlayback = false),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun stopPlaybackService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        private const val SESSION_ACTIVITY_REQUEST_CODE = 0xA61D

        internal fun startPlayback(context: Context, request: AudioPlaybackRequest): Boolean = runCatching {
            ContextCompat.startForegroundService(
                context,
                AudioPlaybackContract.serviceIntent(context, request),
            )
            true
        }.getOrDefault(false)
    }
}
