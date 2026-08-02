package io.github.supermonster003.autojs6.plugin.audioplayer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import io.github.supermonster003.autojs6.plugin.audioplayer.databinding.ActivityAudioPlayerBinding
import io.github.supermonster003.autojs6.plugin.audioplayer.policy.DisplayNamePolicy

/** Controller UI for the private MediaSessionService. */
class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var request: AudioPlaybackRequest
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        renderNotificationPermission()
    }

    private val playerListener = object : Player.Listener {
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            updateTitle(mediaMetadata.title)
        }

        override fun onPlayerError(error: PlaybackException) {
            showPlaybackError()
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            if (error == null) hidePlaybackError() else showPlaybackError()
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
        binding.toolbar.setNavigationOnClickListener { finishAfterTransition() }
        binding.notificationPermissionButton.setOnClickListener { requestNotificationPermission() }
        binding.openExternalButton.setOnClickListener { openWithAnotherApp() }
        updateTitle(request.displayName)
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
        updateTitle(request.displayName)
        hidePlaybackError()
        if (intent.action == AudioPlaybackContract.ACTION_START_PLAYBACK) startPlayback()
    }

    override fun onStart() {
        super.onStart()
        connectController()
    }

    override fun onStop() {
        disconnectController()
        super.onStop()
    }

    private fun startPlayback() {
        if (!AudioPlaybackService.startPlayback(this, request)) showPlaybackError()
    }

    private fun connectController() {
        if (controllerFuture != null) return
        val token = SessionToken(this, ComponentName(this, AudioPlaybackService::class.java))
        val future = MediaController.Builder(this, token).buildAsync()
        controllerFuture = future
        future.addListener(
            {
                if (controllerFuture !== future || isFinishing || isDestroyed) return@addListener
                runCatching { future.get() }
                    .onSuccess { connected ->
                        controller = connected
                        connected.addListener(playerListener)
                        binding.playerView.player = connected
                        updateTitle(connected.mediaMetadata.title)
                        if (connected.playerError == null) hidePlaybackError() else showPlaybackError()
                    }
                    .onFailure { showPlaybackError() }
            },
            ContextCompat.getMainExecutor(this),
        )
    }

    private fun disconnectController() {
        if (!::binding.isInitialized) return
        binding.playerView.player = null
        controller?.removeListener(playerListener)
        controller = null
        controllerFuture?.let(MediaController::releaseFuture)
        controllerFuture = null
    }

    private fun updateTitle(value: CharSequence?) {
        binding.toolbar.title = value
            ?.toString()
            ?.let(DisplayNamePolicy::sanitize)
            ?.takeIf(String::isNotBlank)
            ?: request.displayName
    }

    private fun showPlaybackError() {
        binding.errorPanel.isVisible = true
    }

    private fun hidePlaybackError() {
        binding.errorPanel.isVisible = false
    }

    private fun openWithAnotherApp() {
        controller?.stop()
        stopService(Intent(this, AudioPlaybackService::class.java))
        ExternalAudioOpener.open(this, request)
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
}
