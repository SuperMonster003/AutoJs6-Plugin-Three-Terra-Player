# Audio Player

Audio Player adds an in-app controller and background playback for the audio filename extensions already recognized by the file manager:

`aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave`

When the plugin is installed, Play audio on one file starts at that track and can discover a naturally ordered, bounded queue of readable direct audio siblings. Explicitly selecting 1–128 files and choosing Play selected audio still preserves the host selection order. When the plugin is absent, the host keeps its read-only external ACTION_VIEW flow for one file.

Playback uses Media3 and can continue in the background. The player provides previous / next, an editable queue, sequential / shuffle / repeat-one modes, a sleep timer, A-B loop, speed control, and per-track resume positions. On Android 13+, notification permission is optional and only affects notification-drawer controls.

Same-folder discovery requires AutoJs6 6.8.0 build 5276+ and Explorer Action v12; later plugin capabilities will not raise this baseline. Hosts without the optional session capability retain selected-file-only playback.

Safety and privacy limits:

- The file manager entry point requires the host signature permission and validates protocol v12, ordered targets and ClipData, identifiers, parent relationships, file metadata, declared capabilities, and read-only grants.
- The independent Android entry point accepts only read-only `content` URI requests with `audio/*` and does not forward caller extras or broad grants.
- The plugin requests no storage or Internet access permission and never writes the source file. Media3 contributes the normal `ACCESS_NETWORK_STATE` permission but the APK has no `INTERNET` permission.
- A request-scoped Host Session is pinned to the plugin UID and permits only non-recursive listing of the selected file's direct parent and opening the selected file or a readable direct audio sibling. Playback receives opaque synthetic routes, never filesystem paths, and never guesses a sibling URI.
- Filename extension matching does not guarantee decoding. Support depends on Media3, Android, the device codecs, and the file contents.
- After a decoder error, the file can be opened in another compatible app while this plugin is excluded from the chooser.
