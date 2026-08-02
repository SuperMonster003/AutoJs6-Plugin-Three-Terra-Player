******

### Release history

******

# v1.0.0

###### 2026/08/02

* `Feature` Audio Player plugin with plugin ID `audio-player`, action ID `play-audio`, engine `explorer-action`, and variant `default`
* `Feature` Primary read-only Explorer Action protocol v2 entry for the host's 18 existing audio extensions, an empty Explorer MIME type catalog, and required AutoJs6 host build 5269
* `Feature` Media3 ExoPlayer and MediaSessionService playback with audio focus, noisy-output handling, local wake mode, background playback, system media controls, and a private controller interface
* `Feature` Optional Android 13+ notification permission guidance without blocking playback when permission is denied
* `Feature` Independent read-only Android ACTION_VIEW support for `content` URI audio requests and decoder-failure transfer to another compatible app with self-loop prevention
* `Feature` Strict protocol, URI, ClipData, source, name, MIME, size, and grant validation with no storage or network permission and minimum read-only forwarding
* `Feature` Pure JVM implementation with no native library, unrestricted ABIs declared by `supportedAbis = emptyArray()`, and one ABI-independent APK
* `Feature` Localized metadata, interface text, usage instructions, README files, and changelogs in Spanish, French, Russian, Arabic, Japanese, Korean, English, Simplified Chinese, Hong Kong Traditional Chinese, and Taiwan Traditional Chinese
* `Dependency` Added AndroidX Media3 ExoPlayer, Session, and UI version 1.10.1
