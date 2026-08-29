package io.github.supermonster003.autojs6.plugin.threeterraplayer.policy

data class PlaybackControlAvailability(
    val playPause: Boolean,
    val mediaActions: Boolean,
    val queue: Boolean,
    val playbackTools: Boolean,
)

/** Keeps empty-queue controls inert while still allowing an unbound request to start playback. */
object PlaybackControlPolicy {

    fun resolve(
        controllerConnected: Boolean,
        mediaItemCount: Int,
        pendingPlaybackTarget: Boolean,
    ): PlaybackControlAvailability {
        val hasMediaItem = controllerConnected && mediaItemCount > 0
        return PlaybackControlAvailability(
            playPause = hasMediaItem || (!controllerConnected && pendingPlaybackTarget),
            mediaActions = hasMediaItem,
            queue = hasMediaItem,
            playbackTools = hasMediaItem,
        )
    }
}
