<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>AutoJs6 Explorer向けの読み取り専用音声再生とMedia3バックグラウンドコントロール</p>

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

AutoJs6 Audio Playerプラグインは, AutoJs6 Explorerから開いたファイル用のアプリ内音声コントローラーと非公開バックグラウンド再生サービスを追加します. 音声MIMEタイプを持つcontent URIに対する読み取り専用Android ACTION_VIEWリクエストも受け付けます.

******

### 機能

******

- ホストが認識している18個の音声拡張子に対し, Explorer Actionプロトコルv2を通じて主要な読み取り専用アクション `play-audio` を登録します.
- Media3 ExoPlayerとMediaSessionServiceで音声を再生し, 音声フォーカス, 出力切断処理, ローカルウェイクモード, バックグラウンド再生, システムメディアコントロールに対応します.
- タイトルメタデータと再生コントロール, 任意のAndroid 13+通知権限の説明, 再生エラー表示を備えたコントローラー画面を提供します.
- `audio/*` の読み取り専用 `content` URIに対する独立したAndroid ACTION_VIEWリクエストを受け付け, 呼び出し元のextrasと広範なURI権限を破棄します.
- デコーダー失敗時は別の対応アプリでファイルを開くことができ, 自己ループを防ぐためこのプラグインを除外します.

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

プラグインをインストールすると, AutoJs6 Explorerは一覧の拡張子に主要アクションとして音声を再生を表示します. 選択するとプラグインのコントローラーが開き, 選択ファイルへの一時的な読み取り専用アクセスで非公開再生サービスが開始されます.

プラグインがない場合, このアクションは表示されません. AutoJs6は音声ファイルに対する既存の読み取り専用外部ACTION_VIEWフローを維持するため, インストール済みの別の音声アプリが処理できます. 対応する外部アプリがない場合, ホストに代替再生画面は追加されません.

******

### プラグインインターフェース

******

AutoJs6は次の識別子でプラグインを検出して実行します:

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
supported ABIs: []
```

バージョン1はAutoJs6のメインExplorerで単一ファイル用の主要な読み取り専用アクションを提供します. Explorerカタログは拡張子だけを使い, 独立したAndroid入口は引き続き `audio/*` を受け付けます.

プラグインはネイティブライブラリを含みません. `supportedAbis = emptyArray()` を宣言し, ABIに依存しない単一APKとして公開されます. AutoJs6ホストのビルド5269以降が必要です.

******

### セキュリティ

******

プラグインはストレージ権限とネットワーク権限を要求しません. Explorer入口はAutoJs6署名権限で保護され, プロトコルv2, 対象と親のcontent URI, ClipData, ソース画面, 表示名, MIMEタイプ, 宣言サイズ, 読み取り専用フラグを厳密に検証します. 非公開再生コンポーネントへは対象URIと読み取り権限だけを転送します. 公開Android入口は読み取り専用content音声リクエストだけを受け付け, 書き込み, 永続, 接頭辞権限を拒否し, 呼び出し元の任意のextrasを転送しません.

******

### 安全制限

******

- Explorerアクション1回につき対象ファイルは1つです.
- 宣言サイズが8 TiBを超えるExplorerリクエストは拒否されます.
- Explorerアクションはファイル名拡張子だけで選択され, 実行時には音声MIMEタイプも検証します.
- 公開Android入口にはACTION_VIEW, `content` URI, `audio/*`, 読み取り権限が必要です.
- 通知権限は任意です. 拒否すると通知パネルのコントロールは表示されませんが, 再生は妨げられません.
- 完了時またはデコーダーエラー時に再生を安全に終了します. 外部フォールバックは新しい読み取り権限だけを転送し, このプラグインを除外します.

******

### リリース履歴

******

# v1.0.0

###### 2026/08/02

* `機能` プラグインID `audio-player`, アクションID `play-audio`, エンジン `explorer-action`, バリアント `default` を持つAudio Playerプラグイン
* `機能` ホスト既存の18個の音声拡張子に対応する主要な読み取り専用Explorer Actionプロトコルv2入口, 空のExplorer MIMEカタログ, 必須AutoJs6ホストビルド5269
* `機能` 音声フォーカス, 出力切断処理, ローカルウェイクモード, バックグラウンド再生, システムメディアコントロール, 非公開コントローラー画面を備えたMedia3 ExoPlayerとMediaSessionService再生
* `機能` 権限を拒否しても再生を妨げない任意のAndroid 13+通知権限説明
* `機能` `content` URI音声リクエスト用の独立した読み取り専用Android ACTION_VIEW対応と, デコーダー失敗時の別アプリへの転送および自己ループ防止
* `機能` プロトコル, URI, ClipData, ソース, 名前, MIME, サイズ, 権限の厳密な検証, ストレージ権限とネットワーク権限は不要, 最小限の読み取り権限だけを転送
* `機能` ネイティブライブラリを含まない純粋なJVM実装, `supportedAbis = emptyArray()` によるABI無制限の宣言, ABIに依存しない単一APK
* `機能` スペイン語, フランス語, ロシア語, アラビア語, 日本語, 韓国語, 英語, 簡体字中国語, 香港繁体字中国語, 台湾繁体字中国語のメタデータ, UIテキスト, 使用説明, README, 変更履歴
* `依存関係` AndroidX Media3 ExoPlayer, Session, UI バージョン 1.10.1 を追加

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
