3-Terra Player (前稱 Audio Player) 既是 AutoJs6 檔案管理器的音訊播放外掛程式, 也是一款可獨立使用的簡潔音訊播放器. 安裝並啟用後, 在 AutoJs6 檔案管理器中點按任何音訊檔案即可直接播放, 無需再依賴第三方播放器; 也可以像普通應用程式一樣從主畫面開啟, 一次選擇多個音訊檔案連續播放.

### 使用方法

1. 從 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) 頁面下載最新外掛程式 APK, 安裝到執行 AutoJs6 的裝置上.
2. 開啟 AutoJs6 的外掛程式中心, 確認 `3-Terra Player` 已被辨識並處於啟用狀態.
3. 在 AutoJs6 檔案管理器中點按任何音訊檔案並選擇 `播放音訊`; 或長按多選後在工具列選擇 `播放所選音訊`.
4. 也可以直接從主畫面開啟 `3-Terra Player`, 透過檔案選擇按鈕一次選取多個音訊開始播放.

若外掛程式中心未顯示此外掛程式, 請先將 AutoJs6 升級到 6.8.0 (組建版本 5276) 或更新版本. 外掛程式本身支援 Android 7.0 (API 24) 及以上的裝置, 獨立應用程式模式不依賴 AutoJs6.

### 支援的音訊格式

檔案管理器入口宣告通用音訊類型 `audio/*`, 並明確涵蓋以下 19 種副檔名, 以兼容部分裝置殘缺或舊式的 MIME 類型表:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

副檔名受支援不代表一定能夠解碼: 實際播放能力取決於 Media3, Android 系統版本, 裝置解碼器與檔案內容. 遇到無法播放的檔案時, 可在錯誤面板中選擇使用其他應用程式開啟.

### 權限與安全

外掛程式不要求儲存權限, 對音訊檔案只有唯讀存取且永不寫入; 網絡連線只用於用戶觸發或每日一次的更新檢查. Android 13+ 的通知權限為可選項, 拒絕後只會隱藏通知欄控制項, 不影響播放.

更多說明與完整文件可參閱 [專案主頁](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player) 與 [AutoJs6 文件](https://docs.autojs6.com).
