3-Terra Player is the audio playback plugin for the AutoJs6 file manager and also works as a standalone local audio player. Tap an audio file in the file manager to play it; it supports continuous playback of a folder, local playlists, a sleep timer, A-B loops and background playback, and never modifies source files.

### Getting started

1. Make sure this plugin is enabled in AutoJs6 "Plugin center".
2. In the AutoJs6 file manager, tap an audio file and choose "Play audio"; the player opens and starts playing.
3. To play several files at once, long-press to select them and choose "Play selected audio" in the toolbar.
4. To use it without AutoJs6, open this plugin from its launcher icon and tap "Open audio files".

### Main features

- **Continuous playback**: After you open an audio file, the other audio files in the same folder are queued by file name, up to 128; jump to or remove tracks in "Playback queue".
- **Play selected**: Select several audio files in the file manager and choose "Play selected audio" to play up to 128 of them in the order you selected them.
- **Local playlists**: Open M3U/M3U8, PLS, XSPF and other playlist files with up to 128 items; keep the listed audio in the playlist folder, and pick that folder when prompted after opening from the launcher icon.
- **Playback controls**: Previous, next, seeking and skipping back or forward; switch between "Sequential playback", "Shuffle playback" and "Repeat current track", and choose 0.5x to 2x in "Playback speed".
- **Sleep timer**: In "Sleep timer", choose a duration or "Stop after the current track"; the volume fades out just before playback stops.
- **A-B loop**: Tap "A-B loop" once to set the start and again to set the end, and that section repeats; a third tap clears it.
- **Background playback**: Playback continues after you leave the player or lock the screen, with controls in the media notification and on the lock screen.
- **Resume playback**: Reopening from the launcher icon restores the last queue, track and position, paused; you can turn off "Resume playback" in "Settings".
- **Appearance and settings**: Language, night mode and "Theme color" follow AutoJs6 by default; in "Settings" you can also change the "Launcher icon", "Check for updates" and view "Release history".

### Requirements

- Android 7.0 or later.
- Playing from the file manager requires AutoJs6 6.8.0 or later; the standalone player opened from its launcher icon does not need AutoJs6.
- On Android 13 or later, the media notification and lock screen controls need the notification permission; playback still works if it is denied.

### FAQ

**Why "Play audio" is missing in the file manager**

Make sure AutoJs6 meets the version requirement and this plugin is enabled in "Plugin center"; once both are true, audio files offer "Play audio".

**When an audio file cannot be played**

Decoding depends on the device and the file, so rare codecs or damaged files may not play. When playback fails, tap "Open with another app" to use a different player.

**Why other tracks from the same folder are queued**

This is continuous playback: opening a single audio file queues the other audio files in the same folder by file name, without subfolders. Remove unwanted tracks in "Playback queue".

**What the resume record stores**

Only the latest queue opened from the launcher icon and its playback state, kept on this device only; turning off "Resume playback" clears it at once.

See the [project homepage](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player) for more details.
