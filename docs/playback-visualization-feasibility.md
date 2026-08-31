# 播放可视化可行性调研

日期: 2026/08/31
目标版本: v1.5.0（M6 调研项）

## 约束与结论

3-Terra Player 当前坚持零敏感权限、后台 Media3 播放、可变倍速和低额外功耗。基于这些约束，v1.5.0 **不接入平台 `Visualizer`，也不在正式包中启用实时频谱**。后续若实现，首选仅在播放器页面可见时启用的 Media3 PCM 抽头，先提供低成本 RMS / 波形，再以真机性能数据决定是否加入 FFT。

这不是放弃可视化，而是把“申请录音权限”和“为视觉效果长期占用解码 CPU”排除出默认路径。当前正式包继续保持零权限；M7 可按本文的原型边界继续推进。

## 方案比较

| 方案 | 权限 | 数据能力 | 主要代价 | 结论 |
|---|---|---|---|---|
| Android `android.media.audiofx.Visualizer` | 必须 `RECORD_AUDIO`; 捕获全局输出混音还需 `MODIFY_AUDIO_SETTINGS` | 低质量 8-bit 单声道波形、8-bit FFT、Peak/RMS | 与零权限原则直接冲突；还需管理 native effect 生命周期 | 不采用 |
| Media3 `TeeAudioProcessor` / 自定义 `AudioProcessor` | 无新增系统权限 | 解码后的 PCM，可计算 RMS、波形或 FFT | Media3 `@UnstableApi`; 音频线程必须无阻塞、低分配；需验证倍速、格式切换和设备兼容性 | 推荐为后续实验路径 |
| 独立预解码整首波形 | 无新增系统权限 | 可提前生成整首概览波形 | 重复解码、首屏延迟、缓存与 URI 生命周期复杂；不适合实时频谱 | 只适合作为未来静态波形补充 |

平台文档明确说明 [`Visualizer`](https://developer.android.com/reference/android/media/audiofx/Visualizer) 即使不是录音接口，也因隐私原因要求 `RECORD_AUDIO`; 它返回的是部分、低质量的 8-bit 单声道波形或 FFT。使用 audio session 0 捕获输出混音还要求 `MODIFY_AUDIO_SETTINGS`。因此，即使只分析本应用的播放会话，仍无法满足本项目的零敏感权限约束。

Media3 的 [`AudioProcessor`](https://developer.android.com/reference/androidx/media3/common/audio/AudioProcessor) 在 PCM 管线中处理音频缓冲区；[`TeeAudioProcessor`](https://developer.android.com/reference/androidx/media3/exoplayer/audio/TeeAudioProcessor) 会原样输出输入，同时把数据交给一个 sink，官方说明它可插入自定义 processor chain，并可放在倍速和跳过静音处理之后。这说明“不额外录音、直接消费本应用已解码 PCM”在架构上可行，但相关 API 标为 `@UnstableApi`，且 Tee 本身定位于诊断 / 调试，正式功能应封装为自有、可替换的轻量 processor。

## 原型评估

仓库加入了测试态 `PcmVisualizationPrototypeTest`，对 PCM16 样本按固定窗口计算归一化 RMS：

- 单次扫描为 O(n)，128 样本窗口不需要 FFT，也不保留整首 PCM；
- 静音输出 0，半幅方波输出约 0.5，所有结果被约束在 0…1；
- 最后不足一个窗口的样本仍会输出，避免曲尾丢失；
- 原型只验证信号算法，不修改播放管线、不新增权限，也不会进入正式运行路径。

生产原型建议使用预分配的环形缓冲区，把音频线程产生的 RMS 峰值合并为低频采样点；UI 最多以屏幕刷新节奏读取，不允许从音频回调直接操作 View、分配位图或执行 FFT。页面进入后台时立即停采样。

## 性能与兼容性风险

Media3 的 [`DefaultAudioSink`](https://developer.android.com/reference/androidx/media3/exoplayer/audio/DefaultAudioSink) 区分 PCM、offload 和 passthrough 输出。PCM processor 路径需要覆盖采样率、声道数、PCM16 / float 等格式切换，并验证 seek、切曲、A-B 循环和倍速后的时间一致性。

Android 官方的 [Media3 电量说明](https://developer.android.com/media/media3/exoplayer/battery-consumption) 指出，长时间熄屏音频可从 audio offload 获益，而 offload 会限制倍速和音效等处理能力。由此推断，强制 PCM 抽头可能牺牲部分设备的 offload / passthrough 机会；在没有真机功耗和 underrun 基线前，不应为了默认视觉效果强制改变输出模式。

## 后续准入条件

M7 若继续实现，需要同时满足：

1. 不新增 `RECORD_AUDIO` 或 `MODIFY_AUDIO_SETTINGS`；
2. 可视化只在页面可见且用户启用时运行，后台播放不采样；
3. 音频回调零阻塞、使用有界预分配缓冲区，UI 消费慢时丢旧帧而不是阻塞播放；
4. API 24、29、33、35+ 覆盖 PCM16 / float、单声道 / 立体声、倍速、seek、A-B、切曲；
5. 与无可视化基线相比，无可听 underrun，CPU / 电量增量达到可接受范围；
6. 若必须关闭 offload / passthrough，界面明确采用按需模式，默认关闭。

综上，M6 调研验收完成：平台 `Visualizer` 的权限冲突不可接受；解码 PCM 自绘在技术上可行，已用 RMS 桶算法验证最小信号路径，但正式集成需等待 M7 的音频管线与功耗基线工作。
