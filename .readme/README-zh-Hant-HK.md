<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>檔案管理器外掛程式. 透過應用程式內控制和背景控制播放音訊檔案</p>

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
- 繁體中文 (香港) [zh-Hant-HK] # 目前
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-TW.md)
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

音訊播放器為從檔案管理器開啟的音訊檔案提供應用程式內控制介面和私有背景播放服務. 外掛程式也可以接收面向音訊 MIME 類型 content URI 的唯讀 Android ACTION_VIEW 要求.

******

### 功能

******

- 透過 Explorer Action v12, 為主程式已辨識的所有音訊 MIME 及 19 個已知副檔名註冊 `play-audio` 與 `play-audio-selection`.
- 使用 Media3 ExoPlayer 和 MediaSessionService 播放音訊, 支援音訊焦點, 輸出裝置中斷處理, 本地喚醒模式, 背景播放和系統媒體控制.
- 提供包含專輯封面, 媒體標籤, 技術資訊, 進度拖曳, 快退快進 10 秒, 順序 / 隨機 / 單曲循環模式和 0.5 至 2 倍變速的完整播放介面.
- 按主程式提供的次序播放最多 128 個明確選取的音訊檔案, 支援上一首 / 下一首及可跳轉或移除曲目的播放佇列面板.
- 提供由播放服務持有的預設 / 自訂睡眠計時器, 播完本曲後停止, 最後 5 秒淡出及適合聽力練習的 A-B 區間循環.
- 只記住最近開啟的一個檔案及其位置, 開啟其他檔案時立即取代, 並向系統同步目前元資料和控制.
- 提供可選取最多 128 個文件的啟動頁面, 並接收唯讀 Android ACTION_VIEW `content` URI, 包括舊式 WMA MIME 別名.
- 解碼失敗時可使用其他相容應用程式開啟檔案, 並排除本外掛程式以防止自循環.
- 從檔案瀏覽器開啟單曲時, 透過要求級 Host Session 自動尋找可讀直接同級音訊並按自然次序建立有界佇列; 明確多選仍保持主程式選取次序.
- 由單一色源產生清晰配色並跟隨 AutoJs6, 提供 19 個 Material 500 色及自訂 RGB; 設定亦涵蓋語言、夜間、接續播放、更新、記錄及應用程式資訊.

******

### 檔案瀏覽器副檔名

******

Explorer 目錄宣告 `audio/*`, 並透過以下副檔名兼容舊式或不完整 MIME 表:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

副檔名比對不保證可以解碼. 實際播放能力取決於 Media3, Android 平台, 裝置編解碼器和檔案內容.

******

### 主程式行為

******

安裝外掛程式並在 AutoJs6 外掛中心啟用後, 檔案管理器會為單一檔案顯示主要動作播放音訊, 並在多選工具列顯示播放所選音訊. 單一檔案入口從所選曲目開始, 可按自然次序尋找可讀直接同級音訊; 明確多選保持主程式提供的選取次序.

缺少外掛程式時, 此動作不會顯示. 主程式會保留原有的音訊檔案唯讀外部 ACTION_VIEW 流程, 因此其他已安裝的音訊應用程式仍可處理檔案. 如果沒有相容的外部應用程式, 主程式不會取得替代播放介面.

******

### 外掛程式介面

******

主程式使用以下識別資料探索和執行外掛程式:

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

版本 1.3.0 提供 v12 唯讀動作、有限同級存取、獨立文件選擇器及 Android 音訊 / WMA 入口. 沒有可選 Host Session 時只播放所選檔案.

同目錄尋找需要 AutoJs6 6.8.0 build 5276 或更新版本及 Explorer Action v12; 後續外掛程式能力不會提高此要求.

******

### 安全性

******

外掛程式不要求儲存權限且不會寫入來源檔案. 互聯網只用於手動或每日 GitHub 發行版檢查. 簽章入口嚴格驗證 v12、TARGETS、ClipData、元資料和唯讀授權; Host Session 綁定 UID 且不遞迴, 公開入口保持唯讀.

******

### 安全限制

******

- 單一檔案動作從剛好 1 個所選檔案開始, 可建立可讀直接同級音訊的有界佇列; 多選動作接受 1 至 128 個不重複檔案並保持次序.
- 宣告大小超過 8 TiB 的檔案瀏覽器要求會被拒絕.
- 動作按已辨識音訊 MIME 或明確副檔名清單比對, 並規範化及驗證每個目標.
- 同級尋找只可透過主程式管理的要求級工作階段且不遞迴; 外掛程式絕不猜測同級 URI, 亦不接收檔案系統路徑.
- 公開 Android 入口要求 ACTION_VIEW、`content` URI、支援的音訊或 WMA MIME 及讀取授權.
- 通知權限是可選權限. 拒絕權限會隱藏通知欄控制, 但不會阻止播放.
- 播放完成或解碼錯誤時會安全結束. 外部降級只轉交新的讀取授權, 並排除本外掛程式.

******

### 版本記錄

******

# v1.3.0

###### 2026/08/29

* `新增` 新增啟動頁面與獨立多檔案播放器模式, 並提供獨立設定頁面, 包含語言、夜間模式、主題色彩、接續播放、更新、發行記錄及應用程式與開發者資訊
* `新增` 語言、夜間模式和色源預設透過 AutoJs6 官方唯讀設定契約跟隨主程式; 主程式不可用時仍顯示但停用相應選項, 並使用應用程式預設值
* `新增` 新增手動與每日自動檢查更新、已忽略版本管理及本地化內置發行記錄
* `修復` 修正跟隨 AutoJs6 一直顯示 Host color unavailable 的問題; 外掛程式現已公開主程式設定提供者要求的受保護外掛程式資訊服務入口
* `修復` Explorer 動作除 19 個已知副檔名外亦宣告音訊 MIME 支援並新增 WMA, 讓主程式辨識的所有音訊項目均可直接使用外掛程式
* `優化` 接續播放現在只記住最近開啟的一個檔案, 開啟其他檔案時立即捨棄舊記錄, 播放完畢不會保留位置
* `優化` 固定保留三行元資料區域, 並結合實際選取音軌補充取樣率與位元率, 按 44.1 kHz · MP3 · 128 kbps 的次序顯示
* `優化` A-B 改為依次設定 A 點、B 點和清除的三次點按循環; 底部控制區增加間距, 圖示統一大小並嚴格垂直置中

# v1.2.2

###### 2026/08/27

* `修復` 修復覆蓋安裝插件後, 運行中的 AutoJs6 文件管理器仍傳送已快取的協議 v4 動作, 令所有音訊立即顯示「音訊要求無效」的問題; 網關現兼容唯讀 v4–v12 要求, 對外仍聲明 v12
* `修復` 修復部分 Android / OEM MIME 表將已聲明支援的音訊副檔名標記為通配符或 application 類型時被誤拒的問題; 現由副檔名白名單提供穩定的規範音訊 MIME 類型
* `優化` Explorer 要求拒絕日誌新增不含檔案名稱, 顯示路徑或 URI 的私隱安全原因碼, 日後可直接定位契約差異

# v1.2.1

###### 2026/08/27

* `新增` 單檔「播放音訊」動作現在可透過 Explorer Action v12 探索同目錄最多 128 個可讀音訊檔案, 並建立從選取曲目開始的自然排序佇列
* `修復` 修復 API 24 因 Android 從閘道 Activity 清單自動加入非權限型 FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS 標誌而誤拒合法 Explorer 要求的問題
* `修復` 修復自動切換至同級曲目時重新建立 MediaSession 返回 Intent 所引致的崩潰; 原始授權目標現會獨立於目前播放項目持續作為 Host Session 錨點
* `優化` 選取曲目保留原始 content URI, 同級曲目只透過要求級 Host Session 檔案描述符串流; 不猜測同級 URI, 不增加遞迴, 寫入, 儲存空間或持久存取
* `優化` 音訊副檔名篩選會將與 .m4a 同名的 .mp4 影片排除於佇列之外, 現有明確多選佇列行為維持不變
* `優化` Host Session 擁有權會轉交背景播放服務, 並於取代佇列, 啟動失敗, 播放完成或服務銷毀時關閉
* `依賴` 將內置 Explorer Action API 從通訊協定 v4 升級至向後相容的 v12 同級讀取擴充, 最低主程式組建版本仍為 5276

##### 查看更多版本

* [CHANGELOG-zh-Hant-HK.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-HK.md)

******

### 建置

******

```powershell
.\gradlew.bat :app:assembleDebug
```

發佈建置:

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
