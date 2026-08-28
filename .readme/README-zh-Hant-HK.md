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

- 透過檔案瀏覽器動作通訊協定 v12, 為主程式已辨識的 18 個音訊副檔名同時註冊單一檔案動作 `play-audio` 與有序多選動作 `play-audio-selection`.
- 使用 Media3 ExoPlayer 和 MediaSessionService 播放音訊, 支援音訊焦點, 輸出裝置中斷處理, 本地喚醒模式, 背景播放和系統媒體控制.
- 提供包含專輯封面, 媒體標籤, 技術資訊, 進度拖曳, 快退快進 10 秒, 順序 / 隨機 / 單曲循環模式和 0.5 至 2 倍變速的完整播放介面.
- 按主程式提供的次序播放最多 128 個明確選取的音訊檔案, 支援上一首 / 下一首及可跳轉或移除曲目的播放佇列面板.
- 提供由播放服務持有的預設 / 自訂睡眠計時器, 播完本曲後停止, 最後 5 秒淡出及適合聽力練習的 A-B 區間循環.
- 逐曲記憶播放進度, 並向系統媒體介面同步目前標籤標題, 專輯封面, 上一首 / 下一首及快退快進 10 秒控制.
- 獨立接收面向 `audio/*` 的唯讀 `content` URI Android ACTION_VIEW 要求, 捨棄呼叫端 extras 和廣泛 URI 授權.
- 解碼失敗時可使用其他相容應用程式開啟檔案, 並排除本外掛程式以防止自循環.
- 從檔案瀏覽器開啟單曲時, 透過要求級 Host Session 自動尋找可讀直接同級音訊並按自然次序建立有界佇列; 明確多選仍保持主程式選取次序.

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

安裝外掛程式後, 檔案管理器會為單一檔案顯示主要動作播放音訊, 並在多選工具列顯示播放所選音訊. 單一檔案入口從所選曲目開始, 可按自然次序尋找可讀直接同級音訊; 明確多選保持主程式提供的選取次序.

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
Explorer protocol version: 4
Explorer MIME types: []
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/*
required host build: 5276
```

版本 1.2.1 提供通訊協定 v12 單一檔案唯讀主要動作, 可接收要求級直接同級讀取能力, 並保留有序多選唯讀動作. 獨立 Android 入口仍為單一檔案並接受 `audio/*`. 不提供可選 Host Session 的主程式繼續只播放所選檔案.

同目錄尋找需要 AutoJs6 6.8.0 build 5276 或更新版本及 Explorer Action v12; 後續外掛程式能力不會提高此要求.

******

### 安全性

******

外掛程式不要求儲存空間或互聯網存取權限. 受簽章權限保護的入口嚴格驗證通訊協定 v12, 有序 TARGETS 與 ClipData, 識別碼, 父子關係, 元資料和唯讀旗標. 可選 Host Session 由主程式綁定至外掛程式 UID, 只可列出所選檔案的直接上層目錄並開啟所選檔案或可讀直接同級檔案; 播放元件只接收不透明合成路由, 不接收檔案系統路徑. 公開 Android 入口保持唯讀單一檔案.

******

### 安全限制

******

- 單一檔案動作從剛好 1 個所選檔案開始, 可建立可讀直接同級音訊的有界佇列; 多選動作接受 1 至 128 個不重複檔案並保持次序.
- 宣告大小超過 8 TiB 的檔案瀏覽器要求會被拒絕.
- 檔案瀏覽器動作只按檔案名稱副檔名選取, 執行時仍會驗證音訊 MIME 類型.
- 同級尋找只可透過主程式管理的要求級工作階段且不遞迴; 外掛程式絕不猜測同級 URI, 亦不接收檔案系統路徑.
- 公開 Android 入口要求 ACTION_VIEW, `content` URI, `audio/*` 和讀取授權.
- 通知權限是可選權限. 拒絕權限會隱藏通知欄控制, 但不會阻止播放.
- 播放完成或解碼錯誤時會安全結束. 外部降級只轉交新的讀取授權, 並排除本外掛程式.

******

### 版本記錄

******

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

# v1.2.0

###### 2026/08/27

* `新增` 支援檔案瀏覽器動作通訊協定 v4, 新增有序多選動作, 可將最多 128 個明確選取的音訊檔案建立為播放佇列
* `新增` 新增原生 Media3 播放清單, 上一首 / 下一首控制, 可跳轉或移除曲目的播放佇列面板及順序 / 隨機 / 單曲循環模式
* `新增` 新增由服務持有的睡眠計時器, 支援 15 / 30 / 60 分鐘, 自訂時長, 播完本曲後停止和最後 5 秒淡出
* `新增` 新增 A-B 區間循環, 可重複播放所選片段
* `優化` 系統媒體控制新增上一首, 下一首及快退 / 快進 10 秒命令, 中繼資料與續播位置會跟隨目前播放佇列曲目
* `優化` 檔案瀏覽器要求驗證擴展至有序 TARGETS 與 ClipData, 唯一要求和目標識別碼, 主程式工作階段描述及每個選取檔案, 且不擴大唯讀授權
* `優化` 明確記錄主程式 FileProvider 上層目錄 URI 無法列舉子項; 繼續停用同層檔案自動尋找和 URI 猜測, 並以明確多選作為安全播放佇列路徑
* `依賴` 內置檔案瀏覽器動作 API 從通訊協定 v2 升級至 v4, 最低主程式組建版本提升至 5276
* `依賴` 新增 AndroidX RecyclerView 1.4.0

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
