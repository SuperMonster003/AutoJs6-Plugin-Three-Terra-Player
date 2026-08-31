package io.github.supermonster003.autojs6.plugin.threeterraplayer

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
import androidx.lifecycle.lifecycleScope
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
import io.github.supermonster003.autojs6.plugin.threeterraplayer.databinding.ActivityAudioPlayerBinding
import io.github.supermonster003.autojs6.plugin.threeterraplayer.databinding.BottomSheetPlaybackQueueBinding
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.AbLoopPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.AudioInfoPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.DisplayNamePolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.MimeTypePolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackControlPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackMode
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackModePolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.PlaybackPreferencePolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.SleepTimerPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppPreferenceStore
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemePaletteGenerator
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.ArtworkColorPolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemeDialogStyler
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemePalette
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.SettingsActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemeViewStyler
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemedActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.UiFeedback
import io.github.supermonster003.autojs6.plugin.threeterraplayer.update.AppUpdateCoordinator
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private var pendingPlaybackTarget = true
    private val appPreferenceStore by lazy(LazyThreadSafetyMode.NONE) { AppPreferenceStore(this) }
    private lateinit var playerPalette: AudioThemePalette
    private var artworkJob: Job? = null
    private var artworkRendered = false
    private var artworkSignature: Int? = null
    private var lastRenderedPause: Boolean? = null

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
            renderMetadataForCurrentQueue(mediaMetadata)
            renderQueue()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            selectedAudioStream = null
            if (mediaItem == null) {
                pendingPlaybackTarget = false
                toolState = PlaybackToolState()
                renderEmptyQueueMetadata()
            } else {
                pendingPlaybackTarget = true
                renderMetadata(mediaItem.mediaMetadata)
            }
            renderQueue()
            renderAdjacentControls()
            renderPlaybackTools()
            renderPlayPause()
            hidePlaybackError()
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            pendingPlaybackTarget = !timeline.isEmpty
            if (timeline.isEmpty) {
                selectedAudioStream = null
                toolState = PlaybackToolState()
                renderEmptyQueueMetadata()
                hidePlaybackError()
            } else {
                renderMetadata(activeController()?.mediaMetadata)
            }
            renderControls()
        }

        override fun onTracksChanged(tracks: Tracks) {
            selectedAudioStream = selectedAudioStream(tracks)
            renderMetadataForCurrentQueue(activeController()?.mediaMetadata)
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
            renderControls()
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
        playerPalette = audioPalette
        request = AudioPlaybackContract.resolvePlayerIntent(intent) ?: run {
            finish()
            return
        }
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyEdgeToEdge(
            binding.playerRoot,
            binding.toolbar,
            binding.playerContent,
            binding.notificationPermissionBanner,
        )
        AudioThemeViewStyler.applyPlayer(this, binding, playerPalette)
        binding.toolbar.setNavigationOnClickListener { finishAfterTransition() }
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
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
        pendingPlaybackTarget = true
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
        if (::binding.isInitialized) renderAdjacentControls()
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
        binding.seekBackwardButton.setOnClickListener { view ->
            activeController()?.let { active ->
                UiFeedback.confirm(view)
                active.seekBack()
            }
        }
        binding.seekForwardButton.setOnClickListener { view ->
            activeController()?.let { active ->
                UiFeedback.confirm(view)
                active.seekForward()
            }
        }
        binding.previousButton.setOnClickListener { view ->
            activeController()?.let { active ->
                UiFeedback.confirm(view)
                active.seekToPreviousMediaItem()
            }
        }
        binding.nextButton.setOnClickListener { view ->
            activeController()?.let { active ->
                UiFeedback.confirm(view)
                active.seekToNextMediaItem()
            }
        }
        binding.playbackModeButton.setOnClickListener { cyclePlaybackMode() }
        binding.queueButton.setOnClickListener { showQueue() }
        binding.speedButton.setOnClickListener { showSpeedMenu() }
        binding.sleepTimerButton.setOnClickListener { showSleepTimerMenu() }
        binding.abLoopButton.setOnClickListener { cycleAbLoop() }
        binding.abLoopButton.setOnLongClickListener {
            if (!controlAvailability().playbackTools || toolState.abStartMs == null) {
                false
            } else {
                UiFeedback.confirm(binding.abLoopButton)
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
                UiFeedback.confirm(seekBar)
                activeController()?.seekTo(progressToPosition(seekBar.progress, duration))
            }
        })
    }

    private fun onPlayPauseClicked() {
        if (!controlAvailability().playPause) return
        UiFeedback.confirm(binding.playPauseButton)
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
        if (!controlAvailability().playbackTools) return
        val active = activeController() ?: return
        UiFeedback.confirm(binding.playbackModeButton)
        val current = PlaybackModePolicy.current(active.shuffleModeEnabled, active.repeatMode)
        val state = PlaybackModePolicy.state(PlaybackModePolicy.next(current))
        active.shuffleModeEnabled = state.shuffleEnabled
        active.repeatMode = state.repeatMode
        renderPlaybackMode()
    }

    private fun showSpeedMenu() {
        if (!controlAvailability().playbackTools) return
        val active = activeController() ?: return
        val menu = PopupMenu(this, binding.speedButton)
        PlaybackPreferencePolicy.SPEED_OPTIONS.forEachIndexed { index, speed ->
            menu.menu.add(0, index, index, formatSpeed(speed))
        }
        menu.setOnMenuItemClickListener { item ->
            PlaybackPreferencePolicy.SPEED_OPTIONS.getOrNull(item.itemId)?.let { speed ->
                UiFeedback.confirm(binding.speedButton)
                activeController()?.setPlaybackSpeed(speed)
            }
            true
        }
        menu.show()
    }

    private fun showSleepTimerMenu() {
        if (!controlAvailability().playbackTools) return
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
            .setItems(labels.toTypedArray()) { _, which ->
                UiFeedback.confirm(binding.sleepTimerButton)
                actions[which]()
            }
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
            setTextColor(playerPalette.onSurface)
            setHintTextColor(playerPalette.onSurfaceVariant)
            backgroundTintList = ColorStateList.valueOf(playerPalette.primary)
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
        if (!controlAvailability().playbackTools) return
        val active = activeController() ?: return
        UiFeedback.confirm(binding.abLoopButton)
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
        AudioThemeDialogStyler.apply(dialog, playerPalette)
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
            palette = playerPalette,
            onSelect = { index ->
                activeController()?.takeIf { index in 0 until it.mediaItemCount }?.let { controller ->
                    UiFeedback.confirm(binding.queueButton)
                    controller.seekToDefaultPosition(index)
                    controller.play()
                }
            },
            onRemove = { index ->
                activeController()?.takeIf { index in 0 until it.mediaItemCount }?.let { controller ->
                    UiFeedback.confirm(binding.queueButton)
                    controller.removeMediaItem(index)
                }
            },
        )
        AudioThemeViewStyler.applyQueue(sheetBinding, playerPalette)
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
                        pendingPlaybackTarget = connected.mediaItemCount > 0
                        selectedAudioStream = selectedAudioStream(connected.currentTracks)
                        toolState = PlaybackSessionContract.stateFrom(connected.sessionExtras)
                        renderMetadataForCurrentQueue(connected.mediaMetadata)
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

    private fun controlAvailability() = activeController().let { active ->
        PlaybackControlPolicy.resolve(
            controllerConnected = active != null,
            mediaItemCount = active?.mediaItemCount ?: 0,
            pendingPlaybackTarget = pendingPlaybackTarget,
        )
    }

    private fun durationMs(): Long? = activeController()
        ?.takeIf { it.mediaItemCount > 0 }
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

    private fun renderMetadataForCurrentQueue(metadata: MediaMetadata?) {
        if (controlAvailability().mediaActions) {
            renderMetadata(metadata)
        } else {
            renderEmptyQueueMetadata()
        }
    }

    private fun renderEmptyQueueMetadata() {
        binding.titleText.setText(R.string.queue_empty)
        binding.subtitleText.text = ""
        binding.subtitleText.visibility = View.INVISIBLE
        binding.infoText.text = ""
        binding.infoText.visibility = View.INVISIBLE
        binding.openExternalButton.isVisible = false
        renderArtwork(null)
    }

    private fun renderArtwork(artworkData: ByteArray?) {
        val signature = artworkData?.contentHashCode()
        if (artworkRendered && signature == artworkSignature) return
        artworkRendered = true
        artworkSignature = signature
        artworkJob?.cancel()

        if (artworkData == null) {
            applyPlayerPalette(audioPalette)
            showArtworkPlaceholder()
            return
        }

        // Metadata callbacks run on the main thread. Decode and quantize a bounded sample away
        // from it, then atomically apply the image and its generated semantic palette.
        showArtworkPlaceholder()
        artworkJob = lifecycleScope.launch {
            val result = withContext(Dispatchers.Default) { decodeArtworkResult(artworkData) }
            if (artworkSignature != signature || !::binding.isInitialized) return@launch
            if (result == null) {
                applyPlayerPalette(audioPalette)
                showArtworkPlaceholder()
            } else {
                val palette = result.seed?.let { seed ->
                    AudioThemePaletteGenerator.generate(seed, audioPalette.isDark)
                } ?: audioPalette
                applyPlayerPalette(palette)
                showDecodedArtwork(result.bitmap)
            }
        }
    }

    private fun showArtworkPlaceholder() {
        binding.artworkImage.animate().cancel()
        binding.artworkImage.alpha = 1f
        binding.artworkImage.scaleX = 1f
        binding.artworkImage.scaleY = 1f
        binding.artworkImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
        binding.artworkImage.imageTintList = ColorStateList.valueOf(
            AudioThemePaletteGenerator.withAlpha(
                playerPalette.onPrimaryContainer,
                PLACEHOLDER_ICON_ALPHA,
            ),
        )
        binding.artworkImage.setImageResource(R.drawable.ic_audio_placeholder)
    }

    private fun showDecodedArtwork(bitmap: Bitmap) {
        val view = binding.artworkImage
        view.animate().cancel()
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        view.imageTintList = null
        view.setImageBitmap(bitmap)
        if (!UiFeedback.animationsEnabled(this)) {
            view.alpha = 1f
            view.scaleX = 1f
            view.scaleY = 1f
            return
        }
        view.alpha = 0f
        view.scaleX = ARTWORK_ENTER_SCALE
        view.scaleY = ARTWORK_ENTER_SCALE
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(ARTWORK_ENTER_DURATION_MS)
            .start()
    }

    private fun applyPlayerPalette(value: AudioThemePalette) {
        if (playerPalette == value) return
        playerPalette = value
        AudioThemeViewStyler.applyPlayer(this, binding, value)
        applyWindowPalette(value)
        queueSheetBinding?.let { sheet -> AudioThemeViewStyler.applyQueue(sheet, value) }
        queueAdapter?.updatePalette(value)
    }

    private fun decodeArtworkResult(data: ByteArray): ArtworkResult? = runCatching {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size) ?: return null
        val largestSide = max(bitmap.width, bitmap.height).coerceAtLeast(1)
        val scale = (ARTWORK_SAMPLE_MAX_SIDE.toFloat() / largestSide).coerceAtMost(1f)
        val sampleWidth = (bitmap.width * scale).roundToInt().coerceAtLeast(1)
        val sampleHeight = (bitmap.height * scale).roundToInt().coerceAtLeast(1)
        val sample = if (sampleWidth == bitmap.width && sampleHeight == bitmap.height) {
            bitmap
        } else {
            Bitmap.createScaledBitmap(bitmap, sampleWidth, sampleHeight, true)
        }
        val pixels = IntArray(sample.width * sample.height)
        sample.getPixels(pixels, 0, sample.width, 0, 0, sample.width, sample.height)
        val seed = ArtworkColorPolicy.dominantSeed(pixels)
        if (sample !== bitmap) sample.recycle()
        ArtworkResult(bitmap, seed)
    }.getOrNull()

    private fun renderPlayPause() {
        val showPause = activeController()?.isPlaying == true
        val icon = if (showPause) R.drawable.ic_pause else R.drawable.ic_play
        val previous = lastRenderedPause
        lastRenderedPause = showPause
        val button = binding.playPauseButton
        if (previous != showPause) {
            button.animate().cancel()
            if (previous != null && UiFeedback.animationsEnabled(this)) {
                button.animate()
                    .alpha(0f)
                    .scaleX(PLAY_ICON_MID_SCALE)
                    .scaleY(PLAY_ICON_MID_SCALE)
                    .setDuration(PLAY_ICON_OUT_DURATION_MS)
                    .withEndAction transitionEnd@{
                        if (lastRenderedPause != showPause) return@transitionEnd
                        button.setImageResource(icon)
                        button.animate()
                            .alpha(1f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(PLAY_ICON_IN_DURATION_MS)
                            .start()
                    }
                    .start()
            } else {
                button.alpha = 1f
                button.scaleX = 1f
                button.scaleY = 1f
                button.setImageResource(icon)
            }
        }
        binding.playPauseButton.contentDescription =
            getString(if (showPause) R.string.action_pause else R.string.action_play)
        binding.playPauseButton.isEnabled = controlAvailability().playPause
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
        val enabled = controlAvailability().playbackTools
        binding.playbackModeButton.isEnabled = enabled
        binding.playbackModeButton.isActivated = enabled && mode != PlaybackMode.SEQUENTIAL
    }

    private fun renderSpeed() {
        val speed = activeController()?.playbackParameters?.speed ?: 1f
        binding.speedButton.text = formatSpeed(speed)
        val enabled = controlAvailability().playbackTools
        binding.speedButton.isEnabled = enabled
        binding.speedButton.isActivated = enabled && speed != 1f
    }

    private fun renderAdjacentControls() {
        val active = activeController()
        val mediaActionsEnabled = controlAvailability().mediaActions
        val hasQueue = (active?.mediaItemCount ?: 0) > 1
        binding.seekBackwardButton.isEnabled = mediaActionsEnabled
        binding.seekForwardButton.isEnabled = mediaActionsEnabled
        val seekSeconds = (appPreferenceStore.seekIncrementMs() / 1_000L).toInt()
        binding.seekBackwardButton.contentDescription = resources.getQuantityString(
            R.plurals.action_seek_backward_seconds,
            seekSeconds,
            seekSeconds,
        )
        binding.seekForwardButton.contentDescription = resources.getQuantityString(
            R.plurals.action_seek_forward_seconds,
            seekSeconds,
            seekSeconds,
        )
        binding.previousButton.isEnabled = mediaActionsEnabled && hasQueue && active?.hasPreviousMediaItem() == true
        binding.nextButton.isEnabled = mediaActionsEnabled && hasQueue && active?.hasNextMediaItem() == true
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
        binding.sleepTimerButton.isEnabled = controlAvailability().playbackTools
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
        binding.abLoopButton.isEnabled = controlAvailability().playbackTools
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
        binding.positionText.text = if (controlAvailability().mediaActions) {
            formatTime(position)
        } else {
            TIME_PLACEHOLDER
        }
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
        const val ARTWORK_SAMPLE_MAX_SIDE = 64
        const val ARTWORK_ENTER_DURATION_MS = 220L
        const val ARTWORK_ENTER_SCALE = 0.96f
        const val PLAY_ICON_OUT_DURATION_MS = 70L
        const val PLAY_ICON_IN_DURATION_MS = 110L
        const val PLAY_ICON_MID_SCALE = 0.82f
        const val PLACEHOLDER_ICON_ALPHA = 0xB8
        const val TIME_PLACEHOLDER = "--:--"
        const val SUBTITLE_SEPARATOR = " · "
        const val SPEED_SUFFIX = "×"
        const val TIMER_END_GLYPH = "♪"
        const val CUSTOM_TIMER_DEFAULT_MINUTES = 45
        const val MIN_CUSTOM_TIMER_MINUTES = 1L
        const val MAX_CUSTOM_TIMER_MINUTES = 24L * 60L
    }

    private data class SelectedAudioStream(
        val mimeType: String?,
        val sampleRateHz: Int?,
        val bitrateBps: Int?,
    )

    private data class ArtworkResult(
        val bitmap: Bitmap,
        val seed: Int?,
    )
}
