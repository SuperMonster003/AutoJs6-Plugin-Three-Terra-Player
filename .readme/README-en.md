<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Terra Player</h1>

  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>AutoJs6 plugin and standalone app. Play audio with focused in-app and background controls</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Audio-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Languages

******

The current README.md supports the following languages:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-TW.md)
- English [en] # current
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ar.md)

******

### Introduction

******

3-Terra Player is both an AutoJs6 file-manager plugin and a standalone multi-file audio player with an in-app controller and private background playback service. It also accepts read-only Android ACTION_VIEW content URIs.

******

### Features

******

- Registers both the single-file `play-audio` action and ordered multi-selection `play-audio-selection` action through Explorer Action protocol v12 for every host-recognized audio MIME type plus 19 known extensions.
- Plays audio with Media3 ExoPlayer and MediaSessionService, including audio focus, noisy-output handling, a local wake mode, background playback, and system media controls.
- Provides a full player screen with album artwork, media tags, technical info, seek dragging, 10-second jumps, sequential / shuffle / repeat-one modes, and 0.5x to 2x speed control.
- Plays up to 128 explicitly selected audio files in their host-provided order, with previous / next controls and a queue sheet for jumping to or removing tracks.
- Includes a service-owned sleep timer with presets, a custom duration, stop-after-current, final five-second fade-out, and an A-B loop for listening practice.
- Remembers the position of exactly the most recently opened file, immediately replaces it when another file opens, and mirrors current metadata and controls into system media surfaces.
- Provides a launcher that opens up to 128 selected documents and accepts independent read-only Android ACTION_VIEW `content` URIs, including legacy WMA MIME aliases.
- Offers a decoder-failure fallback that opens the file in another compatible app and excludes this plugin to prevent a self-loop.
- A single-file Explorer launch discovers a naturally ordered, bounded queue of readable audio siblings through a request-scoped Host Session; explicit multi-selection keeps the user's host order.
- Builds an accessible light and dark palette from one source color, follows AutoJs6 by default, and offers 19 localized Material 500 presets plus live-preview custom RGB colors.
- Includes standalone settings for host-following language / night mode / color, resume behavior, manual and automatic update checks, ignored versions, release history, and app information.

******

### Explorer filename extensions

******

The Explorer catalog advertises `audio/*` and also uses these extensions to cover sparse or legacy Android MIME tables:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

An extension match does not guarantee decoding. Actual playback depends on Media3, the Android platform, the device codecs, and the file contents.

******

### Host behavior

******

When the plugin is installed and enabled in AutoJs6, the file manager shows Play audio as a primary action for one file and Play selected audio in the selection toolbar for multiple files. A single-file launch starts from that track and may discover readable direct audio siblings in natural order; explicit multi-selection preserves the host's selected order.

When the plugin is absent, this action is not listed. The host keeps its existing read-only external ACTION_VIEW flow for audio files, so another installed audio app may handle the file. If no compatible external app is available, the host does not gain a replacement playback interface.

******

### Plugin interface

******

The host discovers and executes the plugin with the following identities:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: audio-player
engine: explorer-action
variant: default
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 12 (accepts compatible read-only v4–v12 requests)
Explorer MIME types: [audio/*]
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/* plus legacy WMA MIME aliases
required host build: 5276
```

Version 1.3.0 provides protocol v12 read-only single and ordered multi-selection actions, a request-scoped direct-sibling capability, a standalone document picker, and public read-only Android audio / WMA entries. Hosts without the optional Host Session keep selected-file-only playback.

AutoJs6 6.8.0 build 5276 or later and Explorer Action v12 are required for same-folder discovery; this requirement will not be raised for later plugin capabilities.

******

### Security

******

The app requests no storage permission and never writes source files. Internet access is used only for user-controlled or daily GitHub release checks. Its signature-protected gateway strictly validates protocol v12, ordered TARGETS and ClipData, identifiers, parent relationship, metadata, and read-only flags. Host Session access remains UID-bound and non-recursive; public and standalone document entries retain read-only URI grants.

******

### Safety limits

******

- The single action starts from exactly one selected file and may form a bounded queue of readable direct audio siblings; the selection action accepts 1 to 128 unique files and preserves their order.
- Explorer requests with a declared size above 8 TiB are rejected.
- Explorer actions match host-recognized audio MIME types or the explicit extension list and still normalize and validate each executed target.
- Sibling discovery is non-recursive and available only through the host-owned request-scoped session; the plugin never guesses a sibling URI or receives a filesystem path.
- The public Android entry point requires ACTION_VIEW, a `content` URI, a supported audio or legacy WMA MIME type, and a read grant.
- Notification permission is optional. Denial hides notification-drawer controls but does not block playback.
- Playback ends safely on completion or decoder error. The external fallback transfers only a fresh read grant and excludes this plugin.

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

##### For more releases

* [CHANGELOG-en.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-en.md)

******

### Build

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release build:

```powershell
.\gradlew.bat :app:assembleRelease
```

Build parameters come from `version.properties`. The current minimum SDK is 24 and the target SDK is 36.

******

### Resource layout

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localizes plugin metadata and UI text. `plugin_instruction.md` provides instructions shown by the host. `.python/generate_markdown.py` generates localized README and changelog files from JSON sources.

******

### Links

******

- AutoJs6 documentation: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Android secure file sharing: https://developer.android.com/training/secure-file-sharing
