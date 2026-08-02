<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>為 AutoJs6 檔案瀏覽器提供唯讀音訊播放和 Media3 背景控制</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Audio-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 語言 (Languages)

******

目前 README.md 支援以下語言:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-HK.md)
- 繁體中文 (台灣) [zh-Hant-TW] # 目前
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ar.md)

******

### 簡介

******

AutoJs6 音訊播放器外掛程式為從 AutoJs6 檔案瀏覽器開啟的檔案提供應用程式內音訊控制介面和私有背景播放服務. 外掛程式也可以接收面向音訊 MIME 類型 content URI 的唯讀 Android ACTION_VIEW 要求.

******

### 功能

******

- 透過檔案瀏覽器動作通訊協定 v2 為主程式已辨識的 18 個音訊副檔名註冊主要唯讀動作 `play-audio`.
- 使用 Media3 ExoPlayer 和 MediaSessionService 播放音訊, 支援音訊焦點, 輸出裝置中斷處理, 本地喚醒模式, 背景播放和系統媒體控制.
- 提供包含標題中繼資料和播放控制的介面, 並提供可選的 Android 13+ 通知權限說明和播放錯誤回饋.
- 獨立接收面向 `audio/*` 的唯讀 `content` URI Android ACTION_VIEW 要求, 捨棄呼叫端 extras 和廣泛 URI 授權.
- 解碼失敗時可使用其他相容應用程式開啟檔案, 並排除本外掛程式以防止自循環.

******

### 檔案瀏覽器副檔名

******

檔案瀏覽器目錄刻意只比對以下副檔名, MIME 類型清單為空:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave
```

副檔名比對不保證可以解碼. 實際播放能力取決於 Media3, Android 平台, 裝置編解碼器和檔案內容.

******

### 主程式行為

******

安裝外掛程式後, AutoJs6 檔案瀏覽器會為列出的副檔名顯示主要動作播放音訊. 選擇此動作後, 外掛程式會開啟控制介面, 並使用對所選檔案的暫時唯讀權限啟動私有播放服務.

缺少外掛程式時, 此動作不會顯示. AutoJs6 會保留原有的音訊檔案唯讀外部 ACTION_VIEW 流程, 因此其他已安裝的音訊應用程式仍可處理檔案. 如果沒有相容的外部應用程式, 主程式不會取得替代播放介面.

******

### 外掛程式介面

******

AutoJs6 使用以下識別資訊探索和執行外掛程式:

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
supported ABIs: []
```

版本 1 在 AutoJs6 主檔案瀏覽器中提供單一檔案主要唯讀動作. 檔案瀏覽器目錄只按副檔名比對, 獨立 Android 入口仍接受 `audio/*`.

外掛程式不包含原生程式庫. 外掛程式宣告 `supportedAbis = emptyArray()`, 並以單一 ABI 無關 APK 發行. 需要 AutoJs6 主程式建置版本 5269 或更新版本.

******

### 安全性

******

外掛程式不要求儲存空間或網路權限. 檔案瀏覽器入口受 AutoJs6 簽章權限保護, 並嚴格驗證通訊協定 v2, 目標與上層目錄 content URI, ClipData, 來源介面, 顯示名稱, MIME 類型, 宣告大小和唯讀旗標. 只將目標 URI 和讀取授權轉交給私有播放元件. 公開 Android 入口只接受唯讀 content 音訊要求, 拒絕寫入, 持久和前綴授權, 並且不會轉交呼叫端的任意 extras.

******

### 安全限制

******

- 每次檔案瀏覽器動作只處理 1 個目標檔案.
- 宣告大小超過 8 TiB 的檔案瀏覽器要求會被拒絕.
- 檔案瀏覽器動作只按檔案名稱副檔名選取, 執行時仍會驗證音訊 MIME 類型.
- 公開 Android 入口要求 ACTION_VIEW, `content` URI, `audio/*` 和讀取授權.
- 通知權限是可選權限. 拒絕權限會隱藏通知欄控制, 但不會阻止播放.
- 播放完成或解碼錯誤時會安全結束. 外部降級只轉交新的讀取授權, 並排除本外掛程式.

******

### 版本記錄

******

# v1.0.0

###### 2026/08/02

* `新增` 音訊播放器外掛程式, 外掛程式 ID 為 `audio-player`, 動作 ID 為 `play-audio`, 引擎為 `explorer-action`, 變體為 `default`
* `新增` 適用於主程式現有 18 個音訊副檔名的主要唯讀檔案瀏覽器動作通訊協定 v2 入口, 檔案瀏覽器 MIME 類型目錄為空, 要求 AutoJs6 主程式建置版本 5269
* `新增` Media3 ExoPlayer 和 MediaSessionService 播放, 支援音訊焦點, 輸出裝置中斷處理, 本地喚醒模式, 背景播放, 系統媒體控制和私有控制介面
* `新增` 可選的 Android 13+ 通知權限說明, 拒絕權限時不會阻止播放
* `新增` 面向 `content` URI 音訊要求的獨立唯讀 Android ACTION_VIEW 支援, 解碼失敗時可轉交其他相容應用程式並防止自循環
* `新增` 嚴格驗證通訊協定, URI, ClipData, 來源, 名稱, MIME, 大小和授權, 不要求儲存空間或網路權限, 只轉交最小讀取授權
* `新增` 純 JVM 實作且不包含原生程式庫, 透過 `supportedAbis = emptyArray()` 宣告 ABI 無限制, 發行單一 ABI 無關 APK
* `新增` 外掛程式中繼資料, 介面文字, 使用說明, README 和 CHANGELOG 的多語言資源: 西班牙語/法語/俄語/阿拉伯語/日語/韓語/英語/簡體中文/香港繁體/台灣繁體
* `相依性` 附加 AndroidX Media3 ExoPlayer, Session 和 UI 版本 1.10.1

##### 查看更多版本

* [CHANGELOG-zh-Hant-TW.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-TW.md)

******

### 建置

******

```powershell
.\gradlew.bat :app:assembleDebug
```

發行建置:

```powershell
.\gradlew.bat :app:assembleRelease
```

建置參數來自 `version.properties`. 目前最低 SDK 為 24, 目標 SDK 為 36.

******

### 資源配置

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 為外掛程式中繼資料和介面文字提供本地化. `plugin_instruction.md` 提供主程式顯示的說明. `.python/generate_markdown.py` 根據 JSON 來源檔案產生多語言 README 和更新記錄.

******

### 連結

******

- AutoJs6 文件: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Android 安全檔案分享: https://developer.android.com/training/secure-file-sharing
