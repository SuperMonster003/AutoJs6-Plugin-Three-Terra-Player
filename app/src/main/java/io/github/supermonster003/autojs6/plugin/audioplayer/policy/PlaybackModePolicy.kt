package io.github.supermonster003.autojs6.plugin.audioplayer.policy

internal enum class PlaybackMode {
    SEQUENTIAL,
    SHUFFLE,
    REPEAT_ONE,
}

internal data class PlaybackModeState(
    val shuffleEnabled: Boolean,
    val repeatMode: Int,
)

/** Pure mapping between the three user-facing modes and Media3's two state properties. */
internal object PlaybackModePolicy {

    const val REPEAT_MODE_OFF = 0
    const val REPEAT_MODE_ONE = 1
    const val REPEAT_MODE_ALL = 2

    fun current(shuffleEnabled: Boolean, repeatMode: Int): PlaybackMode = when {
        repeatMode == REPEAT_MODE_ONE -> PlaybackMode.REPEAT_ONE
        shuffleEnabled -> PlaybackMode.SHUFFLE
        else -> PlaybackMode.SEQUENTIAL
    }

    fun next(mode: PlaybackMode): PlaybackMode = when (mode) {
        PlaybackMode.SEQUENTIAL -> PlaybackMode.SHUFFLE
        PlaybackMode.SHUFFLE -> PlaybackMode.REPEAT_ONE
        PlaybackMode.REPEAT_ONE -> PlaybackMode.SEQUENTIAL
    }

    fun state(mode: PlaybackMode): PlaybackModeState = when (mode) {
        PlaybackMode.SEQUENTIAL -> PlaybackModeState(
            shuffleEnabled = false,
            repeatMode = REPEAT_MODE_OFF,
        )

        PlaybackMode.SHUFFLE -> PlaybackModeState(
            shuffleEnabled = true,
            repeatMode = REPEAT_MODE_ALL,
        )

        PlaybackMode.REPEAT_ONE -> PlaybackModeState(
            shuffleEnabled = false,
            repeatMode = REPEAT_MODE_ONE,
        )
    }
}
