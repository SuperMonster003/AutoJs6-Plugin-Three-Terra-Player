******

### 版本記錄

******

# v1.0.0

###### 2026/08/02

* `新增` 音訊播放器外掛程式, 外掛程式 ID 為 `audio-player`, 動作 ID 為 `play-audio`, 引擎為 `explorer-action`, 變體為 `default`
* `新增` 適用於主程式現有 18 個音訊副檔名的主要唯讀檔案瀏覽器動作通訊協定 v2 入口, 檔案瀏覽器 MIME 類型目錄為空, 要求 AutoJs6 主程式組建版本 5269
* `新增` Media3 ExoPlayer 和 MediaSessionService 播放, 支援音訊焦點, 輸出裝置中斷處理, 本地喚醒模式, 背景播放, 系統媒體控制和私有控制介面
* `新增` 可選的 Android 13+ 通知權限說明, 拒絕權限時不會阻止播放
* `新增` 面向 `content` URI 音訊要求的獨立唯讀 Android ACTION_VIEW 支援, 解碼失敗時可轉交其他相容應用程式並防止自循環
* `新增` 嚴格驗證通訊協定, URI, ClipData, 來源, 名稱, MIME, 大小和授權, 不要求儲存空間或網絡權限, 只轉交最小讀取授權
* `新增` 純 JVM 實作且不包含原生程式庫, 透過 `supportedAbis = emptyArray()` 宣告 ABI 無限制, 發佈單一 ABI 無關 APK
* `新增` 外掛程式中繼資料, 介面文字, 使用說明, README 和 CHANGELOG 的多語言資源: 西班牙語/法語/俄語/阿拉伯語/日語/韓語/英語/簡體中文/香港繁體/台灣繁體
* `依賴` 附加 AndroidX Media3 ExoPlayer, Session 和 UI 版本 1.10.1
