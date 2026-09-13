<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="three-terra-player-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>Audio player plugin and standalone app with background playback</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Languages

******

The current README.md supports the following languages:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-TW.md)
- English [en] # current
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ar.md)

******

### Introduction

******

3-Terra Player (formerly Audio Player) is both an audio playback plugin for the AutoJs6 file manager and a clean standalone audio player. Once installed and enabled, tapping any audio file in the AutoJs6 file manager plays it directly, with no third-party player needed; it can also be opened from the launcher like a regular app to pick several audio files and play them in a row.

After installation the plugin is discovered by AutoJs6 automatically, with zero configuration. Playback is powered by Media3 (ExoPlayer), the official Android media framework, with background playback and system media notification controls; audio files are accessed strictly read-only, no storage permission is requested, and source files are never modified or deleted.

******

### Feature Highlights

******

- Local playlists: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL preserve order, titles and repeated entries. Host playback reads the same folder; standalone playback asks for the playlist folder to resolve relative paths. One list at a time, up to 128 items. Network URLs, HLS and nested playlists are not supported.
- One-tap playback in the file manager: tap any audio file in the AutoJs6 file manager to start playing.
- Same-folder auto-play: opening one track discovers the other audio files in the same folder and queues them in natural filename order (up to 128 tracks, starting from the selected one).
- Multi-select queue: tick up to 128 audio files in the file manager and play them in exactly the order you selected.
- Complete player screen: album art, title / artist / album tags, technical info such as sample rate and bit rate, and a draggable progress bar.
- All the everyday controls: previous / next, configurable 5 / 10 / 15 / 30-second rewind and fast-forward, sequential / shuffle / repeat-one modes, a saved default speed from 0.5x to 2x, and selectable queue-finished behavior.
- Queue panel: view upcoming tracks at any time, tap to jump or remove, with the current track clearly marked.
- Sleep timer: 15 / 30 / 60 minute presets or a custom duration, optionally stopping after the current track, with a 5-second fade-out at the end.
- A-B loop: repeat any passage over and over, great for listening practice and learning music by ear.
- Background playback: audio keeps going after leaving the screen or locking the device, controllable from the system media notification and the lock screen.
- Session restore: reopening the standalone app from the launcher restores the last queue, current track, stopped position, repeat / shuffle state, and speed; the restored session stays paused.
- Standalone mode: works without AutoJs6; pick up to 128 audio files from the start page and play them.
- Responsive looks: language, night mode, and base color can follow AutoJs6 or be customized; album art generates a readable live palette across the edge-to-edge player, with system-respecting transitions and haptics.

******

### Usage

******

1. Download the latest plugin APK from the [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) page and install it on the device running AutoJs6.
2. Open the AutoJs6 plugin center and make sure `3-Terra Player` is recognized and enabled.
3. In the AutoJs6 file manager, tap any audio file and choose `Play audio`, or long-press to select multiple files and choose `Play selected audio` from the toolbar.
4. Alternatively, open `3-Terra Player` from the launcher and use the file picker to select several audio files to play.

> If the plugin does not appear in the plugin center, update AutoJs6 to 6.8.0 (version code 5276) or later first. The plugin itself supports Android 7.0 (API 24) and above, and the standalone mode does not depend on AutoJs6.

******

### Supported Audio Formats

******

The file manager entry declares the generic `audio/*` type and explicitly covers the following 19 extensions to stay compatible with sparse or legacy MIME tables on some devices:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

A supported extension does not guarantee decoding: actual playback depends on Media3, the Android version, the device codecs, and the file contents. When a file fails to play, the error panel offers to open it with another app.

******

### FAQ

******

#### No `Play audio` action shows up in the file manager?

Check three things in order: the plugin is installed; it is enabled in the AutoJs6 plugin center; and AutoJs6 is at 6.8.0 (version code 5276) or later. Once all three are true, audio files in the file manager get the playback action.

#### I opened a single track, so why are other songs from the folder in the queue?

That is same-folder auto-play: when one audio file is opened, the plugin discovers the other audio files in the same folder through a host-managed session and queues them in natural filename order for continuous listening. Discovery is strictly read-only and never enters subfolders; on hosts without this capability, only the selected track plays.

#### What order does multi-select playback use?

Exactly the order you ticked the files in. To listen in a specific order, tick the files in that order; once in the player you can also jump to or remove tracks from the queue panel.

#### Does playback continue after leaving the app or locking the screen?

Yes. Playback runs in a background service and can be controlled from the system media notification and the lock screen. On Android 13 and above the notification permission is optional: declining it only hides the notification controls and never stops playback.

#### Will the plugin modify or upload my audio files?

No. The plugin requests no storage permission and accesses audio strictly read-only; network access is used only to check GitHub for new versions (user-triggered or at most once a day), and no files or personal data are ever uploaded.

#### Why does a file stay silent or report a decoder error?

A supported extension does not mean the device can decode the file; uncommon codecs or corrupted files may fail. The error panel shows the exact error code and offers to open the file with another app (excluding this plugin to avoid loops).

#### How do I make the plugin follow the AutoJs6 language and theme?

It follows by default: language, night mode, and theme color sync automatically through the official read-only settings interface of AutoJs6. You can also pin a fixed language or pick your own colors in Settings; without AutoJs6 installed, it falls back to the system appearance and built-in defaults.

#### Where are the sleep timer and the A-B loop?

Both live in the bottom toolbar of the player screen. The timer icon offers presets or a custom duration; the A-B button cycles through 'set point A, set point B, clear' on successive taps.

******

### Permissions and Security

******

Audio files may come from untrusted sources, so the playback flow is guarded by several lines of defense:

- Zero storage permission: the plugin does not request (and cannot obtain) read or write access to device storage; it can only access the individual files explicitly granted by the host or the system.
- Never writes: audio files are opened read-only and are never modified, moved, or deleted.
- Strict validation: the file manager entry is protected by the AutoJs6 signature permission, and every playback request is verified item by item, including the protocol version, target list, and read-only grants; non-compliant requests are rejected outright.
- Bounded discovery: same-folder auto-play reads only through a host-managed, request-scoped session, never recurses into subfolders, never guesses file locations, and the session closes when playback ends.
- Local session only: when resume playback is enabled, app-private storage keeps just the latest standalone picker queue and its playback state; only system-picker URIs with durable read access qualify, Host Session routes are never saved, and turning the setting off clears the record immediately.
- Minimal networking: the Internet permission is used only for user-triggered or once-daily GitHub release checks, never for audio content or usage data.
- Optional notifications: on Android 13+ the notification permission is optional; declining it only hides the notification controls and playback is unaffected.

Please install the plugin only from the official [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) page or other trusted channels; a package from an unknown source may be tampered with even if its name and version look identical.

******

### Plugin Interface

******

The following identifiers are for AutoJs6 host and plugin developers; the host uses them to discover the plugin and negotiate capabilities:

```text
application id: io.github.supermonster003.autojs6.plugin.audioplayer
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: audio-player
engine: explorer-action
variant: default
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 12 (accepts compatible read-only v4-v12 requests)
Explorer MIME types: [audio/*]
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/* plus legacy WMA MIME aliases
required host build: 5276
```

The current version provides protocol v12 read-only single-file and ordered multi-select actions, request-scoped same-folder discovery, a standalone document picker, and a public read-only Android audio entry (including legacy WMA MIME aliases). Hosts without the optional Host Session capability keep playing only the selected file.

Same-folder discovery requires AutoJs6 6.8.0 (version code 5276) or later with Explorer Action v12; future plugin capabilities will not raise this requirement. Since v1.4.0 the product is named 3-Terra Player while the application ID stays `io.github.supermonster003.autojs6.plugin.audioplayer`, so it upgrades in place.

******

### Roadmap

******

Planned capabilities and their progress are maintained as a checkable list in Roadmap.md, organized by milestone with acceptance criteria, covering lyrics, an equalizer, artwork-based colors, edge-to-edge UI, and engineering quality. Unchecked items are intentions rather than current features; feel free to join the discussion via Issues.

- [View Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/Roadmap.md)

******

### Release History

******

#### v1.6.0

_2026/09/13_

- `Feature` Local playlists: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL preserve order, titles and repeated entries. Host playback reads the same folder; standalone playback asks for the playlist folder to resolve relative paths. One list at a time, up to 128 items. Network URLs, HLS and nested playlists are not supported
- `Fix` Language and night-mode dialog choices now use Material Body1 at 16sp instead of the oversized platform list text
- `Fix` Keep the plugin version date in English regardless of the build machine locale
- `Improvement` Consistent localized resources, explicit plugin activation and validated release preparation

#### v1.5.0

_2026/09/12_

- `Feature` Album artwork now generates a readable live color palette for the player gradient, toolbar, controls, and queue; screens draw edge to edge around status bars, cutouts, and gesture areas
- `Feature` Playback settings now include a saved default speed, 5 / 10 / 15 / 30-second rewind and fast-forward increments, and selectable queue-finished behavior: stop, return to the beginning and pause, or replay
- `Feature` Added compact play / pause and artwork transitions plus system-respecting haptic feedback for key playback actions; disabling system animations switches every state directly
- `Fix` Language and night-mode dialog choices now use Material Body1 at 16sp instead of the oversized platform list text
- `Improvement` Artwork decoding and 64x64 color sampling run off the main thread; arbitrary cover colors retain the existing 4.5:1 text and 3:1 outline contrast gates
- `Improvement` Completed the playback visualization feasibility study and an RMS bucket prototype: platform Visualizer remains excluded because it requires recording permission, while a permission-free Media3 PCM tap is documented for future benchmark work
- `Improvement` Standardize the README layout and Gradle platform version management
- `Improvement` Refine the plugin description and normalize punctuation in multilingual resources
- `Improvement` Rename the external viewing entry to External Viewer for consistent viewer semantics
- `Improvement` Open the built-in release history page from the release history button in the update dialog
- `Improvement` Build verification rejects accidental native dependencies and produces a JSON report

#### v1.4.1

_2026/08/31_

- `Feature` The standalone app now restores the last queue, current track, stopped position, repeat mode, shuffle state, and speed when reopened from the launcher; restored sessions stay paused, and only system-picker queues with durable read access are saved
- `Fix` Fixed the player's top-right overflow menu showing white text on a white surface under some themes, which made the Settings entry unreadable
- `Improvement` The AutoJs6 source in theme color settings is now consistently labeled `Follow AutoJs6`; the color picker shows the host color's HEX value directly
- `Improvement` Completed a basic manual review of all ten README, CHANGELOG, and plugin instruction translations, and moved project and in-app update links to the official 3-Terra Player repository

##### For more release history, see

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/assets/doc/CHANGELOG-en.md)

******

### Build

******

This section is for developers who want to build the plugin from source.

Build a debug APK:

```powershell
.\gradlew.bat :app:assembleDebug
```

Build a release APK (signed automatically once signing is configured in the untracked `sign.properties`):

```powershell
.\gradlew.bat :app:assembleRelease
```

For release archiving, run the `:app:appendDigestToReleasedFiles` task to copy signed APKs into `releases/` with the version and a CRC32 digest appended to the file name.

Build parameters live in `version.properties`: minimum SDK 24 (Android 7.0), target SDK 36, current version 1.6.0.

******

### Localization and Doc Generation

******

```text
.readme/common.json
.readme/lang_*.json
.readme/template_readme.md
.readme/template_plugin_instruction.md
.changelog/lang_*.json
.changelog/template_changelog.md
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localizes plugin metadata and UI text, and `plugin_instruction.md` provides the usage notes shown in the host plugin center. The README, changelog, and instruction files are all generated from JSON sources: edit the sources under `.readme/` and `.changelog/`, then run `py .python/generate_markdown.py` to regenerate every artifact (never edit generated files by hand); run `py .python/generate_markdown.py --check` to verify that sources and artifacts are in sync.

******

### License

******

The project code is open source under the [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE). Audio playback is built on [AndroidX Media3](https://developer.android.com/media/media3) (Apache License 2.0).

******

### Links

******

- AutoJs6 documentation: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Android secure file sharing: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/docs/16kb.md)
