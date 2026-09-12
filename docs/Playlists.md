# 本地播放列表

两个播放器使用相同的解析规则. Terra 从列表中选择音频, Ember 选择视频; 标题与读取路径相互独立, 保留文件中的条目顺序和重复项.

## 支持的格式

| 格式 | 解析内容 |
| --- | --- |
| M3U / M3U8 | 每行文件引用, `#EXTINF` 标题, 注释, 空行 |
| PLS | `[playlist]`, `FileN`, `TitleN`; 按数字 N 排序, 不信任声明的条目数 |
| XSPF | XML 命名空间, `trackList/track`, `title`, 多个候选 `location`, `xml:base` |
| WPL | `smil/body/seq/media` 的 `src` |
| ASX / WAX / WVX | `ENTRY`, `TITLE`, 多个候选 `REF HREF`, `BASE`; 标签和属性不区分大小写 |
| MPCPL | `MPCPLAYLIST`, `filename`, `label`; 排除采集设备条目 |
| DPL | `DAUMPLAYLIST`, `file`, `title` |

支持 UTF-8, UTF-8 BOM, UTF-16 BOM, XML 编码声明. 无声明的旧式文本先尝试 UTF-8, 再尝试 GB18030 和 Windows-1252; 无 BOM / 声明的编码检测可能存在歧义. M3U8 的无 BOM 文本按严格 UTF-8 解码.

## 从 AutoJs6 打开

一次打开一个播放列表文件, 使用插件的单文件播放动作. 列表不参与普通媒体文件的同目录自动发现, 也不会作为一个媒体项送给解码器.

宿主需要提供现有的同目录只读会话. 支持直接文件名, `./文件名`, 以及明确位于列表同目录的绝对路径 / `file:` URI. 匹配对象必须来自宿主枚举出的可读普通文件; 不读取符号链接, 不递归子目录, 不扩大宿主会话权限. 子目录或跨目录列表可以改用独立入口.

## 从独立应用或其他应用打开

在播放器的文件选择器中选取一个列表, 或在其他应用的打开方式中选择播放器. 如果列表包含相对路径, 播放器会提示选择列表所在的文件夹. 选择完成后, 通过系统文档接口解析该文件夹及其子目录中的引用.

文件夹授权只用于本次播放, 不会申请存储运行时权限. 单个列表文件的授权不会被当作同目录授权; 不按文件名猜测 `content:` URI. 列表内的完整 `content:` URI 只有在应用已经拥有其只读授权时才可使用. 独立入口中的本地路径应相对于所选择的文件夹, 超出该文件夹的 `..` 引用会被跳过.

例如, 将以下列表保存为 `favorites.m3u`, 与两首音频放在同一个文件夹中:

```m3u
#EXTM3U
#EXTINF:-1,第二首先播放
song02.mp3
#EXTINF:-1,接着播放第一首
song01.mp3
#EXTINF:-1,再听一遍
song02.mp3
```

队列保持 `song02`, `song01`, `song02` 的顺序. 视频列表用 `.mp4` 等视频文件引用即可.

## 边界与反馈

- 一个文档最多 1 MiB, 最多解析 4096 个条目, 播放队列最多 128 项. 超大文档拒绝加载; 超出队列上限的条目计入跳过数.
- 缺失, 不可读, 不符合播放器媒体类型, 网络地址, 嵌套播放列表及越界引用会被跳过. 有可用条目时继续播放并提示数量; 全部不可用时显示明确错误.
- HTTP / HTTPS 媒体与 HLS 流暂不在本地播放列表功能范围内. 包含 `#EXT-X-` 的清单按 HLS 识别并给出专门提示, 不将分片展开成曲目.
- 二进制 FPL, 光盘导航 / MPLS, 嵌套列表展开, ASX 的时间剪辑 / 循环指令不在支持范围内.
- XML 外部实体与 DTD 被禁用, 不从列表中加载外部 XML 资源.
- 原文件保持只读. 普通媒体的单文件播放, 多选队列和同目录自然排序沿用原有行为; 播放列表文件一次打开一个.

## 维护与验证

`playlist/` 中的解析器, 路径策略和文档加载器在两个项目保持一致, 只有包名及主题 Activity 不同. 修改时需要同步同名实现和测试.

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest
py .python/generate_markdown.py
```

测试覆盖格式, 编码, 顺序, 重复项, 候选位置, 容量限制, XML 外部实体, 越界路径, 以及播放队列的 Android Intent 交接. 设备测试需要分别运行 `PlaylistParserInstrumentationTest` 和播放契约测试.

格式参考: [XSPF 规范](https://www.xspf.org/spec), [WPL smil 元素](https://learn.microsoft.com/en-us/previous-versions/windows/desktop/wmp/smil-element), [ASX ENTRY](https://learn.microsoft.com/en-us/previous-versions/windows/embedded/aa451685(v=msdn.10)), [MPC-HC 解析实现](https://github.com/clsid2/mpc-hc/blob/develop/src/mpc-hc/PlayerPlaylistBar.cpp), [HLS RFC 8216](https://www.rfc-editor.org/rfc/rfc8216).
