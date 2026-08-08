<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>文件管理器插件. 通过应用内控制和后台控制播放音频文件</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Audio-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 语言 (Languages)

******

当前 README.md 支持以下语言:

- 简体中文 [zh-Hans] # 当前
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ar.md)

******

### 简介

******

音频播放器为从文件管理器打开的音频文件提供应用内控制界面和私有后台播放服务. 插件也可以接收面向音频 MIME 类型 content URI 的只读 Android ACTION_VIEW 请求.

******

### 功能

******

- 通过文件浏览器动作协议 v2 为宿主已识别的 18 个音频扩展名注册主操作只读动作 `play-audio`.
- 使用 Media3 ExoPlayer 和 MediaSessionService 播放音频, 支持音频焦点, 输出设备断开处理, 本地唤醒模式, 后台播放和系统媒体控制.
- 提供包含标题元数据和播放控制的界面, 并提供可选的 Android 13+ 通知权限说明和播放错误反馈.
- 独立接收面向 `audio/*` 的只读 `content` URI Android ACTION_VIEW 请求, 丢弃调用方 extras 和宽泛 URI 授权.
- 解码失败时可使用其他兼容应用打开文件, 并排除本插件以防止自循环.

******

### 文件浏览器扩展名

******

文件浏览器目录有意仅匹配以下扩展名, MIME 类型列表为空:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave
```

扩展名匹配不保证能够解码. 实际播放能力取决于 Media3, Android 平台, 设备编解码器和文件内容.

******

### 宿主行为

******

安装插件后, 文件管理器会为列出的扩展名显示主操作播放音频. 选择此动作后, 插件会打开控制界面, 并使用对所选文件的临时只读权限启动私有播放服务.

缺少插件时, 此动作不会显示. 宿主会保留原有的音频文件只读外部 ACTION_VIEW 流程, 因此其他已安装的音频应用仍可处理文件. 如果没有兼容的外部应用, 宿主不会获得替代播放界面.

******

### 插件接口

******

宿主使用以下标识发现并执行插件:

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

版本 1 在主文件管理器中提供单文件主操作只读动作. 目录仅按扩展名匹配, 独立 Android 入口仍接受 `audio/*`.

需要宿主构建版本 5269 或更高版本.

******

### 安全性

******

插件不请求存储或网络权限. 文件管理器入口受宿主签名权限保护, 并严格验证协议 v2, 目标与父目录 content URI, ClipData, 来源界面, 显示名称, MIME 类型, 声明大小和只读标志. 仅将目标 URI 和读取授权转发给私有播放组件. 公共 Android 入口仅接受只读 content 音频请求, 拒绝写入, 持久和前缀授权, 并且不会转发调用方的任意 extras.

******

### 安全限制

******

- 每次文件浏览器动作仅处理 1 个目标文件.
- 声明大小超过 8 TiB 的文件浏览器请求将被拒绝.
- 文件浏览器动作仅按文件名扩展名选择, 执行时仍会验证音频 MIME 类型.
- 公共 Android 入口要求 ACTION_VIEW, `content` URI, `audio/*` 和读取授权.
- 通知权限是可选权限. 拒绝权限会隐藏通知栏控制, 但不会阻止播放.
- 播放完成或解码错误时会安全结束. 外部降级仅转交新的读取授权, 并排除本插件.

******

### 版本历史

******

# v1.0.1

###### 2026/08/08

* `修复` 插件中心启用时出现空服务绑定的问题
* `优化` 插件名称, 描述和用户文档更加简洁自然

# v1.0.0

###### 2026/08/02

* `新增` 音频播放器插件, 插件 ID 为 `audio-player`, 动作 ID 为 `play-audio`, 引擎为 `explorer-action`, 变体为 `default`
* `新增` 面向宿主 18 个音频扩展名的文件管理器主操作只读动作, 要求宿主构建版本 5269 或更高版本
* `新增` Media3 ExoPlayer 和 MediaSessionService 播放, 支持音频焦点, 输出设备断开处理, 本地唤醒模式, 后台播放, 系统媒体控制和私有控制界面
* `新增` 可选的 Android 13+ 通知权限说明, 拒绝权限时不会阻止播放
* `新增` 面向 `content` URI 音频请求的独立只读 Android ACTION_VIEW 支持, 解码失败时可转交其他兼容应用并防止自循环
* `新增` 严格验证协议, URI, ClipData, 来源, 名称, MIME, 大小和授权, 不申请存储或网络权限, 仅转发最小读取授权
* `新增` 插件元数据, 界面文本, 使用说明, README 和 CHANGELOG 的多语言资源: 西班牙语/法语/俄语/阿拉伯语/日语/韩语/英语/简体中文/香港繁体/台湾繁体
* `依赖` 附加 AndroidX Media3 ExoPlayer, Session 和 UI 版本 1.10.1

##### 查看更多版本

* [CHANGELOG-zh-Hans.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

******

### 构建

******

```powershell
.\gradlew.bat :app:assembleDebug
```

发布构建:

```powershell
.\gradlew.bat :app:assembleRelease
```

构建参数来自 `version.properties`. 当前最低 SDK 为 24, 目标 SDK 为 36.

******

### 资源布局

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 为插件元数据和界面文本提供本地化. `plugin_instruction.md` 提供宿主显示的说明. `.python/generate_markdown.py` 根据 JSON 源文件生成多语言 README 和更新日志.

******

### 链接

******

- AutoJs6 文档: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Android 安全文件共享: https://developer.android.com/training/secure-file-sharing
