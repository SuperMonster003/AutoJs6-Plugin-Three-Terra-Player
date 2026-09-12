package io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.supermonster003.autojs6.plugin.threeterraplayer.PlaylistPlayback
import io.github.supermonster003.autojs6.plugin.threeterraplayer.R
import io.github.supermonster003.autojs6.plugin.threeterraplayer.theme.AudioThemedActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Private document ingress. A single file grant does not imply access to its neighbours. */
class PlaylistActivity : AudioThemedActivity() {
    private var folderPrompted = false
    private var directory: Uri? = null
    private val folderPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri == null) {
            finish()
        } else {
            directory = uri
            load()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val source = intent.data
        if (source == null || PlaylistLocationPolicy.contentUri(source.toString()) == null) { finish(); return }
        folderPrompted = savedInstanceState?.getBoolean("folderPrompted") ?: false
        directory = savedInstanceState?.getString("directory")?.let(Uri::parse)
        setContentView(FrameLayout(this).apply {
            addView(ProgressBar(this@PlaylistActivity), FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER,
            ))
        })
        if (!folderPrompted || directory != null) load()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("folderPrompted", folderPrompted)
        outState.putString("directory", directory?.toString())
        super.onSaveInstanceState(outState)
    }

    private fun load() {
        lifecycleScope.launch {
            try {
                val entries = withContext(Dispatchers.IO) {
                    val source = PlaylistLoader.source(this@PlaylistActivity, requireNotNull(intent.data), intent.type)
                    PlaylistLoader.read(this@PlaylistActivity, source)
                }
                if (!folderPrompted && entries.any { entry ->
                        entry.locations.any { PlaylistLocationPolicy.relativeSegments(it) != null }
                    }
                ) {
                    MaterialAlertDialogBuilder(this@PlaylistActivity)
                        .setTitle(R.string.playlist_folder_title)
                        .setMessage(R.string.playlist_folder_message)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            folderPrompted = true
                            folderPicker.launch(null)
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> finish() }
                        .setOnCancelListener { finish() }
                        .show()
                    return@launch
                }
                val loaded = withContext(Dispatchers.IO) {
                    PlaylistLoader.documents(this@PlaylistActivity, entries, directory, PlaylistPlayback::mediaMime)
                }
                if (loaded.items.isEmpty()) throw PlaylistException(PlaylistError.EMPTY)
                showSkipped(this@PlaylistActivity, loaded)
                startActivity(PlaylistPlayback.documentIntent(this@PlaylistActivity, loaded))
                finish()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                showError(this@PlaylistActivity, error)
                finish()
            }
        }
    }

    internal companion object {
        fun intent(context: Context, source: PlaylistSource) = Intent(context, PlaylistActivity::class.java).apply {
            setDataAndType(source.uri, source.mimeType ?: "application/octet-stream")
            clipData = ClipData.newRawUri(source.displayName, source.uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        fun showError(context: Context, error: Throwable) {
            val message = when ((error as? PlaylistException)?.reason) {
                PlaylistError.HLS -> R.string.playlist_hls_unsupported
                PlaylistError.EMPTY -> R.string.playlist_empty
                PlaylistError.TOO_LARGE -> R.string.playlist_too_large
                else -> R.string.playlist_invalid
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun showSkipped(context: Context, loaded: LoadedPlaylist) {
            if (loaded.skipped > 0) Toast.makeText(context,
                context.getString(R.string.playlist_skipped, loaded.items.size, loaded.skipped), Toast.LENGTH_LONG,
            ).show()
        }
    }
}
