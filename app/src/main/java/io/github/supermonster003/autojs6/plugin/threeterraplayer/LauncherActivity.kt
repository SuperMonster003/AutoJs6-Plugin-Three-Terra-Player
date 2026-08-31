package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import io.github.supermonster003.autojs6.plugin.threeterraplayer.databinding.ActivityLauncherBinding
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.ExplorerQueuePolicy
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.AppPreferenceStore
import io.github.supermonster003.autojs6.plugin.threeterraplayer.settings.SettingsActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemePaletteGenerator
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemedActivity
import io.github.supermonster003.autojs6.plugin.threeterraplayer.update.AppUpdateCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Launcher entry that turns the plugin package into a small standalone audio player. */
class LauncherActivity : AudioThemedActivity() {

    private lateinit var binding: ActivityLauncherBinding
    private var resolvingFiles = false

    private val documentPicker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        if (uris.isNotEmpty()) resolveAndPlay(uris)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyEdgeToEdge(binding.launcherRoot, binding.toolbar, binding.launcherContent)
        styleViews()
        binding.openAudioButton.setOnClickListener {
            if (!resolvingFiles) documentPicker.launch(AUDIO_DOCUMENT_MIME_TYPES)
        }
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        if (savedInstanceState == null) openRememberedSession()
    }

    override fun onResume() {
        super.onResume()
        AppUpdateCoordinator.maybeCheckAutomatically(this)
    }

    private fun resolveAndPlay(incomingUris: List<Uri>) {
        val uris = incomingUris.distinct().take(ExplorerQueuePolicy.MAX_TARGETS)
        resolvingFiles = true
        renderLoading()
        lifecycleScope.launch {
            val tracks = withContext(Dispatchers.IO) {
                uris.mapNotNull { uri -> ContentAudioRequestResolver.resolve(this@LauncherActivity, uri) }
            }
            resolvingFiles = false
            renderLoading()
            if (tracks.isEmpty()) {
                Toast.makeText(this@LauncherActivity, R.string.error_no_supported_audio, Toast.LENGTH_LONG).show()
                return@launch
            }
            if (tracks.size != uris.size || incomingUris.size > ExplorerQueuePolicy.MAX_TARGETS) {
                Toast.makeText(
                    this@LauncherActivity,
                    getString(R.string.some_files_skipped, tracks.size, incomingUris.size),
                    Toast.LENGTH_LONG,
                ).show()
            }
            // Keep access only for targets that passed the audio contract. The temporary picker
            // grant remains valid for this Activity while resolution runs on Dispatchers.IO.
            tracks.map(AudioTrackRequest::uri).forEach(::retainReadPermission)
            startActivity(
                AudioPlaybackContract.playerIntent(
                    this@LauncherActivity,
                    AudioPlaybackRequest(tracks),
                    startPlayback = true,
                ),
            )
        }
    }

    private fun retainReadPermission(uri: Uri) {
        runCatching {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun openRememberedSession() {
        if (!AppPreferenceStore(this).rememberPlaybackPosition) return
        val snapshot = PlaybackSessionStore(this).load() ?: return
        startActivity(
            AudioPlaybackContract.playerIntent(
                this,
                snapshot.request,
                startPlayback = false,
            ),
        )
    }

    private fun renderLoading() {
        binding.fileLoadingIndicator.visibility = if (resolvingFiles) View.VISIBLE else View.GONE
        binding.openAudioButton.isEnabled = !resolvingFiles
        binding.settingsButton.isEnabled = !resolvingFiles
    }

    private fun styleViews() {
        val palette = audioPalette
        binding.launcherRoot.setBackgroundColor(palette.background)
        binding.launcherContent.setBackgroundColor(palette.background)
        binding.toolbar.setBackgroundColor(palette.appBar)
        binding.toolbar.setTitleTextColor(palette.onAppBar)
        binding.welcomeTitle.setTextColor(palette.onBackground)
        binding.welcomeDescription.setTextColor(palette.onSurfaceVariant)
        binding.fileLoadingIndicator.indeterminateTintList = ColorStateList.valueOf(palette.primary)
        binding.openAudioButton.backgroundTintList = ColorStateList.valueOf(palette.primary)
        binding.openAudioButton.setTextColor(palette.onPrimary)
        binding.openAudioButton.rippleColor = ColorStateList.valueOf(
            AudioThemePaletteGenerator.withAlpha(palette.onPrimary, RIPPLE_ALPHA),
        )
        binding.settingsButton.setTextColor(palette.primary)
        binding.settingsButton.strokeColor = ColorStateList.valueOf(palette.outline)
        binding.settingsButton.rippleColor = ColorStateList.valueOf(
            AudioThemePaletteGenerator.withAlpha(palette.primary, RIPPLE_ALPHA),
        )
    }

    private companion object {
        val AUDIO_DOCUMENT_MIME_TYPES = arrayOf(
            "audio/*",
            "application/ogg",
            "application/x-ogg",
            "application/x-ms-wma",
            "application/vnd.ms-wma",
            "video/x-ms-asf",
        )
        const val RIPPLE_ALPHA = 0x24
    }
}
