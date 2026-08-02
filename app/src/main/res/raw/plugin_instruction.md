# Audio Player

Audio Player adds an in-app controller and background playback for the audio filename extensions already recognized by AutoJs6 Explorer:

`aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave`

When the plugin is installed, choose Play audio from the primary Explorer actions. When it is absent, AutoJs6 keeps its existing read-only external ACTION_VIEW flow, so another installed audio app may still open the file.

Playback uses Media3 and can continue in the background. On Android 13+, notification permission is optional and only affects notification-drawer controls. Denial does not prevent playback.

The plugin requires AutoJs6 build 5269+. It contains no native library and is independent of device ABI.

Safety and privacy limits:

- The Explorer entry point requires the AutoJs6 signature permission and validates protocol v2, both content URIs, ClipData, file metadata, and read-only grants.
- The independent Android entry point accepts only read-only `content` URI requests with `audio/*` and does not forward caller extras or broad grants.
- The plugin requests no storage or network permission and never writes the source file.
- Filename extension matching does not guarantee decoding. Support depends on Media3, Android, the device codecs, and the file contents.
- After a decoder error, the file can be opened in another compatible app while this plugin is excluded from the chooser.
