******

### Release History

******

# v1.5.0

###### 2026/08/31

* `Added` Album artwork now generates a readable live color palette for the player gradient, toolbar, controls, and queue; screens draw edge to edge around status bars, cutouts, and gesture areas
* `Added` Playback settings now include a saved default speed, 5 / 10 / 15 / 30-second rewind and fast-forward increments, and selectable queue-finished behavior: stop, return to the beginning and pause, or replay
* `Added` Added compact play / pause and artwork transitions plus system-respecting haptic feedback for key playback actions; disabling system animations switches every state directly
* `Fixed` Language and night-mode dialog choices now use Material Body1 at 16sp instead of the oversized platform list text
* `Improved` Artwork decoding and 64x64 color sampling run off the main thread; arbitrary cover colors retain the existing 4.5:1 text and 3:1 outline contrast gates
* `Improved` Completed the playback visualization feasibility study and an RMS bucket prototype: platform Visualizer remains excluded because it requires recording permission, while a permission-free Media3 PCM tap is documented for future benchmark work

# v1.4.1

###### 2026/08/31

* `Added` The standalone app now restores the last queue, current track, stopped position, repeat mode, shuffle state, and speed when reopened from the launcher; restored sessions stay paused, and only system-picker queues with durable read access are saved
* `Fixed` Fixed the player's top-right overflow menu showing white text on a white surface under some themes, which made the Settings entry unreadable
* `Improved` The AutoJs6 source in theme color settings is now consistently labeled `Follow AutoJs6`; the color picker shows the host color's HEX value directly
* `Improved` Completed a basic manual review of all ten README, CHANGELOG, and plugin instruction translations, and moved project and in-app update links to the official 3-Terra Player repository

# v1.4.0

###### 2026/08/29

* `Added` Overhauled system media notification: dedicated previous / next / shuffle toggle / exit buttons with the app's own monochrome icon, and the shuffle state stays in sync with the player
* `Fixed` Fixed the plugin possibly being auto-marked as faulty and disabled in the AutoJs6 plugin center: plugin info and the file manager action now use separate service endpoints
* `Fixed` Clearing the play queue no longer leaves stale info behind: the screen enters an explicit empty state, and the playback, seek, speed, timer, and A-B controls are disabled together
* `Fixed` Fixed the play button shadow being clipped by the bottom area; radio buttons, checkboxes, progress bars, and buttons in the settings and update dialogs now follow the theme color
* `Improved` The app and plugin are officially renamed 3-Terra Player: the application ID stays the same, so it upgrades in place and existing settings are untouched
* `Improved` The player's top-right menu is trimmed to a single Settings entry, removing the palette button that duplicated the settings page

# v1.3.0

###### 2026/08/29

* `Added` Added a standalone app mode: pick up to 128 audio files from the start page and play them in a row, no AutoJs6 required
* `Added` Added a settings page: language, night mode, theme color, resume playback, update checks, release history, and app info in one place; language and appearance follow AutoJs6 by default and fall back to built-in defaults when the host is unavailable
* `Added` Added manual and once-daily automatic update checks, with per-version ignore support and a built-in localized release history
* `Fixed` Fixed the theme color always reporting the host color as unavailable when following AutoJs6
* `Fixed` Every entry the host recognizes as audio in the file manager (now including WMA) can invoke this plugin directly
* `Improved` Resume playback remembers only the most recently opened file: opening another file or finishing playback clears the old record automatically
* `Improved` The player screen keeps a fixed three-line info area and fills in sample rate and bit rate, so the layout no longer jumps while switching tracks or loading tags
* `Improved` The A-B loop is now a three-tap cycle of set point A, set point B, and clear; spacing and icon alignment of the bottom controls are polished as well

# v1.2.2

###### 2026/08/27

* `Fixed` Fixed every audio file reporting an invalid audio request after upgrading the plugin in place: the entry now accepts legacy read-only requests cached by a running host
* `Fixed` Fixed supported extensions such as `ogg` and `opus` being rejected on devices with broken MIME tables: an extension allowlist now provides stable canonical audio types
* `Improved` Rejected requests now log a privacy-safe reason code (no file names, paths, or URIs), making feedback and diagnosis easier

# v1.2.1

###### 2026/08/27

* `Added` Opening one track from the file manager now discovers the other audio files in the same folder and plays them in natural filename order: up to 128 tracks, starting from the selected one
* `Fixed` Fixed legitimate playback requests being rejected on Android 7.0 because of a window flag the system attaches automatically
* `Fixed` Fixed a possible crash when auto-advancing to a sibling track
* `Improved` Same-folder discovery is strictly read-only and bounded: no subfolder recursion, no guessed file locations, no storage permission, and videos sharing an audio file's name never enter the queue
* `Improved` The same-folder read session is owned by the background playback service and closes automatically when the queue is replaced, playback completes, or the service is destroyed
* `Dependency` Upgraded the built-in file manager action API to the backward-compatible v12; the minimum host version stays build 5276

# v1.2.0

###### 2026/08/27

* `Added` Added multi-select playback: tick up to 128 audio files in the file manager and queue them with one tap, in exactly the selected order
* `Added` Added previous / next controls, a queue panel with jump and remove, and sequential / shuffle / repeat-one modes
* `Added` Added a sleep timer: 15 / 30 / 60 minute presets or a custom duration, with stop-after-current-track and a 5-second final fade-out
* `Added` Added an A-B loop for repeating a chosen passage
* `Improved` System media controls gained previous, next, and 10-second rewind / fast-forward commands, with notification metadata updating as the queue advances
* `Improved` File manager request validation extends to ordered multi-select targets while keeping grants minimal and read-only
* `Dependency` Upgraded the file manager action API from v2 to v4; the minimum host version rises to build 5276
* `Dependency` Added AndroidX RecyclerView 1.4.0

# v1.1.0

###### 2026/08/27

* `Added` Brand-new player screen: album art, title / artist / album tags, technical info, and a draggable progress bar
* `Added` Added 10-second rewind and fast-forward, a repeat-one toggle, and 0.5x to 2x playback speed
* `Added` Embedded tags and album art are read automatically and synced to the system media notification
* `Added` Added resume playback: reopening the same file continues from the last position, cleared automatically once playback completes
* `Improved` After playback ends or decoding fails, the track can be restarted right from the player without returning to the file manager
* `Improved` Playback errors include the exact error code, making issues easier to report
* `Dependency` Added AndroidX ConstraintLayout 2.2.1
* `Dependency` Removed the unused AndroidX Media3 UI dependency

# v1.0.1

###### 2026/08/08

* `Fixed` Fixed an empty service binding appearing while enabling the plugin in the plugin center
* `Improved` The plugin name, description, and instructions read more naturally

# v1.0.0

###### 2026/08/02

* `Added` First stable release: a Play audio action for the AutoJs6 file manager covering 18 common audio extensions, one tap to play
* `Added` Solid playback built on Media3 ExoPlayer and MediaSessionService: background playback, audio focus handling, auto-pause on output device disconnect, and system media controls
* `Added` Accepts read-only audio open requests from other apps; on decoder errors the file can be handed to another compatible app while avoiding self-loops
* `Added` A strict security baseline: no storage or Internet permission, accepting and forwarding only minimal read-only grants
* `Added` Plugin info, instructions, README, and changelog available in 10 languages
* `Dependency` Added AndroidX Media3 ExoPlayer, Session, and UI 1.10.1
