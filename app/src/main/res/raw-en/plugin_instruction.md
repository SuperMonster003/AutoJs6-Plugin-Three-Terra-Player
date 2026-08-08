# Audio Player

Audio Player adds an in-app controller and background playback for the audio filename extensions already recognized by the file manager:

`aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave`

When the plugin is installed, choose Play audio from the primary file manager actions. When it is absent, the host keeps its existing read-only external ACTION_VIEW flow, so another installed audio app may still open the file.

Playback uses Media3 and can continue in the background. On Android 13+, notification permission is optional and only affects notification-drawer controls. Denial does not prevent playback.

Host build 5269+ is required.

Safety and privacy limits:

- The file manager entry point requires the host signature permission and validates protocol v2, both content URIs, ClipData, file metadata, and read-only grants.
- The independent Android entry point accepts only read-only `content` URI requests with `audio/*` and does not forward caller extras or broad grants.
- The plugin requests no storage or network permission and never writes the source file.
- Filename extension matching does not guarantee decoding. Support depends on Media3, Android, the device codecs, and the file contents.
- After a decoder error, the file can be opened in another compatible app while this plugin is excluded from the chooser.
