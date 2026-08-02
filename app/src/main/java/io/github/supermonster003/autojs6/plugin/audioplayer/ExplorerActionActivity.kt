package io.github.supermonster003.autojs6.plugin.audioplayer

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

/** Signature-protected bridge from AutoJs6 into private playback components. */
class ExplorerActionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            finish()
            return
        }
        val explorerRequest = ExplorerActionIntentPolicy.resolve(intent)
        if (explorerRequest == null || !canRead(explorerRequest)) {
            Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        val request = AudioPlaybackRequest(
            uri = explorerRequest.targetUri,
            mimeType = explorerRequest.mimeType,
            displayName = explorerRequest.displayName,
        )
        runCatching {
            startActivity(AudioPlaybackContract.playerIntent(this, request, startPlayback = true))
        }.onFailure {
            Toast.makeText(this, R.string.error_cannot_read_file, Toast.LENGTH_LONG).show()
        }
        finish()
    }

    private fun canRead(request: ExplorerAudioRequest): Boolean = runCatching {
        contentResolver.openFileDescriptor(request.targetUri, "r")?.use { true } ?: false
    }.getOrDefault(false)
}
