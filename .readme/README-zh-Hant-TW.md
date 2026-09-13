<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="three-terra-player-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>支援背景播放的音訊播放外掛與獨立應用程式</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 語言 (Languages)

******

目前 README.md 支援以下語言:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-HK.md)
- 繁體中文 (台灣) [zh-Hant-TW] # 目前
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ar.md)

******

### 簡介

******

3-Terra Player (原名 Audio Player) 既是 AutoJs6 檔案管理器的音訊播放外掛程式, 也是一款可獨立使用的簡潔音訊播放器. 安裝並啟用後, 在 AutoJs6 檔案管理器中點選任一音訊檔案即可直接播放, 不必再借助第三方播放器; 也可以像一般應用程式一樣從主畫面開啟它, 一次選擇多個音訊檔案連續播放.

外掛程式安裝後由 AutoJs6 自動探索, 無需任何設定. 播放基於 Android 官方媒體框架 Media3 (ExoPlayer), 支援背景播放與系統媒體通知控制; 全程以唯讀方式存取音訊檔案, 不要求儲存權限, 也永遠不會修改或刪除來源檔案.

******

### 功能亮點

******

- 本機播放清單: 支援 M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL; 保留順序, 標題和重複項目. 宿主入口讀取同目錄檔案; 獨立入口可選擇清單資料夾以讀取相對路徑. 一次開啟一個清單, 最多 128 項; 不支援網路位址, HLS 與巢狀清單.
- 檔案管理器一鍵播放: 在 AutoJs6 檔案管理器中點選任一音訊檔案即可開始播放.
- 同資料夾自動連播: 開啟一首歌時自動尋找同資料夾中的其他音訊, 按檔案名稱自然排序連續播放 (最多 128 首, 從所選曲目開始).
- 多選佇列播放: 在檔案管理器中勾選最多 128 個音訊後一鍵播放, 嚴格保持勾選順序.
- 完整播放介面: 專輯封面, 標題 / 藝人 / 專輯標籤, 取樣率與位元率等技術資訊, 以及可拖曳的進度列.
- 常用控制齊全: 上一首 / 下一首, 可選 5 / 10 / 15 / 30 秒快退快進, 順序 / 隨機 / 單曲循環, 可儲存 0.5 至 2 倍預設倍速, 並自選佇列播完行為.
- 播放佇列面板: 隨時查看待播清單, 點選跳轉或移除曲目, 目前曲目一目了然.
- 睡眠計時器: 15 / 30 / 60 分鐘預設或自訂時間, 支援播完本曲再停止, 結束前 5 秒音量淡出.
- A-B 區間循環: 反覆聆聽任意片段, 適合聽力練習與樂器採譜.
- 背景播放: 離開介面或關閉螢幕後播放不中斷, 可在系統媒體通知與鎖定畫面上直接控制.
- 工作階段還原: 從啟動器再次開啟獨立應用程式時, 還原上次佇列, 目前曲目, 停止位置, 循環 / 隨機與倍速; 還原後維持暫停.
- 獨立應用程式模式: 沒有安裝 AutoJs6 也能使用, 從啟動頁面一次選擇最多 128 個音訊檔案播放.
- 回應式外觀: 語言 / 夜間模式 / 基礎主題色彩可跟隨 AutoJs6 或自訂; 專輯封面即時產生可讀色盤並融入沉浸式邊到邊播放器, 轉場動畫與觸覺回饋遵循系統設定.

******

### 使用方式

******

1. 從 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) 頁面下載最新外掛程式 APK, 安裝到執行 AutoJs6 的裝置上.
2. 開啟 AutoJs6 的外掛中心, 確認 `3-Terra Player` 已被辨識並處於啟用狀態.
3. 在 AutoJs6 檔案管理器中點選任一音訊檔案並選擇 `播放音訊`; 或長按多選後在工具列選擇 `播放所選音訊`.
4. 也可以直接從主畫面開啟 `3-Terra Player`, 透過檔案選擇按鈕一次選取多個音訊開始播放.

> 若外掛中心未顯示此外掛程式, 請先將 AutoJs6 升級到 6.8.0 (內部版本號 5276) 或更新版本. 外掛程式本身支援 Android 7.0 (API 24) 及以上的裝置, 獨立應用程式模式不依賴 AutoJs6.

******

### 支援的音訊格式

******

檔案管理器入口宣告通用音訊類型 `audio/*`, 並明確涵蓋以下 19 種副檔名, 以相容部分裝置殘缺或老舊的 MIME 類型表:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

副檔名受支援不代表一定能夠解碼: 實際播放能力取決於 Media3, Android 系統版本, 裝置解碼器與檔案內容. 遇到無法播放的檔案時, 可在錯誤面板中選擇使用其他應用程式開啟.

******

### 常見問題

******

#### 檔案管理器裡沒有出現 `播放音訊` 按鈕?

請依序檢查三點: 外掛程式是否已安裝; AutoJs6 外掛中心裡是否已啟用本外掛程式; AutoJs6 是否為 6.8.0 (內部版本號 5276) 或更新版本. 三者齊備後, 檔案管理器中的音訊檔案就會出現播放入口.

#### 我只點了一首歌, 佇列裡為什麼出現了同資料夾的其他歌曲?

這是同資料夾自動連播特性: 開啟單一音訊時, 外掛程式會透過主程式管理的工作階段尋找同資料夾的其他音訊, 並按檔案名稱自然排序排入佇列, 方便依序聆聽. 尋找過程完全唯讀且不會進入子資料夾; 若主程式版本不支援此能力, 則只播放所選的一首.

#### 多選播放時的順序是怎樣的?

嚴格按照勾選順序播放. 想按特定順序聆聽時, 依目標順序逐一勾選即可; 進入播放器後也可在佇列面板中點選跳轉或移除曲目.

#### 離開介面或關閉螢幕後還會繼續播放嗎?

會. 播放由背景服務承載, 可透過系統媒體通知與鎖定畫面控制項控制. Android 13 及以上系統的通知權限為可選項目: 拒絕後僅隱藏通知欄控制項, 不影響播放本身.

#### 外掛程式會修改或上傳我的音訊檔案嗎?

不會. 外掛程式不要求儲存權限, 對音訊檔案僅有唯讀存取; 網路連線僅用於檢查 GitHub 上的新版本 (由使用者手動觸發或每日至多一次), 不上傳任何檔案或個人資料.

#### 為什麼有的檔案播不出聲音或提示解碼失敗?

副檔名受支援不等於裝置一定能解碼, 部分少見編碼或損壞的檔案可能無法播放. 出錯時錯誤面板會顯示具體錯誤代碼, 並提供使用其他應用程式開啟的入口 (會自動排除本外掛程式以避免自循環).

#### 如何讓外掛程式介面跟隨 AutoJs6 的語言和主題?

預設即跟隨: 語言, 夜間模式與主題色彩會透過 AutoJs6 的官方唯讀設定介面自動同步. 也可在設定頁改為固定語言或自選顏色; 未安裝 AutoJs6 時自動回復為系統外觀與內建預設值.

#### 睡眠計時器和 A-B 循環在哪裡?

都在播放介面底部工具列. 計時器圖示可選擇預設或自訂時間; A-B 按鈕按 '設 A 點, 設 B 點, 清除' 的順序循環點選即可設定或取消區間循環.

******

### 權限與安全性

******

音訊檔案可能來自不可信任的來源, 外掛程式在設計上為播放流程建立了多道防線:

- 零儲存權限: 外掛程式不要求也無法取得裝置儲存空間的讀寫權限, 只能存取主程式或系統明確授權的單一檔案.
- 永不寫入: 對音訊檔案僅有唯讀存取, 不會修改, 移動或刪除任何來源檔案.
- 嚴格驗證: 檔案管理器入口受 AutoJs6 簽章權限保護, 每個播放要求的通訊協定版本, 目標清單與唯讀授權都會逐項驗證, 不符規範的要求直接拒絕.
- 有界尋找: 同資料夾連播僅透過主程式管理的請求級工作階段讀取, 不遞迴子資料夾, 不猜測檔案位址, 工作階段隨播放結束關閉.
- 本機工作階段: 啟用接續播放後, 應用程式私人儲存空間只會儲存最近一個獨立檔案佇列及其播放狀態; 僅接受仍有長期唯讀授權的系統檔案選擇器 URI, 絕不儲存 Host Session 路徑, 關閉此設定會立即清除記錄.
- 最小連線: 網際網路權限僅用於使用者觸發或每日一次的 GitHub 版本檢查, 不涉及任何音訊內容與使用資料.
- 可選通知: Android 13+ 的通知權限為可選項目, 拒絕後僅隱藏通知欄控制項, 播放不受影響.

請僅從官方 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) 頁面或其他可信任的管道取得外掛程式安裝檔; 來源不明的安裝檔即使名稱與版本號相同, 也可能已被竄改.

******

### 外掛程式介面

******

以下資訊提供給 AutoJs6 主程式與外掛程式開發者, 主程式透過這些識別資訊探索外掛程式並完成能力協商:

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

目前版本提供通訊協定 v12 的單檔與有序多選唯讀動作, 請求級同資料夾尋找能力, 獨立文件選擇器以及公開的唯讀 Android 音訊入口 (含舊式 WMA MIME 別名). 不提供可選 Host Session 能力的主程式繼續僅播放所選檔案.

同資料夾尋找需要 AutoJs6 6.8.0 (內部版本號 5276) 及以上並支援 Explorer Action v12; 後續外掛程式能力不會提高此要求. 產品自 v1.4.0 起更名為 3-Terra Player, 應用程式 ID 保持 `io.github.supermonster003.autojs6.plugin.audioplayer` 不變, 可覆蓋安裝升級.

******

### 開發藍圖

******

外掛程式的能力規劃與完成情況以可勾選清單維護在 Roadmap.md 中, 按里程碑組織並附驗收標準, 涵蓋歌詞, 等化器, 封面取色, 沉浸式介面與工程品質等方向. 未勾選項目表示規劃意向而非目前版本能力, 歡迎透過 Issues 參與討論.

- [檢視 Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/Roadmap.md)

******

### 版本記錄

******

#### v1.6.0

_2026/09/13_

- `新增` 本機播放清單: 支援 M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL; 保留順序, 標題和重複項目. 宿主入口讀取同目錄檔案; 獨立入口可選擇清單資料夾以讀取相對路徑. 一次開啟一個清單, 最多 128 項; 不支援網路位址, HLS 與巢狀清單
- `修復` 語言與夜間模式對話框的清單項目改用 16sp Material Body1, 不再沿用過大的平台清單文字
- `優化` 統一多語言資源, 明確外掛啟用契約並驗證發行產物

#### v1.5.0

_2026/09/12_

- `新增` 專輯封面現在會為播放器漸層背景, 工具列, 控制項與佇列即時產生可讀色盤; 各頁面以邊到邊方式融入狀態列, 瀏海與手勢區域
- `新增` 播放設定新增可儲存的預設倍速, 5 / 10 / 15 / 30 秒快退快進間隔, 以及佇列播完後停止, 回到佇列開頭並暫停或自動重播的行為
- `新增` 新增簡潔的播放 / 暫停與封面轉場動畫, 並為主要播放操作加入遵循系統設定的觸覺回饋; 系統關閉動畫時所有狀態直接切換
- `修復` 語言與夜間模式對話框的清單項目改用 16sp Material Body1, 不再沿用過大的平台清單文字
- `優化` 封面解碼與最大 64x64 取色樣本移至背景執行緒; 任意封面顏色繼續通過正文 4.5:1 與輪廓 3:1 的既有對比度門檻
- `優化` 完成播放視覺化可行性研究與 RMS 分桶原型: 平台 Visualizer 因要求錄音權限而繼續排除, 並記錄無權限 Media3 PCM 抽頭供後續效能基準驗證
- `優化` 統一 README 版式與 Gradle 平台版本管理方式
- `優化` 精簡外掛描述並規範多語言資源中的標點符號
- `優化` 將外部檢視入口統一命名為 External Viewer
- `優化` 外掛更新對話框的發行歷史按鈕改為開啟內建發行歷史頁面
- `優化` 建置階段阻止意外引入原生相依套件, 並輸出 JSON 校驗報告

#### v1.4.1

_2026/08/31_

- `新增` 獨立應用程式現在會在從啟動器再次開啟時還原上次佇列, 目前曲目, 停止位置, 循環模式, 隨機狀態與倍速; 還原後維持暫停, 且只儲存仍有長期唯讀授權的系統檔案選擇器佇列
- `修復` 修正播放器右上角更多選單在部分主題下出現白底白字, 無法辨識設定選單項目的問題
- `優化` 主題色設定中的 AutoJs6 來源統一表述為 `跟隨 AutoJs6`; 顏色選擇面板直接顯示主程式顏色的 HEX 值
- `優化` 完成十語言 README, CHANGELOG 與外掛程式說明的基本人工複核, 並將專案首頁與應用程式內更新位址遷移至 3-Terra Player 正式儲存庫

##### 更多版本記錄可參閱

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-TW.md)

******

### 建置

******

本節提供給希望從原始碼建置外掛程式的開發者.

建置 debug APK:

```powershell
.\gradlew.bat :app:assembleDebug
```

建置 release APK (在未納入版本控制的 `sign.properties` 中設定簽章後自動簽署):

```powershell
.\gradlew.bat :app:assembleRelease
```

發行封存可執行 `:app:appendDigestToReleasedFiles` 任務, 將已簽署的 APK 複製到 `releases/` 並在檔案名稱中附加版本號與 CRC32 摘要.

建置參數集中於 `version.properties`: 最低 SDK 24 (Android 7.0), 目標 SDK 36, 目前版本 1.6.0.

******

### 在地化與文件產生

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

`strings.xml` 提供外掛程式中繼資料與介面文字的在地化, `plugin_instruction.md` 提供主程式外掛中心顯示的使用說明. README, 更新記錄與使用說明均由 JSON 來源產生: 修改 `.readme/` 與 `.changelog/` 下的來源檔案後執行 `py .python/generate_markdown.py` 重新產生全部產物, 產生的產物不手動編輯; 執行 `py .python/generate_markdown.py --check` 可驗證來源檔案與產物是否同步.

******

### 授權

******

專案程式碼以 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE) 授權開放原始碼. 音訊播放能力基於 [AndroidX Media3](https://developer.android.com/media/media3) (Apache License 2.0).

******

### 相關連結

******

- AutoJs6 文件: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Android 安全檔案分享: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/docs/16kb.md)
