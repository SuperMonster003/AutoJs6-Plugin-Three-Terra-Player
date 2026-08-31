3-Terra Player (formerly Audio Player) is both an audio playback plugin for the AutoJs6 file manager and a clean standalone audio player. Once installed and enabled, tapping any audio file in the AutoJs6 file manager plays it directly, with no third-party player needed; it can also be opened from the launcher like a regular app to pick several audio files and play them in a row.

### Usage

1. Download the latest plugin APK from the [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) page and install it on the device running AutoJs6.
2. Open the AutoJs6 plugin center and make sure `3-Terra Player` is recognized and enabled.
3. In the AutoJs6 file manager, tap any audio file and choose `Play audio`, or long-press to select multiple files and choose `Play selected audio` from the toolbar.
4. Alternatively, open `3-Terra Player` from the launcher and use the file picker to select several audio files to play.

If the plugin does not appear in the plugin center, update AutoJs6 to 6.8.0 (version code 5276) or later first. The plugin itself supports Android 7.0 (API 24) and above, and the standalone mode does not depend on AutoJs6.

### Supported Audio Formats

The file manager entry declares the generic `audio/*` type and explicitly covers the following 19 extensions to stay compatible with sparse or legacy MIME tables on some devices:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

A supported extension does not guarantee decoding: actual playback depends on Media3, the Android version, the device codecs, and the file contents. When a file fails to play, the error panel offers to open it with another app.

### Permissions and Security

The plugin requests no storage permission, accesses audio strictly read-only, and never writes source files; network access is used only for user-triggered or once-daily update checks. On Android 13+ the notification permission is optional; declining it only hides the notification controls without affecting playback.

For more details, see the [project homepage](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player) and the [AutoJs6 documentation](https://docs.autojs6.com).
