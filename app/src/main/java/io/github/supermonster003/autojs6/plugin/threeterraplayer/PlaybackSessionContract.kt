package io.github.supermonster003.autojs6.plugin.threeterraplayer

import android.os.Bundle
import androidx.media3.session.SessionCommand

internal data class PlaybackToolState(
    val sleepMode: Int = PlaybackSessionContract.SLEEP_MODE_OFF,
    val sleepDeadlineElapsedRealtimeMs: Long = 0L,
    val abStartMs: Long? = null,
    val abEndMs: Long? = null,
)

/** Custom MediaSession commands and state shared by the service and its private controller UI. */
internal object PlaybackSessionContract {

    const val SLEEP_MODE_OFF = 0
    const val SLEEP_MODE_DEADLINE = 1
    const val SLEEP_MODE_END_OF_TRACK = 2

    const val ARG_DURATION_MS = "durationMs"
    const val ARG_POSITION_MS = "positionMs"

    val COMMAND_SET_SLEEP_TIMER = command("SET_SLEEP_TIMER")
    val COMMAND_STOP_AFTER_CURRENT = command("STOP_AFTER_CURRENT")
    val COMMAND_CANCEL_SLEEP_TIMER = command("CANCEL_SLEEP_TIMER")
    val COMMAND_SET_AB_START = command("SET_AB_START")
    val COMMAND_SET_AB_END = command("SET_AB_END")
    val COMMAND_CLEAR_AB_LOOP = command("CLEAR_AB_LOOP")
    val COMMAND_ENABLE_SHUFFLE = command("ENABLE_SHUFFLE")
    val COMMAND_DISABLE_SHUFFLE = command("DISABLE_SHUFFLE")
    val COMMAND_EXIT_PLAYBACK = command("EXIT_PLAYBACK")

    val CUSTOM_COMMANDS = listOf(
        COMMAND_SET_SLEEP_TIMER,
        COMMAND_STOP_AFTER_CURRENT,
        COMMAND_CANCEL_SLEEP_TIMER,
        COMMAND_SET_AB_START,
        COMMAND_SET_AB_END,
        COMMAND_CLEAR_AB_LOOP,
    )

    /** Commands exposed only to Media3's notification controller. */
    val MEDIA_NOTIFICATION_COMMANDS = listOf(
        COMMAND_ENABLE_SHUFFLE,
        COMMAND_DISABLE_SHUFFLE,
        COMMAND_EXIT_PLAYBACK,
    )

    fun stateBundle(state: PlaybackToolState): Bundle = Bundle().apply {
        putInt(EXTRA_SLEEP_MODE, state.sleepMode)
        putLong(EXTRA_SLEEP_DEADLINE, state.sleepDeadlineElapsedRealtimeMs)
        putLong(EXTRA_AB_START, state.abStartMs ?: POSITION_UNSET)
        putLong(EXTRA_AB_END, state.abEndMs ?: POSITION_UNSET)
    }

    fun stateFrom(bundle: Bundle?): PlaybackToolState {
        bundle ?: return PlaybackToolState()
        val sleepMode = bundle.getInt(EXTRA_SLEEP_MODE, SLEEP_MODE_OFF)
            .takeIf { it in SLEEP_MODE_OFF..SLEEP_MODE_END_OF_TRACK }
            ?: SLEEP_MODE_OFF
        return PlaybackToolState(
            sleepMode = sleepMode,
            sleepDeadlineElapsedRealtimeMs = bundle.getLong(EXTRA_SLEEP_DEADLINE, 0L)
                .coerceAtLeast(0L),
            abStartMs = bundle.getLong(EXTRA_AB_START, POSITION_UNSET).takeIf { it >= 0L },
            abEndMs = bundle.getLong(EXTRA_AB_END, POSITION_UNSET).takeIf { it >= 0L },
        )
    }

    private fun command(name: String) = SessionCommand(
        "io.github.supermonster003.autojs6.plugin.threeterraplayer.command.$name",
        Bundle.EMPTY,
    )

    private const val EXTRA_SLEEP_MODE =
        "io.github.supermonster003.autojs6.plugin.threeterraplayer.state.SLEEP_MODE"
    private const val EXTRA_SLEEP_DEADLINE =
        "io.github.supermonster003.autojs6.plugin.threeterraplayer.state.SLEEP_DEADLINE"
    private const val EXTRA_AB_START =
        "io.github.supermonster003.autojs6.plugin.threeterraplayer.state.AB_START"
    private const val EXTRA_AB_END =
        "io.github.supermonster003.autojs6.plugin.threeterraplayer.state.AB_END"
    private const val POSITION_UNSET = -1L
}
