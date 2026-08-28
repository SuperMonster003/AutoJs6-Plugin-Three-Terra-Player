******

### Release history

******

# v1.3.0

###### 2026/08/29

* `Feature` Added a launcher and standalone multi-file player mode, plus a dedicated settings screen for language, night mode, theme color, resume behavior, updates, release history, and app/developer information
* `Feature` Language, night mode, and source color now follow AutoJs6 by default through its official read-only settings contract; unavailable host choices remain visible but disabled and fall back to app defaults
* `Feature` Added manual and daily automatic update checks, ignored-version management, and localized bundled release history
* `Fix` Fixed Follow AutoJs6 reporting Host color unavailable by exposing the protected plugin-info service entry required by the host settings provider
* `Fix` Explorer actions now advertise audio MIME support in addition to 19 known extensions, including WMA, so every host-recognized audio item routes directly to the plugin
* `Improvement` Resume playback now remembers exactly one most recently opened file, immediately discards it when another file opens, and never keeps completed playback
* `Improvement` Reserved a stable three-line metadata area and supplemented tags with selected-stream sample rate and bitrate in the order 44.1 kHz · MP3 · 128 kbps
* `Improvement` A-B is now a discoverable three-tap cycle to set A, set B, and clear; bottom controls gained spacing and precisely centered, consistently sized icons

# v1.2.2

###### 2026/08/27

* `Fix` Explorer playback no longer fails after a plugin upgrade when the running AutoJs6 file manager still sends a cached protocol v4 action; the gateway accepts the compatible read-only v4–v12 envelope while continuing to advertise v12
* `Fix` Advertised audio extensions are no longer rejected when an Android or OEM MIME table reports a wildcard or application MIME type; the extension allow-list now supplies a stable canonical audio MIME type
* `Improvement` Rejected Explorer requests now log a privacy-safe reason code without file names, display paths, or URIs so future contract mismatches can be diagnosed directly

# v1.2.1

###### 2026/08/27

* `Feature` A single Play audio action can now discover up to 128 readable audio files in the same folder through Explorer Action v12 and build a naturally ordered queue starting at the selected track
* `Fix` API 24 no longer rejects a valid Explorer launch when Android adds the non-permission FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS flag from the gateway activity manifest
* `Fix` Automatically advancing to a sibling track no longer crashes while rebuilding the MediaSession return intent; the originally granted target remains the Host Session anchor independently of the active queue item
* `Improvement` The selected track keeps its original content URI while sibling tracks stream only through request-scoped Host Session file descriptors; no sibling URI is guessed and no recursive, write, storage, or persistent access is added
* `Improvement` Audio extension filtering keeps same-named .mp4 video files out of queues containing .m4a audio, while the existing explicit multi-selection queue remains unchanged
* `Improvement` Host Session ownership now transfers to the background playback service and closes on queue replacement, startup failure, completion, or service destruction
* `Dependency` Upgraded the bundled Explorer Action API from protocol v4 to the backward-compatible v12 sibling-read extension while retaining minimum host build 5276

# v1.2.0

###### 2026/08/27

* `Feature` Explorer Action protocol v4 support with a new ordered multi-selection action that builds a playback queue from up to 128 explicitly selected audio files
* `Feature` Native Media3 playlist with previous / next controls, a queue sheet for jumping to or removing tracks, and sequential / shuffle / repeat-one modes
* `Feature` Service-owned sleep timer with 15 / 30 / 60 minute presets, a custom duration, stop-after-current, and a final five-second fade-out
* `Feature` A-B interval loop for repeating a selected section
* `Improvement` System media controls now expose previous, next, and 10-second rewind / fast-forward commands, while metadata and resume positions follow the current queue item
* `Improvement` Explorer request validation now covers ordered TARGETS and ClipData, unique request and target identifiers, host session descriptors, and every selected file without broadening read-only grants
* `Improvement` Documented that the host FileProvider parent URI cannot enumerate children; automatic sibling discovery and URI guessing stay disabled, with explicit multi-selection as the safe queue path
* `Dependency` Upgraded the bundled Explorer Action API from protocol v2 to v4 and raised the required host build to 5276
* `Dependency` Added AndroidX RecyclerView version 1.4.0

# v1.1.0

###### 2026/08/27

* `Feature` Redesigned player screen with album artwork, title / artist / album tags, a draggable progress bar, and playback time labels
* `Feature` Enhanced playback controls: 10-second rewind and fast-forward, single-file loop toggle, and 0.5x to 2x speed playback
* `Feature` Automatic extraction of embedded tags, artwork, and technical properties (codec / sample rate / bitrate) synchronized to system media notifications
* `Feature` Playback position memory: reopening the same file resumes from the last position, and completed playback clears the record
* `Improvement` Playback can restart directly on the player screen after completion or decoding failures without returning to the file manager
* `Improvement` Playback error feedback now includes the specific error code for easier reporting
* `Dependency` Added AndroidX ConstraintLayout version 2.2.1
* `Dependency` Removed the unused AndroidX Media3 UI dependency

# v1.0.1

###### 2026/08/08

* `Fix` Null service binding when enabling the plugin in Plugin Center
* `Improvement` Clearer and more concise plugin name, description, and user documentation

# v1.0.0

###### 2026/08/02

* `Feature` Audio Player plugin with plugin ID `audio-player`, action ID `play-audio`, engine `explorer-action`, and variant `default`
* `Feature` Primary read-only file manager action for the host's 18 audio extensions, requiring host build 5269 or later
* `Feature` Media3 ExoPlayer and MediaSessionService playback with audio focus, noisy-output handling, local wake mode, background playback, system media controls, and a private controller interface
* `Feature` Optional Android 13+ notification permission guidance without blocking playback when permission is denied
* `Feature` Independent read-only Android ACTION_VIEW support for `content` URI audio requests and decoder-failure transfer to another compatible app with self-loop prevention
* `Feature` Strict protocol, URI, ClipData, source, name, MIME, size, and grant validation with no storage or Internet access permission and minimum read-only forwarding
* `Feature` Localized metadata, interface text, usage instructions, README files, and changelogs in Spanish, French, Russian, Arabic, Japanese, Korean, English, Simplified Chinese, Hong Kong Traditional Chinese, and Taiwan Traditional Chinese
* `Dependency` Added AndroidX Media3 ExoPlayer, Session, and UI version 1.10.1
