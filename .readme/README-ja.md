<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="three-terra-player-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>バックグラウンド再生に対応した音声プレーヤープラグイン兼単体アプリ</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 言語 (Languages)

******

現在の README.md は次の言語に対応しています:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-es.md)
- 日本語 [ja] # 現在
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ar.md)

******

### 概要

******

3-Terra Player (旧名 Audio Player) は, AutoJs6 ファイルマネージャー向けの音声再生プラグインであると同時に, 単体でも使えるシンプルな音声プレーヤーです. インストールして有効にすると, AutoJs6 のファイルマネージャーで音声ファイルをタップするだけで直接再生でき, サードパーティ製プレーヤーはもう必要ありません. ホーム画面から普通のアプリのように開き, 複数の音声ファイルをまとめて選んで連続再生することもできます.

インストール後は AutoJs6 が自動的にプラグインを検出するため, 設定は一切不要です. 再生は Android 公式メディアフレームワークの Media3 (ExoPlayer) を基盤とし, バックグラウンド再生とシステムメディア通知からの操作に対応します. 音声ファイルへのアクセスは常に読み取り専用で, ストレージ権限を要求せず, 元のファイルを変更したり削除したりすることは決してありません.

******

### 機能ハイライト

******

- ローカルのプレイリスト: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL の順序, タイトル, 重複項目を保持. ホストでは同じフォルダーのファイルを読み取り, 単独起動ではフォルダーを選択して相対パスを解決. 一度に 1 リスト, 最大 128 項目. ネットワーク URL, HLS, 入れ子のリストは未対応.
- ファイルマネージャーからワンタップ再生: AutoJs6 のファイルマネージャーで音声ファイルをタップするだけで再生が始まります.
- 同じフォルダーの自動連続再生: 1 曲を開くと同じフォルダー内の他の音声を自動検出し, ファイル名の自然順で連続再生します (最大 128 曲, 選択した曲から開始).
- 複数選択キュー再生: ファイルマネージャーで最大 128 個の音声を選択してワンタップで再生でき, 選択した順序を厳密に維持します.
- 充実した再生画面: アルバムアート, タイトル / アーティスト / アルバムのタグ, サンプルレートやビットレートなどの技術情報, ドラッグできるシークバーを備えます.
- 日常の操作を完備: 前の曲 / 次の曲, 5 / 10 / 15 / 30 秒から選べる早戻しと早送り, 順番 / シャッフル / 1 曲リピート, 保存できる 0.5 倍から 2 倍の既定速度, キュー終了時の動作に対応します.
- 再生キューパネル: これから再生する曲をいつでも確認でき, タップで曲へジャンプしたりキューから削除したりできます. 再生中の曲もひと目で分かります.
- スリープタイマー: 15 / 30 / 60 分のプリセットまたは任意の時間を指定でき, 現在の曲が終わってから停止することも可能で, 終了前の 5 秒間は音量がフェードアウトします.
- A-B リピート: 好きな区間を繰り返し聴けるため, リスニング練習や耳コピに最適です.
- バックグラウンド再生: 画面を離れたり消灯したりしても再生は途切れず, システムメディア通知とロック画面から直接操作できます.
- セッション復元: ランチャーから独立アプリを再度開くと, 前回のキュー, 現在の曲, 停止位置, リピート / シャッフル状態, 再生速度を復元します; 復元後は一時停止を維持します.
- 単体アプリモード: AutoJs6 がインストールされていなくても使用でき, スタート画面から最大 128 個の音声ファイルをまとめて選んで再生できます.
- 応答性の高い外観: 言語 / ナイトモード / 基本色は AutoJs6 に従うか個別に設定できます. アルバムアートから読みやすい色を生成してエッジツーエッジのプレーヤーへ反映し, 切り替えと触覚フィードバックはシステム設定に従います.

******

### 使用方法

******

1. [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) ページから最新のプラグイン APK をダウンロードし, AutoJs6 が動作する端末にインストールします.
2. AutoJs6 のプラグインセンターを開き, `3-Terra Player` が認識され有効になっていることを確認します.
3. AutoJs6 のファイルマネージャーで音声ファイルをタップして `音声を再生` を選ぶか, 長押しで複数選択してからツールバーの `選択した音声を再生` を選びます.
4. ホーム画面から `3-Terra Player` を直接開き, ファイル選択ボタンで複数の音声をまとめて選んで再生を始めることもできます.

> プラグインセンターにプラグインが表示されない場合は, まず AutoJs6 を 6.8.0 (内部ビルド 5276) 以上に更新してください. プラグイン自体は Android 7.0 (API 24) 以上の端末をサポートし, 単体アプリモードは AutoJs6 に依存しません.

******

### 対応音声フォーマット

******

ファイルマネージャーの入口は汎用音声タイプ `audio/*` を宣言し, 一部の端末の不完全または古い MIME タイプテーブルとの互換性のため, 次の 19 種類の拡張子も明示的にカバーします:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

拡張子が対応していても必ずデコードできるとは限りません: 実際の再生能力は Media3, Android のバージョン, 端末のデコーダー, ファイルの内容に依存します. 再生できないファイルに出会った場合は, エラーパネルから別のアプリで開くことを選べます.

******

### よくある質問

******

#### ファイルマネージャーに `音声を再生` ボタンが表示されないのですが?

次の 3 点を順に確認してください: プラグインがインストールされていること, AutoJs6 のプラグインセンターで本プラグインが有効になっていること, AutoJs6 が 6.8.0 (内部ビルド 5276) 以上であること. 3 つがそろえば, ファイルマネージャーの音声ファイルに再生の入口が表示されます.

#### 1 曲しかタップしていないのに, なぜ同じフォルダーの他の曲がキューに入っているのですか?

これは同じフォルダーの自動連続再生機能です: 単一の音声を開くと, プラグインはホスト管理のセッションを通じて同じフォルダー内の他の音声を検出し, ファイル名の自然順でキューに並べて続けて聴けるようにします. 検出は完全に読み取り専用で, サブフォルダーには入りません. ホストのバージョンがこの機能に対応していない場合は, 選択した 1 曲だけを再生します.

#### 複数選択で再生するときの順序はどうなりますか?

選択した順序どおりに厳密に再生します. 特定の順序で聴きたいときは, その順序でファイルを選択してください. プレーヤーに入った後も, キューパネルからタップで曲へジャンプしたり削除したりできます.

#### 画面を閉じたり消灯したりしても再生は続きますか?

続きます. 再生はバックグラウンドサービスが担うため, システムメディア通知とロック画面のコントロールから操作できます. Android 13 以上では通知権限は任意です: 拒否すると通知パネルのコントロールが非表示になるだけで, 再生自体には影響しません.

#### プラグインが音声ファイルを変更したりアップロードしたりすることはありますか?

ありません. プラグインはストレージ権限を要求せず, 音声ファイルへのアクセスは読み取り専用だけです. ネットワークは GitHub 上の新バージョンの確認 (ユーザーが手動で実行するか 1 日最大 1 回) にのみ使用し, ファイルや個人データを一切アップロードしません.

#### 一部のファイルで音が出なかったりデコード失敗と表示されたりするのはなぜですか?

拡張子が対応していても端末が必ずデコードできるとは限らず, 一部のマイナーなコーデックや破損したファイルは再生できないことがあります. エラー時にはエラーパネルに具体的なエラーコードが表示され, 別のアプリで開く入口も用意されます (自己ループを避けるため本プラグインは自動的に除外されます).

#### プラグインの画面を AutoJs6 の言語やテーマに合わせるにはどうすればよいですか?

既定でそのように動作します: 言語, ナイトモード, テーマカラーは AutoJs6 の公式読み取り専用設定インターフェースを通じて自動的に同期されます. 設定画面で固定の言語や好みの色に変更することもできます. AutoJs6 がインストールされていない場合は, システムの外観と内蔵の既定値に自動的にフォールバックします.

#### スリープタイマーと A-B リピートはどこにありますか?

どちらも再生画面下部のツールバーにあります. タイマーアイコンからプリセットまたは任意の時間を選べます. A-B ボタンは 'A 点を設定, B 点を設定, 解除' の順にタップで循環し, 区間リピートの設定と解除ができます.

******

### 権限とセキュリティ

******

音声ファイルは信頼できない提供元から届く可能性があるため, プラグインは設計段階から再生フローに複数の防御層を設けています:

- ストレージ権限ゼロ: プラグインは端末ストレージの読み書き権限を要求せず, 取得もできません. アクセスできるのは, ホストまたはシステムが明示的に許可した個々のファイルだけです.
- 書き込みは一切なし: 音声ファイルへのアクセスは読み取り専用だけで, 元のファイルを変更, 移動, 削除することはありません.
- 厳密な検証: ファイルマネージャーの入口は AutoJs6 の署名権限で保護され, 各再生リクエストのプロトコルバージョン, 対象リスト, 読み取り専用の権限付与を項目ごとに検証し, 規約に合わないリクエストは直ちに拒否します.
- 境界のある検出: 同じフォルダーの連続再生はホスト管理のリクエスト単位セッションを通じてのみ読み取り, サブフォルダーへは再帰せず, ファイルの場所を推測することもなく, セッションは再生終了とともに閉じられます.
- ローカルセッションのみ: 続きから再生を有効にすると, アプリ専用ストレージには直近の独立ファイル選択キューと再生状態だけを保存します. 永続的な読み取り権限があるシステム選択 URI だけを対象とし, Host Session の経路は保存せず, 設定を無効にすると記録を直ちに消去します.
- 最小限のネットワーク: インターネット権限はユーザーが実行するか 1 日 1 回の GitHub バージョン確認だけに使用し, 音声の内容や使用データには一切関与しません.
- 任意の通知: Android 13+ の通知権限は任意です. 拒否すると通知パネルのコントロールが非表示になるだけで, 再生には影響しません.

プラグインのインストールパッケージは公式の [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) ページまたはその他の信頼できる経路からのみ入手してください. 出所不明のパッケージは, 名前とバージョン番号が同じでも改ざんされている可能性があります.

******

### プラグインインターフェース

******

以下の情報は AutoJs6 ホストとプラグイン開発者向けです. ホストはこれらの識別子でプラグインを検出し, 機能ネゴシエーションを行います:

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

現在のバージョンは, プロトコル v12 の単一ファイルおよび順序付き複数選択の読み取り専用アクション, リクエスト単位の同じフォルダー検出機能, 単体のドキュメントピッカー, 公開の読み取り専用 Android 音声入口 (旧式の WMA MIME エイリアスを含む) を提供します. 任意の Host Session 機能を提供しないホストでは, 引き続き選択したファイルだけを再生します.

同じフォルダーの検出には AutoJs6 6.8.0 (内部ビルド 5276) 以上と Explorer Action v12 への対応が必要です. 今後のプラグイン機能でこの要件を引き上げることはありません. 製品は v1.4.0 から 3-Terra Player に改名されましたが, アプリケーション ID は `io.github.supermonster003.autojs6.plugin.audioplayer` のまま変わらないため, 上書きインストールで更新できます.

******

### 開発ロードマップ

******

プラグインの機能計画と進捗は, マイルストーンごとに受け入れ基準付きで整理されたチェック可能なリストとして Roadmap.md で管理され, 歌詞, イコライザー, アルバムアートからの配色, エッジツーエッジ UI, エンジニアリング品質などの方向をカバーします. 未チェックの項目は計画中の意向であり, 現行バージョンの機能ではありません. Issues での議論を歓迎します.

- [Roadmap.md を見る](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/Roadmap.md)

******

### リリース履歴

******

#### v1.6.0

_2026/09/13_

- `機能` ローカルのプレイリスト: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL の順序, タイトル, 重複項目を保持. ホストでは同じフォルダーのファイルを読み取り, 単独起動ではフォルダーを選択して相対パスを解決. 一度に 1 リスト, 最大 128 項目. ネットワーク URL, HLS, 入れ子のリストは未対応
- `修正` 言語とナイトモードのダイアログ項目を Material Body1 の 16sp に変更し, 大きすぎるプラットフォーム既定のリスト文字を使わないようにしました
- `修正` ビルド環境の言語にかかわらずプラグインのバージョン日付を英語で表示
- `改善` 多言語リソースの統一, プラグイン有効化の明確化, リリース成果物の検証

#### v1.5.0

_2026/09/12_

- `機能` アルバムアートからグラデーション, ツールバー, 操作ボタン, キューへ読みやすい配色をリアルタイム生成します. 各画面はステータスバー, 画面の切り欠き, ジェスチャー領域を考慮したエッジツーエッジ表示になりました
- `機能` 再生設定に保存可能な既定速度, 5 / 10 / 15 / 30 秒の早戻しと早送り, キュー終了時の停止 / 先頭へ戻って一時停止 / 再再生を追加しました
- `機能` 再生 / 一時停止とアルバムアートに控えめな切り替えを追加し, 主要操作にはシステム設定に従う触覚フィードバックを追加しました. システムのアニメーションが無効なら状態を直接切り替えます
- `修正` 言語とナイトモードのダイアログ項目を Material Body1 の 16sp に変更し, 大きすぎるプラットフォーム既定のリスト文字を使わないようにしました
- `改善` アルバムアートのデコードと最大 64x64 の色サンプル処理をメインスレッド外へ移しました. 任意の色でも本文 4.5:1 と輪郭 3:1 の既存コントラスト基準を維持します
- `改善` 再生可視化の調査と RMS バケットの試作を完了しました. 録音権限が必要な Visualizer は引き続き除外し, 将来の性能検証向けに権限不要の Media3 PCM 抽出経路を記録しました
- `改善` README のレイアウトと Gradle プラットフォームのバージョン管理方式を統一
- `改善` プラグインの説明を簡潔にし, 多言語リソースの句読点を統一
- `改善` 外部表示エントリを External Viewer に改名し, ビューアーの意味を統一
- `改善` 更新ダイアログのリリース履歴ボタンから内蔵のリリース履歴ページを開くように変更
- `改善` 意図しないネイティブ依存関係をビルド時に拒否し, JSON レポートを生成

#### v1.4.1

_2026/08/31_

- `機能` 独立アプリをランチャーから再度開いたとき, 前回のキュー, 現在の曲, 停止位置, リピートモード, シャッフル状態, 再生速度を復元するようになりました; 復元後は一時停止を維持し, 永続的な読み取り権限があるシステムファイル選択キューだけを保存します
- `修正` 一部のテーマでプレーヤー右上のその他メニューが白地に白文字となり, 設定項目を判別できない問題を修正しました
- `改善` テーマカラー設定の AutoJs6 ソース表記を `AutoJs6 に従う` に統一し, カラー選択パネルではホスト色の HEX 値を直接表示するようにしました
- `改善` 10 言語の README, CHANGELOG, プラグイン説明を基本的に目視確認し, プロジェクトとアプリ内更新のリンクを 3-Terra Player 公式リポジトリへ移行しました

##### その他のリリース履歴

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

******

### ビルド

******

この節はソースからプラグインをビルドしたい開発者向けです.

debug APK をビルド:

```powershell
.\gradlew.bat :app:assembleDebug
```

release APK をビルド (バージョン管理外の `sign.properties` に署名を設定すると自動的に署名されます):

```powershell
.\gradlew.bat :app:assembleRelease
```

リリースアーカイブには `:app:appendDigestToReleasedFiles` タスクを実行し, 署名済み APK を `releases/` にコピーして, ファイル名にバージョン番号と CRC32 ダイジェストを付加できます.

ビルドパラメータは `version.properties` に集約されています: 最小 SDK 24 (Android 7.0), ターゲット SDK 36, 現在のバージョン 1.6.0.

******

### ローカライズとドキュメント生成

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

`strings.xml` はプラグインのメタデータと UI テキストのローカライズを提供し, `plugin_instruction.md` はホストのプラグインセンターに表示される使用説明を提供します. README, 更新履歴, 使用説明はすべて JSON ソースから生成されます: `.readme/` と `.changelog/` 配下のソースを編集した後, `py .python/generate_markdown.py` を実行して全成果物を再生成してください (生成物は手動で編集しません). `py .python/generate_markdown.py --check` でソースと成果物の同期を検証できます.

******

### ライセンス

******

プロジェクトコードは [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE) の下で公開されているオープンソースです. 音声再生機能は [AndroidX Media3](https://developer.android.com/media/media3) (Apache License 2.0) を基盤としています.

******

### 関連リンク

******

- AutoJs6 ドキュメント: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Android の安全なファイル共有: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/docs/16kb.md)
