<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>File manager plugin. Play audio files with in-app and background controls</p>

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

Audio Player provides an in-app controller and a private background playback service for audio files opened from the file manager. It can also receive read-only Android ACTION_VIEW requests for content URIs with an audio MIME type.

******

### Features

******

- Registers the primary read-only Explorer action `play-audio` through Explorer Action protocol v2 for the 18 audio filename extensions already recognized by the host.
- Plays audio with Media3 ExoPlayer and MediaSessionService, including audio focus, noisy-output handling, a local wake mode, background playback, and system media controls.
- Provides a controller screen with title metadata, transport controls, optional Android 13+ notification permission guidance, and playback error feedback.
- Accepts independent Android ACTION_VIEW requests for read-only `content` URIs with `audio/*`, while discarding caller extras and broad URI grants.
- Offers a decoder-failure fallback that opens the file in another compatible app and excludes this plugin to prevent a self-loop.

******

### Explorer filename extensions

******

The Explorer catalog intentionally matches only these extensions and declares an empty MIME type list:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave
```

An extension match does not guarantee decoding. Actual playback depends on Media3, the Android platform, the device codecs, and the file contents.

******

### Host behavior

******

When the plugin is installed, the file manager shows Play audio as a primary action for the listed extensions. Selecting it opens the plugin controller and starts the private playback service with temporary read-only access to the selected file.

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
Explorer action id: play-audio
Explorer protocol version: 2
Explorer MIME types: []
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/*
required host build: 5269
```

Version 1 provides a primary read-only single-file action in the main file manager. The catalog uses extension-only matching, while the independent Android entry point continues to accept `audio/*`.

Host build 5269 or later is required.

******

### Security

******

The plugin requests no storage or network permission. Its file manager gateway is protected by the host signature permission and strictly validates protocol v2, the target and parent content URIs, ClipData, source surface, display name, MIME type, declared size, and read-only flags. Only the target URI and a read grant are forwarded to private playback components. The public Android gateway accepts only read-only content audio requests, rejects write, persistable, and prefix grants, and never forwards arbitrary caller extras.

******

### Safety limits

******

- One target file per Explorer action.
- Explorer requests with a declared size above 8 TiB are rejected.
- The Explorer action is selected by filename extension only and still validates an audio MIME type when executed.
- The public Android entry point requires ACTION_VIEW, a `content` URI, `audio/*`, and a read grant.
- Notification permission is optional. Denial hides notification-drawer controls but does not block playback.
- Playback ends safely on completion or decoder error. The external fallback transfers only a fresh read grant and excludes this plugin.

******

### Release history

******

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
* `Feature` Strict protocol, URI, ClipData, source, name, MIME, size, and grant validation with no storage or network permission and minimum read-only forwarding
* `Feature` Localized metadata, interface text, usage instructions, README files, and changelogs in Spanish, French, Russian, Arabic, Japanese, Korean, English, Simplified Chinese, Hong Kong Traditional Chinese, and Taiwan Traditional Chinese
* `Dependency` Added AndroidX Media3 ExoPlayer, Session, and UI version 1.10.1

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
