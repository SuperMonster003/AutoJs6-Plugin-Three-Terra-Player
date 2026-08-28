<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>ファイルマネージャープラグイン. アプリ内操作とバックグラウンド制御で音声ファイルを再生</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Audio-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 言語 (Languages)

******

現在の README.md は次の言語に対応しています:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-es.md)
- 日本語 [ja] # 現在
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ar.md)

******

### 概要

******

オーディオプレーヤーは, ファイルマネージャーから開いた音声ファイル用のアプリ内コントローラーと非公開バックグラウンド再生サービスを提供します. 音声MIMEタイプを持つcontent URIに対する読み取り専用Android ACTION_VIEWリクエストも受け付けます.

******

### 機能

******

- ホストが認識する18個の音声拡張子に対し, Explorer Actionプロトコルv12で単一ファイル用 `play-audio` と順序付き複数選択用 `play-audio-selection` を登録します.
- Media3 ExoPlayerとMediaSessionServiceで音声を再生し, 音声フォーカス, 出力切断処理, ローカルウェイクモード, バックグラウンド再生, システムメディアコントロールに対応します.
- アルバムアート, タグ, 技術情報, シーク, 10秒ジャンプ, 順次 / シャッフル / 1曲リピート, 0.5倍から2倍の速度調整を備えた再生画面を提供します.
- ホストから渡された順序で明示的に選択した最大128ファイルを再生し, 前 / 次の曲と, 曲の選択または削除ができるキューを提供します.
- サービスが保持するプリセット / カスタムのスリープタイマー, 現在の曲の終了後に停止, 最後の5秒のフェードアウト, A-Bリピートに対応します.
- 曲ごとの再生位置を記憶し, 現在のタイトル, アート, 前 / 次, 10秒ジャンプをシステムメディア画面へ反映します.
- `audio/*` の読み取り専用 `content` URIに対する独立したAndroid ACTION_VIEWリクエストを受け付け, 呼び出し元のextrasと広範なURI権限を破棄します.
- デコーダー失敗時は別の対応アプリでファイルを開くことができ, 自己ループを防ぐためこのプラグインを除外します.
- Explorerから1曲を開くと, リクエスト単位のHost Sessionを通じて読み取り可能な直接同階層の音声を自然順の上限付きキューとして検出し, 明示的な複数選択ではホストの選択順を保ちます.

******

### Explorerの拡張子

******

Explorerカタログは意図的に次の拡張子だけを照合し, MIMEタイプリストを空として宣言します:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave
```

拡張子の一致はデコードを保証しません. 実際の再生能力はMedia3, Androidプラットフォーム, 端末のコーデック, ファイル内容に依存します.

******

### ホストの動作

******

インストール後, ファイルマネージャーは1ファイルに音声を再生を, 複数選択ツールバーに選択した音声を再生を表示します. 単一ファイル入口は選択した曲から開始し, 読み取り可能な直接同階層の音声を自然順で検出できます. 明示的な複数選択ではホストの選択順を保ちます.

プラグインがない場合, このアクションは表示されません. ホストは音声ファイルに対する既存の読み取り専用外部ACTION_VIEWフローを維持するため, インストール済みの別の音声アプリが処理できます. 対応する外部アプリがない場合, ホストに代替再生画面は追加されません.

******

### プラグインインターフェース

******

ホストは次の識別子でプラグインを検出して実行します:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: audio-player
engine: explorer-action
variant: default
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 4
Explorer MIME types: []
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/*
required host build: 5276
```

バージョン1.2.1はプロトコルv12の単一ファイル読み取り専用主要アクションを提供し, リクエスト単位の直接同階層読み取り機能を受け取れます. 順序付き複数選択アクションも維持します. 独立したAndroid入口は1ファイルのまま `audio/*` を受け付けます. 任意のHost Sessionがないホストでは選択ファイルだけを再生します.

同一フォルダー検出には AutoJs6 6.8.0 build 5276 以降と Explorer Action v12 が必要です. 今後のプラグイン機能でもこの要件は引き上げません.

******

### セキュリティ

******

プラグインはストレージ権限とインターネットアクセス権限を要求しません. 署名で保護された入口はプロトコルv12, 順序付きTARGETSとClipData, 識別子, 親子関係, メタデータ, 読み取り専用フラグを厳密に検証します. 任意のHost SessionはホストによってプラグインUIDに固定され, 選択ファイルの直接の親の列挙と, 選択ファイルまたは読み取り可能な直接同階層ファイルのオープンだけを許可します. 再生コンポーネントは不透明な合成ルートだけを受け取り, ファイルシステムパスは受け取りません. 公開Android入口は読み取り専用の1ファイルのままです.

******

### 安全制限

******

- 単一アクションは正確に1つの選択ファイルから開始し, 読み取り可能な直接同階層音声の上限付きキューを構成できます. 選択アクションは順序を保った1から128個の重複しないファイルを受け付けます.
- 宣言サイズが8 TiBを超えるExplorerリクエストは拒否されます.
- Explorerアクションはファイル名拡張子だけで選択され, 実行時には音声MIMEタイプも検証します.
- 同階層検出はホスト管理のリクエスト単位セッションだけで利用でき, 再帰しません. プラグインは同階層URIを推測せず, ファイルシステムパスも受け取りません.
- 公開Android入口にはACTION_VIEW, `content` URI, `audio/*`, 読み取り権限が必要です.
- 通知権限は任意です. 拒否すると通知パネルのコントロールは表示されませんが, 再生は妨げられません.
- 完了時またはデコーダーエラー時に再生を安全に終了します. 外部フォールバックは新しい読み取り権限だけを転送し, このプラグインを除外します.

******

### リリース履歴

******

# v1.2.2

###### 2026/08/27

* `修正` プラグインの上書き更新後、実行中の AutoJs6 ファイルマネージャーがキャッシュ済みのプロトコル v4 アクションを送信し続け、すべての音声で無効なリクエストになる問題を修正しました。ゲートウェイは読み取り専用の v4～v12 リクエストを受け付け、引き続き v12 を公開します
* `修正` Android または端末メーカーの MIME テーブルがワイルドカードや application タイプを返す場合に、公開済みの音声拡張子が誤って拒否される問題を修正しました。拡張子の許可リストから安定した正規音声 MIME タイプを補完します
* `改善` 拒否された Explorer リクエストは、ファイル名、表示パス、URI を含まないプライバシー安全な理由コードを記録し、今後の契約差異を直接診断できるようになりました

# v1.2.1

###### 2026/08/27

* `機能` 単一ファイルの「音声を再生」アクションで Explorer Action v12 を介して同じフォルダーの読み取り可能な音声を最大 128 件検出し、選択曲から始まる自然順キューを構築
* `修正` API 24 で Android がゲートウェイ Activity のマニフェストから権限を伴わない FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS を付加した際に、正当な Explorer リクエストを誤って拒否する問題を修正
* `修正` 兄弟曲への自動切り替え時に MediaSession の戻り Intent を再構築してクラッシュする問題を修正。元の許可対象を再生中の項目とは独立した Host Session のアンカーとして保持します
* `改善` 選択曲は元の content URI を保持し、兄弟曲はリクエスト限定 Host Session のファイル記述子だけでストリーム再生。兄弟 URI の推測や再帰、書き込み、ストレージ、永続アクセスは追加しません
* `改善` 音声拡張子フィルターにより .m4a 音声と同名の .mp4 動画をキューから除外し、既存の明示的な複数選択キューの動作は維持
* `改善` Host Session の所有権をバックグラウンド再生サービスへ移し、キュー置換、起動失敗、再生完了、サービス破棄時に閉じます
* `依存関係` 同梱 Explorer Action API をプロトコル v4 から後方互換の v12 兄弟読み取り拡張へ更新し、最小ホストビルド 5276 を維持

# v1.2.0

###### 2026/08/27

* `機能` Explorer Actionプロトコルv4に対応し, 明示的に選択した最大128個の音声ファイルからキューを作る順序付き複数選択アクションを追加
* `機能` 前 / 次, 曲の選択と削除ができるキュー, 順次 / シャッフル / 1曲リピートを備えたMedia3ネイティブプレイリスト
* `機能` 15 / 30 / 60分, カスタム時間, 現在の曲の終了後に停止, 最後の5秒のフェードアウトを備えたサービス管理のスリープタイマー
* `機能` 選択区間を繰り返すA-Bリピート
* `改善` システムメディア操作に前 / 次と10秒巻き戻し / 早送りを追加し, メタデータと再開位置が現在のキュー項目に追従
* `改善` Explorerリクエスト検証を順序付きTARGETSとClipData, 一意の識別子, ホストセッション, 全選択ファイルへ拡張し, 読み取り権限は拡大しない設計
* `改善` 親FileProvider URIでは子を列挙できないことを文書化し, 兄弟ファイルの自動検出とURI推測を無効のまま維持; 明示的な複数選択を安全な経路として採用
* `依存関係` 同梱Explorer Action APIをプロトコルv2からv4へ更新し, 必須ホストビルドを5276へ変更
* `依存関係` AndroidX RecyclerView 1.4.0を追加

##### その他のリリース

* [CHANGELOG-ja.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

******

### ビルド

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Releaseビルド:

```powershell
.\gradlew.bat :app:assembleRelease
```

ビルドパラメーターは `version.properties` から取得します. 現在の最小SDKは24, ターゲットSDKは36です.

******

### リソース構成

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` はプラグインのメタデータとUIテキストをローカライズします. `plugin_instruction.md` はホストが表示する説明を提供します. `.python/generate_markdown.py` はJSONソースからローカライズされたREADMEと変更履歴を生成します.

******

### リンク

******

- AutoJs6ドキュメント: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Androidの安全なファイル共有: https://developer.android.com/training/secure-file-sharing
