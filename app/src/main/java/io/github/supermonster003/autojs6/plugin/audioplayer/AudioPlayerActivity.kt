package io.github.supermonster003.autojs6.plugin.audioplayer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn as ExperimentalOptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import io.github.supermonster003.autojs6.plugin.audioplayer.databinding.ActivityAudioPlayerBinding
import io.github.supermonster003.autojs6.plugin.audioplayer.databinding.BottomSheetPlaybackQueueBinding
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AbLoopPolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.AudioInfoPolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.DisplayNamePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.MimeTypePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.PlaybackMode
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.PlaybackModePolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.SleepTimerPolicy
import io.github.supermonster003.autojs6.plugin.audioplayer.theme.AudioThemePaletteGenerator
import io.github.supermonster003.autojs6.plugin.audioplayer.theme.AudioThemePicker
import io.github.supermonster003.autojs6.plugin.audioplayer.theme.AudioThemeViewStyler
import io.github.supermonster003.autojs6.plugin.audioplayer.theme.AudioThemedActivity
import io.github.supermonster003.autojs6.plugin.audioplayer.update.AppUpdateCoordinator
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ceil

/** Controller UI for the private MediaSessionService. */
class AudioPlayerActivity : AudioThemedActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var request: AudioPlaybackRequest
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var isSeekBarTracking = false
    private var toolState = PlaybackToolState()
    private var queueDialog: BottomSheetDialog? = null
    private var queueSheetBinding: BottomSheetPlaybackQueueBinding? = null
    private var queueAdapter: PlaybackQueueAdapter? = null
    private var selectedAudioStream: SelectedAudioStream? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private val progressTicker = object : Runnable {
        override fun run() {
            renderProgress()
            renderSleepTimer()
            mainHandler.postDelayed(this, PROGRESS_TICK_MS)
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        renderNotificationPermission()
    }

    private val playerListener = object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            renderMetadata(mediaMetadata)
            renderQueue()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            selectedAudioStream = null
            renderMetadata(mediaItem?.mediaMetadata)
            renderQueue()
            renderAdjacentControls()
            hidePlaybackError()
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            renderQueue()
            renderAdjacentControls()
        }

        override fun onTracksChanged(tracks: Tracks) {
            selectedAudioStream = selectedAudioStream(tracks)
            renderMetadata(activeController()?.mediaMetadata)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            renderPlayPause()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            renderPlayPause()
            renderBuffering()
            renderProgress()
            renderAdjacentControls()
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int,
        ) {
            renderProgress()
            renderQueue()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            renderPlaybackMode()
            renderAdjacentControls()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            renderPlaybackMode()
            renderAdjacentControls()
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            renderSpeed()
        }

        override fun onAvailableCommandsChanged(availableCommands: Player.Commands) {
            renderAdjacentControls()
            renderQueue()
        }

        override fun onPlayerError(error: PlaybackException) {
            showPlaybackError(error)
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            if (error == null) hidePlaybackError() else showPlaybackError(error)
        }
    }

    private val controllerCallback = object : MediaController.Listener {
        override fun onDisconnected(disconnected: MediaController) {
            if (controller !== disconnected) return
            disconnectController()
            renderControls()
        }

        override fun onExtrasChanged(controller: MediaController, extras: Bundle) {
            if (this@AudioPlayerActivity.controller !== controller) return
            toolState = PlaybackSessionContract.stateFrom(extras)
            renderPlaybackTools()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        request = AudioPlaybackContract.resolvePlayerIntent(intent) ?: run {
            finish()
            return
        }
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AudioThemeViewStyler.applyPlayer(this, binding)
        binding.toolbar.setNavigationOnClickListener { finishAfterTransition() }
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_choose_theme -> {
                    AudioThemePicker(this) { recreate() }.show()
                    true
                }
                else -> false
            }
        }
        binding.notificationPermissionButton.setOnClickListener { requestNotificationPermission() }
        binding.openExternalButton.setOnClickListener { openWithAnotherApp() }
        bindTransportControls()
        renderMetadata(null)
        renderControls()
        renderNotificationPermission()

        if (savedInstanceState == null && intent.action == AudioPlaybackContract.ACTION_START_PLAYBACK) {
            startPlayback()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val nextRequest = AudioPlaybackContract.resolvePlayerIntent(intent) ?: return
        setIntent(intent)
        request = nextRequest
        renderMetadata(null)
        hidePlaybackError()
        if (intent.action == AudioPlaybackContract.ACTION_START_PLAYBACK) startPlayback()
    }

    override fun onStart() {
        super.onStart()
        connectController()
        mainHandler.removeCallbacks(progressTicker)
        mainHandler.post(progressTicker)
    }

    override fun onResume() {
        super.onResume()
        AppUpdateCoordinator.maybeCheckAutomatically(this)
    }

    override fun onStop() {
        mainHandler.removeCallbacks(progressTicker)
        dismissQueue()
        disconnectController()
        super.onStop()
    }

    private fun bindTransportControls() {
        binding.playPauseButton.setOnClickListener { onPlayPauseClicked() }
        binding.seekBackwardButton.setOnClickListener { activeController()?.seekBack() }
        binding.seekForwardButton.setOnClickListener { activeController()?.seekForward() }
        binding.previousButton.setOnClickListener { activeController()?.seekToPreviousMediaItem() }
        binding.nextButton.setOnClickListener { activeController()?.seekToNextMediaItem() }
        binding.playbackModeButton.setOnClickListener { cyclePlaybackMode() }
        binding.queueButton.setOnClickListener { showQueue() }
        binding.speedButton.setOnClickListener { showSpeedMenu() }
        binding.sleepTimerButton.setOnClickListener { showSleepTimerMenu() }
        binding.abLoopButton.setOnClickListener { cycleAbLoop() }
        binding.abLoopButton.setOnLongClickListener {
            if (toolState.abStartMs == null) {
                false
            } else {
                sendCustomCommand(PlaybackSessionContract.COMMAND_CLEAR_AB_LOOP) {
                    Toast.makeText(this, R.string.ab_loop_cleared, Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
        binding.seekBar.max = SEEK_BAR_MAX
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                durationMs()?.let { duration ->
                    binding.positionText.text = formatTime(progressToPosition(progress, duration))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isSeekBarTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isSeekBarTracking = false
                val duration = durationMs() ?: return
                activeController()?.seekTo(progressToPosition(seekBar.progress, duration))
            }
        })
    }

    private fun onPlayPauseClicked() {
        val active = activeController()
        when {
            active == null -> startPlayback()
            active.playerError != null -> startPlayback()
            active.playbackState == Player.STATE_ENDED -> {
                active.seekToDefaultPosition(0)
                active.play()
            }
            active.playbackState == Player.STATE_IDLE -> {
                active.prepare()
                active.play()
            }
            active.isPlaying -> active.pause()
            else -> active.play()
        }
    }

    private fun cyclePlaybackMode() {
        val active = activeController() ?: return
        val current = PlaybackModePolicy.current(active.shuffleModeEnabled, active.repeatMode)
        val state = PlaybackModePolicy.state(PlaybackModePolicy.next(current))
        active.shuffleModeEnabled = state.shuffleEnabled
        active.repeatMode = state.repeatMode
        renderPlaybackMode()
    }

    private fun showSpeedMenu() {
        val active = activeController() ?: return
        val menu = PopupMenu(this, binding.speedButton)
        SPEED_OPTIONS.forEachIndexed { index, speed ->
            menu.menu.add(0, index, index, formatSpeed(speed))
        }
        menu.setOnMenuItemClickListener { item ->
            SPEED_OPTIONS.getOrNull(item.itemId)?.let { speed ->
                activeController()?.setPlaybackSpeed(speed)
            }
            true
        }
        menu.show()
    }

    private fun showSleepTimerMenu() {
        if (activeController() == null) return
        val labels = ArrayList<String>()
        val actions = ArrayList<() -> Unit>()
        listOf(15, 30, 60).forEach { minutes ->
            labels += resources.getQuantityString(R.plurals.timer_minutes, minutes, minutes)
            actions += { setSleepTimer(minutes.toLong()) }
        }
        labels += getString(R.string.timer_custom)
        actions += ::showCustomSleepTimerDialog
        labels += getString(R.string.timer_end_of_track)
        actions += {
            sendCustomCommand(PlaybackSessionContract.COMMAND_STOP_AFTER_CURRENT)
        }
        if (toolState.sleepMode != PlaybackSessionContract.SLEEP_MODE_OFF) {
            labels += getString(R.string.timer_cancel)
            actions += {
                sendCustomCommand(PlaybackSessionContract.COMMAND_CANCEL_SLEEP_TIMER)
            }
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.sleep_timer)
            .setItems(labels.toTypedArray()) { _, which -> actions[which]() }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
            .also(::tintDialogButtons)
    }

    private fun showCustomSleepTimerDialog() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = getString(R.string.timer_custom_hint)
            setText(String.format(Locale.getDefault(), "%d", CUSTOM_TIMER_DEFAULT_MINUTES))
            setSelectAllOnFocus(true)
            setTextColor(audioPalette.onSurface)
            setHintTextColor(audioPalette.onSurfaceVariant)
            backgroundTintList = ColorStateList.valueOf(audioPalette.primary)
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.timer_custom)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val minutes = input.text?.toString()?.toLongOrNull()
                if (minutes == null || minutes !in MIN_CUSTOM_TIMER_MINUTES..MAX_CUSTOM_TIMER_MINUTES) {
                    Toast.makeText(this, R.string.timer_invalid, Toast.LENGTH_LONG).show()
                } else {
                    setSleepTimer(minutes)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
            .also(::tintDialogButtons)
    }

    private fun setSleepTimer(minutes: Long) {
        val durationMs = minutes * 60_000L
        if (durationMs !in SleepTimerPolicy.MIN_DURATION_MS..SleepTimerPolicy.MAX_DURATION_MS) {
            Toast.makeText(this, R.string.timer_invalid, Toast.LENGTH_LONG).show()
            return
        }
        sendCustomCommand(
            PlaybackSessionContract.COMMAND_SET_SLEEP_TIMER,
            Bundle().apply { putLong(PlaybackSessionContract.ARG_DURATION_MS, durationMs) },
        )
    }

    private fun cycleAbLoop() {
        val active = activeController() ?: return
        val positionMs = active.currentPosition.coerceAtLeast(0L)
        when {
            toolState.abStartMs == null -> sendPositionCommand(
                PlaybackSessionContract.COMMAND_SET_AB_START,
                positionMs,
            ) {
                Toast.makeText(
                    this,
                    getString(R.string.ab_start_set_hint, formatTime(positionMs)),
                    Toast.LENGTH_LONG,
                ).show()
            }
            toolState.abEndMs == null -> {
                val startMs = requireNotNull(toolState.abStartMs)
                if (!AbLoopPolicy.validEnd(
                        startMs,
                        positionMs,
                        durationMs(),
                    )
                ) {
                    Toast.makeText(this, R.string.ab_invalid_end, Toast.LENGTH_LONG).show()
                    return
                }
                sendPositionCommand(
                    PlaybackSessionContract.COMMAND_SET_AB_END,
                    positionMs,
                ) {
                    Toast.makeText(
                        this,
                        getString(
                            R.string.ab_loop_started,
                            formatTime(startMs),
                            formatTime(positionMs),
                        ),
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
            else -> sendCustomCommand(PlaybackSessionContract.COMMAND_CLEAR_AB_LOOP) {
                Toast.makeText(this, R.string.ab_loop_cleared, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tintDialogButtons(dialog: AlertDialog) {
        listOf(
            AlertDialog.BUTTON_POSITIVE,
            AlertDialog.BUTTON_NEGATIVE,
            AlertDialog.BUTTON_NEUTRAL,
        ).forEach { which -> dialog.getButton(which)?.setTextColor(audioPalette.primary) }
    }

    private fun sendPositionCommand(
        command: SessionCommand,
        positionMs: Long,
        onSuccess: () -> Unit = {},
    ) {
        sendCustomCommand(
            command,
            Bundle().apply { putLong(PlaybackSessionContract.ARG_POSITION_MS, positionMs) },
            onSuccess,
        )
    }

    private fun sendCustomCommand(
        command: SessionCommand,
        args: Bundle = Bundle.EMPTY,
        onSuccess: () -> Unit = {},
    ) {
        val active = activeController()
        if (active == null || !active.isSessionCommandAvailable(command)) {
            Toast.makeText(this, R.string.error_control_unavailable, Toast.LENGTH_LONG).show()
            return
        }
        val future = active.sendCustomCommand(command, args)
        future.addListener(
            {
                val result = runCatching { future.get() }.getOrNull()
                if (result?.resultCode == SessionResult.RESULT_SUCCESS) {
                    onSuccess()
                } else {
                    Toast.makeText(this, R.string.error_control_unavailable, Toast.LENGTH_LONG).show()
                }
            },
            ContextCompat.getMainExecutor(this),
        )
    }

    private fun showQueue() {
        val active = activeController() ?: return
        if (active.mediaItemCount == 0) return
        dismissQueue()
        val sheetBinding = BottomSheetPlaybackQueueBinding.inflate(layoutInflater)
        val adapter = PlaybackQueueAdapter(
            palette = audioPalette,
            onSelect = { index ->
                activeController()?.takeIf { index in 0 until it.mediaItemCount }?.let { controller ->
                    controller.seekToDefaultPosition(index)
                    controller.play()
                }
            },
            onRemove = { index ->
                activeController()?.takeIf { index in 0 until it.mediaItemCount }?.removeMediaItem(index)
            },
        )
        AudioThemeViewStyler.applyQueue(sheetBinding, audioPalette)
        sheetBinding.queueList.layoutManager = LinearLayoutManager(this)
        sheetBinding.queueList.adapter = adapter
        val dialog = BottomSheetDialog(this).apply {
            setContentView(sheetBinding.root)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            setOnDismissListener {
                queueDialog = null
                queueSheetBinding = null
                queueAdapter = null
                binding.queueButton.isActivated = false
            }
        }
        queueDialog = dialog
        queueSheetBinding = sheetBinding
        queueAdapter = adapter
        renderQueue()
        dialog.show()
        binding.queueButton.isActivated = true
    }

    private fun dismissQueue() {
        queueDialog?.setOnDismissListener(null)
        queueDialog?.dismiss()
        queueDialog = null
        queueSheetBinding = null
        queueAdapter = null
        if (::binding.isInitialized) binding.queueButton.isActivated = false
    }

    private fun startPlayback() {
        if (AudioPlaybackService.startPlayback(this, request)) {
            connectController()
        } else {
            request.hostSession?.let { session -> runCatching { session.close() } }
            showPlaybackError(null)
        }
    }

    private fun connectController() {
        if (controllerFuture != null) return
        val token = SessionToken(this, ComponentName(this, AudioPlaybackService::class.java))
        val future = MediaController.Builder(this, token)
            .setListener(controllerCallback)
            .buildAsync()
        controllerFuture = future
        future.addListener(
            {
                if (controllerFuture !== future || isFinishing || isDestroyed) return@addListener
                runCatching { future.get() }
                    .onSuccess { connected ->
                        controller = connected
                        connected.addListener(playerListener)
                        selectedAudioStream = selectedAudioStream(connected.currentTracks)
                        toolState = PlaybackSessionContract.stateFrom(connected.sessionExtras)
                        renderMetadata(connected.mediaMetadata)
                        renderControls()
                        if (connected.playerError == null) hidePlaybackError() else showPlaybackError(connected.playerError)
                    }
                    .onFailure {
                        controllerFuture = null
                        showPlaybackError(null)
                    }
            },
            ContextCompat.getMainExecutor(this),
        )
    }

    private fun disconnectController() {
        controller?.removeListener(playerListener)
        controller = null
        selectedAudioStream = null
        controllerFuture?.let { future -> runCatching { MediaController.releaseFuture(future) } }
        controllerFuture = null
    }

    private fun activeController(): MediaController? = controller?.takeIf(MediaController::isConnected)

    private fun durationMs(): Long? = activeController()
        ?.duration
        ?.takeIf { it != C.TIME_UNSET && it > 0L }

    private fun renderControls() {
        renderPlayPause()
        renderBuffering()
        renderPlaybackMode()
        renderSpeed()
        renderProgress()
        renderAdjacentControls()
        renderQueue()
        renderPlaybackTools()
    }

    private fun renderMetadata(metadata: MediaMetadata?) {
        val title = metadata?.title
            ?.toString()
            ?.let(DisplayNamePolicy::sanitize)
            ?.takeIf(String::isNotBlank)
            ?: request.displayName
        binding.titleText.text = title

        val subtitle = listOfNotNull(
            sanitizedOrNull(metadata?.artist),
            sanitizedOrNull(metadata?.albumTitle),
        ).joinToString(SUBTITLE_SEPARATOR)
        binding.subtitleText.text = subtitle
        binding.subtitleText.visibility = if (subtitle.isNotEmpty()) View.VISIBLE else View.INVISIBLE

        val extras = metadata?.extras
        val sourceMimeType = extras?.getString(AudioPlaybackContract.MEDIA_EXTRA_SOURCE_MIME_TYPE)
            ?: request.mimeType
        val info = AudioInfoPolicy.format(
            extras?.getString(AudioMetadataResolver.EXTRA_MIME_TYPE)
                ?: selectedAudioStream?.mimeType
                ?: sourceMimeType,
            extras?.getInt(AudioMetadataResolver.EXTRA_SAMPLE_RATE_HZ, 0)?.takeIf { it > 0 }
                ?: selectedAudioStream?.sampleRateHz,
            extras?.getInt(AudioMetadataResolver.EXTRA_BITRATE_BPS, 0)?.takeIf { it > 0 }
                ?: selectedAudioStream?.bitrateBps,
        )
        binding.infoText.text = info
        binding.infoText.visibility = if (info.isNotEmpty()) View.VISIBLE else View.INVISIBLE

        val sourceUri = extras?.getString(AudioPlaybackContract.MEDIA_EXTRA_SOURCE_URI)
            ?.let(Uri::parse)
            ?: request.uri
        binding.openExternalButton.isVisible =
            sourceUri.scheme == "content" && !sourceUri.authority.isNullOrBlank()

        renderArtwork(metadata?.artworkData)
    }

    private fun renderArtwork(artworkData: ByteArray?) {
        val bitmap = artworkData?.let(::decodeArtwork)
        if (bitmap != null) {
            binding.artworkImage.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.artworkImage.imageTintList = null
            binding.artworkImage.setImageBitmap(bitmap)
        } else {
            binding.artworkImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            binding.artworkImage.imageTintList = ColorStateList.valueOf(
                AudioThemePaletteGenerator.withAlpha(
                    audioPalette.onPrimaryContainer,
                    PLACEHOLDER_ICON_ALPHA,
                ),
            )
            binding.artworkImage.setImageResource(R.drawable.ic_audio_placeholder)
        }
    }

    private fun decodeArtwork(data: ByteArray): Bitmap? = runCatching {
        BitmapFactory.decodeByteArray(data, 0, data.size)
    }.getOrNull()

    private fun renderPlayPause() {
        val showPause = activeController()?.isPlaying == true
        binding.playPauseButton.setImageResource(if (showPause) R.drawable.ic_pause else R.drawable.ic_play)
        binding.playPauseButton.contentDescription =
            getString(if (showPause) R.string.action_pause else R.string.action_play)
    }

    private fun renderBuffering() {
        binding.bufferingIndicator.isVisible =
            activeController()?.playbackState == Player.STATE_BUFFERING
    }

    private fun renderPlaybackMode() {
        val active = activeController()
        val mode = PlaybackModePolicy.current(
            active?.shuffleModeEnabled == true,
            active?.repeatMode ?: Player.REPEAT_MODE_OFF,
        )
        val (icon, label) = when (mode) {
            PlaybackMode.SEQUENTIAL -> R.drawable.ic_sequence to R.string.playback_mode_sequential
            PlaybackMode.SHUFFLE -> R.drawable.ic_shuffle to R.string.playback_mode_shuffle
            PlaybackMode.REPEAT_ONE -> R.drawable.ic_repeat_one to R.string.playback_mode_repeat_one
        }
        binding.playbackModeButton.setIconResource(icon)
        binding.playbackModeButton.contentDescription = getString(label)
        binding.playbackModeButton.isEnabled = active != null
        binding.playbackModeButton.isActivated = active != null && mode != PlaybackMode.SEQUENTIAL
    }

    private fun renderSpeed() {
        val speed = activeController()?.playbackParameters?.speed ?: 1f
        binding.speedButton.text = formatSpeed(speed)
        binding.speedButton.isEnabled = activeController() != null
        binding.speedButton.isActivated = activeController() != null && speed != 1f
    }

    private fun renderAdjacentControls() {
        val active = activeController()
        val hasQueue = (active?.mediaItemCount ?: 0) > 1
        binding.seekBackwardButton.isEnabled = active != null
        binding.seekForwardButton.isEnabled = active != null
        binding.previousButton.isEnabled = hasQueue && active?.hasPreviousMediaItem() == true
        binding.nextButton.isEnabled = hasQueue && active?.hasNextMediaItem() == true
        binding.previousButton.alpha = 1f
        binding.nextButton.alpha = 1f
    }

    private fun renderQueue() {
        val active = activeController()
        val count = active?.mediaItemCount ?: 0
        binding.queueButton.text = NumberFormat.getIntegerInstance().format(count.coerceAtLeast(0))
        binding.queueButton.isEnabled = count > 0
        binding.queueButton.isActivated = queueDialog?.isShowing == true
        binding.queueButton.contentDescription = resources.getQuantityString(
            R.plurals.queue_track_count,
            count,
            count,
        )
        val rows = if (active == null) {
            emptyList()
        } else {
            (0 until count).map { index ->
                val item = active.getMediaItemAt(index)
                PlaybackQueueRow(
                    mediaId = item.mediaId,
                    index = index,
                    title = sanitizedOrNull(item.mediaMetadata.title) ?: getString(R.string.unknown_audio),
                    current = index == active.currentMediaItemIndex,
                )
            }
        }
        queueAdapter?.submitList(rows)
        queueSheetBinding?.let { sheet ->
            sheet.queueCount.text = resources.getQuantityString(
                R.plurals.queue_track_count,
                count,
                count,
            )
            sheet.queueList.isVisible = rows.isNotEmpty()
            sheet.emptyQueue.isVisible = rows.isEmpty()
        }
        if (rows.isEmpty() && queueDialog?.isShowing == true) dismissQueue()
    }

    private fun renderPlaybackTools() {
        renderSleepTimer()
        renderAbLoop()
    }

    private fun renderSleepTimer() {
        val active = toolState.sleepMode != PlaybackSessionContract.SLEEP_MODE_OFF
        binding.sleepTimerButton.isEnabled = activeController() != null
        binding.sleepTimerButton.alpha = 1f
        binding.sleepTimerButton.isActivated = active
        when (toolState.sleepMode) {
            PlaybackSessionContract.SLEEP_MODE_DEADLINE -> {
                val remainingMs = SleepTimerPolicy.remainingMs(
                    toolState.sleepDeadlineElapsedRealtimeMs,
                    SystemClock.elapsedRealtime(),
                )
                val minutes = ceil(remainingMs / 60_000.0).toLong().coerceAtLeast(1L)
                binding.sleepTimerButton.text = String.format(Locale.ROOT, "%dm", minutes)
                binding.sleepTimerButton.contentDescription = resources.getQuantityString(
                    R.plurals.timer_remaining,
                    minutes.toInt(),
                    minutes,
                )
            }
            PlaybackSessionContract.SLEEP_MODE_END_OF_TRACK -> {
                binding.sleepTimerButton.text = TIMER_END_GLYPH
                binding.sleepTimerButton.contentDescription = getString(R.string.timer_end_of_track)
            }
            else -> {
                binding.sleepTimerButton.text = ""
                binding.sleepTimerButton.contentDescription = getString(R.string.action_sleep_timer)
            }
        }
    }

    private fun renderAbLoop() {
        val start = toolState.abStartMs
        val end = toolState.abEndMs
        val active = start != null && end != null
        binding.abLoopButton.isEnabled = activeController() != null
        binding.abLoopButton.alpha = 1f
        binding.abLoopButton.isActivated = active || start != null
        binding.abLoopButton.text = when {
            active -> getString(R.string.ab_short_active)
            start != null -> getString(R.string.ab_short_start_set)
            else -> getString(R.string.ab_short)
        }
        binding.abLoopButton.contentDescription = abStateDescription()
    }

    private fun abStateDescription(): String = when {
        toolState.abStartMs != null && toolState.abEndMs != null -> getString(
            R.string.ab_state_active,
            formatTime(requireNotNull(toolState.abStartMs)),
            formatTime(requireNotNull(toolState.abEndMs)),
        )
        toolState.abStartMs != null -> getString(
            R.string.ab_state_start_set,
            formatTime(requireNotNull(toolState.abStartMs)),
        )
        else -> getString(R.string.ab_state_off)
    }

    private fun renderProgress() {
        val active = activeController()
        val duration = durationMs()
        val position = active?.currentPosition?.coerceAtLeast(0L) ?: 0L
        binding.durationText.text = duration?.let(::formatTime) ?: TIME_PLACEHOLDER
        if (isSeekBarTracking) return
        binding.positionText.text = if (active != null) formatTime(position) else TIME_PLACEHOLDER
        binding.seekBar.isEnabled = duration != null
        binding.seekBar.progress = if (duration != null) {
            ((position * SEEK_BAR_MAX) / duration).toInt().coerceIn(0, SEEK_BAR_MAX)
        } else {
            0
        }
    }

    private fun showPlaybackError(error: PlaybackException?) {
        binding.errorPanel.isVisible = true
        binding.errorDetailText.text = error?.errorCodeName.orEmpty()
        binding.errorDetailText.isVisible = error != null
    }

    private fun hidePlaybackError() {
        binding.errorPanel.isVisible = false
    }

    private fun openWithAnotherApp() {
        val currentRequest = currentTrackRequest() ?: return
        activeController()?.stop()
        stopService(Intent(this, AudioPlaybackService::class.java))
        ExternalAudioOpener.open(this, currentRequest)
    }

    private fun currentTrackRequest(): AudioPlaybackRequest? {
        val active = activeController()
        if (active == null) {
            return request.currentTrack
                .takeIf { it.uri.scheme == "content" && !it.uri.authority.isNullOrBlank() }
                ?.let { AudioPlaybackRequest(it.uri, it.mimeType, it.displayName) }
        }
        val extras = active.mediaMetadata.extras ?: return null
        val uri = extras?.getString(AudioPlaybackContract.MEDIA_EXTRA_SOURCE_URI)
            ?.let(Uri::parse)
            ?.takeIf { it.scheme == "content" && !it.authority.isNullOrBlank() }
        val mimeType = extras?.getString(AudioPlaybackContract.MEDIA_EXTRA_SOURCE_MIME_TYPE)
            ?.let(MimeTypePolicy::normalize)
            ?.takeIf(MimeTypePolicy::isAudio)
        val displayName = extras?.getString(AudioPlaybackContract.MEDIA_EXTRA_SOURCE_DISPLAY_NAME)
            ?.let(DisplayNamePolicy::sanitizeIncoming)
        return if (uri != null && mimeType != null && displayName != null) {
            AudioPlaybackRequest(uri, mimeType, displayName)
        } else {
            null
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun renderNotificationPermission() {
        binding.notificationPermissionBanner.isVisible =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
    }

    private fun sanitizedOrNull(value: CharSequence?): String? = value
        ?.toString()
        ?.let(DisplayNamePolicy::sanitize)
        ?.takeIf(String::isNotBlank)

    @ExperimentalOptIn(markerClass = [UnstableApi::class])
    private fun selectedAudioStream(tracks: Tracks): SelectedAudioStream? {
        tracks.groups.forEach { group ->
            if (group.type != C.TRACK_TYPE_AUDIO || !group.isSelected) return@forEach
            for (index in 0 until group.length) {
                if (!group.isTrackSelected(index)) continue
                val format = group.getTrackFormat(index)
                return SelectedAudioStream(
                    mimeType = format.sampleMimeType ?: format.containerMimeType,
                    sampleRateHz = format.sampleRate.takeIf { it > 0 },
                    bitrateBps = format.averageBitrate.takeIf { it > 0 }
                        ?: format.peakBitrate.takeIf { it > 0 },
                )
            }
        }
        return null
    }

    private fun progressToPosition(progress: Int, durationMs: Long): Long =
        (durationMs * progress.coerceIn(0, SEEK_BAR_MAX)) / SEEK_BAR_MAX

    private fun formatTime(positionMs: Long): String {
        val totalSeconds = positionMs.coerceAtLeast(0L) / 1000L
        val hours = totalSeconds / 3600L
        val minutes = totalSeconds % 3600L / 60L
        val seconds = totalSeconds % 60L
        return if (hours > 0L) {
            String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.ROOT, "%d:%02d", minutes, seconds)
        }
    }

    private fun formatSpeed(speed: Float): String {
        val rounded = (speed * 100).toInt()
        val text = when {
            rounded % 100 == 0 -> (rounded / 100).toString()
            rounded % 10 == 0 -> String.format(Locale.ROOT, "%.1f", speed)
            else -> String.format(Locale.ROOT, "%.2f", speed)
        }
        return "$text$SPEED_SUFFIX"
    }

    private companion object {
        const val SEEK_BAR_MAX = 1000
        const val PROGRESS_TICK_MS = 500L
        const val PLACEHOLDER_ICON_ALPHA = 0xB8
        const val TIME_PLACEHOLDER = "--:--"
        const val SUBTITLE_SEPARATOR = " · "
        const val SPEED_SUFFIX = "×"
        const val TIMER_END_GLYPH = "♪"
        const val CUSTOM_TIMER_DEFAULT_MINUTES = 45
        const val MIN_CUSTOM_TIMER_MINUTES = 1L
        const val MAX_CUSTOM_TIMER_MINUTES = 24L * 60L
        val SPEED_OPTIONS = floatArrayOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
    }

    private data class SelectedAudioStream(
        val mimeType: String?,
        val sampleRateHz: Int?,
        val bitrateBps: Int?,
    )
}
