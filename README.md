<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Terra Player</h1>

  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>AutoJs6 插件与独立应用. 通过专注的应用内控制和后台控制播放音频</p>

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

音频播放器既是 AutoJs6 文件管理器插件, 也是支持多文件的独立简易播放器, 提供应用内控制界面和私有后台播放服务, 同时接受只读 Android ACTION_VIEW content URI.

******

### 功能

******

- 通过文件浏览器动作协议 v12, 为宿主已识别的所有音频 MIME 及 19 个已知扩展名同时注册单文件动作 `play-audio` 与有序多选动作 `play-audio-selection`.
- 使用 Media3 ExoPlayer 和 MediaSessionService 播放音频, 支持音频焦点, 输出设备断开处理, 本地唤醒模式, 后台播放和系统媒体控制.
- 提供包含专辑封面, 媒体标签, 技术信息, 进度拖动, 快退快进 10 秒, 顺序 / 随机 / 单曲循环模式和 0.5 至 2 倍变速的完整播放界面.
- 按宿主给定顺序播放最多 128 个显式选中的音频文件, 支持上一曲 / 下一曲和可点选跳转或移除曲目的队列面板.
- 提供由播放服务持有的预设 / 自定义睡眠定时器, 播完本曲后停止, 最后 5 秒淡出以及适合听力练习的 A-B 区间循环.
- 只记忆最近打开的一个文件及其播放位置, 打开其他文件时立即替换, 并向系统媒体界面同步当前元数据和播放控制.
- 提供可一次选择最多 128 个文档的启动页面, 并独立接收只读 Android ACTION_VIEW `content` URI, 包括旧式 WMA MIME 别名.
- 解码失败时可使用其他兼容应用打开文件, 并排除本插件以防止自循环.
- 从文件浏览器打开单曲时, 通过请求级 Host Session 自动发现可读的直接同级音频并自然排序为有界队列; 显式多选仍保持宿主选择顺序.
- 由一个色源生成兼顾可读性的亮色与暗色配色, 默认跟随 AutoJs6, 并提供 19 个本地化 Material 500 预置色及带实时预览的自定义 RGB 颜色.
- 提供独立设置页面, 可配置跟随宿主的语言 / 夜间模式 / 颜色、续播、手动与自动更新、已忽略版本、发行历史及应用信息.

******

### 文件浏览器扩展名

******

文件浏览器目录声明 `audio/*`, 并同时通过以下扩展名兼容稀疏或旧式 Android MIME 表:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

扩展名匹配不保证能够解码. 实际播放能力取决于 Media3, Android 平台, 设备编解码器和文件内容.

******

### 宿主行为

******

安装插件并在 AutoJs6 插件中心启用后, 文件管理器会为单个文件显示主操作播放音频, 并在多选工具栏显示播放所选音频. 单文件入口从所选曲目开始, 可按自然顺序发现可读直接同级音频; 显式多选保持宿主给定的选择顺序.

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
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 12 (accepts compatible read-only v4–v12 requests)
Explorer MIME types: [audio/*]
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/* plus legacy WMA MIME aliases
required host build: 5276
```

版本 1.3.0 提供协议 v12 单文件与有序多选只读动作、请求级直接同级读取能力、独立文档选择器以及公开只读 Android 音频 / WMA 入口. 不提供可选 Host Session 的宿主继续仅播放所选文件.

同目录发现需要 AutoJs6 6.8.0 build 5276 或更高版本及 Explorer Action v12; 后续插件能力不会提高此要求.

******

### 安全性

******

应用不请求存储权限且永不写入源文件. 互联网权限仅用于用户主动触发或每日一次的 GitHub 发行版检查. 受签名权限保护的入口严格验证协议 v12、有序 TARGETS 与 ClipData、标识符、父子关系、元数据和只读标志; Host Session 仍绑定 UID 且不递归, 公开与独立文档入口只保留读取授权.

******

### 安全限制

******

- 单文件动作从恰好 1 个所选文件开始, 可构建可读直接同级音频的有界队列; 多选动作接受 1 至 128 个不重复文件并保持其顺序.
- 声明大小超过 8 TiB 的文件浏览器请求将被拒绝.
- 文件浏览器动作按宿主识别的音频 MIME 或明确扩展名列表匹配, 执行时仍会规范化并验证每个目标.
- 同级发现仅可通过宿主管理的请求级会话且不递归; 插件绝不猜测同级 URI, 也不接收文件系统路径.
- 公共 Android 入口要求 ACTION_VIEW、`content` URI、受支持的音频或旧式 WMA MIME 类型及读取授权.
- 通知权限是可选权限. 拒绝权限会隐藏通知栏控制, 但不会阻止播放.
- 播放完成或解码错误时会安全结束. 外部降级仅转交新的读取授权, 并排除本插件.

******

### 版本历史

******

# v1.3.0

###### 2026/08/29

* `新增` 新增启动页面与独立多文件播放器模式, 并提供独立设置页面, 包含语言、夜间模式、主题色、续播、更新、发行历史及应用与开发者信息
* `新增` 语言、夜间模式和色源默认通过 AutoJs6 官方只读设置契约跟随宿主; 宿主不可用时仍显示但禁用对应选项, 并回退至应用默认值
* `新增` 新增手动与每日自动检查更新、已忽略版本管理及本地化内置发行历史
* `修复` 修复跟随 AutoJs6 始终显示 Host color unavailable 的问题; 插件现已公开宿主设置提供器要求的受保护插件信息服务入口
* `修复` Explorer 动作在 19 个已知扩展名之外同时声明音频 MIME 支持并新增 WMA, 使宿主识别的所有音频条目均可直接调用插件
* `优化` 续播现在只记忆最近打开的一个文件, 打开其他文件时立即丢弃旧记录, 播放完毕永不保留位置
* `优化` 固定保留三行元数据区域, 并结合实际选中音轨补足采样率与码率, 按 44.1 kHz · MP3 · 128 kbps 的顺序显示
* `优化` A-B 改为依次设置 A 点、B 点和清除的三次点击循环; 底部控制区增加间距, 图标统一大小并严格垂直居中

# v1.2.2

###### 2026/08/27

* `修复` 修复覆盖安装插件后, 运行中的 AutoJs6 文件管理器仍发送缓存的协议 v4 动作, 导致所有音频立即提示“音频请求无效”的问题; 网关现在兼容只读 v4–v12 请求, 对外仍声明 v12
* `修复` 修复部分 Android / OEM MIME 表将已声明支持的音频扩展名标记为通配符或 application 类型时被误拒绝的问题; 现由扩展名白名单提供稳定的规范音频 MIME 类型
* `优化` Explorer 请求拒绝日志新增不含文件名, 显示路径或 URI 的隐私安全原因码, 后续契约差异可直接定位

# v1.2.1

###### 2026/08/27

* `新增` 单文件“播放音频”动作现在可通过 Explorer Action v12 发现同目录最多 128 个可读音频文件, 并构建从选中曲目开始的自然排序队列
* `修复` 修复 API 24 会因 Android 从网关 Activity 清单自动附加非权限型 FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS 标志而误拒绝合法 Explorer 请求的问题
* `修复` 修复自动切换到同级曲目时重建 MediaSession 返回 Intent 导致崩溃的问题; 原始授权目标现在独立于当前播放项持续作为 Host Session 锚点
* `优化` 选中曲目保留原始 content URI, 同级曲目仅通过请求级 Host Session 文件描述符串流; 不猜测同级 URI, 不增加递归, 写入, 存储或持久访问
* `优化` 音频扩展名筛选会将与 .m4a 同名的 .mp4 视频排除在队列外, 既有显式多选队列行为保持不变
* `优化` Host Session 所有权会转交后台播放服务, 并在替换队列, 启动失败, 播放完成或服务销毁时关闭
* `依赖` 将内置 Explorer Action API 从协议 v4 升级到向后兼容的 v12 同级读取扩展, 最低宿主构建版本仍为 5276

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
