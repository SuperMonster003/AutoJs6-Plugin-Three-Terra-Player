<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Terra Player</h1>

  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="three-terra-player-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>音频播放插件与独立应用. 从 AutoJs6 文件管理器一键播放音频, 也可独立使用, 支持后台播放</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/commit/b44bd63ce6e2a7b7a393f61a28430c2815cc2d7e"><img alt="Created" src="https://img.shields.io/date/1785664006?color=2e7d32&label=Created"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 语言 (Languages)

******

当前 README.md 支持以下语言:

- 简体中文 [zh-Hans] # 当前
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ar.md)

******

### 简介

******

3-Terra Player (原名 Audio Player) 既是 AutoJs6 文件管理器的音频播放插件, 也是一款可独立使用的简洁音频播放器. 安装并启用后, 在 AutoJs6 文件管理器中点击任意音频文件即可直接播放, 无需再借助第三方播放器; 也可以像普通应用一样从桌面打开它, 一次选择多个音频文件连续播放.

插件安装后由 AutoJs6 自动发现, 无需任何配置. 播放基于 Android 官方媒体框架 Media3 (ExoPlayer), 支持后台播放与系统媒体通知控制; 全程以只读方式访问音频文件, 不申请存储权限, 也永远不会修改或删除源文件.

******

### 功能亮点

******

- 文件管理器一键播放: 在 AutoJs6 文件管理器中点击任意音频文件即可开始播放.
- 同目录自动连播: 打开一首歌时自动发现同文件夹中的其他音频, 按文件名自然排序连续播放 (最多 128 首, 从所选曲目开始).
- 多选队列播放: 在文件管理器中勾选最多 128 个音频后一键播放, 严格保持勾选顺序.
- 完整播放界面: 专辑封面, 标题 / 艺术家 / 专辑标签, 采样率与码率等技术信息, 以及可拖动的进度条.
- 常用控制齐全: 上一曲 / 下一曲, 快退快进 10 秒, 顺序 / 随机 / 单曲循环, 0.5 至 2 倍变速播放.
- 播放队列面板: 随时查看待播列表, 点选跳转或移除曲目, 当前曲目一目了然.
- 睡眠定时器: 15 / 30 / 60 分钟预设或自定义时长, 支持播完本曲再停止, 结束前 5 秒音量淡出.
- A-B 区间循环: 反复聆听任意片段, 适合听力练习与乐器扒谱.
- 后台播放: 退出界面或熄屏后播放不中断, 可在系统媒体通知与锁屏上直接控制.
- 会话恢复: 从启动器再次打开独立应用时, 恢复上次队列, 当前曲目, 停止位置, 循环 / 随机与倍速; 恢复后保持暂停.
- 独立应用模式: 没有安装 AutoJs6 也能使用, 从启动页一次选择最多 128 个音频文件播放.
- 外观个性化: 语言 / 夜间模式 / 主题色默认跟随 AutoJs6, 也可从 19 种预置色或自定义 RGB 中自选.

******

### 使用方法

******

1. 从 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) 页面下载最新插件 APK, 安装到运行 AutoJs6 的设备上.
2. 打开 AutoJs6 的插件中心, 确认 `3-Terra Player` 已被识别并处于启用状态.
3. 在 AutoJs6 文件管理器中点击任意音频文件并选择 `播放音频`; 或长按多选后在工具栏选择 `播放所选音频`.
4. 也可以直接从桌面打开 `3-Terra Player`, 通过文件选择按钮一次选取多个音频开始播放.

> 若插件中心未显示该插件, 请先将 AutoJs6 升级到 6.8.0 (内部版本号 5276) 或更高版本. 插件自身支持 Android 7.0 (API 24) 及以上的设备, 独立应用模式不依赖 AutoJs6.

******

### 支持的音频格式

******

文件管理器入口声明通用音频类型 `audio/*`, 并显式覆盖以下 19 种扩展名, 以兼容部分设备残缺或老旧的 MIME 类型表:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

扩展名受支持不代表一定能够解码: 实际播放能力取决于 Media3, Android 系统版本, 设备解码器与文件内容. 遇到无法播放的文件时, 可在错误面板中选择使用其他应用打开.

******

### 常见问题

******

#### 文件管理器里没有出现 `播放音频` 按钮?

请依次检查三点: 插件是否已安装; AutoJs6 插件中心里是否已启用本插件; AutoJs6 是否为 6.8.0 (内部版本号 5276) 或更高版本. 三者齐备后, 文件管理器中的音频文件即会出现播放入口.

#### 我只点了一首歌, 队列里为什么出现了同文件夹的其他歌曲?

这是同目录自动连播特性: 打开单个音频时, 插件会通过宿主管理的会话发现同文件夹的其他音频, 并按文件名自然排序排队, 方便顺序聆听. 发现过程完全只读且不进入子文件夹; 若宿主版本不支持该能力, 则只播放所选的一首.

#### 多选播放时的顺序是怎样的?

严格按照勾选顺序播放. 想按特定顺序聆听时, 按目标顺序依次勾选即可; 进入播放器后也可在队列面板中点选跳转或移除曲目.

#### 退出界面或熄屏后还会继续播放吗?

会. 播放由后台服务承载, 可通过系统媒体通知与锁屏控件控制. Android 13 及以上系统的通知权限为可选项: 拒绝后仅隐藏通知栏控件, 不影响播放本身.

#### 插件会修改或上传我的音频文件吗?

不会. 插件不申请存储权限, 对音频文件仅有只读访问; 联网仅用于检查 GitHub 上的新版本 (由用户手动触发或每日至多一次), 不上传任何文件或个人数据.

#### 为什么有的文件放不出声音或提示解码失败?

扩展名受支持不等于设备一定能解码, 部分小众编码或损坏的文件可能无法播放. 出错时错误面板会显示具体错误代码, 并提供使用其他应用打开的入口 (会自动排除本插件以避免自循环).

#### 如何让插件界面跟随 AutoJs6 的语言和主题?

默认即跟随: 语言, 夜间模式与主题色会通过 AutoJs6 的官方只读设置接口自动同步. 也可在设置页改为固定语言或自选颜色; 未安装 AutoJs6 时自动回退到系统外观与内置默认值.

#### 睡眠定时器和 A-B 循环在哪里?

都在播放界面底部工具栏. 定时器图标可选择预设或自定义时长; A-B 按钮按 '设 A 点, 设 B 点, 清除' 的顺序循环点击即可设置或取消区间循环.

******

### 权限与安全

******

音频文件可能来自不可信来源, 插件从设计上为播放流程设置了多道防线:

- 零存储权限: 插件不申请也无法获得设备存储的读写权限, 只能访问宿主或系统明确授权的单个文件.
- 永不写入: 对音频文件仅有只读访问, 不会修改, 移动或删除任何源文件.
- 严格校验: 文件管理器入口受 AutoJs6 签名权限保护, 每个播放请求的协议版本, 目标列表与只读授权都会逐项验证, 不合规请求直接拒绝.
- 有界发现: 同目录连播仅通过宿主管理的请求级会话读取, 不递归子目录, 不猜测文件地址, 会话随播放结束关闭.
- 本地会话: 启用断点续播后, 应用私有存储只保存最近一个独立文件队列及其播放状态; 仅接受仍有长期只读授权的系统文件选择器 URI, 绝不保存 Host Session 路由, 关闭该设置会立即清除记录.
- 最小联网: 互联网权限仅用于用户触发或每日一次的 GitHub 版本检查, 不涉及任何音频内容与使用数据.
- 可选通知: Android 13+ 的通知权限为可选项, 拒绝后仅隐藏通知栏控件, 播放不受影响.

请仅从官方 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) 页面或其他可信渠道获取插件安装包; 来源不明的安装包即使名称与版本号相同, 也可能被篡改.

******

### 插件接口

******

以下信息面向 AutoJs6 宿主与插件开发者, 宿主通过这些标识发现插件并完成能力协商:

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

当前版本提供协议 v12 的单文件与有序多选只读动作, 请求级同目录发现能力, 独立文档选择器以及公开的只读 Android 音频入口 (含旧式 WMA MIME 别名). 不提供可选 Host Session 能力的宿主继续仅播放所选文件.

同目录发现需要 AutoJs6 6.8.0 (内部版本号 5276) 及以上并支持 Explorer Action v12; 后续插件能力不会提高此要求. 产品自 v1.4.0 起更名为 3-Terra Player, 应用 ID 保持 `io.github.supermonster003.autojs6.plugin.audioplayer` 不变, 可覆盖安装升级.

******

### 开发路线图

******

插件的能力规划与完成情况以可勾选清单维护在 Roadmap.md 中, 按里程碑组织并附验收标准, 涵盖歌词, 均衡器, 封面取色, 沉浸式界面与工程质量等方向. 未勾选条目表示规划意向而非当前版本能力, 欢迎通过 Issues 参与讨论.

- [查看 Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/Roadmap.md)

******

### 版本历史

******

#### v1.4.1

_2026/08/31_

- `新增` 独立应用现在会在从启动器再次打开时恢复上次队列, 当前曲目, 停止位置, 循环模式, 随机状态与倍速; 恢复后保持暂停, 且仅保存仍有长期只读授权的系统文件选择器队列
- `修复` 修复播放器右上角更多菜单在部分主题下出现白底白字, 无法辨认设置菜单项的问题
- `优化` 主题色设置中的 AutoJs6 来源统一表述为 `跟随 AutoJs6`; 颜色选择面板直接显示宿主颜色的 HEX 值
- `优化` 完成十语言 README, CHANGELOG 与插件说明的基础人工复核, 并将项目主页与应用内更新地址迁移到 3-Terra Player 正式仓库

#### v1.4.0

_2026/08/29_

- `新增` 系统媒体通知全面升级: 新增上一曲 / 下一曲 / 随机播放开关与退出按钮, 使用应用专属的单色小图标, 随机状态与播放器实时同步
- `修复` 修复插件可能被 AutoJs6 插件中心自动标记为错误并禁用的问题: 插件信息与文件管理器动作现在使用各自独立的服务端点
- `修复` 清空播放队列后不再残留过期信息: 界面进入明确的空状态, 播放, 跳转, 倍速, 定时器与 A-B 等控件同步禁用
- `修复` 修复播放按钮阴影被底部区域裁切的问题; 设置与更新对话框中的单选, 多选, 进度条与按钮统一跟随主题色
- `优化` 应用与插件正式更名为 3-Terra Player: 应用 ID 保持不变, 可直接覆盖安装升级, 既有设置不受影响
- `优化` 播放器右上角菜单精简为仅保留设置入口, 移除与设置页重复的调色盘按钮

#### v1.3.0

_2026/08/29_

- `新增` 新增独立应用模式: 无需 AutoJs6 即可从启动页一次选择最多 128 个音频文件连续播放
- `新增` 新增设置页面: 语言, 夜间模式, 主题色, 断点续播, 检查更新, 发行历史与应用信息集中管理; 语言与外观默认跟随 AutoJs6, 宿主不可用时自动回退到内置默认值
- `新增` 新增手动与每日自动检查更新, 支持忽略指定版本, 并内置多语言发行历史
- `修复` 修复主题色跟随 AutoJs6 时始终提示宿主颜色不可用的问题
- `修复` 文件管理器中所有被宿主识别为音频的条目 (新增 WMA) 现在都可以直接调用本插件播放
- `优化` 断点续播只记住最近打开的一个文件: 打开其他文件或播放完毕后自动清除旧记录
- `优化` 播放界面固定保留三行信息区域并补足采样率与码率显示, 切歌与异步加载时布局不再抖动
- `优化` A-B 循环改为三次点击依次完成设 A 点, 设 B 点与清除; 底部控制区的间距与图标对齐同步优化

##### 更多版本历史可参阅

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

******

### 构建

******

本节面向希望从源码构建插件的开发者.

构建 debug APK:

```powershell
.\gradlew.bat :app:assembleDebug
```

构建 release APK (在不入库的 `sign.properties` 中配置签名后自动签名):

```powershell
.\gradlew.bat :app:assembleRelease
```

发布归档可运行 `:app:appendDigestToReleasedFiles` 任务, 将签名 APK 复制到 `releases/` 并在文件名中附加版本号与 CRC32 摘要.

构建参数集中于 `version.properties`: 最低 SDK 24 (Android 7.0), 目标 SDK 36, 当前版本 1.4.1.

******

### 本地化与文档生成

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

`strings.xml` 提供插件元数据与界面文本的本地化, `plugin_instruction.md` 提供宿主插件中心展示的使用说明. README, 更新日志与使用说明均由 JSON 源生成: 修改 `.readme/` 与 `.changelog/` 下的源文件后运行 `py .python/generate_markdown.py` 重新生成全部产物, 生成产物不手工编辑; 运行 `py .python/generate_markdown.py --check` 可校验源文件与产物是否同步.

******

### 许可

******

项目代码使用 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE) 开源. 音频播放能力基于 [AndroidX Media3](https://developer.android.com/media/media3) (Apache License 2.0).

******

### 相关链接

******

- AutoJs6 文档: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Android 安全文件共享: https://developer.android.com/training/secure-file-sharing
