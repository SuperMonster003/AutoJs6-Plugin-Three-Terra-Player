package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.OptIn as ExperimentalOptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.AbLoopPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.DisplayNamePolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.SleepTimerPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppPreferenceStore
import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.autojs.plugin.explorer.api.IExplorerActionHostSession

/** Owns audio playback, playlists and time-based controls outside the Activity lifecycle. */
@ExperimentalOptIn(markerClass = [UnstableApi::class])
class AudioPlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var positionStore: PlaybackPositionStore
    private lateinit var appPreferenceStore: AppPreferenceStore
    private val sourceRouter = ExplorerAudioSourceRouter()

    private val mainHandler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val tracksByMediaId = LinkedHashMap<String, AudioTrackRequest>()
    private val lastPositionsByMediaId = HashMap<String, Long>()
    private val durationsByMediaId = HashMap<String, Long>()

    private var activeMediaId: String? = null
    private var activePositionKey: String? = null
    private var activeHostSession: IExplorerActionHostSession? = null
    private var activeHostTargetId: String? = null
    private var activeCompleted = false
    private var replacingQueue = false

    private var sleepDeadlineElapsedRealtimeMs: Long? = null
    private var stopAfterCurrent = false
    private var fadeActive = false
    private var volumeBeforeFade = 1f
    private var abStartMs: Long? = null
    private var abEndMs: Long? = null

    private val progressSaver = object : Runnable {
        override fun run() {
            persistActivePosition()
            mainHandler.postDelayed(this, PROGRESS_SAVE_INTERVAL_MS)
        }
    }

    private val playbackToolTicker = object : Runnable {
        override fun run() {
            if (!::player.isInitialized) return
            applyAbLoopIfNeeded()
            if (applySleepTimerIfNeeded()) return
            schedulePlaybackToolTick()
        }
    }

    private val delayedStop = Runnable { stopPlaybackService() }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                activeCompleted = true
                activePositionKey?.let(positionStore::clear)
                clearSleepTimerState(restoreVolume = false)
                stopPlaybackService()
            }
            schedulePlaybackToolTick()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            mainHandler.removeCallbacks(progressSaver)
            if (isPlaying) {
                mainHandler.postDelayed(progressSaver, PROGRESS_SAVE_INTERVAL_MS)
            } else {
                persistActivePosition()
            }
            schedulePlaybackToolTick()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (replacingQueue) return
            val previousMediaId = activeMediaId
            val completedPrevious = reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO ||
                reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT
            previousMediaId?.let { mediaId ->
                val track = tracksByMediaId[mediaId]
                if (completedPrevious) {
                    track?.let(::positionKey)?.let(positionStore::clear)
                    lastPositionsByMediaId.remove(mediaId)
                    durationsByMediaId.remove(mediaId)
                } else {
                    persistCachedPosition(mediaId)
                }
            }

            if (stopAfterCurrent && completedPrevious) {
                player.volume = 0f
                player.pause()
                activeCompleted = true
                activeMediaId = null
                activePositionKey = null
                clearSleepTimerState(restoreVolume = false)
                stopPlaybackService()
                return
            }

            val nextMediaId = mediaItem?.mediaId
            if (nextMediaId == null || nextMediaId == previousMediaId) return
            activateMediaItem(nextMediaId, applyRememberedPosition = true)
            previousMediaId?.let(::stripArtwork)
        }

        @ExperimentalOptIn(markerClass = [UnstableApi::class])
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int,
        ) {
            if (oldPosition.mediaItemIndex == newPosition.mediaItemIndex) return
            val mediaId = oldPosition.mediaItem?.mediaId ?: return
            if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
                tracksByMediaId[mediaId]?.let(::positionKey)?.let(positionStore::clear)
                return
            }
            val durationMs = durationsByMediaId[mediaId] ?: 0L
            persistPosition(mediaId, oldPosition.positionMs, durationMs)
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            if (replacingQueue) return
            if (timeline.isEmpty) {
                activeMediaId = null
                activePositionKey = null
                activeCompleted = false
                tracksByMediaId.clear()
                lastPositionsByMediaId.clear()
                durationsByMediaId.clear()
                clearAbLoopState()
                clearSleepTimerState(restoreVolume = true)
                publishToolState()
                releaseActiveHostSession()
                stopPlaybackService()
            } else {
                updateSessionActivity()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            persistActivePosition()
            stopForeground(STOP_FOREGROUND_REMOVE)
            mainHandler.removeCallbacks(delayedStop)
            mainHandler.postDelayed(delayedStop, ERROR_LINGER_MS)
        }
    }

    private val sessionCallback = object : MediaSession.Callback {
        @ExperimentalOptIn(markerClass = [UnstableApi::class])
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            val builder = MediaSession.ConnectionResult.AcceptedResultBuilder(session)
            if (controller.packageName == packageName) {
                val commands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                    .buildUpon()
                    .apply { PlaybackSessionContract.CUSTOM_COMMANDS.forEach(::add) }
                    .build()
                builder.setAvailableSessionCommands(commands)
            }
            return builder.build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle,
        ): ListenableFuture<SessionResult> {
            if (controller.packageName != packageName) {
                return Futures.immediateFuture(
                    SessionResult(SessionError.ERROR_PERMISSION_DENIED),
                )
            }
            return Futures.immediateFuture(handleCustomCommand(customCommand, args))
        }
    }

    @ExperimentalOptIn(markerClass = [UnstableApi::class])
    override fun onCreate() {
        super.onCreate()
        positionStore = PlaybackPositionStore(this)
        appPreferenceStore = AppPreferenceStore(this)
        if (!appPreferenceStore.rememberPlaybackPosition) positionStore.clearAll()
        val attributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(DefaultDataSource.Factory(this, sourceRouter)),
            )
            .setSeekBackIncrementMs(SEEK_INCREMENT_MS)
            .setSeekForwardIncrementMs(SEEK_INCREMENT_MS)
            .build()
            .apply {
                setAudioAttributes(attributes, true)
                setHandleAudioBecomingNoisy(true)
                setWakeMode(C.WAKE_MODE_LOCAL)
                addListener(playerListener)
            }
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(sessionCallback)
            .setMediaButtonPreferences(mediaButtonPreferences())
            .setSessionExtras(PlaybackSessionContract.stateBundle(toolState()))
            .build()
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
        mainHandler.removeCallbacks(progressSaver)
        mainHandler.removeCallbacks(playbackToolTicker)
        mainHandler.removeCallbacks(delayedStop)
        scope.cancel()
        persistActivePosition()
        if (::mediaSession.isInitialized) mediaSession.release()
        if (::player.isInitialized) {
            player.removeListener(playerListener)
            player.release()
        }
        releaseActiveHostSession()
        super.onDestroy()
    }

    @ExperimentalOptIn(markerClass = [UnstableApi::class])
    private fun play(request: AudioPlaybackRequest): Boolean {
        val previousHostSession = activeHostSession
        return runCatching {
            mainHandler.removeCallbacks(delayedStop)
            persistActivePosition()
            clearAbLoopState()
            if (stopAfterCurrent) clearSleepTimerState(restoreVolume = true)

            val newTracksByMediaId = LinkedHashMap<String, AudioTrackRequest>()
            val mediaItems = request.tracks.mapIndexed { index, track ->
                val mediaId = mediaId(track, index)
                newTracksByMediaId[mediaId] = track
                mediaItem(mediaId, track)
            }
            val currentMediaId = mediaItems[request.startIndex].mediaId
            val currentTrack = requireNotNull(newTracksByMediaId[currentMediaId])
            val resumePositionMs = if (appPreferenceStore.rememberPlaybackPosition) {
                positionStore.resumePositionMs(
                    positionKey(currentTrack, request.hostTargetId),
                    System.currentTimeMillis(),
                )
            } else {
                positionStore.clearAll()
                null
            } ?: 0L

            replacingQueue = true
            try {
                player.stop()
                sourceRouter.configure(request)
                activeHostSession = request.hostSession
                activeHostTargetId = request.hostTargetId
                tracksByMediaId.clear()
                tracksByMediaId.putAll(newTracksByMediaId)
                lastPositionsByMediaId.clear()
                durationsByMediaId.clear()
                player.setMediaItems(mediaItems, request.startIndex, resumePositionMs)
            } finally {
                replacingQueue = false
            }
            activeMediaId = currentMediaId
            activePositionKey = positionKey(currentTrack)
            activeCompleted = false
            lastPositionsByMediaId[currentMediaId] = resumePositionMs
            mediaSession.setSessionActivity(createSessionActivity(request))
            player.prepare()
            player.play()
            enrichMetadataAsync(currentTrack, currentMediaId)
            publishToolState()
            schedulePlaybackToolTick()
            if (!sameSession(previousHostSession, request.hostSession)) closeSession(previousHostSession)
            true
        }.getOrElse {
            replacingQueue = false
            runCatching { player.stop() }
            sourceRouter.clear()
            activeHostSession = null
            activeHostTargetId = null
            activeMediaId = null
            activePositionKey = null
            closeSession(request.hostSession)
            if (!sameSession(previousHostSession, request.hostSession)) closeSession(previousHostSession)
            false
        }
    }

    private fun mediaItem(mediaId: String, track: AudioTrackRequest): MediaItem = MediaItem.Builder()
        .setUri(track.uri)
        .setMimeType(track.mimeType)
        .setMediaId(mediaId)
        .setMediaMetadata(baseMetadata(track))
        .build()

    private fun baseMetadata(track: AudioTrackRequest): MediaMetadata = MediaMetadata.Builder()
        .setTitle(track.displayName)
        .setIsPlayable(true)
        .setExtras(sourceExtras(track))
        .build()

    /** Reads tags and artwork only for the active item, then replaces metadata without a seek. */
    private fun enrichMetadataAsync(track: AudioTrackRequest, mediaId: String) {
        if (track.uri.scheme != ContentResolver.SCHEME_CONTENT) return
        scope.launch {
            val resolved = withContext(Dispatchers.IO) {
                AudioMetadataResolver.resolve(this@AudioPlaybackService, track.uri)
            } ?: return@launch
            val index = mediaItemIndex(mediaId)
            if (index == C.INDEX_UNSET) return@launch
            val item = player.getMediaItemAt(index)
            if (item.mediaId != mediaId) return@launch
            runCatching {
                player.replaceMediaItem(
                    index,
                    item.buildUpon()
                        .setMediaMetadata(enrichedMetadata(track, resolved))
                        .build(),
                )
            }
        }
    }

    private fun enrichedMetadata(
        track: AudioTrackRequest,
        resolved: ResolvedAudioMetadata,
    ): MediaMetadata {
        val builder = MediaMetadata.Builder()
            .setTitle(sanitizedTag(resolved.title) ?: track.displayName)
            .setIsPlayable(true)
        sanitizedTag(resolved.artist)?.let(builder::setArtist)
        sanitizedTag(resolved.album)?.let(builder::setAlbumTitle)
        resolved.artworkData?.let { data ->
            builder.setArtworkData(data, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
        }
        val extras = sourceExtras(track)
        resolved.mimeType?.let { extras.putString(AudioMetadataResolver.EXTRA_MIME_TYPE, it) }
        resolved.sampleRateHz?.let { extras.putInt(AudioMetadataResolver.EXTRA_SAMPLE_RATE_HZ, it) }
        resolved.bitrateBps?.let { extras.putInt(AudioMetadataResolver.EXTRA_BITRATE_BPS, it) }
        builder.setExtras(extras)
        return builder.build()
    }

    /** Keeps queue timelines binder-safe by retaining compressed artwork only on the active item. */
    private fun stripArtwork(mediaId: String) {
        val index = mediaItemIndex(mediaId)
        if (index == C.INDEX_UNSET) return
        val item = player.getMediaItemAt(index)
        if (item.mediaMetadata.artworkData == null) return
        runCatching {
            player.replaceMediaItem(
                index,
                item.buildUpon()
                    .setMediaMetadata(
                        item.mediaMetadata.buildUpon()
                            .setArtworkData(null, null)
                            .build(),
                    )
                    .build(),
            )
        }
    }

    private fun sourceExtras(track: AudioTrackRequest): Bundle = Bundle().apply {
        putString(AudioPlaybackContract.MEDIA_EXTRA_SOURCE_URI, track.uri.toString())
        putString(AudioPlaybackContract.MEDIA_EXTRA_SOURCE_MIME_TYPE, track.mimeType)
        putString(AudioPlaybackContract.MEDIA_EXTRA_SOURCE_DISPLAY_NAME, track.displayName)
    }

    private fun activateMediaItem(mediaId: String, applyRememberedPosition: Boolean) {
        val track = tracksByMediaId[mediaId] ?: return
        if (abStartMs != null || abEndMs != null) {
            clearAbLoopState()
            publishToolState()
        }
        activeMediaId = mediaId
        activePositionKey = positionKey(track)
        activeCompleted = false
        if (applyRememberedPosition && appPreferenceStore.rememberPlaybackPosition) {
            positionStore.resumePositionMs(positionKey(track), System.currentTimeMillis())
                ?.takeIf { it > RESUME_SEEK_TOLERANCE_MS }
                ?.let { player.seekTo(it) }
        }
        updateSessionActivity()
        enrichMetadataAsync(track, mediaId)
        schedulePlaybackToolTick()
    }

    private fun sanitizedTag(value: String?): String? = value
        ?.let(DisplayNamePolicy::sanitize)
        ?.takeIf(String::isNotBlank)

    private fun persistActivePosition() {
        val mediaId = activeMediaId ?: return
        if (activeCompleted || !::player.isInitialized || player.currentMediaItem == null) return
        val durationMs = player.duration.takeIf { it != C.TIME_UNSET && it > 0L } ?: 0L
        persistPosition(mediaId, player.currentPosition, durationMs)
    }

    private fun persistCachedPosition(mediaId: String) {
        persistPosition(
            mediaId,
            lastPositionsByMediaId[mediaId] ?: return,
            durationsByMediaId[mediaId] ?: 0L,
        )
    }

    private fun persistPosition(mediaId: String, positionMs: Long, durationMs: Long) {
        // Media3 may deliver the discontinuity callback after the transition callback. Never let
        // a late callback for the previous item recreate its record after the new item invalidated
        // it under the single-most-recent-file contract.
        if (mediaId != activeMediaId) return
        val track = tracksByMediaId[mediaId] ?: return
        val safePositionMs = positionMs.coerceAtLeast(0L)
        val safeDurationMs = durationMs.coerceAtLeast(0L)
        lastPositionsByMediaId[mediaId] = safePositionMs
        durationsByMediaId[mediaId] = safeDurationMs
        if (!appPreferenceStore.rememberPlaybackPosition) {
            positionStore.clearAll()
            return
        }
        positionStore.save(
            positionKey(track),
            safePositionMs,
            safeDurationMs,
            System.currentTimeMillis(),
        )
    }

    private fun handleCustomCommand(command: SessionCommand, args: Bundle): SessionResult = when (command) {
        PlaybackSessionContract.COMMAND_SET_SLEEP_TIMER -> setSleepTimer(args)
        PlaybackSessionContract.COMMAND_STOP_AFTER_CURRENT -> stopAfterCurrent()
        PlaybackSessionContract.COMMAND_CANCEL_SLEEP_TIMER -> cancelSleepTimer()
        PlaybackSessionContract.COMMAND_SET_AB_START -> setAbStart(args)
        PlaybackSessionContract.COMMAND_SET_AB_END -> setAbEnd(args)
        PlaybackSessionContract.COMMAND_CLEAR_AB_LOOP -> clearAbLoop()
        else -> SessionResult(SessionError.ERROR_NOT_SUPPORTED)
    }

    private fun setSleepTimer(args: Bundle): SessionResult {
        if (!args.containsKey(PlaybackSessionContract.ARG_DURATION_MS)) return badValueResult()
        val deadline = SleepTimerPolicy.deadlineElapsedRealtimeMs(
            SystemClock.elapsedRealtime(),
            args.getLong(PlaybackSessionContract.ARG_DURATION_MS, -1L),
        ) ?: return badValueResult()
        clearSleepTimerState(restoreVolume = true)
        sleepDeadlineElapsedRealtimeMs = deadline
        publishToolState()
        schedulePlaybackToolTick()
        return successResult()
    }

    private fun stopAfterCurrent(): SessionResult {
        clearSleepTimerState(restoreVolume = true)
        clearAbLoopState()
        stopAfterCurrent = true
        player.repeatMode = Player.REPEAT_MODE_OFF
        publishToolState()
        schedulePlaybackToolTick()
        return successResult()
    }

    private fun cancelSleepTimer(): SessionResult {
        clearSleepTimerState(restoreVolume = true)
        publishToolState()
        schedulePlaybackToolTick()
        return successResult()
    }

    private fun setAbStart(args: Bundle): SessionResult {
        if (player.mediaItemCount == 0) return badValueResult()
        val positionMs = commandPosition(args) ?: return badValueResult()
        if (!AbLoopPolicy.validPoint(positionMs, knownDurationMs())) return badValueResult()
        abStartMs = positionMs
        if (!AbLoopPolicy.validEnd(abStartMs, abEndMs ?: -1L, knownDurationMs())) {
            abEndMs = null
        }
        publishToolState()
        schedulePlaybackToolTick()
        return successResult()
    }

    private fun setAbEnd(args: Bundle): SessionResult {
        if (player.mediaItemCount == 0) return badValueResult()
        val positionMs = commandPosition(args) ?: return badValueResult()
        if (!AbLoopPolicy.validEnd(abStartMs, positionMs, knownDurationMs())) return badValueResult()
        abEndMs = positionMs
        player.seekTo(requireNotNull(abStartMs))
        publishToolState()
        schedulePlaybackToolTick()
        return successResult()
    }

    private fun clearAbLoop(): SessionResult {
        clearAbLoopState()
        publishToolState()
        schedulePlaybackToolTick()
        return successResult()
    }

    private fun commandPosition(args: Bundle): Long? =
        args.takeIf { it.containsKey(PlaybackSessionContract.ARG_POSITION_MS) }
            ?.getLong(PlaybackSessionContract.ARG_POSITION_MS, -1L)
            ?.takeIf { it >= 0L }

    private fun applyAbLoopIfNeeded() {
        if (!player.isPlaying) return
        if (AbLoopPolicy.shouldSeekToStart(abStartMs, abEndMs, player.currentPosition)) {
            player.seekTo(requireNotNull(abStartMs))
        }
    }

    /** Returns true when the timer stopped the service. */
    private fun applySleepTimerIfNeeded(): Boolean {
        val deadline = sleepDeadlineElapsedRealtimeMs ?: return false
        val remainingMs = SleepTimerPolicy.remainingMs(deadline, SystemClock.elapsedRealtime())
        if (remainingMs <= 0L) {
            persistActivePosition()
            player.volume = 0f
            player.pause()
            player.stop()
            clearSleepTimerState(restoreVolume = false)
            publishToolState()
            stopPlaybackService()
            return true
        }
        val factor = SleepTimerPolicy.fadeVolumeFactor(remainingMs)
        if (factor < 1f) {
            if (!fadeActive) {
                volumeBeforeFade = player.volume
                fadeActive = true
            }
            player.volume = volumeBeforeFade * factor
        }
        return false
    }

    private fun schedulePlaybackToolTick() {
        mainHandler.removeCallbacks(playbackToolTicker)
        val abDelay = if (player.isPlaying && abStartMs != null && abEndMs != null) {
            AB_LOOP_POLL_MS
        } else {
            null
        }
        val sleepDelay = sleepDeadlineElapsedRealtimeMs?.let { deadline ->
            val remainingMs = SleepTimerPolicy.remainingMs(deadline, SystemClock.elapsedRealtime())
            when {
                remainingMs <= SleepTimerPolicy.FADE_DURATION_MS -> SLEEP_FADE_POLL_MS
                else -> min(
                    remainingMs - SleepTimerPolicy.FADE_DURATION_MS,
                    SLEEP_TIMER_MAX_POLL_MS,
                )
            }
        }
        val delayMs = listOfNotNull(abDelay, sleepDelay).minOrNull() ?: return
        mainHandler.postDelayed(playbackToolTicker, delayMs.coerceAtLeast(MIN_TOOL_POLL_MS))
    }

    private fun clearSleepTimerState(restoreVolume: Boolean) {
        sleepDeadlineElapsedRealtimeMs = null
        stopAfterCurrent = false
        if (restoreVolume && fadeActive && ::player.isInitialized) {
            player.volume = volumeBeforeFade
        }
        fadeActive = false
        volumeBeforeFade = 1f
    }

    private fun clearAbLoopState() {
        abStartMs = null
        abEndMs = null
    }

    private fun knownDurationMs(): Long? = player.duration
        .takeIf { it != C.TIME_UNSET && it > 0L }

    private fun toolState(): PlaybackToolState = PlaybackToolState(
        sleepMode = when {
            stopAfterCurrent -> PlaybackSessionContract.SLEEP_MODE_END_OF_TRACK
            sleepDeadlineElapsedRealtimeMs != null -> PlaybackSessionContract.SLEEP_MODE_DEADLINE
            else -> PlaybackSessionContract.SLEEP_MODE_OFF
        },
        sleepDeadlineElapsedRealtimeMs = sleepDeadlineElapsedRealtimeMs ?: 0L,
        abStartMs = abStartMs,
        abEndMs = abEndMs,
    )

    private fun publishToolState() {
        if (::mediaSession.isInitialized) {
            mediaSession.setSessionExtras(PlaybackSessionContract.stateBundle(toolState()))
        }
    }

    @ExperimentalOptIn(markerClass = [UnstableApi::class])
    private fun updateSessionActivity() {
        currentPlaybackRequest()?.let { request ->
            mediaSession.setSessionActivity(createSessionActivity(request))
        }
    }

    private fun currentPlaybackRequest(): AudioPlaybackRequest? {
        if (!::player.isInitialized || player.mediaItemCount == 0) return null
        val tracks = (0 until player.mediaItemCount).map { index ->
            tracksByMediaId[player.getMediaItemAt(index).mediaId] ?: return null
        }
        val startIndex = player.currentMediaItemIndex.takeIf { it in tracks.indices } ?: 0
        return AudioPlaybackRequest(
            tracks = tracks,
            startIndex = startIndex,
            hostSession = activeHostSession,
            hostTargetId = activeHostTargetId,
        )
    }

    private fun mediaItemIndex(mediaId: String): Int = (0 until player.mediaItemCount)
        .firstOrNull { player.getMediaItemAt(it).mediaId == mediaId }
        ?: C.INDEX_UNSET

    private fun createSessionActivity(request: AudioPlaybackRequest): PendingIntent =
        PendingIntent.getActivity(
            this,
            SESSION_ACTIVITY_REQUEST_CODE,
            AudioPlaybackContract.playerIntent(this, request, startPlayback = false),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    @ExperimentalOptIn(markerClass = [UnstableApi::class])
    private fun mediaButtonPreferences(): List<CommandButton> = listOf(
        CommandButton.Builder(CommandButton.ICON_PREVIOUS)
            .setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
            .setDisplayName(getString(R.string.action_previous_track))
            .setSlots(CommandButton.SLOT_BACK_SECONDARY)
            .build(),
        CommandButton.Builder(CommandButton.ICON_REWIND)
            .setPlayerCommand(Player.COMMAND_SEEK_BACK)
            .setDisplayName(getString(R.string.action_seek_backward))
            .setSlots(CommandButton.SLOT_BACK)
            .build(),
        CommandButton.Builder(CommandButton.ICON_FAST_FORWARD)
            .setPlayerCommand(Player.COMMAND_SEEK_FORWARD)
            .setDisplayName(getString(R.string.action_seek_forward))
            .setSlots(CommandButton.SLOT_FORWARD)
            .build(),
        CommandButton.Builder(CommandButton.ICON_NEXT)
            .setPlayerCommand(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
            .setDisplayName(getString(R.string.action_next_track))
            .setSlots(CommandButton.SLOT_FORWARD_SECONDARY)
            .build(),
    )

    private fun mediaId(track: AudioTrackRequest, index: Int): String = UUID.nameUUIDFromBytes(
        "$index\u0000${track.uri}".toByteArray(UTF_8),
    ).toString()

    private fun positionKey(
        track: AudioTrackRequest,
        hostTargetId: String? = activeHostTargetId,
    ): String = track.hostRelativePath?.let { relativePath ->
        "explorer\u0000${requireNotNull(hostTargetId)}\u0000$relativePath"
    } ?: track.uri.toString()

    private fun releaseActiveHostSession() {
        val session = activeHostSession
        activeHostSession = null
        activeHostTargetId = null
        sourceRouter.clear()
        closeSession(session)
    }

    private fun sameSession(
        first: IExplorerActionHostSession?,
        second: IExplorerActionHostSession?,
    ): Boolean = first?.asBinder() == second?.asBinder()

    private fun closeSession(session: IExplorerActionHostSession?) {
        session?.let { value -> runCatching { value.close() } }
    }

    private fun stopPlaybackService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun successResult() = SessionResult(SessionResult.RESULT_SUCCESS)

    private fun badValueResult() = SessionResult(SessionError.ERROR_BAD_VALUE)

    companion object {
        private const val SESSION_ACTIVITY_REQUEST_CODE = 0xA61D
        private const val SEEK_INCREMENT_MS = 10_000L
        private const val PROGRESS_SAVE_INTERVAL_MS = 5_000L
        private const val ERROR_LINGER_MS = 30_000L
        private const val RESUME_SEEK_TOLERANCE_MS = 1_000L
        private const val AB_LOOP_POLL_MS = 50L
        private const val SLEEP_FADE_POLL_MS = 100L
        private const val SLEEP_TIMER_MAX_POLL_MS = 60_000L
        private const val MIN_TOOL_POLL_MS = 25L

        internal fun startPlayback(context: Context, request: AudioPlaybackRequest): Boolean = runCatching {
            // Every playback ingress first opens AudioPlayerActivity, so this call is made while the
            // app is foreground-visible. MediaSessionService promotes itself once playback starts.
            // A normal started service also lets a decoder/source failure remain visible briefly
            // without violating the startForegroundService notification deadline.
            context.startService(AudioPlaybackContract.serviceIntent(context, request))
            true
        }.getOrDefault(false)
    }
}
