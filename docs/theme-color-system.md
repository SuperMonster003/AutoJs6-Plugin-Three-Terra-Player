# 单色源主题配色系统

本文记录音频播放器的运行时主题机制。目标不是把一个颜色机械地写入所有控件，而是以一个不透明 RGB 色作为唯一视觉输入，生成在亮色与暗色环境中均可读、可区分、可验证的一整套语义颜色。

## 色源与优先级

主题选择持久化为三种互斥模式：

| 模式 | 色源 | 行为 |
|---|---|---|
| 跟随 AutoJs6 | AutoJs6 官方插件设置 Provider 的 `themeColorPrimary` | 默认模式；播放器恢复前台时检测宿主色变化并自动重建界面 |
| Material 500 预置 | 插件内置的 19 个 Material Design 500 色 | 与经典 Material Dialog 颜色组保持一致，选择后不依赖宿主 |
| 自定义 | 用户输入的 `#RRGGBB` | 输入时实时生成五角色预览，确认后持久化 |

AutoJs6 色源通过 `AutoJs6HostSettingsContract` 对应的只读 ContentProvider 获取。Provider 同时受 `org.autojs.permission.PLUGIN` 签名权限、已启用插件入口和官方签名校验保护；插件不读取宿主私有 SharedPreferences，也不猜测其存储路径。返回包必须同时满足协议版本、宿主包名和必要颜色键校验，否则视为不可用。

当 AutoJs6 未安装、被停用、契约版本不受支持或调用方不是官方签名时，播放器仍保留“跟随”选择，并临时使用 AutoJs6 的默认非 INRT 主题色 `#FFDEAD`。这样宿主稍后恢复可用时无需用户重新选择。

对应的 AutoJs6 源码参考点：

- `plugin-api/common-plugin-api/.../AutoJs6HostSettingsContract.kt`：跨进程字段与版本契约；
- `app/.../core/plugin/settings/OfficialPluginSettingsProvider.kt`：只读快照与访问控制；
- `app/.../theme/ThemeColorManager.kt`、`ThemeColor.kt`：宿主默认色、当前色和持久化方式；
- `app/.../res/values/material_design_colors.xml`：Material 颜色原始色表。

插件随附的 `libs/common-plugin-api.aar` 已从同一 AutoJs6 源构建并同步，因此不会在插件源码中复制协议常量。

## HCT 色调生成

色源首先被强制规范为不透明 ARGB，然后交给 Material Color Utilities 的 HCT（Hue、Chroma、Tone）模型。相较于直接修改 HSV 明度，HCT 的 tone 与人眼感知亮度及对比度的关系更稳定，适合为不同色相生成相同语义层级。

生成器建立四条色调轴：

1. 主色轴：保留 seed hue；有彩色 seed 的 chroma 至少为 48、最多为 96，使浅淡输入仍能产生清晰的交互强调色。
2. 次色轴：保留 seed hue，chroma 控制在 16–24，供提示容器等较温和区域使用。
3. 中性色轴：保留 seed hue，只带 0–6 的轻微色相，用于背景和多级 surface。
4. 中性变体轴：chroma 控制在 8–12，用于轮廓、次级文字和底部工具按钮。

当 seed chroma 小于 4 时按无彩色处理，灰色预置不会被强制变成彩色。错误色是唯一有意不继承 seed hue 的语义角色，固定使用红色 HCT 轴，避免“绿色错误”等误导性表达。

应用栏直接保留原始 seed，而不是替换成某个固定 tone；其前景会在纯黑和纯白中选择对比度更高者。因此“跟随 AutoJs6”时仍能一眼识别宿主颜色，包括很亮的 `#FFDEAD`。

## 亮色与暗色角色

主要 tone 映射如下。数值越接近 100 越亮：

| 语义角色 | 亮色 tone | 暗色 tone | 用途 |
|---|---:|---:|---|
| `primary` / `onPrimary` | 40 / 100 | 80 / 20 | 进度、主播放按钮、选中轮廓 |
| `primaryContainer` / `onPrimaryContainer` | 90 / 10 | 30 / 90 | 选中工具按钮、无封面占位区 |
| `secondaryContainer` / `onSecondaryContainer` | 90 / 10 | 30 / 90 | 通知权限提示条 |
| `background` / `onBackground` | 98 / 10 | 6 / 90 | 页面背景与主文字 |
| `surfaceContainerLow` | 96 | 10 | 队列和选择器底板 |
| `surfaceContainer` | 94 | 12 | 普通可选卡片 |
| `surfaceContainerHigh` | 92 | 17 | 错误或提升层级的表面基准 |
| `surfaceContainerHighest` | 90 | 22 | 按压反馈 |
| `bottomControlSurface` / `bottomControlContent` | 86 / 10 | 24 / 90 | 最下排五个功能按钮 |
| `outline` / `outlineVariant` | 50 / 80 | 60 / 30 | 控件边界与弱分隔 |

底部五个按钮同时使用三层区分：普通状态具有独立容器色，外轮廓相对页面背景至少 3:1，图标/文字相对按钮容器至少 4.5:1；激活状态切换到 `primaryContainer`、`onPrimaryContainer` 和 `primary` 轮廓。禁用状态另以透明度降级，避免与可操作控件混淆。

## 对比度约束

生成器的 JVM 测试覆盖 AutoJs6 默认色、全部 19 个 Material 500 seed、纯黑、纯白及极端近黑/近白自定义色，并同时验证亮色和暗色结果：

- 应用栏前景、主/次色前景、容器前景和底部按钮内容：不低于 4.5:1；
- 页面主文字对背景、表面文字对表面：不低于 7:1；
- 轮廓对页面背景：不低于 3:1；
- 所有输出角色必须为完全不透明颜色；
- 应用栏必须逐位保留规范化后的原始 seed。

这些是代码级不变量，不依赖某一组手工挑选的静态 `colors.xml`。新增预置色或调整 tone 映射时，如破坏任一底线，单元测试会直接失败。

## 交互与更新

播放器工具栏的调色板按钮打开主题底部面板。面板包含 AutoJs6 实时色状态、19 个本地化 Material 500 色块、自定义色入口和当前生成结果预览。选择色源后只重建 Activity；MediaSessionService 与播放队列不重启，因此播放不中断。

本地偏好带单调递增 revision。Activity 每次回到前台时比较 revision；若当前为“跟随 AutoJs6”，还比较严格解析后的宿主外观快照。任一变化都会触发一次受控重建，从而同步工具栏、系统栏、进度条、封面占位、提示条、播放控制、队列、对话框和错误面板。

封面动态取色不属于本机制：当前 seed 只来自 AutoJs6、预置或自定义选择。未来若加入封面取色，应作为第四种明确色源接入相同生成器和对比度测试，而不是绕过语义角色直接给控件着色。
