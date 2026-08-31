3-Terra Player (原名 Audio Player) 既是 AutoJs6 文件管理器的音频播放插件, 也是一款可独立使用的简洁音频播放器. 安装并启用后, 在 AutoJs6 文件管理器中点击任意音频文件即可直接播放, 无需再借助第三方播放器; 也可以像普通应用一样从桌面打开它, 一次选择多个音频文件连续播放.

### 使用方法

1. 从 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) 页面下载最新插件 APK, 安装到运行 AutoJs6 的设备上.
2. 打开 AutoJs6 的插件中心, 确认 `3-Terra Player` 已被识别并处于启用状态.
3. 在 AutoJs6 文件管理器中点击任意音频文件并选择 `播放音频`; 或长按多选后在工具栏选择 `播放所选音频`.
4. 也可以直接从桌面打开 `3-Terra Player`, 通过文件选择按钮一次选取多个音频开始播放.

若插件中心未显示该插件, 请先将 AutoJs6 升级到 6.8.0 (内部版本号 5276) 或更高版本. 插件自身支持 Android 7.0 (API 24) 及以上的设备, 独立应用模式不依赖 AutoJs6.

### 支持的音频格式

文件管理器入口声明通用音频类型 `audio/*`, 并显式覆盖以下 19 种扩展名, 以兼容部分设备残缺或老旧的 MIME 类型表:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

扩展名受支持不代表一定能够解码: 实际播放能力取决于 Media3, Android 系统版本, 设备解码器与文件内容. 遇到无法播放的文件时, 可在错误面板中选择使用其他应用打开.

### 权限与安全

插件不申请存储权限, 对音频文件仅有只读访问且永不写入; 联网仅用于用户触发或每日一次的更新检查. Android 13+ 的通知权限为可选项, 拒绝后仅隐藏通知栏控件, 不影响播放.

更多说明与完整文档可参阅 [项目主页](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player) 与 [AutoJs6 文档](https://docs.autojs6.com).
