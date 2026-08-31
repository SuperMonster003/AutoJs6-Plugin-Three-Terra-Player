# v1.4.1 十语言基础复核记录

复核日期: 2026/08/31

复核范围:

- `.readme/lang_*.json`: README 与插件中心说明的共同文案源.
- `.changelog/lang_*.json`: 应用内发行历史与 Markdown CHANGELOG 的共同文案源.
- `app/src/main/res/values*/strings.xml`: Android 界面文案.
- 语言: `zh-Hans`, `zh-Hant-HK`, `zh-Hant-TW`, `en`, `fr`, `es`, `ja`, `ko`, `ru`, `ar`.

## 复核方法

1. 逐语言阅读 v1.4.1 新增的会话恢复, 更多菜单, 主题色来源与仓库迁移文案, 并抽查 README 的功能, FAQ, 安全边界和插件说明.
2. 核对十份 README 的键顺序, 字段类型, 列表长度与 FAQ 结构一致.
3. 核对十份 CHANGELOG 的版本顺序, 日期, 分类集合与每类条目数一致; 最新版本必须与 `version.properties` 一致.
4. 核对 11 个 Android `strings.xml` 目录的键集合一致, 所有 `%1$s` 等格式占位符与默认资源一致, `plugin_description` 与对应 README 简介一致.
5. 扫描未知模板占位符, 重复 JSON 键, 翻译占位标记, 全角文档符号与非目标文字系统混入.
6. 重新生成并比对全部 36 个受控 Markdown / raw 资源产物.

## 逐语言结论

| 语言 | 基础人工复核重点 | 结论 |
|---|---|---|
| 简体中文 (`zh-Hans`) | `跟随 AutoJs6`, 会话恢复范围, 本地存储与 Host Session 边界 | 已修订并通过 |
| 繁体中文香港 (`zh-Hant-HK`) | 香港用词, `主程式`, `工作階段`, `網絡`, 外挂说明 | 已修订并通过 |
| 繁体中文台湾 (`zh-Hant-TW`) | 台湾用词, `主程式`, `工作階段`, `網際網路`, 外掛說明 | 已修订并通过 |
| 英语 (`en`) | launcher / standalone queue / durable read access 等技术语义 | 已修订并通过 |
| 法语 (`fr`) | file d'attente / lecture seule / sélecteur système 术语一致性 | 已修订并通过 |
| 西班牙语 (`es`) | cola / solo lectura / selector del sistema 术语一致性 | 已修订并通过 |
| 日语 (`ja`) | キュー / 一時停止 / 永続的な読み取り権限 术语一致性 | 已修订并通过 |
| 韩语 (`ko`) | 대기열 / 일시 정지 / 영구 읽기 권한 术语一致性 | 已修订并通过 |
| 俄语 (`ru`) | очередь / только чтение / постоянный доступ 术语一致性 | 已修订并通过 |
| 阿拉伯语 (`ar`) | RTL 文案顺序, قائمة الانتظار / إذن قراءة دائم 术语一致性 | 已修订并通过 |

本次修订统一说明了以下行为:

- 从启动器重新打开独立应用时恢复上次队列, 当前曲目, 停止位置, 循环 / 随机状态与倍速, 恢复后保持暂停.
- 只保存系统文件选择器授予长期只读权限的最近一个独立队列; Host Session 路由不落盘; 关闭续播设置立即清除记录.
- 主题色来源使用各语言的 `跟随 AutoJs6` 等价表述, 颜色选择面板只显示实际 HEX 值.
- 项目主页, Releases 与应用内更新检查统一指向 `SuperMonster003/AutoJs6-Plugin-Three-Terra-Player`.

## 自动核查结果

```text
LANG_OK zh-Hans
LANG_OK zh-Hant-HK
LANG_OK zh-Hant-TW
LANG_OK en
LANG_OK fr
LANG_OK es
LANG_OK ja
LANG_OK ko
LANG_OK ru
LANG_OK ar
LOCALIZATION_AUDIT_OK languages=10 android_placeholders=parity unexpected_scripts=none
MARKDOWN_OK languages=10 artifacts=36 mode=check
```

本记录是发布前的基础人工与结构复核, 不等同于由十位母语审校者完成的专业语言认证. 用户提供的 TalkBack 全流程与阿拉伯语 RTL 实机结果另记于 `Roadmap.md`.
