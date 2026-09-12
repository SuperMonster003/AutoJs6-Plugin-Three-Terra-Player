package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.content.Context
import android.content.Intent
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.LoadedPlaylist
import io.github.supermonster003.autojs6.plugin.threeterraplayer.playlist.PlaylistParser
import io.github.supermonster003.autojs6.plugin.threeterraplayer.policy.AudioMimePolicy

internal object PlaylistPlayback {
    fun mediaMime(name: String, declared: String?): String? {
        if (PlaylistParser.format(name, declared) != null) return null
        return AudioMimePolicy.resolve(declared ?: "*/*", name)
    }

    fun documentIntent(context: Context, loaded: LoadedPlaylist): Intent = AudioPlaybackContract.playerIntent(
        context,
        AudioPlaybackRequest(loaded.items.map { item ->
            AudioTrackRequest(requireNotNull(item.uri), item.mimeType, item.displayName, preferDisplayName = true)
        }),
        startPlayback = true,
    )
}
