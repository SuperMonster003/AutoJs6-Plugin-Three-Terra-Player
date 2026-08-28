# Explorer Action 队列可行性调研

调研日期: 2026/08/27.

## 结论与后续落地

Explorer Action v2 传入的父目录 `content://` URI **不能用于枚举同目录子项**. 它由宿主的 `AppFileProvider` 生成, 而 `AppFileProvider` 直接继承 AndroidX `FileProvider`; `FileProvider.query()` 只为 URI 指向的单个文件或目录返回一行 `_display_name` / `_size`, 不提供目录成员列表. `FLAG_GRANT_PREFIX_URI_PERMISSION` 只决定已知后代 URI 的读取授权范围, 不提供后代名称或 URI 发现机制.

Explorer Action v4 增加了有序多目标列表与短期 host session, 但宿主对“单文件 + 只读”动作不会附加可列目录的 session. 因此 v1.2.0 只能通过显式多选形成队列.

Explorer Action v12 新增可选的 `READ_SIBLINGS` 能力后, 上述阻塞已解除. v1.2.1 的单文件动作显式声明该能力, 宿主为目标父目录创建 UID 固定的只读 Host Session; 插件只能分页列出目标的直接同级项并按相对文件名打开只读 PFD. 会话不暴露明文路径, 不允许递归、符号链接越界、写入或持久授权, 关闭后立即失效.

当前实现策略:

- 普通“播放音频”优先通过 v12 Host Session 发现直接同级音频并自然排序; 无能力、失败或超时时回退当前文件.
- 新增 Explorer Action v4 “播放所选音频”入口; 用户在文件管理器中显式选择的 1–128 个音频按宿主传入顺序组成队列.
- 不猜测兄弟 URI, 不把展示路径当文件系统路径, 不申请存储权限; 只在存活的 Host Session 内按需读取直接同级文件.
- 最低宿主保持 `AutoJs6 6.8.0 build 5276`, Explorer Action v12; 后续插件能力不提升 build 门槛.

## 取证记录

| 宿主 / 协议 | 传入能力 | 是否能形成队列 | 自动枚举兄弟文件 |
|---|---|---:|---:|
| AutoJs6 6.8.0 Alpha7, build 5269, Explorer Action v2 | 单个目标 URI、父目录 URI、读取与前缀授权 | 否, 仅单文件 | 否 |
| AutoJs6 6.8.0, build 5276, Explorer Action v4 | 有序 `TARGETS`, 多选动作可接收多个显式目标 | 是, 仅显式选择 | 否 |
| AutoJs6 6.8.0 build 5276 + Explorer Action v12 | 单文件动作可选声明 `READ_SIBLINGS`, 宿主提供直接同级只读 Host Session | 是, 自动或显式选择 | 是, 仅直接同级 |

对应宿主实现证据:

- `ExplorerActionLauncher` 使用 `AppFileProvider.getUriForFile()` 生成目标和父目录 URI.
- `AppFileProvider` 直接继承 `androidx.core.content.FileProvider`, 未覆盖 `query()`.
- AndroidX Core 1.16.0 的 `FileProvider.query()` 为 URI 对应对象构造容量为 1 的 `MatrixCursor`, 不调用 `listFiles()`.
- AutoJs6 commit `01f117a40` 首次加入 Explorer Action v4 多目标能力; v4 host session 只允许在被选中的目录目标内部 `listChildren()`, 不能从单个文件目标越界到其父目录.
- v12 `ExplorerActionLauncher` 在动作通过 catalog 显式声明 `READ_SIBLINGS` 时, 为单文件只读动作创建能力受限的 host session.
- `ExplorerActionHostSession` 将调用者固定为插件 UID, 仅接受 `""` 或单段安全相对文件名, 并同时校验直接父目录、规范路径、可读普通文件及非符号链接.

## 已满足的重新评估条件

原调研设定的下列条件中, 第 2 项已由 Explorer Action v12 满足:

1. 宿主为单文件只读动作提供受限的父目录分页枚举接口, 且返回稳定排序与每个子项的只读句柄 / URI.
2. 协议允许文件动作显式请求“读取同级项目”能力, 由宿主按已安装插件身份建立能力受限会话. **已满足**.
3. 宿主直接在动作请求中附带当前目录的有序音频目标快照.

因此 v1.2.1 已实现自动同目录队列; v4 显式多选仍作为用户主动编排顺序和无 `READ_SIBLINGS` 环境下的兼容路径保留.
